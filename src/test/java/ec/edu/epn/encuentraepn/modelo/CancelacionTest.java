package ec.edu.epn.encuentraepn.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static ec.edu.epn.encuentraepn.DatosDePrueba.reclamacionPendiente;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * HU-19 Cancelar una entrega que no se realizó: reglas de la reclamación.
 */
class CancelacionTest {

    private static final String NO_ASISTIO = "No asistió a la entrega";

    @Test
    @DisplayName("Cancelar una reclamación aceptada la deja CANCELADA con el motivo y la fecha")
    void cancelarUnaReclamacionAceptada() {
        Reclamacion reclamacion = reclamacionPendiente();
        reclamacion.aceptar();

        reclamacion.cancelar("  " + NO_ASISTIO + " ");

        assertEquals(EstadoReclamacion.CANCELADA, reclamacion.getEstado());
        assertEquals(NO_ASISTIO, reclamacion.getMotivoCancelacion());
        assertNotNull(reclamacion.getFechaCancelacion());
    }

    @Test
    @DisplayName("Una reclamación completada (objeto ya entregado) no se puede cancelar")
    void unaReclamacionCompletadaNoSeCancela() {
        Reclamacion reclamacion = reclamacionPendiente();
        reclamacion.aceptar();
        reclamacion.registrarAcuerdo(new AcuerdoEntrega(reclamacion, "Biblioteca Central", LocalDateTime.now()));
        reclamacion.completar();

        assertThrows(IllegalStateException.class, () -> reclamacion.cancelar(NO_ASISTIO));
        assertEquals(EstadoReclamacion.COMPLETADA, reclamacion.getEstado());
    }

    @Test
    @DisplayName("Liberar un reporte reservado lo deja ACTIVO otra vez; uno activo no se puede liberar")
    void liberarUnReporteReservado() {
        Reporte reporte = reclamacionPendiente().getObjeto();
        reporte.publicar();
        reporte.reservar();

        reporte.liberar();

        assertEquals(EstadoReporte.ACTIVO, reporte.getEstado());
        assertThrows(IllegalStateException.class, reporte::liberar);
    }

    @Test
    @DisplayName("Sin motivo no se puede cancelar la entrega")
    void sinMotivoNoSeCancela() {
        Reclamacion reclamacion = reclamacionPendiente();
        reclamacion.aceptar();

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> reclamacion.cancelar(" "));
        assertEquals("Escriba el motivo de la cancelación.", error.getMessage());
    }
}
