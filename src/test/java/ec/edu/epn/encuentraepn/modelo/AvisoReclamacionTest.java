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
     * Prueba con parámetros: cada fila es un cambio de la reclamación, el texto que debe leer el reclamante y la
     * página a la que lo lleva el aviso.
     */
    @ParameterizedTest(name = "{0}: «{1}» → {2}")
    @CsvSource(delimiter = '|', value = {
            "aceptada  | Su reclamación de «Calculadora científica negra» fue aceptada.                                | /inicio#reclamaciones",
            "rechazada | fue rechazada. Motivo: Las respuestas no coinciden con el objeto                              | /inicio#reclamaciones",
            "propuesta | Le propusieron entregarle «Calculadora científica negra» en Biblioteca Central el 2026-11-10 10:00. | /devoluciones?acuerdo="
    })
    @DisplayName("Cada cambio de la reclamación le deja al reclamante un aviso con su texto y su enlace")
    void avisoDeCadaCambio(String cambio, String textoEsperado, String enlaceEsperado) {
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
        assertTrue(aviso.getEnlace().startsWith(enlaceEsperado), aviso.getEnlace());
    }
}
