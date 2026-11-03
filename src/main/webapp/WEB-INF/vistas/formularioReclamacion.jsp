<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%-- «boundary» FormularioReclamacion (CU-03): mostrarPreguntas(preguntas), enviarRespuestas(respuestas), mostrarConfirmacion(reclamacion) --%>
<c:set var="titulo" value="Reclamar objeto"/>
<%@ include file="/WEB-INF/vistas/comun/cabecera.jspf" %>

<c:choose>
    <%-- mostrarConfirmacion(reclamacion) --%>
    <c:when test="${not empty reclamacion}">
        <h1>Reclamación registrada</h1>
        <p class="exito">
            Su reclamación quedó en estado <strong id="estadoReclamacion">${reclamacion.estado.etiqueta}</strong>.
            Quien encontró el objeto revisará sus respuestas antes de coordinar la entrega.
        </p>
        <dl class="datos">
            <dt>Objeto</dt><dd><c:out value="${detalle.descripcionPublica}"/></dd>
            <dt>Fecha de la reclamación</dt><dd>${fn:replace(reclamacion.fechaCreacion, 'T', ' ')}</dd>
        </dl>
        <div class="acciones">
            <a class="boton" href="${ctx}/inicio">Ver mis reclamaciones</a>
        </div>
    </c:when>

    <%-- mostrarPreguntas(preguntas) --%>
    <c:otherwise>
        <h1>Reclamar objeto encontrado</h1>
        <dl class="datos">
            <dt>Objeto</dt><dd><c:out value="${detalle.descripcionPublica}"/></dd>
            <dt>Categoría</dt><dd><c:out value="${detalle.categoria}"/></dd>
            <dt>Encontrado el</dt><dd>${detalle.fechaSuceso}, zona ${detalle.zona}</dd>
        </dl>

        <c:if test="${not empty error}">
            <p class="error"><c:out value="${error}"/></p>
        </c:if>

        <p>Responda las preguntas de quien encontró el objeto. Solo esa persona verá sus respuestas.</p>

        <%-- enviarRespuestas(respuestas) --%>
        <form class="formulario ancho" method="post" action="${ctx}/reclamaciones/nueva">
            <input type="hidden" name="reporte" value="${detalle.id}">
            <c:forEach var="pregunta" items="${preguntas}">
                <c:set var="campo" value="respuesta_${pregunta.id}"/>
                <label for="${campo}"><c:out value="${pregunta.texto}"/></label>
                <input type="text" id="${campo}" name="${campo}" maxlength="300" required
                       value="<c:out value='${param[campo]}'/>">
            </c:forEach>
            <div class="acciones">
                <button type="submit" class="boton">Enviar reclamación</button>
                <a href="${ctx}/reportes/detalle?id=${detalle.id}">Cancelar</a>
            </div>
        </form>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/vistas/comun/pie.jspf" %>
