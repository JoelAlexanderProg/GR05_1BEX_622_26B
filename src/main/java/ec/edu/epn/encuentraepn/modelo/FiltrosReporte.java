package ec.edu.epn.encuentraepn.modelo;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Criterios de búsqueda del mapa (el parámetro «filtros» de los diagramas).
 * Un criterio en null no se aplica. La palabra clave es de la HU-12: la descripción debe contenerla.
 */
public record FiltrosReporte(
        TipoReporte tipo,
        UUID categoriaId,
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        String zona,
        String palabra) {

    public static FiltrosReporte sinFiltros() {
        return new FiltrosReporte(null, null, null, null, null, null);
    }
}
