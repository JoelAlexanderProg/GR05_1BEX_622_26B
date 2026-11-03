<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%-- «boundary» VistaAcuerdoEntrega (CU-04): mostrarPropuesta(acuerdo), confirmarAcuerdo(id) --%>
<c:set var="titulo" value="Acuerdo de entrega"/>
<%@ include file="/WEB-INF/vistas/comun/cabecera.jspf" %>

<h1>Acuerdo de entrega</h1>
<dl class="datos" id="propuesta">
    <dt>Objeto</dt><dd><c:out value="${acuerdo.reclamacion.objeto.descripcionPublica}"/></dd>
    <dt>Lugar</dt><dd><c:out value="${acuerdo.lugar}"/></dd>
    <dt>Fecha propuesta</dt><dd>${fn:replace(acuerdo.fechaPropuesta, 'T', ' ')}</dd>
    <dt>Estado de la reclamación</dt><dd id="estadoReclamacion">${acuerdo.reclamacion.estado.etiqueta}</dd>
    <dt>Confirmación</dt>
    <dd id="confirmacionAcuerdo">
        ${acuerdo.estaConfirmado() ? fn:replace(acuerdo.fechaConfirmacion, 'T', ' ') : 'Pendiente'}
    </dd>
</dl>

<%-- confirmarAcuerdo(id) --%>
<c:if test="${esReclamante and not acuerdo.estaConfirmado()}">
    <form method="post" action="${ctx}/devoluciones" class="acciones">
        <input type="hidden" name="accion" value="confirmarAcuerdo">
        <input type="hidden" name="id" value="${acuerdo.reclamacion.id}">
        <button type="submit" class="boton" id="confirmarAcuerdo">Confirmar acuerdo</button>
    </form>
</c:if>

<div class="acciones">
    <a href="${ctx}/inicio">Volver al inicio</a>
</div>

<%@ include file="/WEB-INF/vistas/comun/pie.jspf" %>
