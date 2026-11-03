package ec.edu.epn.encuentraepn.modelo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static ec.edu.epn.encuentraepn.DatosDePrueba.MOTIVO;
import static ec.edu.epn.encuentraepn.DatosDePrueba.reclamacionPendiente;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * HU-18 Recibir avisos cuando cambia el estado de mi reclamación: texto de cada aviso.
 */
class AvisoReclamacionTest {

    /**
     * Prueba con parámetros: cada fila es un cambio de la reclamación y el texto que debe leer el reclamante.
     */
    @ParameterizedTest(name = "{0}: «{1}»")
    @CsvSource(delimiter = '|', value = {
            "aceptada  | Su reclamación de «Calculadora científica negra» fue aceptada.",
            "rechazada | fue rechazada. Motivo: Las respuestas no coinciden con el objeto",
            "propuesta | Le propusieron entregarle «Calculadora científica negra» en Biblioteca Central el 2026-11-10 10:00."
    })
    @DisplayName("Cada cambio de la reclamación le deja al reclamante un aviso con el texto que corresponde")
    void avisoDeCadaCambio(String cambio, String textoEsperado) {
        Reclamacion reclamacion = reclamacionPendiente();
        Aviso aviso = switch (cambio) {
            case "aceptada" -> {
                reclamacion.aceptar();
                yield Aviso.reclamacionAceptada(reclamacion);
            }
            case "rechazada" -> {
                reclamacion.rechazar(MOTIVO);
                yield Aviso.reclamacionRechazada(reclamacion);
            }
            default -> {
                reclamacion.aceptar();
                yield Aviso.entregaPropuesta(new AcuerdoEntrega(reclamacion, "Biblioteca Central",
                        LocalDateTime.of(2026, 11, 10, 10, 0)));
            }
        };

        assertEquals(reclamacion.getReclamante(), aviso.getDestinatario());
        assertTrue(aviso.getTexto().contains(textoEsperado), aviso.getTexto());
    }
}
