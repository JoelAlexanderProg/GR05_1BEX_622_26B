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

    private final AvisoDAO avisoDAO = new AvisoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("avisos", consultarAvisos(actorIdentificado(request)));
        mostrar("avisos.jsp", request, response);
    }

    /** Avisos del actor, del más reciente al más antiguo. */
    public List<Aviso> consultarAvisos(Usuario actor) {
        return avisoDAO.listarPara(actor);
    }
}
