package ec.edu.epn.encuentraepn.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static ec.edu.epn.encuentraepn.DatosDePrueba.reporteActivo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Incremento 4: pruebas de las reglas nuevas de la entidad Reporte
 * (HU-11 Retirar un reporte propio y HU-12 Buscar reportes por palabra clave).
 */
class ReporteTest {

    @Test
    @DisplayName("Retirar un reporte activo lo deja en estado RETIRADO")
    void retirarUnReporteActivoLoDejaRetirado() {
        Reporte reporte = reporteActivo();

        reporte.retirar();

        assertEquals(EstadoReporte.RETIRADO, reporte.getEstado());
    }

    @Test
    @DisplayName("Un reporte reservado no se puede retirar y sigue RESERVADO")
    void unReporteReservadoNoSePuedeRetirar() {
        Reporte reporte = reporteActivo();
        reporte.reservar();

        assertThrows(IllegalStateException.class, reporte::retirar);
        assertEquals(EstadoReporte.RESERVADO, reporte.getEstado());
    }

    @Test
    @DisplayName("Una palabra que está en la descripción coincide y otra que no está, no")
    void unaPalabraDeLaDescripcionCoincide() {
        Reporte reporte = reporteActivo();   // «Calculadora científica negra»

        assertTrue(reporte.contienePalabra("negra"));
        assertFalse(reporte.contienePalabra("paraguas"));
    }

    /**
     * Prueba con parámetros: cada fila es una forma de escribir la palabra buscada.
     * Las comillas simples conservan los espacios.
     */
    @ParameterizedTest(name = "{2}: «{0}» → {1}")
    @CsvSource({
            "cámara,       true,  minúsculas",
            "CAMARA,       true,  mayúsculas y sin tilde",
            "camara,       true,  sin tilde",
            "'  Cámara  ', true,  espacios sobrantes",
            "paraguas,     false, palabra que no está"
    })
    @DisplayName("La búsqueda no distingue mayúsculas, tildes ni espacios sobrantes")
    void laBusquedaNoDistingueMayusculasTildesNiEspacios(String palabra, boolean coincide, String caso) {
        Reporte reporte = reporteActivo("Cámara Canon en estuche gris");

        assertEquals(coincide, reporte.contienePalabra(palabra));
    }
}
