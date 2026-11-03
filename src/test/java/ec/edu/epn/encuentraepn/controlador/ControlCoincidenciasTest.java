package ec.edu.epn.encuentraepn.controlador;

import ec.edu.epn.encuentraepn.dao.ReporteDAO;
import ec.edu.epn.encuentraepn.modelo.Categoria;
import ec.edu.epn.encuentraepn.modelo.Coincidencia;
import ec.edu.epn.encuentraepn.modelo.Reporte;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static ec.edu.epn.encuentraepn.DatosDePrueba.encontradoA;
import static ec.edu.epn.encuentraepn.DatosDePrueba.reportePerdido;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * HU-14 Ver los objetos encontrados que coinciden con mi objeto perdido. Prueba del control con el acceso a
 * datos simulado.
 */
@ExtendWith(MockitoExtension.class)
class ControlCoincidenciasTest {

    @Mock
    private ReporteDAO reporteDAO;

    private final Categoria calculadoras = new Categoria("Calculadoras");

    @Test
    @DisplayName("Las coincidencias descartan los objetos lejanos y van de la más cercana a la más lejana")
    void coincidenciasOrdenadasPorDistancia() {
        Reporte perdido = reportePerdido(calculadoras);
        Reporte a150 = encontradoA(calculadoras, 150, 1);
        Reporte a40 = encontradoA(calculadoras, 40, 3);
        Reporte a500 = encontradoA(calculadoras, 500, 1);
        UUID id = UUID.randomUUID();
        when(reporteDAO.buscarPorId(id)).thenReturn(perdido);
        when(reporteDAO.listarEncontradosActivos(calculadoras)).thenReturn(List.of(a150, a500, a40));
        ControlCoincidencias control = new ControlCoincidencias(reporteDAO);

        List<Coincidencia> coincidencias = control.buscarCoincidencias(perdido.getAutor(), id);

        assertEquals(List.of(a40, a150), coincidencias.stream().map(Coincidencia::encontrado).toList());
        assertEquals(40, coincidencias.get(0).metros());
    }

    @Test
    @DisplayName("Solo quien registró el reporte puede ver sus coincidencias")
    void otroUsuarioNoVeLasCoincidencias() {
        Reporte perdido = reportePerdido(calculadoras);
        Reporte encontrado = encontradoA(calculadoras, 40, 1);
        UUID id = UUID.randomUUID();
        when(reporteDAO.buscarPorId(id)).thenReturn(perdido);
        ControlCoincidencias control = new ControlCoincidencias(reporteDAO);

        assertThrows(OperacionInvalidaException.class,
                () -> control.buscarCoincidencias(encontrado.getAutor(), id));
    }
}
