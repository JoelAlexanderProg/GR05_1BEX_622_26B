package ec.edu.epn.encuentraepn.controlador;

import ec.edu.epn.encuentraepn.dao.CategoriaDAO;
import ec.edu.epn.encuentraepn.dao.ReclamacionDAO;
import ec.edu.epn.encuentraepn.dao.ReporteDAO;
import ec.edu.epn.encuentraepn.modelo.Campus;
import ec.edu.epn.encuentraepn.modelo.Categoria;
import ec.edu.epn.encuentraepn.modelo.DatosReporte;
import ec.edu.epn.encuentraepn.modelo.Reclamacion;
import ec.edu.epn.encuentraepn.modelo.Reporte;
import ec.edu.epn.encuentraepn.modelo.TipoReporte;
import ec.edu.epn.encuentraepn.modelo.Ubicacion;
import ec.edu.epn.encuentraepn.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * CU-01 Registrar objeto perdido o encontrado.
 * Diagrama de clases: «control» ControlRegistroReporte. Vista: formularioReporte.jsp («boundary» FormularioReporte).
 * Diagrama de secuencia: SEC-01. También permite a su autor retirar un reporte (HU-11).
 */
@WebServlet({"/reportes/nuevo", "/reportes/retirar"})
public class ControlRegistroReporte extends ControlBase {

    /** Motivo con el que se rechazan las reclamaciones pendientes de un reporte retirado (HU-11). */
    public static final String MOTIVO_RETIRO = "El reporte fue retirado por su autor.";

    private final ReporteDAO reporteDAO;
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final ReclamacionDAO reclamacionDAO;

    public ControlRegistroReporte() {
        this(new ReporteDAO(), new ReclamacionDAO());
    }

    /**
     * Recibe el acceso a datos de los reportes y de las reclamaciones, para que una prueba pueda
     * reemplazarlos por objetos simulados.
     */
    ControlRegistroReporte(ReporteDAO reporteDAO, ReclamacionDAO reclamacionDAO) {
        this.reporteDAO = reporteDAO;
        this.reclamacionDAO = reclamacionDAO;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getParameter("registrado") != null) {
            // SEC-01 · 13-14: mostrarConfirmacion(reporte) → reporte publicado
            request.setAttribute("reporte", reporteDAO.buscarPorId(leerId(request, "registrado")));
        }
        // SEC-01 · 1-2: mostrarFormulario() → formulario disponible
        mostrarFormulario(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("/reportes/retirar".equals(request.getServletPath())) {
            // HU-11: retirarReporte(actor, id); la regla de la entidad se muestra como error
            try {
                retirarReporte(actorIdentificado(request), leerId(request, "id"));
            } catch (IllegalStateException e) {
                throw new OperacionInvalidaException("Solo se puede retirar un reporte activo.");
            }
            redirigir(request, response, "/inicio?retirado");
            return;
        }
        // SEC-01 · 3: solicitarPublicacion(datos)
        DatosReporte datos = leerDatos(request);
        try {
            // SEC-01 · 4: registrarReporte(usuario, datos)
            Reporte reporte = registrarReporte(actorIdentificado(request), datos);
            // SEC-01 · 12: reporte registrado
            redirigir(request, response, "/reportes/nuevo?registrado=" + reporte.getId());
        } catch (OperacionInvalidaException e) {
            request.setAttribute("error", e.getMessage());
            mostrarFormulario(request, response);
        }
    }

    public Reporte registrarReporte(Usuario usuario, DatosReporte datos) {
        // Nota de SEC-01: el control valida campos y categoría
        validarCampos(datos);
        Categoria categoria = categoriaDAO.buscarPorId(datos.categoriaId());
        if (categoria == null) {
            throw new OperacionInvalidaException("Seleccione una categoría.");
        }

        // SEC-01 · 5: «create» Ubicacion(coordenadas, zona)
        Ubicacion ubicacion = new Ubicacion(datos.latitud(), datos.longitud(),
                Campus.zonaDe(datos.latitud(), datos.longitud()));
        // SEC-01 · 6-7: estaDentroDelCampus() → true
        if (!ubicacion.estaDentroDelCampus()) {
            throw new OperacionInvalidaException("La ubicación debe estar dentro del campus.");
        }

        // SEC-01 · 8: «create» Reporte(datos, usuario, categoria, ubicacion)
        Reporte reporte = new Reporte(datos, usuario, categoria, ubicacion);

        // SEC-01 · 9: opt [tipo = ENCONTRADO] «create» preguntas asociadas al reporte
        if (reporte.esEncontrado()) {
            for (String pregunta : datos.preguntas()) {
                reporte.agregarPregunta(pregunta);
            }
        }

        // SEC-01 · 10-11: publicar() → reporte ACTIVO
        reporte.publicar();

        // Nota de SEC-01: el registro conserva el reporte y sus preguntas
        return reporteDAO.guardar(reporte);
    }

    /**
     * HU-11: quien registró el reporte lo retira. Sus reclamaciones pendientes quedan rechazadas.
     */
    public void retirarReporte(Usuario actor, UUID reporteId) {
        Reporte reporte = reporteDAO.buscarPorId(reporteId);
        if (reporte == null || !reporte.esAutor(actor)) {
            throw new OperacionInvalidaException("Solo quien registró el reporte puede retirarlo.");
        }
        reporte.retirar();
        for (Reclamacion reclamacion : reclamacionDAO.listarPendientes(reporte)) {
            reclamacion.rechazar(MOTIVO_RETIRO);
        }
        // El retiro y los rechazos se guardan en una sola transacción
        reporteDAO.actualizar(reporte);
    }

    private void validarCampos(DatosReporte datos) {
        if (datos.tipo() == null) {
            throw new OperacionInvalidaException("Indique si el objeto está perdido o fue encontrado.");
        }
        if (datos.descripcionPublica().isEmpty() || datos.descripcionPublica().length() > 200) {
            throw new OperacionInvalidaException("Escriba una descripción de hasta 200 caracteres.");
        }
        if (datos.fechaSuceso() == null || datos.fechaSuceso().isAfter(LocalDate.now())) {
            throw new OperacionInvalidaException("Indique una fecha que no sea posterior a hoy.");
        }
        if (datos.latitud() == null || datos.longitud() == null) {
            throw new OperacionInvalidaException("Marque la ubicación en el mapa.");
        }
        if (datos.tipo() == TipoReporte.ENCONTRADO && datos.preguntas().isEmpty()) {
            throw new OperacionInvalidaException("Escriba al menos una pregunta de verificación.");
        }
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("categorias", categoriaDAO.listarTodas());
        request.setAttribute("hoy", LocalDate.now());
        mostrar("formularioReporte.jsp", request, response);
    }

    private DatosReporte leerDatos(HttpServletRequest request) {
        String descripcion = request.getParameter("descripcion");
        String[] preguntas = request.getParameterValues("pregunta");
        List<String> preguntasEscritas = preguntas == null ? List.of()
                : Arrays.stream(preguntas).map(String::trim).filter(texto -> !texto.isEmpty()).toList();
        return new DatosReporte(
                leerTipo(request.getParameter("tipo")),
                leerIdOpcional(request.getParameter("categoria")),
                descripcion == null ? "" : descripcion.trim(),
                leerFecha(request.getParameter("fecha")),
                leerCoordenada(request.getParameter("latitud")),
                leerCoordenada(request.getParameter("longitud")),
                preguntasEscritas);
    }

    private Double leerCoordenada(String valor) {
        try {
            return Double.valueOf(valor);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }
}
