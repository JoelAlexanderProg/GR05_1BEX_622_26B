package ec.edu.epn.encuentraepn.controlador;

import ec.edu.epn.encuentraepn.dao.AvisoDAO;
import ec.edu.epn.encuentraepn.dao.PuntoEntregaDAO;
import ec.edu.epn.encuentraepn.dao.ReclamacionDAO;
import ec.edu.epn.encuentraepn.modelo.AcuerdoEntrega;
import ec.edu.epn.encuentraepn.modelo.PuntoEntrega;
import ec.edu.epn.encuentraepn.modelo.Reclamacion;
import ec.edu.epn.encuentraepn.modelo.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.UUID;

import static ec.edu.epn.encuentraepn.DatosDePrueba.reclamacionPendiente;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * HU-21 Elegir un punto de entrega oficial del campus. Prueba del control con el acceso a datos simulado.
 */
@ExtendWith(MockitoExtension.class)
class ControlPuntoEntregaTest {

    @Mock
    private ReclamacionDAO reclamacionDAO;

    @Mock
    private AvisoDAO avisoDAO;

    @Mock
    private PuntoEntregaDAO puntoEntregaDAO;

    private final Usuario bruno = new Usuario("Bruno Salas", "bruno.salas@epn.edu.ec");
    private final UUID id = UUID.randomUUID();
    private final UUID puntoId = UUID.randomUUID();
    private final PuntoEntrega biblioteca = new PuntoEntrega("Biblioteca Central",
            "Planta baja, mostrador de préstamo", LocalTime.of(7, 0), LocalTime.of(19, 0));
    /** El próximo lunes: un día en que el punto atiende. */
    private final LocalDateTime proximoLunes = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
    private Reclamacion reclamacion;
    private ControlDevoluciones control;

    @BeforeEach
    void preparar() {
        reclamacion = reclamacionPendiente(bruno);
        reclamacion.aceptar();
        when(reclamacionDAO.buscarPorId(id)).thenReturn(reclamacion);
        when(puntoEntregaDAO.buscarPorId(puntoId)).thenReturn(biblioteca);
        control = new ControlDevoluciones(reclamacionDAO, avisoDAO, puntoEntregaDAO);
    }

    @Test
    @DisplayName("La entrega se propone en el punto oficial cuando la fecha está dentro de su horario")
    void proponerEnUnPuntoOficial() {
        AcuerdoEntrega acuerdo = control.proponerEntrega(bruno, id, puntoId, null,
                proximoLunes.with(LocalTime.of(10, 0)));

        assertEquals(biblioteca, acuerdo.getPunto());
        assertEquals("Biblioteca Central", acuerdo.getLugar());
        verify(reclamacionDAO).actualizar(reclamacion);
    }

    @Test
    @DisplayName("Fuera del horario del punto la propuesta no se guarda y el mensaje muestra el horario")
    void fueraDelHorarioNoSeGuarda() {
        OperacionInvalidaException error = assertThrows(OperacionInvalidaException.class,
                () -> control.proponerEntrega(bruno, id, puntoId, null, proximoLunes.with(LocalTime.of(21, 0))));

        assertEquals("«Biblioteca Central» atiende de lunes a viernes, de 07:00 a 19:00. "
                + "Elija una fecha dentro de ese horario.", error.getMessage());
        assertNull(reclamacion.getAcuerdo());
        verify(reclamacionDAO, never()).actualizar(any(Reclamacion.class));
    }
}
