# HU-12 · Tareas

| Tarea | Descripción | Sale de | Se comprueba con |
|---|---|---|---|
| T-12.1 | Agregar a la entidad Reporte la operación contienePalabra(palabra), que compara sin distinguir mayúsculas, tildes ni espacios sobrantes | «entity» Reporte | TDD 4 y TDD 5 (con parámetros) |
| T-12.2 | Agregar la palabra a FiltrosReporte y aplicarla en ControlConsultaReportes.consultarReportes junto con los demás filtros | «control» ControlConsultaReportes | Prueba en la aplicación |
| T-12.3 | Agregar el campo «Palabra clave» al panel de filtros del mapa | «boundary» MapaReportes | Prueba en la aplicación |
| T-12.4 | Comprobar los tres escenarios en la aplicación | Criterio de aceptación | Escenarios 1 a 3 |

**Análisis de la historia (clases de análisis):**

- Interfaz: MapaReportes (mapaReportes.jsp): campo «Palabra clave» en el panel de filtros
- Control: ControlConsultaReportes: aplica la palabra junto con los demás filtros
- Entidad: Reporte (decide si su descripción contiene la palabra) y FiltrosReporte (lleva la palabra)

**De dónde salen las tareas:** Las tareas salen del análisis de la historia: una por cada clase de análisis que cambia (entidad, control e interfaz) y una para comprobar los escenarios. La comparación de textos es la parte con más reglas (mayúsculas, tildes y espacios), así que se aísla en la entidad, donde se prueba con TDD sin base de datos. La interfaz y el control solo pasan la palabra.