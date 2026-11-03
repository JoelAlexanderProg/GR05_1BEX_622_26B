package ec.edu.epn.encuentraepn.controlador;

import ec.edu.epn.encuentraepn.dao.ReclamacionDAO;
import ec.edu.epn.encuentraepn.dao.ReporteDAO;
import ec.edu.epn.encuentraepn.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Página de inicio. Al usuario identificado le muestra sus reportes y sus reclamaciones,
 * que son el punto de entrada a la gestión de la devolución (CU-04).
 */
@WebServlet("/inicio")
public class ControlInicio extends ControlBase {

    private final ReporteDAO reporteDAO = new ReporteDAO();
    private final ReclamacionDAO reclamacionDAO = new ReclamacionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario actor = actor(request);
        if (actor != null) {
            request.setAttribute("misReportes", reporteDAO.listarPorAutor(actor));
            request.setAttribute("misReclamaciones", reclamacionDAO.listarPorReclamante(actor));
        }
        mostrar("inicio.jsp", request, response);
    }
}
