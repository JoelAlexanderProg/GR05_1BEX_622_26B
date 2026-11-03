package ec.edu.epn.encuentraepn.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * HU-21 Elegir un punto de entrega oficial del campus: horario de atención del punto.
 */
class PuntoEntregaTest {

    private final PuntoEntrega biblioteca = new PuntoEntrega("Biblioteca Central",
            "Planta baja, mostrador de préstamo", LocalTime.of(7, 0), LocalTime.of(19, 0));

    /**
     * Prueba con parámetros: cada fila es una fecha y hora y si la Biblioteca Central atiende
     * (el 9 de noviembre de 2026 es lunes y el 14, sábado).
     */
    @ParameterizedTest(name = "{2}: {0} → {1}")
    @CsvSource({
            "2026-11-09T10:00, true,  lunes por la mañana",
            "2026-11-09T07:00, true,  justo a la hora de apertura",
            "2026-11-13T18:59, true,  viernes un minuto antes del cierre",
            "2026-11-09T19:00, false, a la hora de cierre",
            "2026-11-09T06:59, false, antes de abrir",
            "2026-11-09T21:00, false, por la noche",
            "2026-11-14T10:00, false, sábado"
    })
    @DisplayName("El punto atiende de lunes a viernes, desde la apertura y antes del cierre")
    void horarioDeAtencion(LocalDateTime fecha, boolean atiende, String caso) {
        assertEquals(atiende, biblioteca.atiendeA(fecha));
    }

    @Test
    @DisplayName("El horario se muestra como «de lunes a viernes, de 07:00 a 19:00»")
    void horarioParaMostrar() {
        assertEquals("de lunes a viernes, de 07:00 a 19:00", biblioteca.getHorario());
    }
}
