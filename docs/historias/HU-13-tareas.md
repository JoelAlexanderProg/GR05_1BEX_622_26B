# HU-13 · Tareas

| Tarea | Descripción | Sale de | Se comprueba con |
|---|---|---|---|
| T-13.1 | Crear EstadisticasReportes, que cuenta los reportes por categoría y por zona, de mayor a menor | «entity» EstadisticasReportes | TDD 6 |
| T-13.2 | Agregar ReporteDAO.listarTodos() y crear ControlEstadisticas.consultarEstadisticas() | «control» ControlEstadisticas | Prueba en la aplicación |
| T-13.3 | Crear la vista de estadísticas con las dos tablas, el mensaje sin reportes y el enlace en el menú | «boundary» VistaEstadisticas | Prueba en la aplicación |
| T-13.4 | Comprobar los tres escenarios en la aplicación | Criterio de aceptación | Escenarios 1 a 3 |

**Análisis de la historia (clases de análisis):**

- Interfaz: VistaEstadisticas (estadisticas.jsp): tablas por categoría y por zona, y enlace en el menú
- Control: ControlEstadisticas: obtiene los reportes y arma las estadísticas
- Entidad: Reporte (solo lectura) y EstadisticasReportes (cuenta por categoría y por zona)

**De dónde salen las tareas:** Es una consulta nueva y no cabe en ningún caso de uso existente, así que se abre una épica nueva. Las tareas siguen el mismo análisis que las otras historias: una clase que calcula (probada con TDD, sin base de datos), un control y una consulta que la alimentan, y una vista que la muestra. Al final se comprueban los escenarios.