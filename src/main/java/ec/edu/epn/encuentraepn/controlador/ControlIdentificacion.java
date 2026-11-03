package ec.edu.epn.encuentraepn.controlador;

import ec.edu.epn.encuentraepn.dao.UsuarioDAO;
import ec.edu.epn.encuentraepn.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Identificación del usuario con su nombre y su correo institucional.
 * Los modelos la asumen como condición previa de las operaciones que requieren un actor;
 * aquí se resuelve de la forma más simple, sin contraseña.
 */
@WebServlet({"/identificacion", "/salir"})
public class ControlIdentificacion extends ControlBase {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("/salir".equals(request.getServletPath())) {
            sesion.cerrar(request);
            redirigir(request, response, "/inicio");
        } else {
            mostrar("identificacion.jsp", request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nombre = limpiar(request.getParameter("nombre"));
        String correo = limpiar(request.getParameter("correo")).toLowerCase();
        if (nombre.isEmpty() || !correo.endsWith(Usuario.DOMINIO) || correo.length() == Usuario.DOMINIO.length()) {
            request.setAttribute("error", "Escriba su nombre y su correo institucional (" + Usuario.DOMINIO + ").");
            mostrar("identificacion.jsp", request, response);
            return;
        }
        Usuario usuario = usuarioDAO.obtenerORegistrar(nombre, correo);
        sesion.iniciar(request, usuario);
        redirigir(request, response, destino(request.getParameter("destino")));
    }

    /**
     * Página a la que el usuario quería ir antes de identificarse. Solo se aceptan rutas de la aplicación.
     */
    private String destino(String valor) {
        boolean rutaInterna = valor != null && valor.startsWith("/") && !valor.startsWith("//");
        return rutaInterna ? valor : "/inicio";
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
