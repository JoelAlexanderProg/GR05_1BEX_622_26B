# Historias de usuario del Incremento 4

Planificación del incremento que se desarrolla en la Prueba 1 con Programación Extrema (XP). Son tres historias nuevas que todavía no estaban desarrolladas:

| Historia | Título | Prioridad | Puntos | Épica / feature |
|---|---|---|---|---|
| HU-11 | Retirar un reporte propio | Alta | 3 | E1 Registrar objetos perdidos o encontrados / F1 Reporte de objetos |
| HU-12 | Buscar reportes por palabra clave | Media | 2 | E2 Consultar y filtrar reportes en el mapa / F2 Mapa de reportes |
| HU-13 | Ver estadísticas de los objetos reportados | Baja | 3 | E5 Analizar los objetos reportados / F6 Estadísticas de objetos |

Cada historia tiene cuatro archivos en esta carpeta:

| Archivo | Contenido |
|---|---|
| `HU-1n-tarjeta.md` | Tarjeta de la historia: título, prioridad, estimación y la historia en formato «Como… quiero… para…» |
| `HU-1n-criterios.md` | Criterio de aceptación y sus tres escenarios en formato Dado / Cuando / Entonces |
| `HU-1n-tareas.md` | Tareas de desarrollo y de dónde sale cada una |
| `HU-1n-pruebas.md` | Cómo se comprueba cada escenario: prueba unitaria (TDD) y prueba en la aplicación |

Cada historia se integra por separado: sus archivos, sus ciclos TDD y su interfaz se unen a `main` juntos y Jenkins los construye y los prueba.
