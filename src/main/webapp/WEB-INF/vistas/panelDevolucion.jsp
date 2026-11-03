<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%-- «boundary» PanelDevolucion (CU-04): mostrarReclamaciones(lista), mostrarRespuestas(respuestas),
     aceptarReclamacion(id), proponerEntrega(datos), confirmarDevolucion(id) --%>
<c:set var="titulo" value="Devolución del objeto"/>
<%@ include file="/WEB-INF/vistas/comun/cabecera.jspf" %>

<h1>Devolución del objeto</h1>
<dl class="datos">
    <dt>Objeto</dt><dd><c:out value="${reporte.descripcionPublica}"/></dd>
    <dt>Estado del reporte</dt><dd id="estadoReporte">${reporte.estado.etiqueta}</dd>
    <c:if test="${not empty reporte.fechaDevolucion}">
        <dt>Fecha de devolución</dt><dd id="fechaDevolucion">${fn:replace(reporte.fechaDevolucion, 'T', ' ')}</dd>
    </c:if>
</dl>

<%-- mostrarReclamaciones(lista) --%>
<h2>Reclamaciones pendientes</h2>
<c:choose>
    <c:when test="${empty pendientes}">
        <p class="vacio">No hay reclamaciones pendientes.</p>
    </c:when>
    <c:otherwise>
        <table id="pendientes">
            <thead>
            <tr><th>Reclamante</th><th>Fecha</th><th>Estado</th><th></th></tr>
            </thead>
            <tbody>
            <c:forEach var="pendiente" items="${pendientes}">
                <tr>
                    <td><c:out value="${pendiente.reclamante.nombre}"/></td>
                    <td>${fn:replace(pendiente.fechaCreacion, 'T', ' ')}</td>
                    <td>${pendiente.estado.etiqueta}</td>
                    <td class="enlaces"><a href="${ctx}/devoluciones?reclamacion=${pendiente.id}">Revisar respuestas</a></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

<c:if test="${not empty enCurso and enCurso.id ne reclamacion.id}">
    <p>
        Reclamación ${fn:toLowerCase(enCurso.estado.etiqueta)} de <c:out value="${enCurso.reclamante.nombre}"/>:
        <a id="enCurso" href="${ctx}/devoluciones?reclamacion=${enCurso.id}">continuar con la devolución</a>
    </p>
</c:if>

<c:if test="${not empty reclamacion}">
    <%-- mostrarRespuestas(respuestas) --%>
    <h2>Reclamación de <c:out value="${reclamacion.reclamante.nombre}"/></h2>
    <p>Estado de la reclamación: <strong id="estadoReclamacion">${reclamacion.estado.etiqueta}</strong></p>
    <c:if test="${not empty reclamacion.motivoRechazo}">
        <p>Motivo del rechazo: <span id="motivoRechazo"><c:out value="${reclamacion.motivoRechazo}"/></span></p>
    </c:if>
    <table id="respuestas">
        <thead>
        <tr><th>Pregunta</th><th>Respuesta del reclamante</th></tr>
        </thead>
        <tbody>
        <c:forEach var="respuesta" items="${respuestas}">
            <tr>
                <td><c:out value="${respuesta.pregunta.texto}"/></td>
                <td><c:out value="${respuesta.textoPrivado}"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <c:set var="acuerdo" value="${reclamacion.acuerdo}"/>
    <c:choose>
        <%-- aceptarReclamacion(id) y, desde el Incremento 3, rechazarReclamacion(id, motivo) --%>
        <c:when test="${reclamacion.estaPendiente()}">
            <c:if test="${reporte.estaActivo()}">
                <form method="post" action="${ctx}/devoluciones" class="acciones">
                    <input type="hidden" name="accion" value="aceptar">
                    <input type="hidden" name="id" value="${reclamacion.id}">
                    <button type="submit" class="boton" id="aceptar">Aceptar reclamación</button>
                </form>
            </c:if>
            <h2>Rechazar reclamación</h2>
            <form method="post" action="${ctx}/devoluciones" class="formulario">
                <input type="hidden" name="accion" value="rechazar">
                <input type="hidden" name="id" value="${reclamacion.id}">
                <label for="motivo">Motivo del rechazo (lo verá el reclamante)</label>
                <textarea id="motivo" name="motivo" rows="3" maxlength="200" required></textarea>
                <div class="acciones">
                    <button type="submit" class="boton secundario" id="rechazar">Rechazar reclamación</button>
                </div>
            </form>
        </c:when>

        <%-- proponerEntrega(datos) --%>
        <c:when test="${reclamacion.estaAceptada() and empty acuerdo}">
            <h2>Proponer entrega</h2>
            <form method="post" action="${ctx}/devoluciones" class="formulario">
                <input type="hidden" name="accion" value="proponer">
                <input type="hidden" name="id" value="${reclamacion.id}">
                <label for="lugar">Lugar de entrega</label>
                <input type="text" id="lugar" name="lugar" maxlength="150" required>
                <label for="fecha">Fecha y hora</label>
                <input type="datetime-local" id="fecha" name="fecha" required>
                <div class="acciones">
                    <button type="submit" class="boton" id="proponer">Proponer entrega</button>
                </div>
            </form>
        </c:when>

        <c:when test="${not empty acuerdo}">
            <h2>Acuerdo de entrega</h2>
            <dl class="datos" id="acuerdo">
                <dt>Lugar</dt><dd><c:out value="${acuerdo.lugar}"/></dd>
                <dt>Fecha propuesta</dt><dd>${fn:replace(acuerdo.fechaPropuesta, 'T', ' ')}</dd>
                <dt>Confirmación del reclamante</dt>
                <dd id="confirmacionAcuerdo">
                    ${acuerdo.estaConfirmado() ? fn:replace(acuerdo.fechaConfirmacion, 'T', ' ') : 'Pendiente'}
                </dd>
            </dl>
            <%-- confirmarDevolucion(id) --%>
            <c:if test="${reclamacion.estaAceptada() and acuerdo.estaConfirmado()}">
                <p>Cuando haya entregado el objeto al reclamante, confirme la devolución.</p>
                <form method="post" action="${ctx}/devoluciones" class="acciones">
                    <input type="hidden" name="accion" value="confirmarDevolucion">
                    <input type="hidden" name="id" value="${reclamacion.id}">
                    <button type="submit" class="boton" id="confirmarDevolucion">Confirmar devolución</button>
                </form>
            </c:if>
        </c:when>
    </c:choose>
</c:if>

<div class="acciones">
    <a href="${ctx}/inicio">Volver al inicio</a>
</div>

<%@ include file="/WEB-INF/vistas/comun/pie.jspf" %>
