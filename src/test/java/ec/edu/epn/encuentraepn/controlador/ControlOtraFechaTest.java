package ec.edu.epn.encuentraepn.controlador;

import ec.edu.epn.encuentraepn.dao.AvisoDAO;
import ec.edu.epn.encuentraepn.dao.ReclamacionDAO;
import ec.edu.epn.encuentraepn.modelo.AcuerdoEntrega;
import ec.edu.epn.encuentraepn.modelo.Reclamacion;
import ec.edu.epn.encuentraepn.modelo.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static ec.edu.epn.encuentraepn.DatosDePrueba.reclamacionPendiente;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * HU-20 Pedir otra fecha para la entrega. Prueba del control con el acceso a datos simulado.
 */
@ExtendWith(MockitoExtension.class)
class ControlOtraFechaTest {

    @Mock
    private ReclamacionDAO reclamacionDAO;

    @Mock
    private AvisoDAO avisoDAO;

    private final Usuario bruno = new Usuario("Bruno Salas", "bruno.salas@epn.edu.ec");
    private final UUID id = UUID.randomUUID();
    private final LocalDateTime nuevaFecha = LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.HOURS);
    private Reclamacion reclamacion;
    private ControlDevoluciones control;

    @BeforeEach
    void preparar() {
        reclamacion = reclamacionPendiente(bruno);
        reclamacion.aceptar();
        reclamacion.registrarAcuerdo(new AcuerdoEntrega(reclamacion, "Biblioteca Central",
                LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.HOURS)));
        when(reclamacionDAO.buscarPorId(id)).thenReturn(reclamacion);
        control = new ControlDevoluciones(reclamacionDAO, avisoDAO);
    }

    @Test
    @DisplayName("El reclamante pide otra fecha y quien encontró el objeto la acepta: el acuerdo queda confirmado")
    void pedirYAceptarOtraFecha() {
        control.solicitarOtraFecha(reclamacion.getReclamante(), id, nuevaFecha);
        control.aceptarOtraFecha(bruno, id);

        assertEquals(nuevaFecha, reclamacion.getAcuerdo().getFechaPropuesta());
        assertTrue(reclamacion.getAcuerdo().estaConfirmado());
        verify(reclamacionDAO, times(2)).actualizar(reclamacion);
    }

    @Test
    @DisplayName("Quien encontró el objeto no puede pedir otra fecha en nombre del reclamante")
    void soloElReclamantePideOtraFecha() {
        assertThrows(OperacionInvalidaException.class, () -> control.solicitarOtraFecha(bruno, id, nuevaFecha));
    }
}
