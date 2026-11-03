<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%-- «boundary» FormularioReporte (CU-01): mostrarFormulario(), solicitarPublicacion(datos), mostrarConfirmacion(reporte) --%>
<c:set var="titulo" value="Registrar reporte"/>
<%@ include file="/WEB-INF/vistas/comun/cabecera.jspf" %>

<c:choose>
    <%-- mostrarConfirmacion(reporte) --%>
    <c:when test="${not empty reporte}">
        <h1>Reporte publicado</h1>
        <p class="exito">Su reporte quedó registrado y ya aparece en el mapa.</p>
        <dl class="datos" id="confirmacion">
            <dt>Tipo</dt><dd>${reporte.tipo.etiqueta}</dd>
            <dt>Categoría</dt><dd><c:out value="${reporte.categoria.nombre}"/></dd>
            <dt>Descripción</dt><dd><c:out value="${reporte.descripcionPublica}"/></dd>
            <dt>Fecha</dt><dd>${reporte.fechaSuceso}</dd>
            <dt>Zona</dt><dd>${reporte.ubicacion.zona}</dd>
            <dt>Estado</dt><dd>${reporte.estado.etiqueta}</dd>
            <dt>Registrado por</dt><dd><c:out value="${reporte.autor.nombre}"/></dd>
            <c:if test="${reporte.esEncontrado()}">
                <dt>Preguntas de verificación</dt>
                <dd>
                    <ol>
                        <c:forEach var="pregunta" items="${reporte.preguntas}">
                            <li><c:out value="${pregunta.texto}"/></li>
                        </c:forEach>
                    </ol>
                </dd>
            </c:if>
        </dl>
        <div class="acciones">
            <a class="boton" href="${ctx}/reportes/detalle?id=${reporte.id}">Ver el reporte</a>
            <a href="${ctx}/reportes/nuevo">Registrar otro reporte</a>
        </div>
    </c:when>

    <%-- mostrarFormulario() --%>
    <c:otherwise>
        <h1>Registrar reporte</h1>

        <c:if test="${not empty error}">
            <p class="error"><c:out value="${error}"/></p>
        </c:if>

        <%-- solicitarPublicacion(datos) --%>
        <form class="formulario ancho" method="post" action="${ctx}/reportes/nuevo">
            <fieldset>
                <legend>¿Qué pasó con el objeto?</legend>
                <label class="opcion">
                    <input type="radio" name="tipo" value="PERDIDO" required ${param.tipo eq 'PERDIDO' ? 'checked' : ''}>
                    Lo perdí
                </label>
                <label class="opcion">
                    <input type="radio" name="tipo" value="ENCONTRADO" required ${param.tipo eq 'ENCONTRADO' ? 'checked' : ''}>
                    Lo encontré
                </label>
            </fieldset>

            <label for="categoria">Categoría</label>
            <select id="categoria" name="categoria" required>
                <option value="">Seleccione una categoría</option>
                <c:forEach var="categoria" items="${categorias}">
                    <option value="${categoria.id}" ${param.categoria eq categoria.id.toString() ? 'selected' : ''}>
                        <c:out value="${categoria.nombre}"/>
                    </option>
                </c:forEach>
            </select>

            <label for="descripcion">Descripción pública</label>
            <textarea id="descripcion" name="descripcion" maxlength="200" rows="2" required><c:out value="${param.descripcion}"/></textarea>
            <small>La verá cualquier persona. No incluya detalles que solo el dueño conoce.</small>

            <label for="fecha">Fecha en que se perdió o se encontró</label>
            <input type="date" id="fecha" name="fecha" max="${hoy}" required
                   value="<c:out value='${empty param.fecha ? hoy : param.fecha}'/>">

            <label>Ubicación aproximada: haga clic en el mapa, dentro del área punteada</label>
            <%@ include file="/WEB-INF/vistas/comun/mapa.jspf" %>
            <input type="hidden" id="latitud" name="latitud" value="<c:out value='${param.latitud}'/>">
            <input type="hidden" id="longitud" name="longitud" value="<c:out value='${param.longitud}'/>">
            <small id="ubicacionElegida">Todavía no ha marcado la ubicación.</small>

            <fieldset id="preguntas" hidden>
                <legend>Preguntas de verificación</legend>
                <small>Pregunte por detalles que solo el dueño conoce. Las responderá quien reclame el objeto.</small>
                <c:forEach var="numero" begin="0" end="2">
                    <input type="text" name="pregunta" maxlength="200" placeholder="Pregunta ${numero + 1}"
                           value="<c:out value='${paramValues.pregunta[numero]}'/>">
                </c:forEach>
            </fieldset>

            <div class="acciones">
                <button type="submit" class="boton">Publicar reporte</button>
                <a href="${ctx}/inicio">Cancelar</a>
            </div>
        </form>

        <script>
            const mapa = crearMapaCampus();
            const latitud = document.getElementById('latitud');
            const longitud = document.getElementById('longitud');
            const preguntas = document.getElementById('preguntas');
            let marcador = null;

            function marcarUbicacion(posicion) {
                if (marcador) {
                    mapa.removeLayer(marcador);
                }
                marcador = crearMarcador(posicion.lat, posicion.lng).addTo(mapa);
                latitud.value = posicion.lat.toFixed(6);
                longitud.value = posicion.lng.toFixed(6);
                document.getElementById('ubicacionElegida').textContent =
                    'Ubicación marcada: ' + latitud.value + ', ' + longitud.value;
            }

            function mostrarPreguntas() {
                const tipo = document.querySelector('input[name="tipo"]:checked');
                preguntas.hidden = !tipo || tipo.value !== 'ENCONTRADO';
            }

            mapa.on('click', (evento) => marcarUbicacion(evento.latlng));
            document.querySelectorAll('input[name="tipo"]').forEach((opcion) => opcion.addEventListener('change', mostrarPreguntas));
            mostrarPreguntas();
            if (latitud.value && longitud.value) {
                marcarUbicacion(L.latLng(Number(latitud.value), Number(longitud.value)));
            }
        </script>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/vistas/comun/pie.jspf" %>
