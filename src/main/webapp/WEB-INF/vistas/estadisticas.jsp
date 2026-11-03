<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- «boundary» VistaEstadisticas (HU-13): cantidad de reportes por categoría y por zona del campus --%>
<c:set var="titulo" value="Estadísticas"/>
<%@ include file="/WEB-INF/vistas/comun/cabecera.jspf" %>

<h1>Estadísticas de los objetos reportados</h1>

<c:choose>
    <c:when test="${estadisticas.total() == 0}">
        <p class="vacio" id="sinReportes">Todavía no hay reportes registrados.</p>
    </c:when>
    <c:otherwise>
        <p>Se han registrado <strong id="total">${estadisticas.total()}</strong> reportes de objetos perdidos y encontrados.</p>
        <div class="dos-columnas">
            <section>
                <h2>Por categoría</h2>
                <table id="porCategoria">
                    <thead><tr><th>Categoría</th><th>Reportes</th></tr></thead>
                    <tbody>
                    <c:forEach var="fila" items="${estadisticas.porCategoria()}">
                        <tr><td><c:out value="${fila.key}"/></td><td>${fila.value}</td></tr>
                    </c:forEach>
                    </tbody>
                </table>
            </section>
            <section>
                <h2>Por zona del campus</h2>
                <table id="porZona">
                    <thead><tr><th>Zona</th><th>Reportes</th></tr></thead>
                    <tbody>
                    <c:forEach var="fila" items="${estadisticas.porZona()}">
                        <tr><td>${fila.key}</td><td>${fila.value}</td></tr>
                    </c:forEach>
                    </tbody>
                </table>
            </section>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/vistas/comun/pie.jspf" %>
