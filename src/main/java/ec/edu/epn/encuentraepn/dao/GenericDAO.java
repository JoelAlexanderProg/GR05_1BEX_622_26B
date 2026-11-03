package ec.edu.epn.encuentraepn.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Operaciones de persistencia comunes a todas las entidades.
 *
 * @param <T> clase de la entidad
 */
public abstract class GenericDAO<T> {

    private final Class<T> clase;

    protected GenericDAO(Class<T> clase) {
        this.clase = clase;
    }

    protected EntityManager em() {
        return JPAUtil.getEntityManager();
    }

    public T guardar(T entidad) {
        enTransaccion(em -> em.persist(entidad));
        return entidad;
    }

    /**
     * Guarda los cambios de la entidad y de las entidades relacionadas que se
     * modificaron en la misma petición, en una sola transacción.
     */
    public T actualizar(T entidad) {
        enTransaccion(em -> {
            if (!em.contains(entidad)) {
                em.merge(entidad);
            }
        });
        return entidad;
    }

    public T buscarPorId(UUID id) {
        return id == null ? null : em().find(clase, id);
    }

    protected void enTransaccion(Consumer<EntityManager> operacion) {
        EntityManager em = em();
        EntityTransaction transaccion = em.getTransaction();
        try {
            transaccion.begin();
            operacion.accept(em);
            transaccion.commit();
        } catch (RuntimeException e) {
            if (transaccion.isActive()) {
                transaccion.rollback();
            }
            throw e;
        }
    }
}
