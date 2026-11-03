# GR05_1BEX_622_26B: EncuentraEPN

Sistema web georreferenciado de objetos perdidos y encontrados en el campus de la Escuela Politécnica Nacional.

Examen del primer bimestre de Metodologías Ágiles (ISWD622, EPN, 2026-B), Grupo 5: plan de un release con dos iteraciones de una semana y su implementación. El punto de partida es la aplicación de la Prueba 1 ([GR05_1BPR1_622_26B](https://github.com/JoelAlexanderProg/GR05_1BPR1_622_26B), Incremento 4).

## Casos de uso implementados

| Incremento | Caso de uso | Control (Servlet) | Vistas (JSP) |
|---|---|---|---|
| 1 | CU-01 Registrar objeto perdido o encontrado | `ControlRegistroReporte` | `formularioReporte.jsp` |
| 1 | CU-02 Consultar y filtrar reportes en el mapa | `ControlConsultaReportes` | `mapaReportes.jsp`, `detalleReporte.jsp` |
| 2 | CU-03 Reclamar un objeto encontrado | `ControlReclamaciones` | `formularioReclamacion.jsp` |
| 2 | CU-04 Gestionar devolución del objeto | `ControlDevoluciones` | `panelDevolucion.jsp`, `vistaAcuerdoEntrega.jsp` |
| 3 | Rechazar una reclamación (Práctica 2, desarrollado con TDD) | `ControlDevoluciones` | `panelDevolucion.jsp`, `inicio.jsp` |
| 4 | HU-11 Retirar un reporte propio (Prueba 1) | `ControlRegistroReporte` | `inicio.jsp` |
| 4 | HU-12 Buscar reportes por palabra clave (Prueba 1) | `ControlConsultaReportes` | `mapaReportes.jsp` |
| 4 | HU-13 Ver estadísticas de los objetos reportados (Prueba 1) | `ControlEstadisticas` | `estadisticas.jsp` |

## Trazabilidad de los modelos al código

El código conserva los nombres de los diagramas de la Tarea 1:

| En los modelos | En el código |
|---|---|
| Clases «entity» (`Reporte`, `Reclamacion`, `AcuerdoEntrega`…) | Entidades JPA del paquete `modelo`, con los mismos atributos y operaciones |
| Clases «control» (`ControlRegistroReporte`…) | Servlets del paquete `controlador`, con las mismas operaciones |
| Clases «boundary» (`FormularioReporte`, `MapaReportes`…) | Páginas JSP de `WEB-INF/vistas` con el mismo nombre |
| Mensajes de los diagramas de secuencia (SEC-01 a SEC-04) | Comentarios `// SEC-0n · número de mensaje` junto a la línea que realiza cada mensaje |

Los diagramas de diseño, ya adaptados a Java Web, están en [`docs/modelos`](docs/modelos): las fuentes PlantUML en `src` y las imágenes en `img`.

## Tecnologías

| Capa | Tecnología |
|---|---|
| Vista | JSP con JSTL; mapa con Leaflet y OpenStreetMap |
| Controlador | Servlets (Jakarta Servlet 6) |
| Persistencia (ORM) | JPA con Hibernate |
| Base de datos | H2 embebida (archivo en `~/gr05_1bex/`) |
| Servidor | Apache Tomcat 10.1 |
| Construcción | Maven |
| Pruebas unitarias | JUnit 5 y Mockito |

No se usan frameworks de aplicación.

## Estructura

```
src/main/java/ec/edu/epn/encuentraepn/
├── modelo/        entidades JPA, enumeraciones y datos de apoyo (Campus, DatosReporte, FiltrosReporte, DatosPublicos)
├── dao/           JPAUtil, GenericDAO<T> y un DAO por entidad raíz
├── controlador/   un Servlet por clase de control, más la identificación y la página de inicio
└── web/           filtros, sesión del usuario y carga inicial de categorías
src/main/webapp/
├── WEB-INF/vistas/   una JSP por clase de interfaz
├── css/, js/         estilos y mapa
└── lib/leaflet/      librería del mapa
src/test/java/        pruebas unitarias (JUnit 5 y Mockito)
docs/modelos/         diagramas de clases y de secuencia del diseño
docs/historias/       historias de usuario del Incremento 4: tarjeta, criterios, tareas y pruebas de aceptación
pruebas/              casos de prueba automatizados en el navegador (CP-01A a CP-08)
jenkins/              imagen de Jenkins del grupo: plugins y configuración como código
Dockerfile            imagen de la aplicación (Tomcat 10.1 con el WAR)
Jenkinsfile           pipeline de integración continua
```

## Despliegue con Docker

Solo se necesita Docker. La imagen publicada en Docker Hub es [joelalexanderp/encuentraepn](https://hub.docker.com/r/joelalexanderp/encuentraepn):

```bash
docker run -d --name encuentraepn -p 8080:8080 joelalexanderp/encuentraepn:1.0
```

Luego abrir <http://localhost:8080/>. La aplicación arranca con la base de datos vacía (solo con las categorías). Para conservar los datos aunque se borre el contenedor, agregar un volumen: `-v encuentraepn-datos:/root/gr05_1bex`.

Para construir la imagen desde el código: `docker build -t encuentraepn .` y luego `docker run -d -p 8080:8080 encuentraepn`. Para detener y borrar el contenedor: `docker rm -f encuentraepn`.

## Integración continua con Jenkins en Docker

La imagen [joelalexanderp/gr05-jenkins](https://hub.docker.com/r/joelalexanderp/gr05-jenkins) es la oficial `jenkins/jenkins:lts-jdk21` con los plugins (`jenkins/plugins.txt`) y la configuración como código (`jenkins/casc.yaml`): el usuario `admin` y la tarea Pipeline `GR05_1BPR1_622_26B`, que toma el `Jenkinsfile` de la rama `main` de este repositorio. No necesita credenciales porque el repositorio es público.

```bash
docker run -d --name jenkins-gr05 -p 8090:8080 -v jenkins-gr05-home:/var/jenkins_home -e JENKINS_ADMIN_PASSWORD=<clave> joelalexanderp/gr05-jenkins:1.0
```

Luego abrir <http://localhost:8090/>. El pipeline revisa el repositorio cada 2 minutos y, si hay commits nuevos, ejecuta las etapas **Compilación**, **Pruebas unitarias** (con el reporte de JUnit) y **Empaquetado** (el WAR queda como artefacto).

## Cómo ejecutar sin Docker

Solo se necesita Java 17 o superior. Maven y Tomcat se descargan automáticamente la primera vez.

```bash
# Windows (PowerShell o símbolo del sistema)
.\mvnw.cmd package cargo:run

# Linux o macOS
./mvnw package cargo:run
```

Luego abrir <http://localhost:8080/GR05_1BEX_622_26B/>. Para detener el servidor, presionar `Ctrl + C`.

Para probar el flujo completo se necesitan dos usuarios, por ejemplo en dos navegadores: uno registra un objeto encontrado y el otro lo reclama. La identificación pide solo el nombre y un correo `@epn.edu.ec`.

## Casos de prueba

`pruebas/casos-de-prueba.js` ejecuta en un navegador los cinco casos de prueba del flujo básico definidos en la Tarea 1 y el caso CP-05 del Incremento 3 (rechazar una reclamación), con dos usuarios en sesiones separadas, y comprueba el resultado esperado de cada paso. Con la aplicación en ejecución y la base de datos vacía:

```bash
cd pruebas
npm install puppeteer-core
node casos-de-prueba.js capturas
```

`pruebas/casos-incremento-4.js` comprueba los nueve escenarios de las historias del Incremento 4 (CP-06 a CP-08) con datos de ejemplo. Con la aplicación recién iniciada en Docker: `node casos-incremento-4.js capturas http://localhost:8080`.

## Incremento 4: Programación Extrema (Prueba 1)

Tres historias de usuario nuevas, planificadas en [`docs/historias`](docs/historias), desarrolladas con TDD e integradas con Jenkins:

| Historia | Qué hace |
|---|---|
| HU-11 Retirar un reporte propio | En «Mis reportes», el autor retira un reporte activo: deja de aparecer en el mapa y sus reclamaciones pendientes quedan rechazadas |
| HU-12 Buscar reportes por palabra clave | El mapa tiene un buscador que no distingue mayúsculas, tildes ni espacios sobrantes y se combina con los demás filtros |
| HU-13 Ver estadísticas de los objetos reportados | Página con la cantidad de reportes por categoría y por zona, de mayor a menor |

Cada prueba tiene sus commits `TDD n · Rojo`, `TDD n · Verde` y, cuando hizo falta, `TDD n · Refactor`:

| N.º | Historia | Tipo | Prueba | Refactorización |
|---|---|---|---|---|
| 1 | HU-11 | Unitaria | `ReporteTest`: retirar un reporte activo lo deja RETIRADO | — |
| 2 | HU-11 | Unitaria | `ReporteTest`: un reporte reservado no se puede retirar | Extract Method (`cambiarEstado`) |
| 3 | HU-11 | Con mocks (Mockito) | `ControlRegistroReporteTest`: retirar rechaza las reclamaciones pendientes y guarda | Replace Magic Literal (`MOTIVO_RETIRO`) |
| 4 | HU-12 | Unitaria | `ReporteTest`: una palabra de la descripción coincide y otra no | — |
| 5 | HU-12 | Con parámetros | `ReporteTest`: la búsqueda no distingue mayúsculas, tildes ni espacios | Replace Temp with Query (`normalizar`) |
| 6 | HU-13 | Unitaria | `EstadisticasReportesTest`: cuenta por categoría y por zona | Parameterize Method (`contarPor`) |

## Incremento 3: rechazar una reclamación (TDD, Práctica 2)

Quien registró un objeto encontrado puede rechazar una reclamación pendiente indicando el motivo. La reclamación queda RECHAZADA, el reclamante ve el motivo en «Mis reclamaciones» y el reporte sigue activo para otras reclamaciones.

Se desarrolló con TDD: cada prueba tiene tres commits, `TDD n · Rojo`, `TDD n · Verde` y `TDD n · Refactor`.

| N.º | Tipo | Prueba | Qué comprueba |
|---|---|---|---|
| 1 | Unitaria | `ReclamacionTest` | Al rechazar una reclamación pendiente, queda RECHAZADA |
| 2 | Unitaria | `ReclamacionTest` | Se guardan el motivo, sin espacios sobrantes, y la fecha del rechazo |
| 3 | Unitaria | `ReclamacionTest` | Una reclamación ya aceptada no se puede rechazar |
| 4 | Unitaria | `ReclamacionTest` | Un motivo de más de 200 caracteres no se acepta |
| 5 | Con mocks (Mockito) | `ControlDevolucionesTest` | El control guarda el rechazo con un DAO simulado, sin base de datos |
| 6 | Con parámetros | `ReclamacionTest` | Un motivo vacío o en blanco no se acepta (un caso por fila) |

## Origen del código

El punto de partida es la aplicación de la Práctica 2, con el Incremento 3: repositorio [GR05_1BP2_622_26B](https://github.com/JoelAlexanderProg/GR05_1BP2_622_26B), commit [`960097a`](https://github.com/JoelAlexanderProg/GR05_1BP2_622_26B/commit/960097a). A su vez, esa aplicación parte de la Tarea 3 y de las refactorizaciones de la Práctica 1.

## Cómo ejecutar las pruebas unitarias

Solo se necesita Java 17 o superior. Desde la carpeta del proyecto:

    .\mvnw.cmd test      (Windows)
    ./mvnw test          (Linux o macOS)

Maven ejecuta todas las pruebas de `src/test/java` y al final muestra cuántas pasaron. Si alguna falla, indica cuál y por qué.

## Integrantes

- Lenin Alejandro Jerez Chimbo
- Dylan Isaí Maldonado Morales
- Joel Alexander Places Lucero
- Francisco Gabriel Villalba Portilla
