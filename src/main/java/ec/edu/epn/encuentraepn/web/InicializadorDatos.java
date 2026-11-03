package ec.edu.epn.encuentraepn.web;

import ec.edu.epn.encuentraepn.dao.CategoriaDAO;
import ec.edu.epn.encuentraepn.dao.JPAUtil;
import ec.edu.epn.encuentraepn.modelo.Categoria;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.List;

/**
 * Al iniciar la aplicación registra las categorías si la base de datos está vacía.
 * Al detenerla cierra la conexión con la base de datos.
 */
@WebListener
public class InicializadorDatos implements ServletContextListener {

    private static final List<String> CATEGORIAS = List.of(
            "Calculadoras", "Documentos", "Electrónicos", "Llaves", "Ropa y accesorios", "Útiles", "Otros");

    @Override
    public void contextInitialized(ServletContextEvent evento) {
        CategoriaDAO categoriaDAO = new CategoriaDAO();
        try {
            if (categoriaDAO.listarTodas().isEmpty()) {
                CATEGORIAS.forEach(nombre -> categoriaDAO.guardar(new Categoria(nombre)));
            }
        } finally {
            JPAUtil.cerrarEntityManager();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent evento) {
        JPAUtil.cerrarFabrica();
    }
}
