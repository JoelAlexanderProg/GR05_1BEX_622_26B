package ec.edu.epn.encuentraepn.controlador;

import ec.edu.epn.encuentraepn.dao.ReclamacionDAO;
import ec.edu.epn.encuentraepn.dao.ReporteDAO;
import ec.edu.epn.encuentraepn.modelo.AcuerdoEntrega;
import ec.edu.epn.encuentraepn.modelo.Reclamacion;
import ec.edu.epn.encuentraepn.modelo.Reporte;
import ec.edu.epn.encuentraepn.modelo.RespuestaVerificacion;
import ec.edu.epn.encuentraepn.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * CU-04 Gestionar devolución del objeto.
 * Diagrama de clases: «control» ControlDevoluciones. Vistas: panelDevolucion.jsp («boundary» PanelDevolucion)
 * y vistaAcuerdoEntrega.jsp («boundary» VistaAcuerdoEntrega). Diagrama de secuencia: SEC-04.
 */
@WebServlet("/devoluciones")
public class ControlDevoluciones extends ControlBase {

    private final ReporteDAO reporteDAO = new ReporteDAO();
    private final ReclamacionDAO reclamacionDAO;

    public ControlDevoluciones() {
        this(new ReclamacionDAO());
    }

    /**
     * Recibe el acceso a datos de las reclamaciones, para que una prueba pueda reemplazarlo por un objeto simulado.
     */
    ControlDevoluciones(ReclamacionDAO reclamacionDAO) {
        this.reclamacionDAO = reclamacionDAO;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario actor = actorIdentificado(request);
        if (request.getParameter("acuerdo") != null) {
            mostrarPropuesta(actor, request, response);
        } else if (request.getParameter("reclamacion") != null) {
            mostrarRespuestas(actor, request, response);
        } else {
            // SEC-04 · 1: abrir reclamaciones del reporte
            mostrarPanel(actor, leerId(request, "reporte"), request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario actor = actorIdentificado(request);
        UUID id = leerId(request, "id");
        String accion = request.getParameter("accion");
        if ("aceptar".equals(accion)) {
            // SEC-04 · 14-15: aceptarReclamacion(id) → aceptarReclamacion(actor, id)
            aceptarReclamacion(actor, id);
            // SEC-04 · 20: aceptación registrada
            redirigir(request, response, "/devoluciones?reclamacion=" + id);
        } else if ("proponer".equals(accion)) {
            // SEC-04 · 21-22: proponerEntrega(datos) → proponerEntrega(actor, id, lugar, fecha)
            proponerEntrega(actor, id, request.getParameter("lugar"), leerFechaHora(request.getParameter("fecha")));
            // SEC-04 · 24: propuesta guardada
            redirigir(request, response, "/devoluciones?reclamacion=" + id);
        } else if ("confirmarAcuerdo".equals(accion)) {
            // SEC-04 · 32-33: confirmarAcuerdo(id) → confirmarAcuerdo(actor, id)
            confirmarAcuerdo(actor, id);
            // SEC-04 · 36-37: acuerdo confirmado → confirmación visible
            redirigir(request, response, "/devoluciones?acuerdo=" + id);
        } else if ("confirmarDevolucion".equals(accion)) {
            // SEC-04 · 38-39: confirmarDevolucion(id) → confirmarDevolucion(actor, id, fecha)
            confirmarDevolucion(actor, id, ahora());
            // SEC-04 · 44-45: devolución registrada → confirmación de devolución
            redirigir(request, response, "/devoluciones?reclamacion=" + id);
        } else if ("rechazar".equals(accion)) {
            // Incremento 3: rechazarReclamacion(actor, id, motivo); las reglas de la entidad se muestran como error
            try {
                rechazarReclamacion(actor, id, request.getParameter("motivo"));
            } catch (IllegalArgumentException | IllegalStateException e) {
                throw new OperacionInvalidaException(e.getMessage());
            }
            redirigir(request, response, "/devoluciones?reclamacion=" + id);
        } else {
            throw new OperacionInvalidaException("La acción solicitada no existe.");
        }
    }

    public List<Reclamacion> listarPendientes(Usuario actor, UUID reporteId) {
        // Nota de SEC-04: el control verifica que el actor sea el autor del reporte
        Reporte reporte = buscarReportePropio(actor, reporteId);
        // SEC-04 · 3-4: consultar pendientes del reporte [lectura] → reclamaciones pendientes
        return reclamacionDAO.listarPendientes(reporte);
    }

    public List<RespuestaVerificacion> consultarRespuestas(Usuario actor, UUID reclamacionId) {
        Reclamacion reclamacion = buscarReclamacionRecibida(actor, reclamacionId);
        // SEC-04 · 9-10: consultar respuestas y preguntas asociadas [lectura] → preguntas y respuestas privadas
        return reclamacion.getRespuestas();
    }

    public void aceptarReclamacion(Usuario actor, UUID id) {
        // SEC-04 · 15: obtener la reclamación recibida por el actor
        Reclamacion reclamacion = buscarReclamacionRecibida(actor, id);
        Reporte reporte = reclamacion.getObjeto();
        // Nota de SEC-04: el control comprueba que no haya otra reclamación aceptada
        if (!reclamacion.estaPendiente()
                || !reporte.estaActivo()
                || reclamacionDAO.buscarEnCurso(reporte) != null) {
            throw new OperacionInvalidaException(
                    "La reclamación debe estar pendiente, corresponder a un reporte activo y no tener otra reclamación aceptada.");
        }
        // SEC-04 · 16-17: aceptar() → ACEPTADA
        reclamacion.aceptar();
        // SEC-04 · 18-19: reservar() → RESERVADO
        reporte.reservar();
        // SEC-04 · 19a: la aceptación y la reserva se guardan en una sola transacción
        reclamacionDAO.actualizar(reclamacion);
    }

    /**
     * Incremento 3: quien registró el objeto rechaza una reclamación pendiente e indica el motivo.
     */
    public void rechazarReclamacion(Usuario actor, UUID id, String motivo) {
        Reclamacion reclamacion = buscarReclamacionRecibida(actor, id);
        reclamacion.rechazar(motivo);
        reclamacionDAO.actualizar(reclamacion);
    }

    public AcuerdoEntrega proponerEntrega(Usuario actor, UUID id, String lugar, LocalDateTime fecha) {
        Reclamacion reclamacion = buscarReclamacionRecibida(actor, id);
        if (!reclamacion.estaAceptada() || reclamacion.getAcuerdo() != null) {
            throw new OperacionInvalidaException("Solo se puede proponer una entrega para una reclamación aceptada sin propuesta.");
        }
        if (lugar == null || lugar.isBlank() || fecha == null || !fecha.isAfter(ahora())) {
            throw new OperacionInvalidaException("Indique el lugar y una fecha futura para la entrega.");
        }
        // SEC-04 · 23: «create» AcuerdoEntrega(reclamacion, lugar, fecha)
        AcuerdoEntrega acuerdo = new AcuerdoEntrega(reclamacion, lugar.trim(), fecha);
        reclamacion.registrarAcuerdo(acuerdo);
        reclamacionDAO.actualizar(reclamacion);
        return acuerdo;
    }

    public AcuerdoEntrega consultarAcuerdo(Usuario actor, UUID id) {
        // Nota de SEC-04: en cada operación se verifica el participante autorizado
        Reclamacion reclamacion = reclamacionDAO.buscarPorId(id);
        if (reclamacion == null
                || !(reclamacion.esReclamante(actor) || reclamacion.getObjeto().esAutor(actor))) {
            throw new OperacionInvalidaException("La reclamación indicada no existe.");
        }
        // SEC-04 · 27-28: consultar propuesta de la reclamación [lectura] → acuerdo
        AcuerdoEntrega acuerdo = reclamacion.getAcuerdo();
        if (acuerdo == null) {
            throw new OperacionInvalidaException("Todavía no hay una propuesta de entrega para esta reclamación.");
        }
        return acuerdo;
    }

    public void confirmarAcuerdo(Usuario actor, UUID id) {
        AcuerdoEntrega acuerdo = consultarAcuerdo(actor, id);
        Reclamacion reclamacion = acuerdo.getReclamacion();
        if (!reclamacion.esReclamante(actor) || acuerdo.estaConfirmado()) {
            throw new OperacionInvalidaException("Solo el reclamante puede confirmar un acuerdo pendiente.");
        }
        // SEC-04 · 34-35: confirmar(fecha) → fecha de confirmación registrada
        acuerdo.confirmar(ahora());
        reclamacionDAO.actualizar(reclamacion);
    }

    public void confirmarDevolucion(Usuario actor, UUID id, LocalDateTime fecha) {
        Reclamacion reclamacion = buscarReclamacionRecibida(actor, id);
        AcuerdoEntrega acuerdo = reclamacion.getAcuerdo();
        // Nota de SEC-04: el control comprueba acuerdo confirmado y reclamación aceptada
        if (!reclamacion.estaAceptada() || acuerdo == null || !acuerdo.estaConfirmado()) {
            throw new OperacionInvalidaException("La devolución requiere una reclamación aceptada y un acuerdo confirmado.");
        }
        // SEC-04 · 40-41: marcarEntregado(fecha) → ENTREGADO y fechaDevolucion
        reclamacion.getObjeto().marcarEntregado(fecha);
        // SEC-04 · 42-43: completar() → COMPLETADA
        reclamacion.completar();
        // Nota de SEC-04: registra ambos estados finales conjuntamente (una sola transacción)
        reclamacionDAO.actualizar(reclamacion);
    }

    private void mostrarPropuesta(Usuario actor, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // SEC-04 · 25: consultar propuesta (reclamante, en otra sesión)
        UUID id = leerId(request, "acuerdo");
        // SEC-04 · 26: consultarAcuerdo(actor, id)
        AcuerdoEntrega acuerdo = consultarAcuerdo(actor, id);
        // SEC-04 · 29-31: acuerdo autorizado → mostrarPropuesta(acuerdo) → lugar y fecha propuestos
        request.setAttribute("acuerdo", acuerdo);
        request.setAttribute("esReclamante", acuerdo.getReclamacion().esReclamante(actor));
        mostrar("vistaAcuerdoEntrega.jsp", request, response);
    }

    private void mostrarRespuestas(Usuario actor, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // SEC-04 · 7: seleccionar reclamación
        UUID reclamacionId = leerId(request, "reclamacion");
        // SEC-04 · 8: consultarRespuestas(actor, reclamacionId)
        List<RespuestaVerificacion> respuestas = consultarRespuestas(actor, reclamacionId);
        // SEC-04 · 11-13: respuestas → mostrarRespuestas(respuestas) → disponibles para revisión
        Reclamacion reclamacion = reclamacionDAO.buscarPorId(reclamacionId);
        request.setAttribute("reclamacion", reclamacion);
        request.setAttribute("respuestas", respuestas);
        mostrarPanel(actor, reclamacion.getObjeto().getId(), request, response);
    }

    private void mostrarPanel(Usuario actor, UUID reporteId, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // SEC-04 · 2: listarPendientes(actor, reporteId)
        List<Reclamacion> pendientes = listarPendientes(actor, reporteId);
        // SEC-04 · 5-6: lista → mostrarReclamaciones(lista)
        Reporte reporte = reporteDAO.buscarPorId(reporteId);
        request.setAttribute("reporte", reporte);
        request.setAttribute("pendientes", pendientes);
        request.setAttribute("enCurso", reclamacionDAO.buscarEnCurso(reporte));
        mostrar("panelDevolucion.jsp", request, response);
    }

    private Reporte buscarReportePropio(Usuario actor, UUID reporteId) {
        Reporte reporte = reporteDAO.buscarPorId(reporteId);
        if (reporte == null || !reporte.esAutor(actor)) {
            throw new OperacionInvalidaException("Solo quien registró el objeto puede gestionar su devolución.");
        }
        return reporte;
    }

    private Reclamacion buscarReclamacionRecibida(Usuario actor, UUID reclamacionId) {
        Reclamacion reclamacion = reclamacionDAO.buscarPorId(reclamacionId);
        if (reclamacion == null || !reclamacion.getObjeto().esAutor(actor)) {
            throw new OperacionInvalidaException("Solo quien registró el objeto puede gestionar su devolución.");
        }
        return reclamacion;
    }

    private LocalDateTime ahora() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    }

    private LocalDateTime leerFechaHora(String valor) {
        try {
            return LocalDateTime.parse(valor);
        } catch (DateTimeParseException | NullPointerException e) {
            return null;
        }
    }
}
