<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="No se pudo completar la operación"/>
<%@ include file="/WEB-INF/vistas/comun/cabecera.jspf" %>

<h1>No se pudo completar la operación</h1>
<p class="error"><c:out value="${error}"/></p>
<div class="acciones">
    <a class="boton" href="${ctx}/inicio">Ir al inicio</a>
    <a href="${ctx}/reportes">Ver mapa de reportes</a>
</div>

<%@ include file="/WEB-INF/vistas/comun/pie.jspf" %>
