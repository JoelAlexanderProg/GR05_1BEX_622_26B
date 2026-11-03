package ec.edu.epn.encuentraepn.web;

import ec.edu.epn.encuentraepn.dao.CategoriaDAO;
import ec.edu.epn.encuentraepn.dao.JPAUtil;
import ec.edu.epn.encuentraepn.dao.PuntoEntregaDAO;
import ec.edu.epn.encuentraepn.modelo.Categoria;
import ec.edu.epn.encuentraepn.modelo.PuntoEntrega;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.time.LocalTime;
import java.util.List;

/**
 * Al iniciar la aplicación registra las categorías y, desde la HU-21, los puntos de entrega oficiales,
 * si todavía no existen.
 * Al detenerla cierra la conexión con la base de datos.
 */
@WebListener
public class InicializadorDatos implements ServletContextListener {

    private static final List<String> CATEGORIAS = List.of(
            "Calculadoras", "Documentos", "Electrónicos", "Llaves", "Ropa y accesorios", "Útiles", "Otros");

    /** HU-21: puntos de entrega de ejemplo; el cliente puede cambiar los lugares y los horarios. */
    private static final List<PuntoEntrega> PUNTOS_ENTREGA = List.of(
            new PuntoEntrega("Biblioteca Central", "Planta baja, mostrador de préstamo",
                    LocalTime.of(7, 0), LocalTime.of(19, 0)),
            new PuntoEntrega("Garita de la entrada principal", "Entrada por la avenida Ladrón de Guevara",
                    LocalTime.of(6, 30), LocalTime.of(21, 0)),
            new PuntoEntrega("Dirección de Bienestar Estudiantil", "Ventanilla de atención a estudiantes",
                    LocalTime.of(8, 0), LocalTime.of(17, 0)),
            new PuntoEntrega("Edificio de Administración Central", "Planta baja, recepción",
                    LocalTime.of(8, 0), LocalTime.of(16, 30)));

    @Override
    public void contextInitialized(ServletContextEvent evento) {
        CategoriaDAO categoriaDAO = new CategoriaDAO();
        try {
            if (categoriaDAO.listarTodas().isEmpty()) {
                CATEGORIAS.forEach(nombre -> categoriaDAO.guardar(new Categoria(nombre)));
            }
            PuntoEntregaDAO puntoEntregaDAO = new PuntoEntregaDAO();
            if (puntoEntregaDAO.listarTodos().isEmpty()) {
                PUNTOS_ENTREGA.forEach(puntoEntregaDAO::guardar);
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
