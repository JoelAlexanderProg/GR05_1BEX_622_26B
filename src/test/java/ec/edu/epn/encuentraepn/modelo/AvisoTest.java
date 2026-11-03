package ec.edu.epn.encuentraepn.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static ec.edu.epn.encuentraepn.DatosDePrueba.encontradoA;
import static ec.edu.epn.encuentraepn.DatosDePrueba.reportePerdido;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * HU-16 Recibir un aviso cuando se reporta un objeto que coincide con mi pérdida: contenido del aviso.
 */
class AvisoTest {

    private final Categoria calculadoras = new Categoria("Calculadoras");

    @Test
    @DisplayName("El aviso de coincidencia es para el dueño del objeto perdido y nombra los dos objetos y la distancia")
    void avisoDeCoincidencia() {
        Reporte perdido = reportePerdido(calculadoras);
        Reporte encontrado = encontradoA(calculadoras, 50, 2);

        Aviso aviso = Aviso.coincidencia(perdido, encontrado);

        assertEquals(perdido.getAutor(), aviso.getDestinatario());
        assertTrue(aviso.getTexto().contains("«Calculadora Casio gris»"));
        assertTrue(aviso.getTexto().contains("«Calculadora encontrada», a 50 m"));
        assertTrue(aviso.getEnlace().startsWith("/reportes/detalle?id="));
        assertFalse(aviso.isLeido());
    }

    @Test
    @DisplayName("Un aviso nuevo queda leído cuando el usuario lo ve")
    void marcarUnAvisoComoLeido() {
        Aviso aviso = Aviso.coincidencia(reportePerdido(calculadoras), encontradoA(calculadoras, 50, 2));

        aviso.marcarLeido();

        assertTrue(aviso.isLeido());
    }
}
