package ec.edu.epn.encuentraepn.controlador;

import ec.edu.epn.encuentraepn.dao.ReporteDAO;
import ec.edu.epn.encuentraepn.modelo.Coincidencia;
import ec.edu.epn.encuentraepn.modelo.Reporte;
import ec.edu.epn.encuentraepn.modelo.TipoReporte;
import ec.edu.epn.encuentraepn.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * HU-14 Ver los objetos encontrados que coinciden con mi objeto perdido.
 * «control» ControlCoincidencias. Vista: coincidencias.jsp («boundary» VistaCoincidencias).
 */
@WebServlet("/coincidencias")
public class ControlCoincidencias extends ControlBase {

    private final ReporteDAO reporteDAO;

    public ControlCoincidencias() {
        this(new ReporteDAO());
    }

    /**
     * Recibe el acceso a datos de los reportes, para que una prueba pueda reemplazarlo por un objeto simulado.
     */
    ControlCoincidencias(ReporteDAO reporteDAO) {
        this.reporteDAO = reporteDAO;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UUID reporteId = leerId(request, "reporte");
        List<Coincidencia> coincidencias = buscarCoincidencias(actorIdentificado(request), reporteId);
        request.setAttribute("perdido", reporteDAO.buscarPorId(reporteId));
        request.setAttribute("coincidencias", coincidencias);
        mostrar("coincidencias.jsp", request, response);
    }

    /**
     * Objetos encontrados que coinciden con el reporte de pérdida del actor, del más cercano al más lejano.
     */
    public List<Coincidencia> buscarCoincidencias(Usuario actor, UUID reporteId) {
        Reporte perdido = reporteDAO.buscarPorId(reporteId);
        if (perdido == null || !perdido.esAutor(actor)) {
            throw new OperacionInvalidaException("Solo quien registró el reporte puede ver sus coincidencias.");
        }
        if (perdido.getTipo() != TipoReporte.PERDIDO) {
            throw new OperacionInvalidaException("Solo los reportes de objetos perdidos tienen coincidencias.");
        }
        return reporteDAO.listarEncontradosActivos(perdido.getCategoria()).stream()
                .filter(perdido::coincideCon)
                .map(encontrado -> new Coincidencia(perdido, encontrado))
                .sorted(Comparator.comparingLong(Coincidencia::metros))
                .toList();
    }
}
