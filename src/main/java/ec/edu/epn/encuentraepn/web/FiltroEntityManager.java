package ec.edu.epn.encuentraepn.web;

import ec.edu.epn.encuentraepn.dao.JPAUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Cierra el EntityManager de la petición cuando esta termina, de modo que las
 * vistas JSP todavía pueden leer los datos relacionados de las entidades.
 */
@WebFilter("/*")
public class FiltroEntityManager extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain cadena)
            throws IOException, ServletException {
        try {
            cadena.doFilter(request, response);
        } finally {
            JPAUtil.cerrarEntityManager();
        }
    }
}
