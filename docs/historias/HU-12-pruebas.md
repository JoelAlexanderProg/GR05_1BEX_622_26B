# HU-12 · Pruebas de aceptación

Cada escenario del criterio de aceptación se comprueba con una prueba unitaria escrita con TDD y con una prueba en la aplicación.

| Escenario | Prueba unitaria (TDD) | Prueba en la aplicación |
|---|---|---|
| 1 Palabra con coincidencias | TDD 4 · ReporteTest: una palabra que está en la descripción coincide; otra que no está, no | Buscar «mochila» en el mapa |
| 2 Sin coincidencias | TDD 5 · ReporteTest con parámetros: la fila «paraguas» no coincide | Buscar «paraguas» y ver el mensaje |
| 3 Mayúsculas y tildes | TDD 5 · ReporteTest con parámetros: «CAMARA», «cámara» y « Cámara » coinciden | Buscar «CAMARA» en el mapa |
