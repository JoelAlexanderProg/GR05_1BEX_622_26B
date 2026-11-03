package ec.edu.epn.encuentraepn.dao;

import ec.edu.epn.encuentraepn.modelo.Categoria;

import java.util.List;

public class CategoriaDAO extends GenericDAO<Categoria> {

    public CategoriaDAO() {
        super(Categoria.class);
    }

    public List<Categoria> listarTodas() {
        return em()
                .createQuery("SELECT c FROM Categoria c ORDER BY c.nombre", Categoria.class)
                .getResultList();
    }
}
