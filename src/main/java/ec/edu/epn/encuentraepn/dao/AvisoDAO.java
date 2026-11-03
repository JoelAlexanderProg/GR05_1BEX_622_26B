package ec.edu.epn.encuentraepn.dao;

import ec.edu.epn.encuentraepn.modelo.Aviso;
import ec.edu.epn.encuentraepn.modelo.Usuario;

import java.util.List;

/**
 * HU-16: acceso a datos de los avisos de cada usuario.
 */
public class AvisoDAO extends GenericDAO<Aviso> {

    public AvisoDAO() {
        super(Aviso.class);
    }

    /** Avisos del usuario, del más reciente al más antiguo. */
    public List<Aviso> listarPara(Usuario destinatario) {
        return em()
                .createQuery("SELECT a FROM Aviso a WHERE a.destinatario = :destinatario ORDER BY a.fecha DESC",
                        Aviso.class)
                .setParameter("destinatario", destinatario)
                .getResultList();
    }

    /** HU-17: cantidad de avisos que el usuario todavía no ha visto. */
    public long contarSinLeer(Usuario destinatario) {
        return em()
                .createQuery("SELECT COUNT(a) FROM Aviso a WHERE a.destinatario = :destinatario AND a.leido = false",
                        Long.class)
                .setParameter("destinatario", destinatario)
                .getSingleResult();
    }

    /** HU-17: guarda en una sola transacción los avisos que se marcaron como leídos. */
    public void marcarLeidos(List<Aviso> avisos) {
        enTransaccion(em -> avisos.forEach(aviso -> {
            if (!em.contains(aviso)) {
                em.merge(aviso);
            }
        }));
    }
}
