// Casos de prueba del Incremento 4 (Prueba 1): comprueban en el navegador los escenarios de los criterios
// de aceptación de las historias HU-11 (CP-06), HU-12 (CP-07) y HU-13 (CP-08), con dos sesiones
// (Bruno Salas y Andrea Paz).
//
// Uso:      npm install puppeteer-core
//           node casos-incremento-4.js <carpeta de capturas> [URL base] [--solo-datos]
// Requiere: Node.js y Google Chrome. La aplicación debe estar en ejecución con la base de datos vacía.
//           Con --solo-datos solo registra los datos de ejemplo (sirve para preparar una demostración).
const puppeteer = require('puppeteer-core');
const fs = require('fs');
const path = require('path');

const SALIDA = process.argv[2];
const BASE = process.argv[3] || 'http://localhost:8080';
const SOLO_DATOS = process.argv.includes('--solo-datos');
const CHROME = 'C:/Program Files/Google/Chrome/Application/chrome.exe';

const BRUNO = { nombre: 'Bruno Salas', correo: 'bruno.salas@epn.edu.ec' };
const ANDREA = { nombre: 'Andrea Paz', correo: 'andrea.paz@epn.edu.ec' };
const MOCHILA = 'Mochila negra con cuadernos de Cálculo';
const CAMARA = 'Cámara Canon en estuche gris';
const CALCULADORA = 'Calculadora científica Casio';
const AUDIFONOS = 'Audífonos inalámbricos blancos';
const LLAVES = 'Llaves con llavero azul';
const MOTIVO_RETIRO = 'El reporte fue retirado por su autor.';

// Datos de ejemplo de historias.json: tres objetos encontrados por Bruno y dos perdidos por Andrea
const REPORTES = [
  { autor: 'b', tipo: 'ENCONTRADO', categoria: 'Útiles', descripcion: MOCHILA,
    punto: { latitud: -0.21045, longitud: -78.48920 }, preguntas: ['¿Qué nombre tiene el cuaderno?'] },
  { autor: 'b', tipo: 'ENCONTRADO', categoria: 'Electrónicos', descripcion: CAMARA,
    punto: { latitud: -0.20800, longitud: -78.48800 }, preguntas: ['¿Qué tiene en la correa?'] },
  { autor: 'b', tipo: 'ENCONTRADO', categoria: 'Calculadoras', descripcion: CALCULADORA,
    punto: { latitud: -0.21080, longitud: -78.48870 }, preguntas: ['¿Qué iniciales tiene detrás?'] },
  { autor: 'a', tipo: 'PERDIDO', categoria: 'Electrónicos', descripcion: AUDIFONOS,
    punto: { latitud: -0.21300, longitud: -78.48900 } },
  { autor: 'a', tipo: 'PERDIDO', categoria: 'Llaves', descripcion: LLAVES,
    punto: { latitud: -0.21010, longitud: -78.48980 } },
];

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

const textoDe = (page, selector) => page.$eval(selector, (e) => e.innerText.trim()).catch(() => '');
const existe = async (page, selector) => (await page.$(selector)) !== null;

async function esperarMapa(page) {
  if (await existe(page, '#mapa')) {
    await page.waitForNetworkIdle({ idleTime: 600, timeout: 15000 }).catch(() => {});
  }
}

async function captura(page, nombre) {
  if (!SOLO_DATOS) {
    await esperarMapa(page);
    await page.screenshot({ path: path.join(SALIDA, nombre), fullPage: true });
  }
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

async function registrarReporte(page, datos) {
  await ir(page, '/reportes/nuevo');
  await page.click(`input[name="tipo"][value="${datos.tipo}"]`);
  const categoria = await page.$$eval('#categoria option', (opciones, nombre) =>
    opciones.find((o) => o.textContent.trim() === nombre).value, datos.categoria);
  await page.select('#categoria', categoria);
  await page.type('#descripcion', datos.descripcion);
  await page.$eval('#fecha', (campo) => { campo.value = '2026-10-24'; });
  await marcarEnMapa(page, datos.punto);
  const campos = await page.$$('input[name="pregunta"]');
  for (let i = 0; i < (datos.preguntas || []).length; i += 1) {
    await campos[i].type(datos.preguntas[i]);
  }
  await enviar(page, 'form.formulario button[type="submit"]');
}

async function idDeReporte(page, descripcion) {
  await ir(page, '/reportes');
  return page.$$eval('#resultados li a', (enlaces, d) => {
    const enlace = enlaces.find((a) => a.textContent.includes(d));
    return enlace ? new URL(enlace.href).searchParams.get('id') : null;
  }, descripcion);
}

async function reclamar(page, idReporte) {
  await ir(page, `/reportes/detalle?id=${idReporte}`);
  await enviar(page, '#reclamar');
  for (const campo of await page.$$('form.formulario input[type="text"]')) {
    await campo.type('Respuesta de prueba');
  }
  await enviar(page, 'form.formulario button[type="submit"]');
}

async function aceptarPrimeraPendiente(page, idReporte) {
  await ir(page, `/devoluciones?reporte=${idReporte}`);
  await enviar(page, '#pendientes tbody a');
  await enviar(page, '#aceptar');
}

const filaDe = (page, tabla, descripcion) => page.$$eval(`${tabla} tbody tr`, (filas, d) => {
  const fila = filas.find((f) => f.innerText.includes(d));
  return fila ? fila.innerText : '';
}, descripcion);

async function buscar(page, palabra) {
  await ir(page, '/reportes');
  await page.type('#palabra', palabra);
  await enviar(page, 'form.filtros button[type="submit"]');
}

const descripcionesEnMapa = (page) => page.$$eval('#resultados li a', (enlaces) =>
  enlaces.map((a) => a.textContent.replace(/\s+/g, ' ').trim()));

(async () => {
  if (SALIDA) fs.mkdirSync(SALIDA, { recursive: true });
  const navegador = await puppeteer.launch({ executablePath: CHROME, headless: true, args: ['--lang=es-EC'] });
  const abrirSesion = async () => {
    const contexto = await navegador.createBrowserContext();
    const page = await contexto.newPage();
    await page.setViewport({ width: 1200, height: 760, deviceScaleFactor: 1.5 });
    page.on('dialog', (dialogo) => dialogo.accept());   // confirma «¿Retirar este reporte?»
    return page;
  };
  const a = await abrirSesion();   // sesión de Andrea Paz
  const b = await abrirSesion();   // sesión de Bruno Salas
  await identificar(a, ANDREA);
  await identificar(b, BRUNO);

  // ------------------------------------------------------------------ CP-08 · HU-13 (escenario 3, base vacía)
  if (!SOLO_DATOS) {
    caso('CP-08', 'HU-13 Ver estadísticas de los objetos reportados');
    await ir(a, '/estadisticas');
    await captura(a, 'cp08-3-sin-reportes.png');
    paso(3, 'Sin reportes se muestra «Todavía no hay reportes registrados.»',
      (await textoDe(a, '#sinReportes')) === 'Todavía no hay reportes registrados.');
  }

  // Datos de ejemplo: cinco reportes; Andrea reclama la mochila y la calculadora; Bruno acepta la de la calculadora
  for (const reporte of REPORTES) {
    await registrarReporte(reporte.autor === 'a' ? a : b, reporte);
  }
  const idMochila = await idDeReporte(a, MOCHILA);
  const idCalculadora = await idDeReporte(a, CALCULADORA);
  await reclamar(a, idMochila);
  await reclamar(a, idCalculadora);
  await aceptarPrimeraPendiente(b, idCalculadora);
  console.log('Datos de ejemplo registrados');
  if (SOLO_DATOS) {
    await navegador.close();
    return;
  }

  // ------------------------------------------------------------------ CP-08 · HU-13 (escenarios 1 y 2)
  await ir(a, '/estadisticas');
  const filas = (tabla) => a.$$eval(`${tabla} tbody tr`, (trs) => trs.map((f) => f.innerText.replace(/\s+/g, ' ').trim()));
  const categorias = await filas('#porCategoria');
  const zonas = await filas('#porZona');
  await captura(a, 'cp08-1-estadisticas.png');
  paso(1, 'Por categoría, «Electrónicos: 2» aparece antes que «Llaves: 1»',
    categorias[0] === 'Electrónicos 2' && categorias.indexOf('Llaves 1') > 0);
  paso(2, 'Por zona, «Centro: 3» aparece antes que «Norte: 1»',
    zonas[0] === 'Centro 3' && zonas.indexOf('Norte 1') > 0);

  // ------------------------------------------------------------------ CP-07 · HU-12
  caso('CP-07', 'HU-12 Buscar reportes por palabra clave');
  await buscar(a, 'mochila');
  let mapa = await descripcionesEnMapa(a);
  await captura(a, 'cp07-1-buscar-mochila.png');
  paso(1, 'Al buscar «mochila» el mapa muestra solo la mochila',
    mapa.length === 1 && mapa[0].includes(MOCHILA));

  await buscar(a, 'paraguas');
  const sinResultados = await textoDe(a, 'main');
  await captura(a, 'cp07-2-buscar-paraguas.png');
  paso(2, 'Al buscar «paraguas» se muestra «No hay reportes activos que cumplan los filtros.»',
    (await descripcionesEnMapa(a)).length === 0
    && sinResultados.includes('No hay reportes activos que cumplan los filtros.'));

  await buscar(a, 'CAMARA');
  mapa = await descripcionesEnMapa(a);
  await captura(a, 'cp07-3-buscar-camara.png');
  paso(3, 'Al buscar «CAMARA», en mayúsculas y sin tilde, el mapa muestra la cámara',
    mapa.length === 1 && mapa[0].includes(CAMARA));

  // ------------------------------------------------------------------ CP-06 · HU-11
  caso('CP-06', 'HU-11 Retirar un reporte propio');
  await ir(b, '/inicio');
  const filaCalculadora = await filaDe(b, '#misReportes', CALCULADORA);
  await captura(b, 'cp06-1-mis-reportes.png');
  const formRetirar = await b.$$eval('#misReportes tbody tr', (filas, d) => {
    const fila = filas.find((f) => f.innerText.includes(d));
    return fila ? fila.querySelector('form[action$="/reportes/retirar"]') !== null : null;
  }, CALCULADORA);
  paso(2, 'El reporte reservado (calculadora) no muestra la opción «Retirar»',
    filaCalculadora.includes('Reservado') && formRetirar === false);

  const botonMochila = await b.$$eval('#misReportes tbody tr', (filas, d) => {
    const i = filas.findIndex((f) => f.innerText.includes(d));
    return i;
  }, MOCHILA);
  await enviar(b, `#misReportes tbody tr:nth-child(${botonMochila + 1}) button.boton-enlace`);
  const filaMochila = await filaDe(b, '#misReportes', MOCHILA);
  await captura(b, 'cp06-2-reporte-retirado.png');
  await ir(b, '/reportes');
  mapa = await descripcionesEnMapa(b);
  await captura(b, 'cp06-3-mapa-sin-mochila.png');
  paso(1, 'Al retirar la mochila queda «Retirado» y ya no aparece en el mapa',
    filaMochila.includes('Retirado') && !mapa.some((d) => d.includes(MOCHILA))
    && mapa.some((d) => d.includes(CAMARA)));

  await ir(a, '/inicio');
  const reclamacionMochila = await filaDe(a, '#misReclamaciones', MOCHILA);
  await captura(a, 'cp06-4-reclamacion-rechazada.png');
  paso(3, 'La reclamación pendiente de Andrea queda «Rechazada» con el motivo del retiro',
    reclamacionMochila.includes('Rechazada') && reclamacionMochila.includes(MOTIVO_RETIRO));

  await navegador.close();

  const fallos = resultados.flatMap((c) => c.pasos.filter((p) => !p.cumple).map((p) => `${c.id} escenario ${p.numero}`));
  fs.writeFileSync(path.join(SALIDA, 'resultados.json'), JSON.stringify({
    fecha: new Date().toISOString().slice(0, 10), casos: resultados }, null, 1));
  console.log(fallos.length ? `\nFALLOS: ${fallos.join(', ')}` : '\nTODOS LOS ESCENARIOS SE CUMPLEN');
  process.exit(fallos.length ? 1 : 0);
})().catch((e) => { console.error(e); process.exit(1); });
