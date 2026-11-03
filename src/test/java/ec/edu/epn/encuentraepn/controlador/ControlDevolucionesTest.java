package ec.edu.epn.encuentraepn.controlador;

import ec.edu.epn.encuentraepn.dao.ReclamacionDAO;
import ec.edu.epn.encuentraepn.modelo.EstadoReclamacion;
import ec.edu.epn.encuentraepn.modelo.Reclamacion;
import ec.edu.epn.encuentraepn.modelo.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static ec.edu.epn.encuentraepn.DatosDePrueba.MOTIVO;
import static ec.edu.epn.encuentraepn.DatosDePrueba.reclamacionPendiente;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Incremento 3: rechazar una reclamación. Prueba del control con el acceso a datos simulado.
 */
@ExtendWith(MockitoExtension.class)
class ControlDevolucionesTest {

    @Mock
    private ReclamacionDAO reclamacionDAO;

    @Test
    @DisplayName("Rechazar una reclamación la guarda con el DAO, sin usar la base de datos")
    void rechazarReclamacionLaGuardaConElDAO() {
        Usuario encontrante = new Usuario("Bruno Salas", "bruno.salas@epn.edu.ec");
        Reclamacion reclamacion = reclamacionPendiente(encontrante);
        UUID id = UUID.randomUUID();
        when(reclamacionDAO.buscarPorId(id)).thenReturn(reclamacion);
        ControlDevoluciones control = new ControlDevoluciones(reclamacionDAO);

        control.rechazarReclamacion(encontrante, id, MOTIVO);

        verify(reclamacionDAO).actualizar(reclamacion);
        assertEquals(EstadoReclamacion.RECHAZADA, reclamacion.getEstado());
    }
}
