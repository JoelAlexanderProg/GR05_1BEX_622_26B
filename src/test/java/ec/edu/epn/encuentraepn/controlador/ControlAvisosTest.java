package ec.edu.epn.encuentraepn.controlador;

import ec.edu.epn.encuentraepn.dao.AvisoDAO;
import ec.edu.epn.encuentraepn.modelo.Aviso;
import ec.edu.epn.encuentraepn.modelo.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

/**
 * HU-17 Ver cuántos avisos tengo sin leer: al abrir la bandeja, los avisos nuevos quedan leídos.
 */
@ExtendWith(MockitoExtension.class)
class ControlAvisosTest {

    @Mock
    private AvisoDAO avisoDAO;

    @Test
    @DisplayName("Abrir la bandeja marca como leídos los avisos nuevos y los devuelve para resaltarlos")
    void abrirLaBandejaMarcaLosNuevosComoLeidos() {
        Usuario andrea = new Usuario("Andrea Paz", "andrea.paz@epn.edu.ec");
        Aviso leido = new Aviso(andrea, "Aviso de ayer", "/inicio");
        leido.marcarLeido();
        Aviso nuevo1 = new Aviso(andrea, "Primer aviso nuevo", "/inicio");
        Aviso nuevo2 = new Aviso(andrea, "Segundo aviso nuevo", "/inicio");
        ControlAvisos control = new ControlAvisos(avisoDAO);

        List<Aviso> nuevos = control.marcarComoLeidos(List.of(nuevo2, leido, nuevo1));

        assertEquals(List.of(nuevo2, nuevo1), nuevos);
        assertTrue(nuevo1.isLeido() && nuevo2.isLeido());
        verify(avisoDAO).marcarLeidos(nuevos);
    }
}
