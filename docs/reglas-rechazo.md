# Reglas del rechazo de una reclamación (Incremento 3)

Quien registró un objeto encontrado puede rechazar una reclamación que no lo convence. Estas son las reglas y la prueba unitaria que comprueba cada una:

| Regla | Prueba |
|---|---|
| Al rechazar una reclamación pendiente, queda en estado RECHAZADA. | ReclamacionTest: prueba 1 |
| Se guardan el motivo, sin espacios sobrantes, y la fecha del rechazo. | ReclamacionTest: prueba 2 |
| Una reclamación ya aceptada no se puede rechazar. | ReclamacionTest: prueba 3 |
| El motivo admite hasta 200 caracteres. | ReclamacionTest: prueba 4 |
| El control guarda el rechazo con el acceso a datos. | ControlDevolucionesTest (prueba con mocks) |
| El motivo no puede estar vacío ni en blanco. | ReclamacionTest (prueba con parámetros) |

El reporte sigue activo después del rechazo, así que el objeto puede recibir otras reclamaciones. El reclamante ve el motivo en «Mis reclamaciones».