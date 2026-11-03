<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%-- «boundary» MapaReportes (CU-02): mostrarMapa(reportes), aplicarFiltros(filtros), seleccionarMarcador(idReporte) --%>
<c:set var="titulo" value="Mapa de reportes"/>
<%@ include file="/WEB-INF/vistas/comun/cabecera.jspf" %>

<h1>Mapa de reportes</h1>

<%-- aplicarFiltros(filtros) --%>
<form class="filtros" method="get" action="${ctx}/reportes">
    <%-- HU-12: búsqueda por palabra clave --%>
    <label>Palabra clave
        <input type="search" id="palabra" name="palabra" value="<c:out value='${param.palabra}'/>" placeholder="Ej.: mochila">
    </label>
    <label>Tipo
        <select name="tipo">
            <option value="">Todos</option>
            <c:forEach var="tipo" items="${tipos}">
                <option value="${tipo}" ${param.tipo eq tipo.name() ? 'selected' : ''}>${tipo.etiqueta}</option>
            </c:forEach>
        </select>
    </label>
    <label>Categoría
        <select name="categoria">
            <option value="">Todas</option>
            <c:forEach var="categoria" items="${categorias}">
                <option value="${categoria.id}" ${param.categoria eq categoria.id.toString() ? 'selected' : ''}>
                    <c:out value="${categoria.nombre}"/>
                </option>
            </c:forEach>
        </select>
    </label>
    <label>Desde
        <input type="date" name="desde" value="<c:out value='${param.desde}'/>">
    </label>
    <label>Hasta
        <input type="date" name="hasta" value="<c:out value='${param.hasta}'/>">
    </label>
    <label>Zona
        <select name="zona">
            <option value="">Todas</option>
            <c:forEach var="zona" items="${zonas}">
                <option value="${zona}" ${param.zona eq zona ? 'selected' : ''}>${zona}</option>
            </c:forEach>
        </select>
    </label>
    <button type="submit" class="boton">Aplicar filtros</button>
    <a href="${ctx}/reportes">Quitar filtros</a>
</form>

<%-- mostrarMapa(reportes) --%>
<div class="dos-columnas">
    <%@ include file="/WEB-INF/vistas/comun/mapa.jspf" %>
    <section>
        <h2>Reportes activos (${fn:length(reportes)})</h2>
        <c:if test="${empty reportes}">
            <p class="vacio">No hay reportes activos que cumplan los filtros.</p>
        </c:if>
        <ul id="resultados" class="resultados">
            <c:forEach var="reporte" items="${reportes}">
                <li data-latitud="${reporte.ubicacion.latitud}" data-longitud="${reporte.ubicacion.longitud}"
                    data-tipo="${reporte.tipo}">
                    <a href="${ctx}/reportes/detalle?id=${reporte.id}">
                        <span class="etiqueta ${fn:toLowerCase(reporte.tipo)}">${reporte.tipo.etiqueta}</span>
                        <c:out value="${reporte.descripcionPublica}"/>
                    </a>
                    <small><c:out value="${reporte.categoria.nombre}"/> · ${reporte.fechaSuceso} · Zona ${reporte.ubicacion.zona}</small>
                </li>
            </c:forEach>
        </ul>
    </section>
</div>

<script>
    const mapa = crearMapaCampus();
    // Un marcador por reporte; al seleccionarlo se abre su detalle público: seleccionarMarcador(idReporte)
    const usados = new Map();   // cuántos marcadores hay ya en cada punto
    document.querySelectorAll('#resultados li').forEach((resultado) => {
        const enlace = resultado.querySelector('a');
        const punto = resultado.dataset.latitud + ',' + resultado.dataset.longitud;
        const repetidos = usados.get(punto) || 0;
        usados.set(punto, repetidos + 1);
        // Los reportes del mismo punto se dibujan uno junto a otro para que no se tapen
        crearMarcador(Number(resultado.dataset.latitud), Number(resultado.dataset.longitud) + repetidos * 0.00012,
            resultado.dataset.tipo)
            .bindTooltip(enlace.textContent.trim().replace(/\s+/g, ' '))
            .on('click', () => { window.location.href = enlace.href; })
            .addTo(mapa);
    });
</script>

<%@ include file="/WEB-INF/vistas/comun/pie.jspf" %>
