package ec.edu.epn.encuentraepn.dao;

import ec.edu.epn.encuentraepn.modelo.EstadoReclamacion;
import ec.edu.epn.encuentraepn.modelo.Reclamacion;
import ec.edu.epn.encuentraepn.modelo.Reporte;
import ec.edu.epn.encuentraepn.modelo.Usuario;

import java.util.List;

public class ReclamacionDAO extends GenericDAO<Reclamacion> {

    public ReclamacionDAO() {
        super(Reclamacion.class);
    }

    // SEC-04 · 3: consultar pendientes del reporte [lectura]
    public List<Reclamacion> listarPendientes(Reporte reporte) {
        return em()
                .createQuery("SELECT c FROM Reclamacion c WHERE c.objeto = :reporte AND c.estado = :estado "
                        + "ORDER BY c.fechaCreacion", Reclamacion.class)
                .setParameter("reporte", reporte)
                .setParameter("estado", EstadoReclamacion.PENDIENTE)
                .getResultList();
    }

    /**
     * Reclamación aceptada o completada del reporte, o null si todavía no hay ninguna.
     */
    public Reclamacion buscarEnCurso(Reporte reporte) {
        List<Reclamacion> reclamaciones = em()
                .createQuery("SELECT c FROM Reclamacion c WHERE c.objeto = :reporte AND c.estado IN :estados",
                        Reclamacion.class)
                .setParameter("reporte", reporte)
                .setParameter("estados", List.of(EstadoReclamacion.ACEPTADA, EstadoReclamacion.COMPLETADA))
                .getResultList();
        return reclamaciones.isEmpty() ? null : reclamaciones.get(0);
    }

    public boolean existeDe(Usuario reclamante, Reporte reporte) {
        Long cantidad = em()
                .createQuery("SELECT COUNT(c) FROM Reclamacion c WHERE c.objeto = :reporte "
                        + "AND c.reclamante = :reclamante", Long.class)
                .setParameter("reporte", reporte)
                .setParameter("reclamante", reclamante)
                .getSingleResult();
        return cantidad > 0;
    }

    public List<Reclamacion> listarPorReclamante(Usuario reclamante) {
        return em()
                .createQuery("SELECT c FROM Reclamacion c WHERE c.reclamante = :reclamante "
                        + "ORDER BY c.fechaCreacion DESC", Reclamacion.class)
                .setParameter("reclamante", reclamante)
                .getResultList();
    }
}
