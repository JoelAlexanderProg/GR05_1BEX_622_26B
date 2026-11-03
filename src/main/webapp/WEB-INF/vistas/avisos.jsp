<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%-- «boundary» BandejaAvisos (HU-16): avisos del usuario, del más reciente al más antiguo --%>
<c:set var="titulo" value="Mis avisos"/>
<%@ include file="/WEB-INF/vistas/comun/cabecera.jspf" %>

<h1>Mis avisos</h1>
<c:choose>
    <c:when test="${empty avisos}">
        <p class="vacio" id="sinAvisos">Todavía no tiene avisos.</p>
    </c:when>
    <c:otherwise>
        <ul id="avisos" class="avisos">
            <c:forEach var="aviso" items="${avisos}">
                <%-- HU-17: los avisos que el usuario todavía no había visto se resaltan como nuevos --%>
                <li class="${nuevos.contains(aviso) ? 'nuevo' : ''}">
                    <small class="fecha">${fn:replace(fn:substring(aviso.fecha, 0, 16), 'T', ' ')}<c:if
                            test="${nuevos.contains(aviso)}"> · <strong>Nuevo</strong></c:if></small>
                    <p><c:out value="${aviso.texto}"/></p>
                    <a href="${ctx}${aviso.enlace}">Ver</a>
                </li>
            </c:forEach>
        </ul>
    </c:otherwise>
</c:choose>

<div class="acciones">
    <a href="${ctx}/inicio">Volver al inicio</a>
</div>

<%@ include file="/WEB-INF/vistas/comun/pie.jspf" %>
