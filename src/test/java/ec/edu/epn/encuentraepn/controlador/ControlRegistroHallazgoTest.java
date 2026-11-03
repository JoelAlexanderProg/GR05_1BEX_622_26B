package ec.edu.epn.encuentraepn.controlador;

import ec.edu.epn.encuentraepn.dao.AvisoDAO;
import ec.edu.epn.encuentraepn.dao.CategoriaDAO;
import ec.edu.epn.encuentraepn.dao.ReclamacionDAO;
import ec.edu.epn.encuentraepn.dao.ReporteDAO;
import ec.edu.epn.encuentraepn.modelo.Categoria;
import ec.edu.epn.encuentraepn.modelo.DatosReporte;
import ec.edu.epn.encuentraepn.modelo.Reporte;
import ec.edu.epn.encuentraepn.modelo.TipoReporte;
import ec.edu.epn.encuentraepn.modelo.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static ec.edu.epn.encuentraepn.DatosDePrueba.LATITUD;
import static ec.edu.epn.encuentraepn.DatosDePrueba.LONGITUD;
import static ec.edu.epn.encuentraepn.DatosDePrueba.reportePerdido;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * HU-15 Avisar al dueño de un objeto perdido que yo lo encontré. Prueba del registro con el acceso a datos simulado.
 */
@ExtendWith(MockitoExtension.class)
class ControlRegistroHallazgoTest {

    @Mock
    private ReporteDAO reporteDAO;

    @Mock
    private ReclamacionDAO reclamacionDAO;

    @Mock
    private CategoriaDAO categoriaDAO;

    @Mock
    private AvisoDAO avisoDAO;

    private final Categoria llaves = new Categoria("Llaves");
    private final UUID categoriaId = UUID.randomUUID();
    private final UUID perdidoId = UUID.randomUUID();
    private final Usuario diego = new Usuario("Diego Mora", "diego.mora@epn.edu.ec");
    private ControlRegistroReporte control;

    @BeforeEach
    void preparar() {
        control = new ControlRegistroReporte(reporteDAO, reclamacionDAO, categoriaDAO, avisoDAO);
        when(categoriaDAO.buscarPorId(categoriaId)).thenReturn(llaves);
    }

    private DatosReporte llavesEncontradas() {
        return new DatosReporte(TipoReporte.ENCONTRADO, categoriaId, "Llaves con llavero rojo", LocalDate.now(),
                LATITUD, LONGITUD, List.of("¿De qué color es el llavero?"));
    }

    @Test
    @DisplayName("El objeto encontrado se guarda vinculado con el reporte de pérdida de su dueño")
    void registraElHallazgoVinculado() {
        Reporte perdido = reportePerdido(llaves);
        when(reporteDAO.buscarPorId(perdidoId)).thenReturn(perdido);

        Reporte encontrado = control.registrarReporte(diego, llavesEncontradas(), perdidoId);

        assertEquals(perdido, encontrado.getReportePerdido());
        verify(reporteDAO).guardar(encontrado);
    }

    @Test
    @DisplayName("Si el reporte de pérdida ya no está activo, no se registra nada")
    void noRegistraSiElReportePerdidoNoEstaActivo() {
        Reporte perdido = reportePerdido(llaves);
        perdido.retirar();
        when(reporteDAO.buscarPorId(perdidoId)).thenReturn(perdido);

        OperacionInvalidaException error = assertThrows(OperacionInvalidaException.class,
                () -> control.registrarReporte(diego, llavesEncontradas(), perdidoId));

        assertEquals("Este reporte de pérdida ya no está activo.", error.getMessage());
        verify(reporteDAO, never()).guardar(any());
    }
}
