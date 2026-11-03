package ec.edu.epn.encuentraepn.dao;

import ec.edu.epn.encuentraepn.modelo.PuntoEntrega;

import java.util.List;

/**
 * HU-21: acceso a datos de los puntos de entrega oficiales del campus.
 */
public class PuntoEntregaDAO extends GenericDAO<PuntoEntrega> {

    public PuntoEntregaDAO() {
        super(PuntoEntrega.class);
    }

    /** Puntos de entrega en orden alfabético. */
    public List<PuntoEntrega> listarTodos() {
        return em().createQuery("SELECT p FROM PuntoEntrega p ORDER BY p.nombre", PuntoEntrega.class)
                .getResultList();
    }
}
