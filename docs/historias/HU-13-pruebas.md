# HU-13 · Pruebas de aceptación

Cada escenario del criterio de aceptación se comprueba con una prueba unitaria escrita con TDD y con una prueba en la aplicación.

| Escenario | Prueba unitaria (TDD) | Prueba en la aplicación |
|---|---|---|
| 1 Cantidad por categoría | TDD 6 · EstadisticasReportesTest: cuenta los reportes por categoría y por zona | Abrir «Estadísticas» con los reportes de ejemplo |
| 2 Cantidad por zona | TDD 6 · EstadisticasReportesTest: cuenta los reportes por categoría y por zona | Abrir «Estadísticas» con los reportes de ejemplo |
| 3 Sin reportes | No aplica: es un mensaje de la vista | Abrir «Estadísticas» con la base de datos vacía |
