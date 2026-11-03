package ec.edu.epn.encuentraepn.controlador;

import ec.edu.epn.encuentraepn.dao.ReporteDAO;
import ec.edu.epn.encuentraepn.modelo.EstadisticasReportes;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * HU-13 Ver estadísticas de los objetos reportados.
 * «control» ControlEstadisticas. Vista: estadisticas.jsp («boundary» VistaEstadisticas).
 */
@WebServlet("/estadisticas")
public class ControlEstadisticas extends ControlBase {

    private final ReporteDAO reporteDAO = new ReporteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("estadisticas", consultarEstadisticas());
        mostrar("estadisticas.jsp", request, response);
    }

    /**
     * Cuenta todos los reportes registrados por categoría y por zona.
     */
    public EstadisticasReportes consultarEstadisticas() {
        return new EstadisticasReportes(reporteDAO.listarTodos());
    }
}
