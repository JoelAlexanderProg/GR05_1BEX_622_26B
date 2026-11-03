# HU-11 · Pruebas de aceptación

Cada escenario del criterio de aceptación se comprueba con una prueba unitaria escrita con TDD y con una prueba en la aplicación.

| Escenario | Prueba unitaria (TDD) | Prueba en la aplicación |
|---|---|---|
| 1 Reporte activo | TDD 1 · ReporteTest: retirar un reporte activo lo deja RETIRADO | Retirar la mochila desde «Mis reportes» y comprobar que ya no está en el mapa |
| 2 Reporte reservado | TDD 2 · ReporteTest: un reporte reservado no se puede retirar | Comprobar que la calculadora reservada no muestra «Retirar» |
| 3 Reclamaciones pendientes | TDD 3 · ControlRegistroReporteTest (mocks): retirar rechaza las reclamaciones pendientes y guarda el reporte | Entrar como Andrea y ver su reclamación «Rechazada» con el motivo |
