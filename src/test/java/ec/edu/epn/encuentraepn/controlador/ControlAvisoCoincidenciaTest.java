package ec.edu.epn.encuentraepn.controlador;

import ec.edu.epn.encuentraepn.dao.AvisoDAO;
import ec.edu.epn.encuentraepn.dao.CategoriaDAO;
import ec.edu.epn.encuentraepn.dao.ReclamacionDAO;
import ec.edu.epn.encuentraepn.dao.ReporteDAO;
import ec.edu.epn.encuentraepn.modelo.Aviso;
import ec.edu.epn.encuentraepn.modelo.Categoria;
import ec.edu.epn.encuentraepn.modelo.Reporte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static ec.edu.epn.encuentraepn.DatosDePrueba.encontradoA;
import static ec.edu.epn.encuentraepn.DatosDePrueba.reportePerdido;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * HU-16 Recibir un aviso cuando se reporta un objeto que coincide con mi pérdida. Prueba del control con el
 * acceso a datos simulado.
 */
@ExtendWith(MockitoExtension.class)
class ControlAvisoCoincidenciaTest {

    @Mock
    private ReporteDAO reporteDAO;

    @Mock
    private ReclamacionDAO reclamacionDAO;

    @Mock
    private CategoriaDAO categoriaDAO;

    @Mock
    private AvisoDAO avisoDAO;

    private final Categoria calculadoras = new Categoria("Calculadoras");
    private Reporte perdido;
    private ControlRegistroReporte control;

    @BeforeEach
    void preparar() {
        perdido = reportePerdido(calculadoras);
        when(reporteDAO.listarPerdidosActivos(calculadoras)).thenReturn(List.of(perdido));
        control = new ControlRegistroReporte(reporteDAO, reclamacionDAO, categoriaDAO, avisoDAO);
    }

    @Test
    @DisplayName("Al publicar un objeto encontrado que coincide, el dueño del objeto perdido recibe un aviso")
    void elDuenoRecibeUnAviso() {
        control.avisarCoincidencias(encontradoA(calculadoras, 50, 2));

        ArgumentCaptor<Aviso> aviso = ArgumentCaptor.forClass(Aviso.class);
        verify(avisoDAO).guardar(aviso.capture());
        assertEquals(perdido.getAutor(), aviso.getValue().getDestinatario());
    }

    @Test
    @DisplayName("Un objeto encontrado que no coincide no genera avisos")
    void unObjetoQueNoCoincideNoAvisa() {
        control.avisarCoincidencias(encontradoA(calculadoras, 300, 2));

        verify(avisoDAO, never()).guardar(any());
    }
}
