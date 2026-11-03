package ec.edu.epn.encuentraepn.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static ec.edu.epn.encuentraepn.DatosDePrueba.encontradoA;
import static ec.edu.epn.encuentraepn.DatosDePrueba.reportePerdido;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * HU-14 Ver los objetos encontrados que coinciden con mi objeto perdido: regla de coincidencia de Reporte.
 */
class CoincidenciaTest {

    private final Categoria calculadoras = new Categoria("Calculadoras");
    private final Categoria llaves = new Categoria("Llaves");

    /**
     * Prueba con parámetros: cada fila es un objeto encontrado y si debe coincidir con la calculadora perdida
     * el 1 de octubre.
     */
    @ParameterizedTest(name = "{4}")
    @CsvSource({
            "true,  50,  2,  true,  misma categoría a 50 m y 2 días después",
            "true,  190, 0,  true,  misma categoría a 190 m el mismo día",
            "true,  50,  30, true,  misma categoría el día 30",
            "false, 50,  2,  false, otra categoría",
            "true,  300, 2,  false, a 300 m",
            "true,  50,  31, false, 31 días después",
            "true,  50,  -1, false, encontrado un día antes de la pérdida"
    })
    @DisplayName("Un objeto encontrado coincide si es de la misma categoría, a 200 m o menos y hasta 30 días después")
    void reglaDeCoincidencia(boolean mismaCategoria, double metros, int dias, boolean coincide, String caso) {
        Reporte perdido = reportePerdido(calculadoras);
        Reporte encontrado = encontradoA(mismaCategoria ? calculadoras : llaves, metros, dias);

        assertEquals(coincide, perdido.coincideCon(encontrado));
    }

    @Test
    @DisplayName("Un objeto encontrado que ya está reservado no coincide")
    void unObjetoReservadoNoCoincide() {
        Reporte perdido = reportePerdido(calculadoras);
        Reporte encontrado = encontradoA(calculadoras, 50, 2);
        encontrado.reservar();

        assertFalse(perdido.coincideCon(encontrado));
    }
}
