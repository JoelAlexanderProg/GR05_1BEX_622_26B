# HU-11 · Tareas

| Tarea | Descripción | Sale de | Se comprueba con |
|---|---|---|---|
| T-11.1 | Agregar a la entidad Reporte el estado RETIRADO y la operación retirar(), que solo acepta reportes activos | «entity» Reporte | TDD 1 y TDD 2 |
| T-11.2 | Implementar ControlRegistroReporte.retirarReporte: comprueba que el actor sea el autor, retira el reporte y rechaza sus reclamaciones pendientes en una sola transacción | «control» ControlRegistroReporte | TDD 3 (con mocks) |
| T-11.3 | Agregar el botón «Retirar» a los reportes activos de «Mis reportes», con confirmación y mensaje de resultado | «boundary» InicioUsuario | Prueba en la aplicación |
| T-11.4 | Comprobar los tres escenarios en la aplicación | Criterio de aceptación | Escenarios 1 a 3 |

**Análisis de la historia (clases de análisis):**

- Interfaz: InicioUsuario (inicio.jsp): lista «Mis reportes» con el botón «Retirar»
- Control: ControlRegistroReporte: comprueba que el actor sea el autor y coordina el retiro
- Entidad: Reporte (nuevo estado RETIRADO) y Reclamacion (se rechazan las pendientes)

**De dónde salen las tareas:** Las tareas salen del análisis de la historia. Cada clase de análisis que hay que crear o cambiar (entidad, control e interfaz) da una tarea, y se construyen en ese orden con TDD, de adentro hacia afuera. Al final hay una tarea para comprobar los escenarios. La regla «solo reportes activos» depende del estado del reporte, así que va en la entidad. «Solo el autor» y «rechazar las reclamaciones pendientes» coordinan varias entidades, así que van en el control.