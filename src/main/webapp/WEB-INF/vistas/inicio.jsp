<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="titulo" value="Inicio"/>
<%@ include file="/WEB-INF/vistas/comun/cabecera.jspf" %>

<h1>Objetos perdidos y encontrados en el campus</h1>
<p>Registre un objeto que perdió o que encontró, búsquelo en el mapa del campus y coordine su devolución.</p>
<div class="acciones">
    <a class="boton" href="${ctx}/reportes">Ver mapa de reportes</a>
    <a class="boton secundario" href="${ctx}/reportes/nuevo">Registrar reporte</a>
</div>

<c:if test="${not empty sessionScope.usuario}">
    <h2>Mis reportes</h2>
    <c:if test="${param.retirado != null}">
        <p class="exito" id="retirado">El reporte se retiró y ya no aparece en el mapa. Sus reclamaciones pendientes quedaron rechazadas.</p>
    </c:if>
    <c:choose>
        <c:when test="${empty misReportes}">
            <p class="vacio">Todavía no ha registrado reportes.</p>
        </c:when>
        <c:otherwise>
            <table id="misReportes">
                <thead>
                <tr><th>Tipo</th><th>Descripción</th><th>Fecha</th><th>Estado</th><th></th></tr>
                </thead>
                <tbody>
                <c:forEach var="reporte" items="${misReportes}">
                    <tr>
                        <td><span class="etiqueta ${fn:toLowerCase(reporte.tipo)}">${reporte.tipo.etiqueta}</span></td>
                        <td><c:out value="${reporte.descripcionPublica}"/></td>
                        <td>${reporte.fechaSuceso}</td>
                        <td>${reporte.estado.etiqueta}</td>
                        <td class="enlaces">
                            <a href="${ctx}/reportes/detalle?id=${reporte.id}">Detalle</a>
                            <c:if test="${reporte.esEncontrado()}">
                                <a href="${ctx}/devoluciones?reporte=${reporte.id}">Reclamaciones</a>
                            </c:if>
                            <%-- HU-14: un objeto perdido activo puede tener objetos encontrados que coinciden --%>
                            <c:if test="${not reporte.esEncontrado() and reporte.estaActivo()}">
                                <a href="${ctx}/coincidencias?reporte=${reporte.id}">Coincidencias</a>
                            </c:if>
                            <%-- HU-11: solo los reportes activos se pueden retirar --%>
                            <c:if test="${reporte.estaActivo()}">
                                <form method="post" action="${ctx}/reportes/retirar"
                                      onsubmit="return confirm('¿Retirar este reporte? Dejará de aparecer en el mapa.')">
                                    <input type="hidden" name="id" value="${reporte.id}">
                                    <button type="submit" class="boton-enlace">Retirar</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>

    <%-- HU-15: objetos encontrados que otras personas vincularon con mis reportes de pérdida --%>
    <c:if test="${not empty hallazgos}">
        <h2>Alguien encontró mis objetos</h2>
        <table id="hallazgos">
            <thead>
            <tr><th>Mi objeto perdido</th><th>Objeto encontrado</th><th>Encontrado por</th><th>Fecha</th><th></th></tr>
            </thead>
            <tbody>
            <c:forEach var="hallazgo" items="${hallazgos}">
                <tr>
                    <td><c:out value="${hallazgo.reportePerdido.descripcionPublica}"/></td>
                    <td><c:out value="${hallazgo.descripcionPublica}"/></td>
                    <td><c:out value="${hallazgo.autor.nombre}"/></td>
                    <td>${hallazgo.fechaSuceso}</td>
                    <td class="enlaces"><a href="${ctx}/reportes/detalle?id=${hallazgo.id}">Ver y reclamar</a></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

    <%-- HU-18: los avisos de la reclamación llevan a esta sección --%>
    <h2 id="reclamaciones">Mis reclamaciones</h2>
    <c:choose>
        <c:when test="${empty misReclamaciones}">
            <p class="vacio">Todavía no ha reclamado objetos.</p>
        </c:when>
        <c:otherwise>
            <table id="misReclamaciones">
                <thead>
                <tr><th>Objeto</th><th>Fecha</th><th>Estado</th><th></th></tr>
                </thead>
                <tbody>
                <c:forEach var="reclamacion" items="${misReclamaciones}">
                    <tr>
                        <td><c:out value="${reclamacion.objeto.descripcionPublica}"/></td>
                        <td>${fn:replace(reclamacion.fechaCreacion, 'T', ' ')}</td>
                        <td>${reclamacion.estado.etiqueta}
                            <c:if test="${not empty reclamacion.motivoRechazo}">
                                <div class="motivo">Motivo: <c:out value="${reclamacion.motivoRechazo}"/></div>
                            </c:if>
                            <%-- HU-19: motivo de una entrega cancelada --%>
                            <c:if test="${not empty reclamacion.motivoCancelacion}">
                                <div class="motivo">Motivo: <c:out value="${reclamacion.motivoCancelacion}"/></div>
                            </c:if>
                        </td>
                        <td class="enlaces">
                            <c:if test="${not empty reclamacion.acuerdo}">
                                <a href="${ctx}/devoluciones?acuerdo=${reclamacion.id}">Acuerdo de entrega</a>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</c:if>

<%@ include file="/WEB-INF/vistas/comun/pie.jspf" %>
