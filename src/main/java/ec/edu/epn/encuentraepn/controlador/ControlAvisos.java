package ec.edu.epn.encuentraepn.controlador;

import ec.edu.epn.encuentraepn.dao.AvisoDAO;
import ec.edu.epn.encuentraepn.modelo.Aviso;
import ec.edu.epn.encuentraepn.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * HU-16 Recibir un aviso cuando se reporta un objeto que coincide con mi pérdida.
 * «control» ControlAvisos. Vista: avisos.jsp («boundary» BandejaAvisos).
 */
@WebServlet("/avisos")
public class ControlAvisos extends ControlBase {

    private final AvisoDAO avisoDAO;

    public ControlAvisos() {
        this(new AvisoDAO());
    }

    /**
     * Recibe el acceso a datos de los avisos, para que una prueba pueda reemplazarlo por un objeto simulado.
     */
    ControlAvisos(AvisoDAO avisoDAO) {
        this.avisoDAO = avisoDAO;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Aviso> avisos = consultarAvisos(actorIdentificado(request));
        request.setAttribute("avisos", avisos);
        // HU-17: al abrir la bandeja, los avisos nuevos se resaltan y quedan leídos
        request.setAttribute("nuevos", marcarComoLeidos(avisos));
        mostrar("avisos.jsp", request, response);
    }

    /** Avisos del actor, del más reciente al más antiguo. */
    public List<Aviso> consultarAvisos(Usuario actor) {
        return avisoDAO.listarPara(actor);
    }

    /**
     * HU-17: marca como leídos los avisos que todavía no lo estaban y los devuelve, para resaltarlos como nuevos.
     */
    public List<Aviso> marcarComoLeidos(List<Aviso> avisos) {
        List<Aviso> nuevos = avisos.stream().filter(aviso -> !aviso.isLeido()).toList();
        if (!nuevos.isEmpty()) {
            nuevos.forEach(Aviso::marcarLeido);
            avisoDAO.marcarLeidos(nuevos);
        }
        return nuevos;
    }
}
