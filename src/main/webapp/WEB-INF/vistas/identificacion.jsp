<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Identificación"/>
<%@ include file="/WEB-INF/vistas/comun/cabecera.jspf" %>

<h1>Identificación</h1>
<p>Para registrar reportes, reclamar objetos o gestionar devoluciones indique quién es.</p>

<c:if test="${not empty error}">
    <p class="error"><c:out value="${error}"/></p>
</c:if>

<form class="formulario" method="post" action="${ctx}/identificacion">
    <input type="hidden" name="destino" value="<c:out value='${param.destino}'/>">

    <label for="nombre">Nombre</label>
    <input type="text" id="nombre" name="nombre" maxlength="100" required
           value="<c:out value='${param.nombre}'/>">

    <label for="correo">Correo institucional</label>
    <input type="email" id="correo" name="correo" maxlength="100" required placeholder="nombre.apellido@epn.edu.ec"
           value="<c:out value='${param.correo}'/>">

    <div class="acciones">
        <button type="submit" class="boton">Continuar</button>
    </div>
</form>

<%@ include file="/WEB-INF/vistas/comun/pie.jspf" %>
