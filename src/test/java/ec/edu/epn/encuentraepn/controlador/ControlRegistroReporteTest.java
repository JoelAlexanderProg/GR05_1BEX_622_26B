package ec.edu.epn.encuentraepn.controlador;

import ec.edu.epn.encuentraepn.dao.ReclamacionDAO;
import ec.edu.epn.encuentraepn.dao.ReporteDAO;
import ec.edu.epn.encuentraepn.modelo.EstadoReclamacion;
import ec.edu.epn.encuentraepn.modelo.EstadoReporte;
import ec.edu.epn.encuentraepn.modelo.Reclamacion;
import ec.edu.epn.encuentraepn.modelo.Reporte;
import ec.edu.epn.encuentraepn.modelo.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static ec.edu.epn.encuentraepn.DatosDePrueba.reclamacionPendiente;
import static ec.edu.epn.encuentraepn.controlador.ControlRegistroReporte.MOTIVO_RETIRO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * HU-11 Retirar un reporte propio. Prueba del control con el acceso a datos simulado.
 */
@ExtendWith(MockitoExtension.class)
class ControlRegistroReporteTest {

    @Mock
    private ReporteDAO reporteDAO;

    @Mock
    private ReclamacionDAO reclamacionDAO;

    @Test
    @DisplayName("Retirar un reporte rechaza sus reclamaciones pendientes y lo guarda con el DAO, sin usar la base de datos")
    void retirarReporteRechazaLasPendientesYLoGuarda() {
        Usuario encontrante = new Usuario("Bruno Salas", "bruno.salas@epn.edu.ec");
        Reclamacion pendiente = reclamacionPendiente(encontrante);
        Reporte reporte = pendiente.getObjeto();
        reporte.publicar();
        UUID id = UUID.randomUUID();
        when(reporteDAO.buscarPorId(id)).thenReturn(reporte);
        when(reclamacionDAO.listarPendientes(reporte)).thenReturn(List.of(pendiente));
        ControlRegistroReporte control = new ControlRegistroReporte(reporteDAO, reclamacionDAO);

        control.retirarReporte(encontrante, id);

        verify(reporteDAO).actualizar(reporte);
        assertEquals(EstadoReporte.RETIRADO, reporte.getEstado());
        assertEquals(EstadoReclamacion.RECHAZADA, pendiente.getEstado());
        assertEquals(MOTIVO_RETIRO, pendiente.getMotivoRechazo());
    }
}
