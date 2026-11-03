// Pruebas de aceptación del Release 2 (Examen 1B): comprueban en el navegador los tres escenarios de cada
// historia de usuario (HU-14 a HU-21). Cada historia usa sus propias personas y sus propios objetos, para que
// los avisos y las coincidencias de una no cambien el resultado de otra.
//
// Uso:      npm install puppeteer-core
//           node casos-release-2.js <carpeta de capturas> [URL base]
// Requiere: Node.js y Google Chrome. La aplicación debe estar en ejecución con la base de datos vacía
//           (solo con las categorías). Al final escribe resultados.json en la carpeta de capturas.
const puppeteer = require('puppeteer-core');
const fs = require('fs');
const path = require('path');

const SALIDA = process.argv[2] || 'capturas';
const BASE = process.argv[3] || 'http://localhost:8080';
const CHROME = 'C:/Program Files/Google/Chrome/Application/chrome.exe';

// Lugar de referencia en la zona Centro del campus y metros que hay en un grado de longitud junto al ecuador
const ORIGEN = { latitud: -0.2100, longitud: -78.4900 };
const METROS_POR_GRADO = 111195;

const resultados = [];
let casoActual = null;

function caso(id, nombre) {
  casoActual = { id, nombre, pasos: [] };
  resultados.push(casoActual);
  console.log(`\n${id} ${nombre}`);
}

function paso(numero, esperado, cumple) {
  casoActual.pasos.push({ numero, esperado, cumple: Boolean(cumple) });
  console.log(`  ${cumple ? 'ok   ' : 'FALLO'} escenario ${numero}: ${esperado}`);
}

/** Punto a los metros indicados al este del lugar de referencia. */
const aMetros = (metros) => ({ latitud: ORIGEN.latitud, longitud: ORIGEN.longitud + metros / METROS_POR_GRADO });

/** Fecha (aaaa-mm-dd) de hace los días indicados. */
function haceDias(dias) {
  const fecha = new Date();
  fecha.setDate(fecha.getDate() - dias);
  return `${fecha.getFullYear()}-${String(fecha.getMonth() + 1).padStart(2, '0')}-${String(fecha.getDate()).padStart(2, '0')}`;
}

const textoDe = (page, selector) => page.$eval(selector, (e) => e.innerText.trim()).catch(() => '');
const existe = async (page, selector) => (await page.$(selector)) !== null;
const textos = (page, selector) => page.$$eval(selector, (es) => es.map((e) => e.innerText.replace(/\s+/g, ' ').trim()));

async function esperarMapa(page) {
  if (await existe(page, '#mapa')) {
    await page.waitForNetworkIdle({ idleTime: 600, timeout: 15000 }).catch(() => {});
  }
}

async function captura(page, nombre) {
  await esperarMapa(page);
  await page.screenshot({ path: path.join(SALIDA, nombre), fullPage: true });
}

async function ir(page, ruta) {
  await page.goto(BASE + ruta, { waitUntil: 'domcontentloaded' });
}

async function enviar(page, selector) {
  await Promise.all([page.waitForNavigation({ waitUntil: 'domcontentloaded' }), page.click(selector)]);
}

async function identificar(page, usuario) {
  await ir(page, '/identificacion');
  await page.type('#nombre', usuario.nombre);
  await page.type('#correo', usuario.correo);
  await enviar(page, 'form.formulario button[type="submit"]');
}

async function marcarEnMapa(page, punto) {
  await page.$eval('#mapa', (m) => m.scrollIntoView({ block: 'center' }));
  await esperarMapa(page);
  const posicion = await page.evaluate((p) => {
    const marco = document.getElementById('mapa').getBoundingClientRect();
    const pixel = mapa.latLngToContainerPoint([p.latitud, p.longitud]);
    return { x: marco.x + pixel.x, y: marco.y + pixel.y };
  }, punto);
  await page.mouse.click(posicion.x, posicion.y);
}

/** Llena el formulario de registro (ya abierto) y lo publica. Devuelve el identificador del reporte. */
async function completarReporte(page, datos) {
  if (datos.tipo) await page.click(`input[name="tipo"][value="${datos.tipo}"]`);
  if (datos.categoria) {
    const categoria = await page.$$eval('#categoria option', (opciones, nombre) =>
      opciones.find((o) => o.textContent.trim() === nombre).value, datos.categoria);
    await page.select('#categoria', categoria);
  }
  await page.type('#descripcion', datos.descripcion);
  await page.$eval('#fecha', (campo, fecha) => { campo.value = fecha; }, datos.fecha);
  await marcarEnMapa(page, datos.punto);
  const campos = await page.$$('input[name="pregunta"]');
  for (let i = 0; i < (datos.preguntas || []).length; i += 1) {
    await campos[i].type(datos.preguntas[i]);
  }
  await enviar(page, 'form.formulario button[type="submit"]');
  return new URL(page.url()).searchParams.get('registrado');
}

async function registrarReporte(page, datos) {
  await ir(page, '/reportes/nuevo');
  return completarReporte(page, datos);
}

/** Fecha y hora (aaaa-mm-ddThh:mm) del próximo día de lunes a viernes, a la hora indicada. */
function proximoDiaHabil(hora) {
  const fecha = new Date();
  do {
    fecha.setDate(fecha.getDate() + 1);
  } while (fecha.getDay() === 0 || fecha.getDay() === 6);
  const dos = (n) => String(n).padStart(2, '0');
  return `${fecha.getFullYear()}-${dos(fecha.getMonth() + 1)}-${dos(fecha.getDate())}T${hora}`;
}

/** El reclamante responde las preguntas de verificación del objeto encontrado. */
async function reclamar(page, idReporte) {
  await ir(page, `/reportes/detalle?id=${idReporte}`);
  await enviar(page, '#reclamar');
  for (const campo of await page.$$('form.formulario input[type="text"]')) {
    await campo.type('Respuesta de prueba');
  }
  await enviar(page, 'form.formulario button[type="submit"]');
}

/** Quien encontró el objeto abre la reclamación pendiente de la persona indicada. */
async function revisarReclamacionDe(page, idReporte, nombre) {
  await ir(page, `/devoluciones?reporte=${idReporte}`);
  const enlace = await page.$$eval('#pendientes tbody tr', (filas, n) =>
    filas.find((f) => f.innerText.includes(n)).querySelector('a').href, nombre);
  await page.goto(enlace, { waitUntil: 'domcontentloaded' });
}

/** Con la reclamación aceptada abierta, propone el lugar (o un punto oficial) y la fecha de la entrega. */
async function proponerEntrega(page, { lugar, punto, fecha }) {
  if (punto) {
    const valor = await page.$$eval('#punto option', (opciones, nombre) =>
      opciones.find((o) => o.textContent.includes(nombre)).value, punto);
    await page.select('#punto', valor);
  }
  if (lugar) await page.type('#lugar', lugar);
  await page.$eval('#fecha', (campo, f) => { campo.value = f; }, fecha);
  await enviar(page, '#proponer');
}

/** Registra un objeto encontrado, lo reclama el reclamante y quien lo encontró acepta la reclamación. */
async function reclamacionAceptada(encontrante, reclamante, nombreReclamante, datos) {
  const idReporte = await registrarReporte(encontrante, { tipo: 'ENCONTRADO', fecha: haceDias(1),
    preguntas: ['¿Qué tiene escrito?'], ...datos });
  await reclamar(reclamante, idReporte);
  await revisarReclamacionDe(encontrante, idReporte, nombreReclamante);
  await enviar(encontrante, '#aceptar');
  return { idReporte, idReclamacion: new URL(encontrante.url()).searchParams.get('reclamacion') };
}

const SECCIONES = [];

// ---------------------------------------------------------------------------------------------- HU-14
SECCIONES.push(async ({ sesion }) => {
  caso('HU-14', 'Ver los objetos encontrados que coinciden con mi objeto perdido');
  const andrea = await sesion({ nombre: 'Andrea Paz', correo: 'andrea.paz@epn.edu.ec' });
  const bruno = await sesion({ nombre: 'Bruno Salas', correo: 'bruno.salas@epn.edu.ec' });
  const idPerdido = await registrarReporte(andrea, { tipo: 'PERDIDO', categoria: 'Calculadoras',
    descripcion: 'Calculadora Casio gris', fecha: haceDias(40), punto: aMetros(0) });
  const encontrado = (descripcion, categoria, metros, dias) => registrarReporte(bruno, { tipo: 'ENCONTRADO',
    categoria, descripcion, fecha: haceDias(dias), punto: aMetros(metros), preguntas: ['¿Qué tiene escrito atrás?'] });
  await encontrado('Calculadora con estuche azul', 'Calculadoras', 150, 37);
  await encontrado('Calculadora gris sin tapa', 'Calculadoras', 40, 38);
  await encontrado('Llaves de casillero', 'Llaves', 20, 38);
  await encontrado('Calculadora del laboratorio', 'Calculadoras', 350, 38);
  await encontrado('Calculadora olvidada en el aula', 'Calculadoras', 30, 5);

  await ir(andrea, '/inicio');
  await enviar(andrea, '#misReportes a[href*="/coincidencias"]');
  const lista = await textos(andrea, '#coincidencias li a');
  await captura(andrea, 'hu14-coincidencias.png');
  const enLista = (d) => lista.some((t) => t.includes(d));
  const metrosDe = (d) => Number((lista.find((t) => t.includes(d)) || '').match(/A (\d+) m/)?.[1]);
  paso(1, 'La calculadora a unos 40 m y 2 días después aparece con su distancia',
    enLista('Calculadora gris sin tapa') && Math.abs(metrosDe('Calculadora gris sin tapa') - 40) <= 8
    && await existe(andrea, `#coincidencias a[href*="/reportes/detalle?id="]`));
  paso(2, 'No aparecen las llaves (otra categoría), la de 350 m ni la encontrada 35 días después',
    lista.length === 2 && !enLista('Llaves de casillero') && !enLista('Calculadora del laboratorio')
    && !enLista('Calculadora olvidada en el aula'));
  paso(3, 'La de unos 40 m aparece antes que la de unos 150 m',
    lista[0].includes('Calculadora gris sin tapa') && lista[1].includes('Calculadora con estuche azul'));
  return idPerdido;
});

// ---------------------------------------------------------------------------------------------- HU-15
SECCIONES.push(async ({ sesion }) => {
  caso('HU-15', 'Avisar al dueño de un objeto perdido que yo lo encontré');
  const carla = await sesion({ nombre: 'Carla Ruiz', correo: 'carla.ruiz@epn.edu.ec' });
  const diego = await sesion({ nombre: 'Diego Mora', correo: 'diego.mora@epn.edu.ec' });
  const idLlaves = await registrarReporte(carla, { tipo: 'PERDIDO', categoria: 'Llaves',
    descripcion: 'Llaves con llavero rojo', fecha: haceDias(3), punto: aMetros(-250) });

  await ir(diego, `/reportes/detalle?id=${idLlaves}`);
  await captura(diego, 'hu15-detalle-yo-lo-encontre.png');
  await enviar(diego, '#loEncontre');
  const tipoMarcado = await diego.$eval('input[name="tipo"][value="ENCONTRADO"]', (r) => r.checked);
  const categoria = await diego.$eval('#categoria', (s) => s.options[s.selectedIndex].text.trim());
  await captura(diego, 'hu15-formulario-precargado.png');
  await completarReporte(diego, { descripcion: 'Llavero rojo con tres llaves', fecha: haceDias(2),
    punto: aMetros(-240), preguntas: ['¿Qué dice el llavero?'] });
  await ir(carla, '/inicio');
  const fila = await textoDe(carla, '#hallazgos tbody tr');
  await captura(carla, 'hu15-mis-reportes-hallazgo.png');
  paso(1, 'Carla ve en «Mis reportes» el hallazgo de Diego, con un enlace para reclamarlo',
    tipoMarcado && categoria === 'Llaves' && fila.includes('Llaves con llavero rojo')
    && fila.includes('Llavero rojo con tres llaves') && fila.includes('Diego Mora')
    && await existe(carla, '#hallazgos a[href*="/reportes/detalle?id="]'));

  await ir(carla, `/reportes/detalle?id=${idLlaves}`);
  paso(2, 'En su propio reporte, Carla no ve el botón «Yo lo encontré»', !(await existe(carla, '#loEncontre')));

  const idCarnet = await registrarReporte(carla, { tipo: 'PERDIDO', categoria: 'Documentos',
    descripcion: 'Carnet estudiantil de Carla', fecha: haceDias(1), punto: aMetros(-200) });
  await ir(diego, `/reportes/nuevo?perdido=${idCarnet}`);
  await ir(carla, '/inicio');
  await Promise.all([carla.waitForNavigation({ waitUntil: 'domcontentloaded' }),
    carla.$$eval('#misReportes tbody tr', (filas) => {
      filas.find((f) => f.innerText.includes('Carnet estudiantil de Carla')).querySelector('button.boton-enlace').click();
    })]);
  await ir(diego, `/reportes/nuevo?perdido=${idCarnet}`);
  const error = await textoDe(diego, 'main');
  await captura(diego, 'hu15-reporte-retirado.png');
  await ir(diego, '/inicio');
  const misReportes = await textoDe(diego, '#misReportes');
  paso(3, 'Sobre el carnet ya retirado, el sistema responde «Este reporte de pérdida ya no está activo.» y no registra nada',
    error.includes('Este reporte de pérdida ya no está activo.') && !misReportes.includes('Carnet'));
});

// ---------------------------------------------------------------------------------------------- HU-16
SECCIONES.push(async ({ sesion }) => {
  caso('HU-16', 'Recibir un aviso cuando se reporta un objeto que coincide con mi pérdida');
  const elena = await sesion({ nombre: 'Elena Vega', correo: 'elena.vega@epn.edu.ec' });
  const fabian = await sesion({ nombre: 'Fabián Torres', correo: 'fabian.torres@epn.edu.ec' });
  // Elena pierde una chompa al este del campus, lejos de los objetos de las otras historias
  await registrarReporte(elena, { tipo: 'PERDIDO', categoria: 'Ropa y accesorios',
    descripcion: 'Chompa azul de la EPN', fecha: haceDias(4), punto: aMetros(480) });
  const encontrado = (descripcion, categoria, metros) => registrarReporte(fabian, { tipo: 'ENCONTRADO',
    categoria, descripcion, fecha: haceDias(2), punto: aMetros(metros), preguntas: ['¿Qué talla es?'] });

  await encontrado('Chompa azul en una banca', 'Ropa y accesorios', 450);
  await ir(elena, '/avisos');
  let avisos = await textos(elena, '#avisos li');
  await captura(elena, 'hu16-aviso-coincidencia.png');
  paso(1, 'Elena tiene en «Mis avisos» el aviso de coincidencia con un enlace al reporte de Fabián',
    avisos.length === 1 && avisos[0].includes('«Chompa azul de la EPN»') && avisos[0].includes('«Chompa azul en una banca»')
    && await existe(elena, '#avisos a[href*="/reportes/detalle?id="]'));

  await encontrado('Llavero con forma de búho', 'Llaves', 480);
  await ir(elena, '/avisos');
  avisos = await textos(elena, '#avisos li');
  paso(2, 'Las llaves encontradas en el mismo lugar no generan aviso para Elena', avisos.length === 1);

  await encontrado('Chompa azul talla M', 'Ropa y accesorios', 500);
  await ir(elena, '/avisos');
  avisos = await textos(elena, '#avisos li');
  const conFecha = await elena.$$eval('#avisos li .fecha', (fs) => fs.every((f) => /\d{4}-\d{2}-\d{2} \d{2}:\d{2}/.test(f.textContent)));
  await captura(elena, 'hu16-avisos-ordenados.png');
  paso(3, 'Con dos avisos, el más reciente (chompa talla M) aparece primero, cada uno con su fecha y su enlace',
    avisos.length === 2 && avisos[0].includes('Chompa azul talla M') && avisos[1].includes('Chompa azul en una banca')
    && conFecha && (await elena.$$('#avisos li a')).length === 2);
});

// ---------------------------------------------------------------------------------------------- HU-17
SECCIONES.push(async ({ sesion }) => {
  caso('HU-17', 'Ver cuántos avisos tengo sin leer');
  const gabriela = await sesion({ nombre: 'Gabriela León', correo: 'gabriela.leon@epn.edu.ec' });
  const hugo = await sesion({ nombre: 'Hugo Ríos', correo: 'hugo.rios@epn.edu.ec' });
  // Gabriela pierde unos audífonos y Hugo reporta dos parecidos cerca: ella recibe 2 avisos
  await registrarReporte(gabriela, { tipo: 'PERDIDO', categoria: 'Electrónicos',
    descripcion: 'Audífonos blancos con estuche', fecha: haceDias(3), punto: aMetros(-100) });
  for (const [descripcion, metros] of [['Audífonos en la cafetería', -90], ['Audífonos blancos sin estuche', -120]]) {
    await registrarReporte(hugo, { tipo: 'ENCONTRADO', categoria: 'Electrónicos', descripcion,
      fecha: haceDias(1), punto: aMetros(metros), preguntas: ['¿De qué marca son?'] });
  }

  await ir(gabriela, '/reportes');
  const barra = await textoDe(gabriela, '#enlaceAvisos');
  await captura(gabriela, 'hu17-contador.png');
  paso(1, 'Con 2 avisos sin leer, la barra muestra «Avisos (2)»', barra === 'Avisos (2)');

  await ir(gabriela, '/avisos');
  const resaltados = (await gabriela.$$('#avisos li.nuevo')).length;
  await captura(gabriela, 'hu17-avisos-nuevos.png');
  await ir(gabriela, '/inicio');
  const despues = await textoDe(gabriela, '#enlaceAvisos');
  paso(2, 'Al abrir «Mis avisos» los 2 aparecen como nuevos y después la barra muestra «Avisos» sin número',
    resaltados === 2 && despues === 'Avisos');

  await ir(hugo, '/inicio');
  const barraHugo = await textoDe(hugo, '#enlaceAvisos');
  await captura(hugo, 'hu17-sin-avisos.png');
  paso(3, 'Hugo, sin avisos sin leer, ve «Avisos» sin número', barraHugo === 'Avisos' && !(await existe(hugo, '#avisosSinLeer')));
});

// ---------------------------------------------------------------------------------------------- HU-18
SECCIONES.push(async ({ sesion }) => {
  caso('HU-18', 'Recibir avisos cuando cambia el estado de mi reclamación');
  const ivan = await sesion({ nombre: 'Iván Castro', correo: 'ivan.castro@epn.edu.ec' });
  const juana = await sesion({ nombre: 'Juana Pérez', correo: 'juana.perez@epn.edu.ec' });
  const karen = await sesion({ nombre: 'Karen Díaz', correo: 'karen.diaz@epn.edu.ec' });
  const idEstuche = await registrarReporte(ivan, { tipo: 'ENCONTRADO', categoria: 'Útiles',
    descripcion: 'Estuche de geometría verde', fecha: haceDias(1), punto: aMetros(300), preguntas: ['¿Qué tiene escrito?'] });
  await reclamar(juana, idEstuche);
  await reclamar(karen, idEstuche);

  await revisarReclamacionDe(ivan, idEstuche, 'Juana Pérez');
  await enviar(ivan, '#aceptar');
  await ir(juana, '/avisos');
  const avisoJuana = await textoDe(juana, '#avisos li');
  await captura(juana, 'hu18-aviso-aceptada.png');
  paso(1, 'Juana recibe «Su reclamación de «Estuche de geometría verde» fue aceptada»',
    avisoJuana.includes('Su reclamación de «Estuche de geometría verde» fue aceptada'));

  await revisarReclamacionDe(ivan, idEstuche, 'Karen Díaz');
  await ivan.type('#motivo', 'El estuche tiene otro nombre escrito');
  await enviar(ivan, '#rechazar');
  await ir(karen, '/avisos');
  const avisoKaren = await textoDe(karen, '#avisos li');
  await captura(karen, 'hu18-aviso-rechazada.png');
  paso(2, 'Karen recibe el aviso de rechazo con el motivo',
    avisoKaren.includes('fue rechazada. Motivo: El estuche tiene otro nombre escrito'));

  await revisarReclamacionDeAceptada(ivan, idEstuche);
  await proponerEntrega(ivan, { lugar: 'Puerta de la Biblioteca Central', fecha: proximoDiaHabil('10:00') });
  await ir(juana, '/avisos');
  const propuesta = (await textos(juana, '#avisos li'))[0];
  await captura(juana, 'hu18-aviso-propuesta.png');
  await enviar(juana, '#avisos li a');
  const llevaAlAcuerdo = await existe(juana, '#confirmarAcuerdo');
  paso(3, 'Juana recibe el lugar y la fecha de la entrega, y el aviso la lleva al acuerdo para confirmarlo',
    propuesta.includes('en Puerta de la Biblioteca Central el ') && llevaAlAcuerdo);
});

/** Quien encontró el objeto abre la reclamación aceptada desde el panel del reporte. */
async function revisarReclamacionDeAceptada(page, idReporte) {
  await ir(page, `/devoluciones?reporte=${idReporte}`);
  await enviar(page, '#enCurso');
}

(async () => {
  fs.mkdirSync(SALIDA, { recursive: true });
  const navegador = await puppeteer.launch({ executablePath: CHROME, headless: true, args: ['--lang=es-EC'] });
  // Cada persona tiene su propia sesión, como si usara su propio navegador
  const sesion = async (usuario) => {
    const contexto = await navegador.createBrowserContext();
    const page = await contexto.newPage();
    await page.setViewport({ width: 1200, height: 760, deviceScaleFactor: 1.5 });
    page.on('dialog', (dialogo) => dialogo.accept());
    await identificar(page, usuario);
    return page;
  };
  for (const seccion of SECCIONES) {
    await seccion({ sesion });
  }
  await navegador.close();

  const fallos = resultados.flatMap((c) => c.pasos.filter((p) => !p.cumple).map((p) => `${c.id} escenario ${p.numero}`));
  fs.writeFileSync(path.join(SALIDA, 'resultados.json'), JSON.stringify({
    fecha: new Date().toISOString().slice(0, 10), casos: resultados }, null, 1));
  console.log(fallos.length ? `\nFALLOS: ${fallos.join(', ')}` : '\nTODOS LOS ESCENARIOS SE CUMPLEN');
  process.exit(fallos.length ? 1 : 0);
})().catch((e) => { console.error(e); process.exit(1); });
