package ec.edu.epn.encuentraepn.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Administra la conexión con la base de datos. Entrega un EntityManager por
 * petición: se crea la primera vez que se pide y FiltroEntityManager lo cierra
 * al terminar la petición.
 */
public final class JPAUtil {

    private static final EntityManagerFactory FABRICA =
            Persistence.createEntityManagerFactory("encuentraepnPU");

    private static final ThreadLocal<EntityManager> ACTUAL = new ThreadLocal<>();

    private JPAUtil() {
    }

    public static EntityManager getEntityManager() {
        EntityManager em = ACTUAL.get();
        if (em == null || !em.isOpen()) {
            em = FABRICA.createEntityManager();
            ACTUAL.set(em);
        }
        return em;
    }

    public static void cerrarEntityManager() {
        EntityManager em = ACTUAL.get();
        if (em != null && em.isOpen()) {
            em.close();
        }
        ACTUAL.remove();
    }

    public static void cerrarFabrica() {
        FABRICA.close();
    }
}
