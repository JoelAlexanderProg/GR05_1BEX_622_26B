package ec.edu.epn.encuentraepn.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static ec.edu.epn.encuentraepn.DatosDePrueba.reporteDe;
import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * HU-13 Ver estadísticas de los objetos reportados. Prueba del conteo por categoría y por zona.
 */
class EstadisticasReportesTest {

    @Test
    @DisplayName("Cuenta los reportes por categoría y por zona, de mayor a menor cantidad")
    void cuentaLosReportesPorCategoriaYPorZona() {
        List<Reporte> reportes = List.of(
                reporteDe("Llaves", Campus.ZONA_NORTE),
                reporteDe("Electrónicos", Campus.ZONA_CENTRO),
                reporteDe("Electrónicos", Campus.ZONA_CENTRO));

        EstadisticasReportes estadisticas = new EstadisticasReportes(reportes);

        assertEquals(List.of(entry("Electrónicos", 2L), entry("Llaves", 1L)),
                List.copyOf(estadisticas.porCategoria().entrySet()));
        assertEquals(List.of(entry("Centro", 2L), entry("Norte", 1L)),
                List.copyOf(estadisticas.porZona().entrySet()));
    }
}
