// Mapa del campus que comparten las vistas (Leaflet + OpenStreetMap).

const COLOR_POR_TIPO = { PERDIDO: '#b3261e', ENCONTRADO: '#1e6b3a' };

/**
 * Crea el mapa en el elemento #mapa, ajustado a los límites del campus,
 * y dibuja esos límites con una línea punteada.
 */
function crearMapaCampus() {
    const contenedor = document.getElementById('mapa');
    const limites = [
        [Number(contenedor.dataset.latMin), Number(contenedor.dataset.lonMin)],
        [Number(contenedor.dataset.latMax), Number(contenedor.dataset.lonMax)]
    ];
    const mapa = L.map(contenedor).fitBounds(limites);
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; OpenStreetMap'
    }).addTo(mapa);
    L.rectangle(limites, { color: '#12355b', weight: 1, dashArray: '6 4', fill: false, interactive: false })
        .addTo(mapa);
    return mapa;
}

/** Marcador circular con el color del tipo de reporte. */
function crearMarcador(latitud, longitud, tipo) {
    return L.circleMarker([latitud, longitud], {
        radius: 9,
        color: '#ffffff',
        weight: 2,
        fillColor: COLOR_POR_TIPO[tipo] || '#12355b',
        fillOpacity: 0.95
    });
}
