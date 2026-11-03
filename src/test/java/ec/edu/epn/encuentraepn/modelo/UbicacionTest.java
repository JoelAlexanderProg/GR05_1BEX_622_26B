package ec.edu.epn.encuentraepn.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * HU-14 Ver los objetos encontrados que coinciden con mi objeto perdido: distancia entre dos lugares del campus.
 */
class UbicacionTest {

    @Test
    @DisplayName("Una milésima de grado de longitud en el campus equivale a unos 111 metros")
    void distanciaDeUnaMilesimaDeGrado() {
        Ubicacion biblioteca = new Ubicacion(-0.2100, -78.4900, Campus.ZONA_CENTRO);
        Ubicacion cafeteria = new Ubicacion(-0.2100, -78.4890, Campus.ZONA_CENTRO);

        assertEquals(111.2, biblioteca.distanciaA(cafeteria), 0.5);
    }

    @Test
    @DisplayName("La distancia es la misma en los dos sentidos y es cero hasta el mismo lugar")
    void distanciaSimetricaYCeroAlMismoLugar() {
        Ubicacion norte = new Ubicacion(-0.2075, -78.4880, Campus.ZONA_NORTE);
        Ubicacion sur = new Ubicacion(-0.2135, -78.4920, Campus.ZONA_SUR);

        assertEquals(norte.distanciaA(sur), sur.distanciaA(norte), 0.001);
        assertEquals(0, norte.distanciaA(norte), 0.001);
    }
}
