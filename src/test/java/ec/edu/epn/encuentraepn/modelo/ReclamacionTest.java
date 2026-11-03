package ec.edu.epn.encuentraepn.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static ec.edu.epn.encuentraepn.DatosDePrueba.MOTIVO;
import static ec.edu.epn.encuentraepn.DatosDePrueba.reclamacionPendiente;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Incremento 3: rechazar una reclamación. Pruebas de las reglas de la entidad Reclamacion.
 */
class ReclamacionTest {

    @Test
    @DisplayName("Rechazar una reclamación pendiente la deja en estado RECHAZADA")
    void rechazarUnaReclamacionPendienteLaDejaRechazada() {
        Reclamacion reclamacion = reclamacionPendiente();

        reclamacion.rechazar(MOTIVO);

        assertEquals(EstadoReclamacion.RECHAZADA, reclamacion.getEstado());
    }

    @Test
    @DisplayName("Al rechazar se guardan el motivo, sin espacios sobrantes, y la fecha del rechazo")
    void rechazarGuardaElMotivoYLaFecha() {
        Reclamacion reclamacion = reclamacionPendiente();

        reclamacion.rechazar("  " + MOTIVO + "  ");

        assertEquals(MOTIVO, reclamacion.getMotivoRechazo());
        assertNotNull(reclamacion.getFechaRechazo());
    }

    @Test
    @DisplayName("Una reclamación ya aceptada no se puede rechazar y sigue ACEPTADA")
    void unaReclamacionAceptadaNoSePuedeRechazar() {
        Reclamacion reclamacion = reclamacionPendiente();
        reclamacion.aceptar();

        assertThrows(IllegalStateException.class, () -> reclamacion.rechazar(MOTIVO));
        assertEquals(EstadoReclamacion.ACEPTADA, reclamacion.getEstado());
    }

    @Test
    @DisplayName("Un motivo de más de 200 caracteres no se acepta y la reclamación sigue PENDIENTE")
    void unMotivoDeMasDe200CaracteresNoSeAcepta() {
        Reclamacion reclamacion = reclamacionPendiente();
        String motivoLargo = "a".repeat(Reclamacion.MAX_MOTIVO + 1);

        assertThrows(IllegalArgumentException.class, () -> reclamacion.rechazar(motivoLargo));
        assertEquals(EstadoReclamacion.PENDIENTE, reclamacion.getEstado());
    }

    /**
     * Cada fila es un caso: el motivo que se intenta usar y una descripción del caso.
     * NULO representa un motivo nulo; las comillas simples conservan los espacios.
     */
    @ParameterizedTest(name = "{1}")
    @CsvSource(nullValues = "NULO", value = {
            "NULO,       sin motivo (nulo)",
            "'',         motivo vacío",
            "'   ',      solo espacios",
            "'\t\t',     solo tabulaciones",
            "' \t ',     espacios y tabulaciones",
  
    })
    @DisplayName("Un motivo vacío o en blanco no se acepta y la reclamación sigue PENDIENTE")
    void unMotivoVacioNoSeAcepta(String motivo, String caso) {
        Reclamacion reclamacion = reclamacionPendiente();

        assertThrows(IllegalArgumentException.class, () -> reclamacion.rechazar(motivo));
        assertEquals(EstadoReclamacion.PENDIENTE, reclamacion.getEstado());
    }
}
