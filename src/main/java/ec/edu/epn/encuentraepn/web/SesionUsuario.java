package ec.edu.epn.encuentraepn.web;

import ec.edu.epn.encuentraepn.dao.UsuarioDAO;
import ec.edu.epn.encuentraepn.modelo.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Usuario identificado en la sesión: lo guarda al identificarse, lo entrega
 * en cada petición y lo retira al salir.
 */
public class SesionUsuario {

    private static final String USUARIO_EN_SESION = "usuario";

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public void iniciar(HttpServletRequest request, Usuario usuario) {
        request.getSession().setAttribute(USUARIO_EN_SESION, usuario);
    }

    public boolean hayUsuario(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        return sesion != null && sesion.getAttribute(USUARIO_EN_SESION) != null;
    }

    /**
     * Usuario identificado en la sesión, o null si no hay ninguno.
     */
    public Usuario usuario(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        Usuario enSesion = sesion == null ? null : (Usuario) sesion.getAttribute(USUARIO_EN_SESION);
        return enSesion == null ? null : usuarioDAO.buscarPorId(enSesion.getId());
    }

    public void cerrar(HttpServletRequest request) {
        request.getSession().invalidate();
    }
}
