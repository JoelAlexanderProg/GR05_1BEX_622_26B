<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page import="ec.edu.epn.encuentraepn.modelo.Reporte" %>
<%-- «boundary» VistaCoincidencias (HU-14): objetos encontrados que pueden ser el objeto perdido --%>
<c:set var="titulo" value="Coincidencias"/>
<%@ include file="/WEB-INF/vistas/comun/cabecera.jspf" %>

<h1>Coincidencias de mi objeto perdido</h1>
<dl class="datos" id="perdido">
    <dt>Objeto perdido</dt><dd><c:out value="${perdido.descripcionPublica}"/></dd>
    <dt>Categoría</dt><dd><c:out value="${perdido.categoria.nombre}"/></dd>
    <dt>Fecha</dt><dd>${perdido.fechaSuceso}</dd>
</dl>
<p>Se muestran los objetos encontrados de la misma categoría, a ${Reporte.RADIO_COINCIDENCIA_METROS} m o menos del
    lugar de la pérdida y hasta ${Reporte.DIAS_COINCIDENCIA} días después, del más cercano al más lejano.</p>

<div class="dos-columnas">
    <section>
        <c:choose>
            <c:when test="${empty coincidencias}">
                <p class="vacio" id="sinCoincidencias">Todavía no hay objetos encontrados que coincidan con su reporte.</p>
            </c:when>
            <c:otherwise>
                <ul id="coincidencias" class="resultados">
                    <c:forEach var="coincidencia" items="${coincidencias}">
                        <c:set var="encontrado" value="${coincidencia.encontrado()}"/>
                        <li data-latitud="${encontrado.ubicacion.latitud}" data-longitud="${encontrado.ubicacion.longitud}">
                            <a href="${ctx}/reportes/detalle?id=${encontrado.id}">
                                <span class="etiqueta encontrado">A ${coincidencia.metros()} m</span>
                                <c:out value="${encontrado.descripcionPublica}"/>
                            </a>
                            <small>Encontrado el ${encontrado.fechaSuceso} · Zona ${encontrado.ubicacion.zona}</small>
                        </li>
                    </c:forEach>
                </ul>
            </c:otherwise>
        </c:choose>
    </section>
    <%@ include file="/WEB-INF/vistas/comun/mapa.jspf" %>
</div>

<div class="acciones">
    <a href="${ctx}/inicio">Volver al inicio</a>
</div>

<script>
    const mapa = crearMapaCampus();
    // El objeto perdido en rojo y cada coincidencia en verde; al seleccionarla se abre su detalle para reclamarla
    crearMarcador(${perdido.ubicacion.latitud}, ${perdido.ubicacion.longitud}, 'PERDIDO')
        .bindTooltip('Lugar de la pérdida').addTo(mapa);
    document.querySelectorAll('#coincidencias li').forEach((coincidencia) => {
        const enlace = coincidencia.querySelector('a');
        crearMarcador(Number(coincidencia.dataset.latitud), Number(coincidencia.dataset.longitud), 'ENCONTRADO')
            .bindTooltip(enlace.textContent.trim().replace(/\s+/g, ' '))
            .on('click', () => { window.location.href = enlace.href; })
            .addTo(mapa);
    });
</script>

<%@ include file="/WEB-INF/vistas/comun/pie.jspf" %>
