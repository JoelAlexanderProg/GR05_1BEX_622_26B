# Release 2 de EncuentraEPN: plan y trazabilidad

Plan del Examen 1B (Metodologías Ágiles, EPN 2026-B, Grupo 5). Las horas son estimaciones de planificación en horas ideales; no son mediciones.

**Objetivo del release:** Que más objetos vuelvan a su dueño y en menos tiempo: el sistema une a quien perdió con quien encontró, avisa a cada uno lo que pasa y evita que una entrega quede trabada.

## Capacidad y velocidad del equipo

- Iteración de 1 semana: 5 días y **40 horas reales** para todo el equipo (5 integrantes).
- Reunión de planificación de la iteración: 2 h
- Reunión diaria de pie (15 minutos durante 5 días): 1,25 h
- Revisión de la entrega con el cliente: 1,75 h
- Interrupciones y retrasos (margen): 5 h
- **Capacidad: 40 − 10 = 30 horas ideales por iteración** (1 hora ideal ≈ 1,33 horas reales).
- **Velocidad:** 30 horas ideales por iteración, la que el equipo completó en cada iteración del Release 1 (Yesterday's Weather).
- Cada tarea dura entre 1 y 3 horas ideales.

## Épicas y features

| Épica | Objetivo | Feature | Historias |
|---|---|---|---|
| E5 Conectar pérdidas con hallazgos | Que el dueño de un objeto perdido lo encuentre sin revisar el mapa a mano. | F6 Coincidencias de objetos | HU-14 |
| E5 Conectar pérdidas con hallazgos | Que el dueño de un objeto perdido lo encuentre sin revisar el mapa a mano. | F7 Respuesta a reportes de pérdida | HU-15 |
| E6 Mantener informados a los participantes | Que nadie tenga que entrar a revisar si algo cambió: el sistema avisa. | F8 Avisos automáticos | HU-16, HU-18 |
| E6 Mantener informados a los participantes | Que nadie tenga que entrar a revisar si algo cambió: el sistema avisa. | F9 Bandeja de avisos | HU-17 |
| E7 Asegurar la entrega del objeto | Que toda entrega acordada termine en el dueño o libere el objeto. | F10 Cambios en la entrega | HU-19, HU-20 |
| E7 Asegurar la entrega del objeto | Que toda entrega acordada termine en el dueño o libere el objeto. | F11 Puntos de entrega oficiales | HU-21 |

## Iteración 1

**Objetivo:** Que quien perdió un objeto se entere de que alguien lo encontró, sin revisar el mapa a mano.

| N.º | Historia | Prioridad | Estimación (h ideales) |
|---|---|---|---|
| HU-14 | Ver los objetos encontrados que coinciden con mi objeto perdido | Alta | 9 |
| HU-15 | Avisar al dueño de un objeto perdido que yo lo encontré | Alta | 8 |
| HU-16 | Recibir un aviso cuando se reporta un objeto que coincide con mi pérdida | Alta | 8 |
| HU-17 | Ver cuántos avisos tengo sin leer | Media | 5 |
| | **Estimado** | | **30 h ideales** |
| | **Real** | | **40 h reales** |

### HU-14 Ver los objetos encontrados que coinciden con mi objeto perdido

E5 Conectar pérdidas con hallazgos → F6 Coincidencias de objetos → HU-14 · prioridad Alta · 9 h ideales

> Como miembro de la comunidad EPN que perdió un objeto, quiero ver los objetos encontrados que coinciden con mi reporte para reclamar el mío sin revisar todo el mapa.

**Criterios de aceptación**

1. *Un objeto encontrado coincide.* **Dado** que Andrea reportó perdida una calculadora y Bruno reportó encontrada una calculadora a 50 m, dos días después, **cuando** Andrea abre las coincidencias de su reporte, **entonces** ve la calculadora de Bruno con su distancia y un enlace para reclamarla.
2. *Un objeto que no coincide no aparece.* **Dado** que hay objetos encontrados de otra categoría, a más de 200 m o más de 30 días después de la pérdida, **cuando** Andrea abre las coincidencias de su reporte, **entonces** ninguno de esos objetos aparece en la lista.
3. *Las coincidencias van de la más cercana a la más lejana.* **Dado** que dos calculadoras encontradas coinciden con el reporte de Andrea, una a 150 m y otra a 40 m, **cuando** Andrea abre las coincidencias de su reporte, **entonces** la de 40 m aparece primero.

| Tarea | Estimación | Código | Commit |
|---|---|---|---|
| T1. Calcular la distancia entre dos ubicaciones (fórmula de Haversine), con prueba unitaria | 2 h | `Ubicacion.distanciaA` | [c555714](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/c555714) |
| T2. Regla de coincidencia: misma categoría, 200 m o menos y hasta 30 días, con prueba parametrizada | 2 h | `Reporte.coincideCon` | [fa78008](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/fa78008) |
| T3. Buscar las coincidencias de un reporte perdido, ordenadas por distancia, con prueba con mocks | 2 h | `ControlCoincidencias.buscarCoincidencias, ReporteDAO.listarEncontradosActivos` | [dea26d2](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/dea26d2) |
| T4. Página de coincidencias con lista y mapa, enlazada desde «Mis reportes» | 2 h | `coincidencias.jsp, inicio.jsp` | [2ada504](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/2ada504) |
| T5. Automatizar las pruebas de aceptación de los 3 escenarios en el navegador | 1 h | `pruebas/casos-release-2.js` | [210791b](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/210791b) |

### HU-15 Avisar al dueño de un objeto perdido que yo lo encontré

E5 Conectar pérdidas con hallazgos → F7 Respuesta a reportes de pérdida → HU-15 · prioridad Alta · 8 h ideales

> Como miembro de la comunidad EPN que encontró un objeto, quiero avisar al dueño de un reporte de objeto perdido que yo lo tengo para que pueda reclamarlo.

**Criterios de aceptación**

1. *El dueño ve el hallazgo.* **Dado** que Andrea reportó perdidas unas llaves, **cuando** Bruno abre ese reporte, pulsa «Yo lo encontré» y publica su reporte de objeto encontrado, **entonces** Andrea ve el hallazgo de Bruno en «Mis reportes», con un enlace para reclamarlo.
2. *No se puede avisar sobre un reporte propio.* **Dado** que Andrea reportó perdidas unas llaves, **cuando** Andrea abre el detalle de su propio reporte, **entonces** no aparece el botón «Yo lo encontré».
3. *No se puede avisar sobre un reporte que ya no está activo.* **Dado** que Andrea retiró su reporte de llaves perdidas, **cuando** Bruno intenta avisar que las encontró, **entonces** el sistema responde «Este reporte de pérdida ya no está activo.» y no registra nada.

| Tarea | Estimación | Código | Commit |
|---|---|---|---|
| T1. Vincular un reporte encontrado con el reporte perdido, con pruebas unitarias de las reglas | 2 h | `Reporte.vincularConPerdido` | [9445266](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/9445266) |
| T2. Registrar el hallazgo vinculado desde el control, con prueba con mocks | 2 h | `ControlRegistroReporte.registrarReporte(usuario, datos, perdidoId)` | [033ed4f](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/033ed4f) |
| T3. Botón «Yo lo encontré» en el detalle y formulario precargado | 1,5 h | `detalleReporte.jsp, formularioReporte.jsp` | [37d25bb](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/37d25bb) |
| T4. Mostrar los hallazgos de mis objetos perdidos en «Mis reportes» | 1,5 h | `ReporteDAO.listarHallazgosPara, inicio.jsp` | [4cf94d9](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/4cf94d9) |
| T5. Automatizar las pruebas de aceptación de los 3 escenarios en el navegador | 1 h | `pruebas/casos-release-2.js` | [4410e96](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/4410e96) |

### HU-16 Recibir un aviso cuando se reporta un objeto que coincide con mi pérdida

E6 Mantener informados a los participantes → F8 Avisos automáticos → HU-16 · prioridad Alta · 8 h ideales

> Como miembro de la comunidad EPN que perdió un objeto, quiero recibir un aviso cuando alguien reporta un objeto encontrado que coincide con el mío para enterarme sin revisar el mapa cada día.

**Criterios de aceptación**

1. *Llega un aviso de coincidencia.* **Dado** que Andrea reportó perdida una calculadora, **cuando** Bruno reporta encontrada una calculadora a 50 m, dos días después, **entonces** Andrea tiene en «Mis avisos» un aviso de coincidencia con un enlace al reporte de Bruno.
2. *Un objeto que no coincide no genera aviso.* **Dado** que Andrea reportó perdida una calculadora, **cuando** Bruno reporta encontradas unas llaves en el mismo lugar, **entonces** Andrea no recibe ningún aviso.
3. *Los avisos van del más reciente al más antiguo.* **Dado** que Andrea recibió dos avisos de coincidencia, uno después del otro, **cuando** abre «Mis avisos», **entonces** ve primero el más reciente, cada uno con su fecha y su enlace.

| Tarea | Estimación | Código | Commit |
|---|---|---|---|
| T1. Entidad Aviso y su acceso a datos, con prueba unitaria del texto del aviso | 2 h | `Aviso, AvisoDAO` | [2788aaa](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/2788aaa) |
| T2. Consultar los reportes perdidos activos de una categoría | 1 h | `ReporteDAO.listarPerdidosActivos` | [9f6088d](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/9f6088d) |
| T3. Generar los avisos de coincidencia al publicar un objeto encontrado, con prueba con mocks | 2 h | `ControlRegistroReporte.avisarCoincidencias` | [2389d6f](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/2389d6f) |
| T4. Página «Mis avisos», del más reciente al más antiguo | 2 h | `ControlAvisos, avisos.jsp` | [99ed060](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/99ed060) |
| T5. Automatizar las pruebas de aceptación de los 3 escenarios en el navegador | 1 h | `pruebas/casos-release-2.js` | [58a3c18](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/58a3c18) |

### HU-17 Ver cuántos avisos tengo sin leer

E6 Mantener informados a los participantes → F9 Bandeja de avisos → HU-17 · prioridad Media · 5 h ideales

> Como usuario identificado, quiero ver en la barra superior cuántos avisos tengo sin leer para saber de un vistazo si pasó algo nuevo.

**Criterios de aceptación**

1. *El contador muestra los avisos nuevos.* **Dado** que Andrea tiene 2 avisos sin leer, **cuando** abre cualquier página, **entonces** la barra superior muestra «Avisos (2)».
2. *Abrir la bandeja marca los avisos como leídos.* **Dado** que Andrea tiene 2 avisos sin leer, **cuando** abre «Mis avisos» y luego otra página, **entonces** los 2 avisos aparecen resaltados como nuevos y después la barra muestra «Avisos» sin número.
3. *Sin avisos nuevos no hay contador.* **Dado** que Bruno no tiene avisos sin leer, **cuando** abre cualquier página, **entonces** la barra muestra «Avisos» sin número.

| Tarea | Estimación | Código | Commit |
|---|---|---|---|
| T1. Marcar como leídos los avisos al abrir la bandeja, con pruebas unitaria y con mocks | 1,5 h | `Aviso.marcarLeido, ControlAvisos.marcarComoLeidos, AvisoDAO.marcarLeidos` | [2d3b5a9](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/2d3b5a9) |
| T2. Contar los avisos sin leer antes de mostrar cada página | 1,5 h | `AvisoDAO.contarSinLeer, ControlBase.mostrar` | [3697945](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/3697945) |
| T3. Contador en la barra y avisos nuevos resaltados en la bandeja | 1 h | `cabecera.jspf, avisos.jsp` | [56e3e3d](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/56e3e3d) |
| T4. Automatizar las pruebas de aceptación de los 3 escenarios en el navegador | 1 h | `pruebas/casos-release-2.js` | [84c472f](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/84c472f) |

## Iteración 2

**Objetivo:** Que toda entrega acordada termine: o el objeto llega a su dueño, o vuelve a estar disponible.

| N.º | Historia | Prioridad | Estimación (h ideales) |
|---|---|---|---|
| HU-18 | Recibir avisos cuando cambia el estado de mi reclamación | Alta | 6 |
| HU-19 | Cancelar una entrega que no se realizó | Alta | 8 |
| HU-20 | Pedir otra fecha para la entrega | Alta | 8 |
| HU-21 | Elegir un punto de entrega oficial del campus | Media | 8 |
| | **Estimado** | | **30 h ideales** |
| | **Real** | | **40 h reales** |

### HU-18 Recibir avisos cuando cambia el estado de mi reclamación

E6 Mantener informados a los participantes → F8 Avisos automáticos → HU-18 · prioridad Alta · 6 h ideales

> Como reclamante, quiero recibir un aviso cuando aceptan o rechazan mi reclamación o me proponen la entrega para responder a tiempo.

**Criterios de aceptación**

1. *Aviso de reclamación aceptada.* **Dado** que Andrea reclamó la calculadora de Bruno, **cuando** Bruno acepta la reclamación, **entonces** Andrea recibe el aviso «Su reclamación de … fue aceptada».
2. *Aviso de reclamación rechazada con el motivo.* **Dado** que Carlos reclamó la misma calculadora, **cuando** Bruno la rechaza con un motivo, **entonces** Carlos recibe un aviso que incluye el motivo.
3. *Aviso de entrega propuesta con enlace al acuerdo.* **Dado** que Bruno aceptó la reclamación de Andrea, **cuando** Bruno propone el lugar y la fecha de la entrega, **entonces** Andrea recibe un aviso con el lugar y la fecha, que la lleva al acuerdo para confirmarlo.

| Tarea | Estimación | Código | Commit |
|---|---|---|---|
| T1. Textos de los avisos de la reclamación, con prueba parametrizada | 2 h | `Aviso.reclamacionAceptada, Aviso.reclamacionRechazada, Aviso.entregaPropuesta` | [59f9f30](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/59f9f30) |
| T2. Crear el aviso al aceptar, rechazar y proponer la entrega, con prueba con mocks | 2 h | `ControlDevoluciones.avisar` | [74f9feb](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/74f9feb) |
| T3. Enlace de cada aviso a la reclamación o al acuerdo | 1 h | `Aviso.enlace, avisos.jsp` | [03ee643](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/03ee643) |
| T4. Automatizar las pruebas de aceptación de los 3 escenarios en el navegador | 1 h | `pruebas/casos-release-2.js` | [1558d3b](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/1558d3b) |

### HU-19 Cancelar una entrega que no se realizó

E7 Asegurar la entrega del objeto → F10 Cambios en la entrega → HU-19 · prioridad Alta · 8 h ideales

> Como miembro de la comunidad EPN que encontró un objeto, quiero cancelar una entrega acordada que no se realizó para que el objeto vuelva a estar disponible para otras reclamaciones.

**Criterios de aceptación**

1. *El objeto vuelve a estar disponible.* **Dado** que Bruno aceptó la reclamación de Andrea y acordaron una entrega, **cuando** Bruno cancela la entrega con el motivo «No asistió a la entrega», **entonces** la reclamación queda Cancelada y el objeto vuelve a estar Activo en el mapa.
2. *El reclamante ve el motivo.* **Dado** que Bruno canceló la entrega de Andrea, **cuando** Andrea abre «Mis reclamaciones», **entonces** ve su reclamación Cancelada con el motivo.
3. *Una entrega ya realizada no se cancela.* **Dado** que Bruno ya confirmó la devolución del objeto, **cuando** intenta cancelar la entrega, **entonces** el sistema responde «Solo se puede cancelar una entrega que todavía no se realizó.».

| Tarea | Estimación | Código | Commit |
|---|---|---|---|
| T1. Estado CANCELADA y cancelar una reclamación aceptada con motivo, con pruebas unitarias | 2 h | `EstadoReclamacion.CANCELADA, Reclamacion.cancelar` | [45c5e86](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/45c5e86) |
| T2. Liberar un reporte reservado (RESERVADO → ACTIVO), con prueba unitaria | 1 h | `Reporte.liberar` | [df6c220](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/df6c220) |
| T3. Cancelar la entrega desde el control, con prueba con mocks | 2 h | `ControlDevoluciones.cancelarEntrega` | [9954037](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/9954037) |
| T4. Formulario de cancelación en el panel y motivo en «Mis reclamaciones» | 2 h | `panelDevolucion.jsp, inicio.jsp` | [71f3beb](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/71f3beb) |
| T5. Automatizar las pruebas de aceptación de los 3 escenarios en el navegador | 1 h | `pruebas/casos-release-2.js` | [e30f0b6](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/e30f0b6) |

### HU-20 Pedir otra fecha para la entrega

E7 Asegurar la entrega del objeto → F10 Cambios en la entrega → HU-20 · prioridad Alta · 8 h ideales

> Como reclamante, quiero pedir otra fecha para la entrega propuesta cuando no puedo asistir para no perder mi objeto por un cruce de horarios.

**Criterios de aceptación**

1. *El reclamante pide otra fecha.* **Dado** que Bruno propuso entregar la calculadora mañana a las 10:00, **cuando** Andrea pide pasado mañana a las 15:00, **entonces** el acuerdo muestra a los dos la nueva fecha solicitada.
2. *El encontrante acepta la nueva fecha.* **Dado** que Andrea pidió otra fecha, **cuando** Bruno acepta la nueva fecha, **entonces** el acuerdo queda con esa fecha y confirmado, y Bruno puede registrar la devolución.
3. *La nueva fecha debe ser futura.* **Dado** que Bruno propuso una entrega, **cuando** Andrea pide una fecha que ya pasó, **entonces** el sistema responde «Indique una fecha futura para la entrega.».

| Tarea | Estimación | Código | Commit |
|---|---|---|---|
| T1. Solicitar y aceptar otra fecha en el acuerdo, con pruebas unitarias | 2 h | `AcuerdoEntrega.solicitarOtraFecha, AcuerdoEntrega.aceptarOtraFecha` | [13337b9](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/13337b9) |
| T2. Operaciones del reclamante y del encontrante en el control, con prueba con mocks | 2 h | `ControlDevoluciones.solicitarOtraFecha, ControlDevoluciones.aceptarOtraFecha` | [60e35b2](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/60e35b2) |
| T3. Formulario para pedir otra fecha en el acuerdo de entrega | 1,5 h | `vistaAcuerdoEntrega.jsp` | [0a990e3](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/0a990e3) |
| T4. Fecha solicitada y botón para aceptarla en el panel del encontrante | 1,5 h | `panelDevolucion.jsp` | [5bbb8e0](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/5bbb8e0) |
| T5. Automatizar las pruebas de aceptación de los 3 escenarios en el navegador | 1 h | `pruebas/casos-release-2.js` | [92793db](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/92793db) |

### HU-21 Elegir un punto de entrega oficial del campus

E7 Asegurar la entrega del objeto → F11 Puntos de entrega oficiales → HU-21 · prioridad Media · 8 h ideales

> Como miembro de la comunidad EPN que encontró un objeto, quiero elegir un punto de entrega oficial del campus con su horario de atención para que la entrega sea en un lugar seguro y conocido.

**Criterios de aceptación**

1. *La entrega se propone en un punto oficial.* **Dado** que Bruno aceptó la reclamación de Andrea, **cuando** elige «Biblioteca Central» y una fecha de lunes a viernes dentro de su horario, **entonces** el acuerdo queda en ese punto.
2. *La fecha debe estar dentro del horario del punto.* **Dado** que Bruno aceptó la reclamación de Andrea, **cuando** elige «Biblioteca Central» y una fecha a las 21:00, **entonces** el sistema responde con el horario de atención del punto y no guarda la propuesta.
3. *El reclamante ve el punto y su horario.* **Dado** que Bruno propuso la entrega en «Biblioteca Central», **cuando** Andrea abre el acuerdo de entrega, **entonces** ve el nombre del punto, su referencia y su horario de atención.

| Tarea | Estimación | Código | Commit |
|---|---|---|---|
| T1. Entidad PuntoEntrega con su horario y la regla «atiende a esta hora», con prueba parametrizada | 2 h | `PuntoEntrega.atiendeA` | [00afe1a](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/00afe1a) |
| T2. Carga inicial de los puntos oficiales y su acceso a datos | 1 h | `PuntoEntregaDAO, InicializadorDatos` | [4c32528](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/4c32528) |
| T3. Proponer la entrega en un punto oficial desde el control, con prueba con mocks | 2 h | `ControlDevoluciones.proponerEntrega(actor, id, puntoId, lugar, fecha)` | [9396ea2](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/9396ea2) |
| T4. Selector de puntos en el panel y punto con horario en el acuerdo | 2 h | `panelDevolucion.jsp, vistaAcuerdoEntrega.jsp` | [d032bba](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/d032bba) |
| T5. Automatizar las pruebas de aceptación de los 3 escenarios en el navegador | 1 h | `pruebas/casos-release-2.js` | [db09adf](https://github.com/JoelAlexanderProg/GR05_1BEX_622_26B/commit/db09adf) |

## Fuera de este release

| Historia candidata | Estimación | Por qué se aplazó |
|---|---|---|
| Editar un reporte propio | 6 h | Hoy se puede retirar y volver a registrar |
| Caducar los reportes con más de 60 días | 8 h | Con pocos reportes el mapa todavía no se satura |
| Ver la tasa de devolución en las estadísticas | 4 h | Sirve para medir el release, pero no devuelve objetos |
| Adjuntar una foto del objeto | 10 h | Una foto pública puede revelar las respuestas de verificación |
| Enviar mensajes entre encontrante y reclamante | 12 h | La entrega ya se coordina con el acuerdo y la HU-20 |
