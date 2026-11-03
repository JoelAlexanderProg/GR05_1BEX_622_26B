package ec.edu.epn.encuentraepn.controlador;

import ec.edu.epn.encuentraepn.dao.CategoriaDAO;
import ec.edu.epn.encuentraepn.dao.ReporteDAO;
import ec.edu.epn.encuentraepn.modelo.Campus;
import ec.edu.epn.encuentraepn.modelo.DatosPublicos;
import ec.edu.epn.encuentraepn.modelo.FiltrosReporte;
import ec.edu.epn.encuentraepn.modelo.Reporte;
import ec.edu.epn.encuentraepn.modelo.TipoReporte;
import ec.edu.epn.encuentraepn.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * CU-02 Consultar y filtrar reportes en el mapa.
 * Diagrama de clases: «control» ControlConsultaReportes. Vistas: mapaReportes.jsp («boundary» MapaReportes)
 * y detalleReporte.jsp («boundary» DetalleReporte). Diagrama de secuencia: SEC-02.
 */
@WebServlet({"/reportes", "/reportes/detalle"})
public class ControlConsultaReportes extends ControlBase {

    private final ReporteDAO reporteDAO = new ReporteDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("/reportes/detalle".equals(request.getServletPath())) {
            mostrarDetalle(request, response);
        } else {
            mostrarMapa(request, response);
        }
    }

    private void mostrarMapa(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // SEC-02 · 1: abrir sección de búsqueda (sin filtros) · 8: aplicarFiltros(filtros)
        FiltrosReporte filtros = leerFiltros(request);
        // SEC-02 · 2 y 9: consultarReportes(filtros)
        List<Reporte> reportes = consultarReportes(filtros);
        // SEC-02 · 5-7 y 12-14: reportes públicos → mostrarMapa(reportes)
        request.setAttribute("reportes", reportes);
        request.setAttribute("categorias", categoriaDAO.listarTodas());
        request.setAttribute("tipos", TipoReporte.values());
        request.setAttribute("zonas", Campus.ZONAS);
        mostrar("mapaReportes.jsp", request, response);
    }

    private void mostrarDetalle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // SEC-02 · 15: seleccionarMarcador(idReporte)
        UUID idReporte = leerId(request, "id");
        // SEC-02 · 16: consultarDetallePublico(idReporte)
        DatosPublicos datosPublicos = consultarDetallePublico(idReporte);
        // SEC-02 · 20-21: mostrarDetalle(datosPublicos) → detalle público visible
        request.setAttribute("detalle", datosPublicos);
        indicarAcciones(request, idReporte);
        mostrar("detalleReporte.jsp", request, response);
    }

    public List<Reporte> consultarReportes(FiltrosReporte filtros) {
        // SEC-02 · 3-4: consultar reportes activos [lectura]
        // SEC-02 · 10-11: consultar por tipo, categoría, fecha y zona [lectura]
        List<Reporte> reportes = reporteDAO.consultarActivos(filtros);
        if (filtros.palabra() == null) {
            return reportes;
        }
        // HU-12: búsqueda por palabra clave, sin distinguir mayúsculas, tildes ni espacios sobrantes
        return reportes.stream().filter(reporte -> reporte.contienePalabra(filtros.palabra())).toList();
    }

    public DatosPublicos consultarDetallePublico(UUID idReporte) {
        // SEC-02 · 17-18: consultar reporte seleccionado [lectura]
        Reporte reporte = reporteDAO.buscarPorId(idReporte);
        if (reporte == null) {
            throw new OperacionInvalidaException("El reporte indicado no existe.");
        }
        // SEC-02 · 19: datosPublicos
        return new DatosPublicos(reporte);
    }

    /**
     * Indica a la vista qué acciones ofrecer: reclamar (CU-03) o revisar las reclamaciones (CU-04).
     */
    private void indicarAcciones(HttpServletRequest request, UUID idReporte) {
        Reporte reporte = reporteDAO.buscarPorId(idReporte);
        Usuario actor = actor(request);
        boolean esAutor = actor != null && reporte.esAutor(actor);
        request.setAttribute("esAutor", esAutor);
        request.setAttribute("puedeReclamar", reporte.esEncontrado() && reporte.estaActivo() && !esAutor);
    }

    private FiltrosReporte leerFiltros(HttpServletRequest request) {
        return new FiltrosReporte(
                leerTipo(request.getParameter("tipo")),
                leerIdOpcional(request.getParameter("categoria")),
                leerFecha(request.getParameter("desde")),
                leerFecha(request.getParameter("hasta")),
                Campus.buscarZona(request.getParameter("zona")),
                leerPalabra(request.getParameter("palabra")));
    }

    /** Palabra clave escrita en el buscador, o null si está vacía. */
    private String leerPalabra(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
