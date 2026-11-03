package ec.edu.epn.encuentraepn.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static ec.edu.epn.encuentraepn.DatosDePrueba.encontradoA;
import static ec.edu.epn.encuentraepn.DatosDePrueba.reportePerdido;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * HU-15 Avisar al dueño de un objeto perdido que yo lo encontré: vínculo entre el objeto encontrado y el reporte
 * de pérdida.
 */
class HallazgoTest {

    private final Categoria llaves = new Categoria("Llaves");

    @Test
    @DisplayName("Un objeto encontrado queda vinculado con el reporte de pérdida activo de otra persona")
    void vinculaElHallazgoConElReportePerdido() {
        Reporte perdido = reportePerdido(llaves);
        Reporte encontrado = encontradoA(llaves, 10, 1);

        encontrado.vincularConPerdido(perdido);

        assertEquals(perdido, encontrado.getReportePerdido());
    }

    @Test
    @DisplayName("Nadie puede avisar que encontró un objeto que él mismo reportó como perdido")
    void noSePuedeAvisarSobreUnReportePropio() {
        Reporte perdido = reportePerdido(llaves);
        Reporte propio = new Reporte(new DatosReporte(TipoReporte.ENCONTRADO, null, "Llaves", perdido.getFechaSuceso(),
                -0.21, -78.49, List.of("¿Cuántas llaves son?")),
                perdido.getAutor(), llaves, perdido.getUbicacion());

        assertThrows(IllegalArgumentException.class, () -> propio.vincularConPerdido(perdido));
        assertNull(propio.getReportePerdido());
    }

    @Test
    @DisplayName("Un reporte de pérdida que ya no está activo no recibe hallazgos")
    void unReporteRetiradoNoRecibeHallazgos() {
        Reporte perdido = reportePerdido(llaves);
        perdido.retirar();
        Reporte encontrado = encontradoA(llaves, 10, 1);

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> encontrado.vincularConPerdido(perdido));
        assertEquals("Este reporte de pérdida ya no está activo.", error.getMessage());
    }
}
