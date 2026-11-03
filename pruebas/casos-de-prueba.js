// Ejecuta los casos de prueba del flujo básico definidos en la Tarea 1 (CP-01A, CP-01B, CP-02, CP-03, CP-04)
// y el caso CP-05 del Incremento 3 (rechazar una reclamación) sobre la aplicación en ejecución,
// con dos sesiones de navegador (U-A y U-B), y guarda capturas y resultados.
//
// Uso:      npm install puppeteer-core
//           node casos-de-prueba.js <carpeta de capturas> [URL base]
// Requiere: Node.js y Google Chrome. La aplicación debe estar en ejecución con la base de datos vacía
//           (solo con las categorías): borrar antes la carpeta ~/gr05_1bex.
const puppeteer = require('puppeteer-core');
const fs = require('fs');
const path = require('path');

const SALIDA = process.argv[2];
const BASE = process.argv[3] || 'http://localhost:8080/GR05_1BEX_622_26B';
const CHROME = 'C:/Program Files/Google/Chrome/Application/chrome.exe';

// Datos de prueba de la Tarea 1
const U_A = { nombre: 'Andrea Paz', correo: 'andrea.paz@epn.edu.ec' };      // reclamante
const U_B = { nombre: 'Bruno Salas', correo: 'bruno.salas@epn.edu.ec' };    // encontrante
const P1 = { latitud: -0.21045, longitud: -78.48920 };   // zona Centro (Z1)
const P1_B = { latitud: -0.21080, longitud: -78.48870 }; // zona Centro, cerca de P1
const P1_C = { latitud: -0.21010, longitud: -78.48980 }; // zona Centro, cerca de P1
const P2 = { latitud: -0.20800, longitud: -78.48800 };   // zona Norte (Z2)
const Q1 = '¿Qué iniciales tiene detrás?';
const Q2 = '¿Qué detalle tiene la funda?';
const F1 = 'Calculadora científica negra encontrada';
const F2 = 'Calculadora científica negra';
const F3 = 'Llavero con tres llaves';
const F4 = 'Calculadora gris con tapa';
const F5 = 'Calculadora azul sin funda';
const LUGAR = "Entrada principal del Edificio de Sistemas";
const MOTIVO = 'La marca que indica no coincide con la de la calculadora';

const resultados = [];
let casoActual = null;

function caso(id, nombre) {
  casoActual = { id, nombre, pasos: [] };
  resultados.push(casoActual);
  console.log(`\n${id} ${nombre}`);
}

function paso(numero, esperado, cumple) {
  casoActual.pasos.push({ numero, esperado, cumple: Boolean(cumple) });
  console.log(`  ${cumple ? 'ok   ' : 'FALLO'} paso ${numero}: ${esperado}`);
}

const texto = (page) => page.$eval('main', (m) => m.innerText);
const textoDe = (page, selector) => page.$eval(selector, (e) => e.innerText.trim()).catch(() => null);
const existe = async (page, selector) => (await page.$(selector)) !== null;

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

async function llenarReporte(page, datos) {
  await ir(page, '/reportes/nuevo');
  await page.click(`input[name="tipo"][value="${datos.tipo}"]`);
  const categoria = await page.$$eval('#categoria option', (opciones, nombre) =>
    opciones.find((o) => o.textContent.trim() === nombre).value, datos.categoria);
  await page.select('#categoria', categoria);
  await page.type('#descripcion', datos.descripcion);
  await page.$eval('#fecha', (campo, valor) => { campo.value = valor; }, datos.fecha);
  await marcarEnMapa(page, datos.punto);
  const campos = await page.$$('input[name="pregunta"]');
  for (let i = 0; i < (datos.preguntas || []).length; i += 1) {
    await campos[i].type(datos.preguntas[i]);
  }
}

async function registrarReporte(page, datos) {
  await llenarReporte(page, datos);
  await enviar(page, 'form.formulario button[type="submit"]');
}

async function idDeReporte(page, descripcion) {
  await ir(page, '/reportes');
  return page.$$eval('#resultados li a', (enlaces, d) => {
    const enlace = enlaces.find((a) => a.textContent.includes(d));
    return enlace ? new URL(enlace.href).searchParams.get('id') : null;
  }, descripcion);
}

const descripcionesEnMapa = (page) => page.$$eval('#resultados li a', (enlaces) =>
  enlaces.map((a) => a.textContent.replace(/\s+/g, ' ').trim().replace(/^(Perdido|Encontrado) /, '')));

(async () => {
  fs.mkdirSync(SALIDA, { recursive: true });
  const navegador = await puppeteer.launch({ executablePath: CHROME, headless: true, args: ['--lang=es-EC'] });
  const abrirSesion = async () => {
    const contexto = await navegador.createBrowserContext();
    const page = await contexto.newPage();
    await page.setViewport({ width: 1200, height: 760, deviceScaleFactor: 1.5 });
    return page;
  };
  const a = await abrirSesion();   // sesión de U-A
  const b = await abrirSesion();   // sesión de U-B

  // Identificación de los dos usuarios (precondición de los casos de prueba)
  await ir(a, '/reportes/nuevo');
  const pideIdentificacion = a.url().includes('/identificacion');
  await identificar(a, U_A);
  await captura(a, '01-identificacion.png');
  await enviar(a, 'form.formulario button[type="submit"]');
  await identificar(b, U_B);
  await enviar(b, 'form.formulario button[type="submit"]');

  // ------------------------------------------------------------------ CP-01A
  caso('CP-01A', 'Registrar un objeto perdido');
  await ir(a, '/reportes/nuevo');
  paso(1, 'Se muestra el formulario con tipo, categoría, descripción, fecha y mapa',
    pideIdentificacion && await existe(a, 'input[name="tipo"]') && await existe(a, '#categoria')
    && await existe(a, '#descripcion') && await existe(a, '#fecha') && await existe(a, '#mapa'));
  await llenarReporte(a, { tipo: 'PERDIDO', categoria: 'Calculadoras', descripcion: F2, fecha: '2026-09-28', punto: P1 });
  paso(2, 'Los datos quedan visibles; no se exigen preguntas de verificación',
    await a.$eval('#preguntas', (p) => p.hidden) && await a.$eval('#descripcion', (d) => d.value) === F2);
  paso(3, 'Se muestra el marcador en P1 y queda seleccionada su ubicación',
    (await textoDe(a, '#ubicacionElegida')).startsWith('Ubicación marcada: -0.2104'));
  await captura(a, '02-cp01a-formulario-perdido.png');
  await enviar(a, 'form.formulario button[type="submit"]');
  let pagina = await texto(a);
  paso(4, 'El sistema confirma el registro y muestra el reporte publicado', pagina.includes('Reporte publicado'));
  paso(5, 'Conserva tipo PERDIDO, datos, ubicación P1 (zona Centro) y autor U-A; estado ACTIVO; sin preguntas',
    pagina.includes('Perdido') && pagina.includes(F2) && pagina.includes('2026-09-28') && pagina.includes('Centro')
    && pagina.includes(U_A.nombre) && pagina.includes('Activo') && !pagina.includes('Preguntas de verificación'));
  await captura(a, '03-cp01a-reporte-publicado.png');

  // ------------------------------------------------------------------ CP-01B
  caso('CP-01B', 'Registrar un objeto encontrado');
  await ir(b, '/reportes/nuevo');
  paso(1, 'Se muestra el formulario de registro', await existe(b, 'form.formulario'));
  await llenarReporte(b, { tipo: 'ENCONTRADO', categoria: 'Calculadoras', descripcion: F1, fecha: '2026-09-28', punto: P1, preguntas: [Q1, Q2] });
  paso(2, 'Se conservan los valores y se habilita el ingreso de preguntas de verificación',
    !(await b.$eval('#preguntas', (p) => p.hidden)));
  paso(3, 'El marcador representa P1 y se muestran las dos preguntas introducidas',
    (await textoDe(b, '#ubicacionElegida')).startsWith('Ubicación marcada: -0.2104')
    && (await b.$$eval('input[name="pregunta"]', (c) => c.filter((x) => x.value).length)) === 2);
  await captura(b, '04-cp01b-formulario-encontrado.png');
  await enviar(b, 'form.formulario button[type="submit"]');
  pagina = await texto(b);
  paso(4, 'El sistema confirma la publicación', pagina.includes('Reporte publicado'));
  paso(5, 'El reporte contiene los datos, autor U-B y estado ACTIVO; Q1 y Q2 están vinculadas; no se piden respuestas esperadas',
    pagina.includes('Encontrado') && pagina.includes(F1) && pagina.includes(U_B.nombre) && pagina.includes('Activo')
    && pagina.includes(Q1) && pagina.includes(Q2));
  await captura(b, '05-cp01b-reporte-publicado.png');

  // ------------------------------------------------------------------ CP-02
  caso('CP-02', 'Consultar y filtrar reportes en el mapa');
  // Precondición: cargar F3, F4 y F5 (F1 y F2 se registraron en CP-01B y CP-01A)
  await registrarReporte(b, { tipo: 'ENCONTRADO', categoria: 'Llaves', descripcion: F3, fecha: '2026-09-28', punto: P1_B, preguntas: ['¿De qué color es el llavero?'] });
  await registrarReporte(b, { tipo: 'ENCONTRADO', categoria: 'Calculadoras', descripcion: F4, fecha: '2026-09-27', punto: P1_C, preguntas: ['¿Qué marca es?'] });
  await registrarReporte(b, { tipo: 'ENCONTRADO', categoria: 'Calculadoras', descripcion: F5, fecha: '2026-09-28', punto: P2, preguntas: ['¿Qué marca es?'] });

  await ir(a, '/reportes');
  let lista = await descripcionesEnMapa(a);
  const marcadores = () => a.$$eval('#mapa path.leaflet-interactive', (m) => m.length);
  await esperarMapa(a);
  paso(1, 'Se muestra el mapa con los reportes activos del conjunto de prueba (F1 a F5)',
    lista.length === 5 && [F1, F2, F3, F4, F5].every((f) => lista.includes(f)) && await marcadores() === 5);
  await captura(a, '06-cp02-mapa-inicial.png');

  await a.select('select[name="tipo"]', 'ENCONTRADO');
  const calculadoras = await a.$$eval('select[name="categoria"] option', (o) => o.find((x) => x.textContent.trim() === 'Calculadoras').value);
  await a.select('select[name="categoria"]', calculadoras);
  await a.$eval('input[name="desde"]', (c) => { c.value = '2026-09-28'; });
  await a.$eval('input[name="hasta"]', (c) => { c.value = '2026-09-28'; });
  await a.select('select[name="zona"]', 'Centro');
  paso(2, 'Los controles muestran los valores seleccionados',
    await a.$eval('select[name="tipo"]', (s) => s.value) === 'ENCONTRADO' && await a.$eval('select[name="zona"]', (s) => s.value) === 'Centro');
  await enviar(a, 'form.filtros button[type="submit"]');
  lista = await descripcionesEnMapa(a);
  await esperarMapa(a);
  paso(3, 'El mapa muestra F1 y excluye F2, F3, F4 y F5',
    lista.length === 1 && lista[0] === F1 && await marcadores() === 1
    && await a.$eval('select[name="tipo"]', (s) => s.value) === 'ENCONTRADO');
  await captura(a, '07-cp02-mapa-filtrado.png');

  const marcador = await a.$('#mapa path.leaflet-interactive');
  await Promise.all([a.waitForNavigation({ waitUntil: 'domcontentloaded' }), marcador.click()]);
  pagina = await textoDe(a, '#detalle');
  const paginaCompleta = await texto(a);
  paso(4, 'Se abre el detalle público de F1 (tipo, categoría, descripción, fecha, ubicación); sin preguntas ni respuestas',
    a.url().includes('/reportes/detalle') && pagina.includes('Encontrado') && pagina.includes('Calculadoras') && pagina.includes(F1)
    && pagina.includes('2026-09-28') && pagina.includes('Zona Centro')
    && !paginaCompleta.includes(Q1) && !paginaCompleta.includes(U_B.nombre));
  await captura(a, '08-cp02-detalle-publico.png');

  // ------------------------------------------------------------------ CP-03
  caso('CP-03', 'Reclamar un objeto encontrado');
  const idF1 = new URL(a.url()).searchParams.get('id');
  await enviar(a, '#reclamar');
  pagina = await texto(a);
  paso(1, 'Se presenta el formulario de reclamación con Q1 y Q2', pagina.includes(Q1) && pagina.includes(Q2));
  const respuestas = await a.$$('form.formulario input[type="text"]');
  await respuestas[0].type('LJ');
  await respuestas[1].type('Una etiqueta azul dentro de la funda');
  paso(2, 'Cada respuesta queda asociada a su pregunta en el formulario', respuestas.length === 2);
  await captura(a, '09-cp03-formulario-reclamacion.png');
  await enviar(a, 'form.formulario button[type="submit"]');
  paso(3, 'El sistema confirma su registro', (await texto(a)).includes('Reclamación registrada'));
  await captura(a, '10-cp03-reclamacion-registrada.png');

  await ir(a, '/inicio');
  const miReclamacion = await textoDe(a, '#misReclamaciones tbody');
  await ir(a, `/reportes/detalle?id=${idF1}`);
  paso(4, 'Existe una reclamación PENDIENTE de U-A sobre F1; F1 sigue ACTIVO y no hay entrega registrada',
    miReclamacion.includes(F1) && miReclamacion.includes('Pendiente') && (await textoDe(a, '#detalle')).includes('Activo'));

  await ir(b, `/devoluciones?reporte=${idF1}`);
  const pendientes = await textoDe(b, '#pendientes tbody');
  await captura(b, '11-cp04-reclamaciones-pendientes.png');
  await enviar(b, '#pendientes tbody a');
  const revision = await textoDe(b, '#respuestas tbody');
  paso(5, 'U-B puede revisar la reclamación; las respuestas corresponden a Q1 y Q2; su estado no cambia',
    pendientes.includes(U_A.nombre) && revision.includes(Q1) && revision.includes('LJ') && revision.includes(Q2)
    && revision.includes('Una etiqueta azul dentro de la funda') && (await textoDe(b, '#estadoReclamacion')) === 'Pendiente');

  // ------------------------------------------------------------------ CP-04
  caso('CP-04', 'Aceptar una reclamación y completar la devolución');
  paso(1, 'Se muestra la reclamación pendiente de U-A', pendientes.includes(U_A.nombre) && pendientes.includes('Pendiente'));
  paso(2, 'Se muestran sus preguntas y respuestas exactas para revisión', revision.includes('LJ') && revision.includes(Q2));
  await captura(b, '12-cp04-revision-respuestas.png');

  await enviar(b, '#aceptar');
  paso(3, 'La reclamación cambia a ACEPTADA y F1 a RESERVADO; aún no tiene fecha de devolución',
    (await textoDe(b, '#estadoReclamacion')) === 'Aceptada' && (await textoDe(b, '#estadoReporte')) === 'Reservado'
    && !(await existe(b, '#fechaDevolucion')));
  const propuesta = new Date(Date.now() + 24 * 60 * 60 * 1000);
  const fechaPropuesta = `${propuesta.getFullYear()}-${String(propuesta.getMonth() + 1).padStart(2, '0')}-${String(propuesta.getDate()).padStart(2, '0')}T15:00`;
  await b.type('#lugar', LUGAR);
  await b.$eval('#fecha', (c, v) => { c.value = v; }, fechaPropuesta);
  await captura(b, '13-cp04-proponer-entrega.png');
  await enviar(b, '#proponer');
  let acuerdo = await textoDe(b, '#acuerdo');
  paso(4, 'Se conserva un AcuerdoEntrega con el lugar y la fecha indicados; todavía sin fecha de confirmación',
    acuerdo.includes(LUGAR) && acuerdo.includes(fechaPropuesta.replace('T', ' ')) && (await textoDe(b, '#confirmacionAcuerdo')) === 'Pendiente'
    && !(await existe(b, '#confirmarDevolucion')));
  await captura(b, '14-cp04-acuerdo-propuesto.png');

  await ir(a, '/inicio');
  await enviar(a, '#misReclamaciones tbody a');
  let vista = await textoDe(a, '#propuesta');
  paso(5, 'U-A, en otra sesión, puede consultar el lugar y la fecha de su reclamación',
    a.url().includes('/devoluciones?acuerdo=') && vista.includes(LUGAR) && vista.includes(fechaPropuesta.replace('T', ' ')));
  await captura(a, '15-cp04-vista-acuerdo.png');

  await enviar(a, '#confirmarAcuerdo');
  await ir(a, `/reportes/detalle?id=${idF1}`);
  const estadoTrasAcuerdo = await textoDe(a, '#detalle');
  await ir(a, '/inicio');
  await enviar(a, '#misReclamaciones tbody a');
  paso(6, 'Se registra la confirmación del reclamante y su fecha; el reporte sigue RESERVADO y la reclamación ACEPTADA',
    (await textoDe(a, '#confirmacionAcuerdo')) !== 'Pendiente' && (await textoDe(a, '#estadoReclamacion')) === 'Aceptada'
    && estadoTrasAcuerdo.includes('Reservado'));
  await captura(a, '16-cp04-acuerdo-confirmado.png');

  await ir(b, `/devoluciones?reporte=${idF1}`);
  await enviar(b, '#enCurso');
  paso(7, 'La entrega física es externa: los estados de la aplicación no cambian hasta que U-B la confirma',
    (await textoDe(b, '#estadoReporte')) === 'Reservado' && (await textoDe(b, '#estadoReclamacion')) === 'Aceptada'
    && await existe(b, '#confirmarDevolucion'));
  await captura(b, '17-cp04-confirmar-devolucion.png');

  await enviar(b, '#confirmarDevolucion');
  paso(8, 'F1 cambia a ENTREGADO, la reclamación a COMPLETADA y se registra la fecha de devolución',
    (await textoDe(b, '#estadoReporte')) === 'Entregado' && (await textoDe(b, '#estadoReclamacion')) === 'Completada'
    && await existe(b, '#fechaDevolucion'));
  await captura(b, '18-cp04-devolucion-registrada.png');

  acuerdo = await textoDe(b, '#acuerdo');
  const respuestasFinales = await textoDe(b, '#respuestas tbody');
  await ir(a, '/inicio');
  const reclamacionFinal = await textoDe(a, '#misReclamaciones tbody');
  await captura(a, '19-cp04-mis-reclamaciones.png');
  await ir(a, '/reportes');
  lista = await descripcionesEnMapa(a);
  await ir(a, `/reportes/detalle?id=${idF1}`);
  paso(9, 'Se conservan el acuerdo y las respuestas; estados y fecha esperados; el objeto ya no admite reclamaciones',
    acuerdo.includes(LUGAR) && respuestasFinales.includes('LJ') && reclamacionFinal.includes('Completada')
    && !lista.includes(F1) && lista.length === 4 && !(await existe(a, '#reclamar')));
  await captura(a, '20-cp04-detalle-entregado.png');

  // ------------------------------------------------------------------ CP-05 (Incremento 3)
  caso('CP-05', 'Rechazar una reclamación');
  const idF4 = await idDeReporte(a, F4);
  await ir(a, `/reportes/detalle?id=${idF4}`);
  await enviar(a, '#reclamar');
  await (await a.$('form.formulario input[type="text"]')).type('Casio');
  await enviar(a, 'form.formulario button[type="submit"]');
  paso(1, 'U-A registra una reclamación sobre F4, que queda PENDIENTE', (await texto(a)).includes('Reclamación registrada'));

  await ir(b, `/devoluciones?reporte=${idF4}`);
  await enviar(b, '#pendientes tbody a');
  paso(2, 'U-B revisa la reclamación y el panel ofrece rechazarla con un motivo',
    (await textoDe(b, '#estadoReclamacion')) === 'Pendiente' && await existe(b, '#motivo') && await existe(b, '#rechazar'));
  await b.type('#motivo', MOTIVO);
  await captura(b, '21-cp05-rechazar-reclamacion.png');
  await enviar(b, '#rechazar');
  paso(3, 'La reclamación queda RECHAZADA y se muestra el motivo',
    (await textoDe(b, '#estadoReclamacion')) === 'Rechazada' && (await textoDe(b, '#motivoRechazo')) === MOTIVO);
  await captura(b, '22-cp05-reclamacion-rechazada.png');

  await ir(b, `/devoluciones?reporte=${idF4}`);
  const panelF4 = await texto(b);
  await ir(b, `/reportes/detalle?id=${idF4}`);
  paso(4, 'F4 no tiene reclamaciones pendientes ni en curso y sigue ACTIVO para otras reclamaciones',
    panelF4.includes('No hay reclamaciones pendientes') && !panelF4.includes('continuar con la devolución')
    && (await textoDe(b, '#detalle')).includes('Activo'));

  await ir(a, '/inicio');
  const misReclamaciones = await textoDe(a, '#misReclamaciones tbody');
  paso(5, 'U-A ve en «Mis reclamaciones» que su reclamación fue rechazada y el motivo',
    misReclamaciones.includes(F4) && misReclamaciones.includes('Rechazada') && misReclamaciones.includes(MOTIVO));
  await captura(a, '23-cp05-mis-reclamaciones.png');

  await navegador.close();

  const fallos = resultados.flatMap((c) => c.pasos.filter((p) => !p.cumple).map((p) => `${c.id} paso ${p.numero}`));
  fs.writeFileSync(path.join(SALIDA, 'resultados.json'), JSON.stringify({
    fecha: new Date().toISOString().slice(0, 10),
    datos: { U_A, U_B, P1, P2, Q1, Q2, lugar: LUGAR, fechaPropuesta },
    casos: resultados,
  }, null, 1));
  console.log(fallos.length ? `\nFALLOS: ${fallos.join(', ')}` : '\nTODOS LOS CASOS DE PRUEBA PASARON');
  process.exit(fallos.length ? 1 : 0);
})().catch((e) => { console.error(e); process.exit(1); });
