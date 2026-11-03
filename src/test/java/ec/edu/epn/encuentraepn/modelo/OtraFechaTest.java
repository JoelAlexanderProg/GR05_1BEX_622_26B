package ec.edu.epn.encuentraepn.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static ec.edu.epn.encuentraepn.DatosDePrueba.reclamacionPendiente;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * HU-20 Pedir otra fecha para la entrega: reglas del acuerdo de entrega.
 */
class OtraFechaTest {

    /** «Ahora» fijo para que la prueba no dependa del reloj: lunes 9 de noviembre de 2026, 08:00. */
    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 9, 8, 0);
    private static final LocalDateTime MANANA_10 = AHORA.plusDays(1).withHour(10);
    private static final LocalDateTime PASADO_15 = AHORA.plusDays(2).withHour(15);

    private AcuerdoEntrega acuerdo;

    @BeforeEach
    void preparar() {
        Reclamacion reclamacion = reclamacionPendiente();
        reclamacion.aceptar();
        acuerdo = new AcuerdoEntrega(reclamacion, "Biblioteca Central", MANANA_10);
    }

    @Test
    @DisplayName("El reclamante pide otra fecha futura y queda registrada sin confirmar el acuerdo")
    void pedirOtraFecha() {
        acuerdo.solicitarOtraFecha(PASADO_15, AHORA);

        assertEquals(PASADO_15, acuerdo.getFechaSolicitada());
        assertEquals(MANANA_10, acuerdo.getFechaPropuesta());
        assertFalse(acuerdo.estaConfirmado());
    }

    @Test
    @DisplayName("Al aceptar la nueva fecha, el acuerdo queda con esa fecha y confirmado")
    void aceptarLaNuevaFecha() {
        acuerdo.solicitarOtraFecha(PASADO_15, AHORA);

        acuerdo.aceptarOtraFecha(AHORA.plusHours(1));

        assertEquals(PASADO_15, acuerdo.getFechaPropuesta());
        assertFalse(acuerdo.hayFechaSolicitada());
        assertTrue(acuerdo.estaConfirmado());
    }

    @Test
    @DisplayName("Una fecha que ya pasó no se puede pedir, ni otra fecha sobre un acuerdo confirmado")
    void fechaPasadaOAcuerdoConfirmado() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> acuerdo.solicitarOtraFecha(AHORA.minusHours(2), AHORA));
        assertEquals("Indique una fecha futura para la entrega.", error.getMessage());

        acuerdo.confirmar(AHORA);
        assertThrows(IllegalStateException.class, () -> acuerdo.solicitarOtraFecha(PASADO_15, AHORA));
    }
}
