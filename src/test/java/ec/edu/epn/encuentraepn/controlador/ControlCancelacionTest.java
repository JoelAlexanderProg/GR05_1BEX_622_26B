package ec.edu.epn.encuentraepn.controlador;

import ec.edu.epn.encuentraepn.dao.AvisoDAO;
import ec.edu.epn.encuentraepn.dao.ReclamacionDAO;
import ec.edu.epn.encuentraepn.modelo.AcuerdoEntrega;
import ec.edu.epn.encuentraepn.modelo.EstadoReclamacion;
import ec.edu.epn.encuentraepn.modelo.EstadoReporte;
import ec.edu.epn.encuentraepn.modelo.Reclamacion;
import ec.edu.epn.encuentraepn.modelo.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static ec.edu.epn.encuentraepn.DatosDePrueba.reclamacionPendiente;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * HU-19 Cancelar una entrega que no se realizó. Prueba del control con el acceso a datos simulado.
 */
@ExtendWith(MockitoExtension.class)
class ControlCancelacionTest {

    @Mock
    private ReclamacionDAO reclamacionDAO;

    @Mock
    private AvisoDAO avisoDAO;

    private final Usuario bruno = new Usuario("Bruno Salas", "bruno.salas@epn.edu.ec");
    private final UUID id = UUID.randomUUID();
    private Reclamacion reclamacion;
    private ControlDevoluciones control;

    /** Reclamación aceptada con su acuerdo de entrega: el objeto está RESERVADO. */
    @BeforeEach
    void preparar() {
        reclamacion = reclamacionPendiente(bruno);
        reclamacion.getObjeto().publicar();
        reclamacion.aceptar();
        reclamacion.getObjeto().reservar();
        reclamacion.registrarAcuerdo(new AcuerdoEntrega(reclamacion, "Biblioteca Central", LocalDateTime.now()));
        when(reclamacionDAO.buscarPorId(id)).thenReturn(reclamacion);
        control = new ControlDevoluciones(reclamacionDAO, avisoDAO);
    }

    @Test
    @DisplayName("Cancelar la entrega deja la reclamación CANCELADA, libera el objeto y lo guarda con el DAO")
    void cancelarLaEntregaLiberaElObjeto() {
        control.cancelarEntrega(bruno, id, "No asistió a la entrega");

        assertEquals(EstadoReclamacion.CANCELADA, reclamacion.getEstado());
        assertEquals(EstadoReporte.ACTIVO, reclamacion.getObjeto().getEstado());
        verify(reclamacionDAO).actualizar(reclamacion);
    }

    @Test
    @DisplayName("Una entrega ya realizada no se puede cancelar")
    void unaEntregaRealizadaNoSeCancela() {
        reclamacion.getAcuerdo().confirmar(LocalDateTime.now());
        reclamacion.getObjeto().marcarEntregado(LocalDateTime.now());
        reclamacion.completar();

        OperacionInvalidaException error = assertThrows(OperacionInvalidaException.class,
                () -> control.cancelarEntrega(bruno, id, "No asistió a la entrega"));

        assertEquals("Solo se puede cancelar una entrega que todavía no se realizó.", error.getMessage());
        assertEquals(EstadoReclamacion.COMPLETADA, reclamacion.getEstado());
        verify(reclamacionDAO, never()).actualizar(any(Reclamacion.class));
    }
}
