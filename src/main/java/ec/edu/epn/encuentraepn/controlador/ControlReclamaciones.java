package ec.edu.epn.encuentraepn.controlador;

import ec.edu.epn.encuentraepn.dao.ReclamacionDAO;
import ec.edu.epn.encuentraepn.dao.ReporteDAO;
import ec.edu.epn.encuentraepn.modelo.DatosPublicos;
import ec.edu.epn.encuentraepn.modelo.PreguntaVerificacion;
import ec.edu.epn.encuentraepn.modelo.Reclamacion;
import ec.edu.epn.encuentraepn.modelo.Reporte;
import ec.edu.epn.encuentraepn.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * CU-03 Reclamar un objeto encontrado.
 * Diagrama de clases: «control» ControlReclamaciones. Vista: formularioReclamacion.jsp
 * («boundary» FormularioReclamacion). Diagrama de secuencia: SEC-03.
 */
@WebServlet("/reclamaciones/nueva")
public class ControlReclamaciones extends ControlBase {

    private static final String PREFIJO_RESPUESTA = "respuesta_";

    private final ReporteDAO reporteDAO = new ReporteDAO();
    private final ReclamacionDAO reclamacionDAO = new ReclamacionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario actor = actorIdentificado(request);
        if (request.getParameter("registrada") != null) {
            // SEC-03 · 17-18: mostrarConfirmacion(reclamacion) → reclamación registrada
            mostrarConfirmacion(actor, request, response);
            return;
        }
        // SEC-03 · 1: solicitarReclamacion(idReporte)
        UUID reporteId = leerId(request, "reporte");
        // SEC-03 · 2: obtenerPreguntas(actor, reporteId)
        List<PreguntaVerificacion> preguntas = obtenerPreguntas(actor, reporteId);
        // SEC-03 · 7-9: preguntas → mostrarPreguntas(preguntas) → preguntas disponibles
        mostrarPreguntas(preguntas, reporteId, request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario actor = actorIdentificado(request);
        UUID reporteId = leerId(request, "reporte");
        // SEC-03 · 10: enviarRespuestas(respuestas)
        Map<UUID, String> respuestas = leerRespuestas(request);
        try {
            // SEC-03 · 11: registrarReclamacion(actor, reporteId, respuestas)
            Reclamacion reclamacion = registrarReclamacion(actor, reporteId, respuestas);
            // SEC-03 · 16: reclamación pendiente
            redirigir(request, response, "/reclamaciones/nueva?registrada=" + reclamacion.getId());
        } catch (OperacionInvalidaException e) {
            request.setAttribute("error", e.getMessage());
            mostrarPreguntas(obtenerPreguntas(actor, reporteId), reporteId, request, response);
        }
    }

    public List<PreguntaVerificacion> obtenerPreguntas(Usuario actor, UUID reporteId) {
        // SEC-03 · 3-4: consultar tipo, estado y autor → ENCONTRADO, ACTIVO, autor distinto
        Reporte reporte = buscarReporteReclamable(actor, reporteId);
        // SEC-03 · 5-6: consultar preguntas del reporte → preguntas de verificación
        return reporte.getPreguntas();
    }

    public Reclamacion registrarReclamacion(Usuario actor, UUID reporteId, Map<UUID, String> respuestas) {
        // SEC-03 · 12-13: revalidar estado y autor → disponible y autor distinto
        Reporte reporte = buscarReporteReclamable(actor, reporteId);
        if (reclamacionDAO.existeDe(actor, reporte)) {
            throw new OperacionInvalidaException("Ya registró una reclamación sobre este objeto.");
        }

        // Nota de SEC-03: las respuestas corresponden a todas las preguntas del reporte, sin duplicados
        List<PreguntaVerificacion> preguntas = reporte.getPreguntas();
        boolean todasRespondidas = preguntas.stream()
                .allMatch(pregunta -> respuestas.containsKey(pregunta.getId()));
        if (!todasRespondidas || respuestas.size() != preguntas.size()) {
            throw new OperacionInvalidaException("Responda todas las preguntas de verificación.");
        }

        // SEC-03 · 14: «create» Reclamacion(actor, reporte, PENDIENTE)
        Reclamacion reclamacion = new Reclamacion(actor, reporte);
        // SEC-03 · 15: «create» respuestas privadas (reclamacion, pregunta, texto)
        for (PreguntaVerificacion pregunta : preguntas) {
            reclamacion.agregarRespuesta(pregunta, respuestas.get(pregunta.getId()));
        }

        // Nota de SEC-03: se conserva la reclamación junto con sus respuestas; no se acepta ni se entrega
        return reclamacionDAO.guardar(reclamacion);
    }

    private Reporte buscarReporteReclamable(Usuario actor, UUID reporteId) {
        Reporte reporte = reporteDAO.buscarPorId(reporteId);
        if (reporte == null) {
            throw new OperacionInvalidaException("El reporte indicado no existe.");
        }
        if (!reporte.esEncontrado()) {
            throw new OperacionInvalidaException("Solo se pueden reclamar objetos encontrados.");
        }
        if (!reporte.estaActivo()) {
            throw new OperacionInvalidaException("Este objeto ya no está disponible para reclamaciones.");
        }
        if (reporte.esAutor(actor)) {
            throw new OperacionInvalidaException("No puede reclamar un objeto que usted mismo registró.");
        }
        return reporte;
    }

    private void mostrarPreguntas(List<PreguntaVerificacion> preguntas, UUID reporteId,
                                  HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("preguntas", preguntas);
        request.setAttribute("detalle", new DatosPublicos(reporteDAO.buscarPorId(reporteId)));
        mostrar("formularioReclamacion.jsp", request, response);
    }

    private void mostrarConfirmacion(Usuario actor, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Reclamacion reclamacion = reclamacionDAO.buscarPorId(leerId(request, "registrada"));
        if (reclamacion == null || !reclamacion.esReclamante(actor)) {
            throw new OperacionInvalidaException("La reclamación indicada no existe.");
        }
        request.setAttribute("reclamacion", reclamacion);
        request.setAttribute("detalle", new DatosPublicos(reclamacion.getObjeto()));
        mostrar("formularioReclamacion.jsp", request, response);
    }

    /**
     * Lee los campos «respuesta_<id de la pregunta>» del formulario. Las respuestas en blanco se descartan.
     */
    private Map<UUID, String> leerRespuestas(HttpServletRequest request) {
        Map<UUID, String> respuestas = new HashMap<>();
        request.getParameterMap().forEach((nombre, valores) -> {
            if (nombre.startsWith(PREFIJO_RESPUESTA) && !valores[0].isBlank()) {
                try {
                    respuestas.put(UUID.fromString(nombre.substring(PREFIJO_RESPUESTA.length())), valores[0].trim());
                } catch (IllegalArgumentException e) {
                    // El campo no corresponde a una pregunta; se ignora
                }
            }
        });
        return respuestas;
    }
}
