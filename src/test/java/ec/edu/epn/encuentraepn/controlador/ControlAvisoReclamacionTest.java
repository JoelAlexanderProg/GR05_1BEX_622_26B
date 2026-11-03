package ec.edu.epn.encuentraepn.controlador;

import ec.edu.epn.encuentraepn.dao.AvisoDAO;
import ec.edu.epn.encuentraepn.dao.ReclamacionDAO;
import ec.edu.epn.encuentraepn.modelo.Aviso;
import ec.edu.epn.encuentraepn.modelo.Reclamacion;
import ec.edu.epn.encuentraepn.modelo.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static ec.edu.epn.encuentraepn.DatosDePrueba.MOTIVO;
import static ec.edu.epn.encuentraepn.DatosDePrueba.reclamacionPendiente;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * HU-18 Recibir avisos cuando cambia el estado de mi reclamación. Prueba del control con el acceso a datos simulado.
 */
@ExtendWith(MockitoExtension.class)
class ControlAvisoReclamacionTest {

    @Mock
    private ReclamacionDAO reclamacionDAO;

    @Mock
    private AvisoDAO avisoDAO;

    private final Usuario bruno = new Usuario("Bruno Salas", "bruno.salas@epn.edu.ec");
    private final UUID id = UUID.randomUUID();
    private Reclamacion reclamacion;
    private ControlDevoluciones control;

    @BeforeEach
    void preparar() {
        reclamacion = reclamacionPendiente(bruno);
        reclamacion.getObjeto().publicar();
        when(reclamacionDAO.buscarPorId(id)).thenReturn(reclamacion);
        control = new ControlDevoluciones(reclamacionDAO, avisoDAO);
    }

    private Aviso avisoGuardado() {
        ArgumentCaptor<Aviso> aviso = ArgumentCaptor.forClass(Aviso.class);
        verify(avisoDAO).guardar(aviso.capture());
        assertEquals(reclamacion.getReclamante(), aviso.getValue().getDestinatario());
        return aviso.getValue();
    }

    @Test
    @DisplayName("Al aceptar la reclamación, el reclamante recibe el aviso de que fue aceptada")
    void avisoAlAceptar() {
        control.aceptarReclamacion(bruno, id);

        assertTrue(avisoGuardado().getTexto().contains("fue aceptada"));
    }

    @Test
    @DisplayName("Al rechazar la reclamación, el aviso incluye el motivo")
    void avisoAlRechazar() {
        control.rechazarReclamacion(bruno, id, MOTIVO);

        assertTrue(avisoGuardado().getTexto().contains("Motivo: " + MOTIVO));
    }

    @Test
    @DisplayName("Al proponer la entrega, el aviso incluye el lugar y la fecha")
    void avisoAlProponerLaEntrega() {
        reclamacion.aceptar();
        LocalDateTime manana = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);

        control.proponerEntrega(bruno, id, "Biblioteca Central", manana);

        assertTrue(avisoGuardado().getTexto().contains("en Biblioteca Central el "));
    }
}
