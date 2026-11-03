package ec.edu.epn.encuentraepn.dao;

import ec.edu.epn.encuentraepn.modelo.FiltrosReporte;
import ec.edu.epn.encuentraepn.modelo.EstadoReporte;
import ec.edu.epn.encuentraepn.modelo.Reporte;
import ec.edu.epn.encuentraepn.modelo.Usuario;

import java.util.List;

public class ReporteDAO extends GenericDAO<Reporte> {

    public ReporteDAO() {
        super(Reporte.class);
    }

    // SEC-02 · 3: consultar reportes activos [lectura]
    // SEC-02 · 10: consultar por tipo, categoría, fecha y zona [lectura]
    public List<Reporte> consultarActivos(FiltrosReporte filtros) {
        return em()
                .createQuery("""
                        SELECT r FROM Reporte r
                        WHERE r.estado = :estado
                          AND (:tipo IS NULL OR r.tipo = :tipo)
                          AND (:categoriaId IS NULL OR r.categoria.id = :categoriaId)
                          AND (:fechaDesde IS NULL OR r.fechaSuceso >= :fechaDesde)
                          AND (:fechaHasta IS NULL OR r.fechaSuceso <= :fechaHasta)
                          AND (:zona IS NULL OR r.ubicacion.zona = :zona)
                        ORDER BY r.fechaSuceso DESC
                        """, Reporte.class)
                .setParameter("estado", EstadoReporte.ACTIVO)
                .setParameter("tipo", filtros.tipo())
                .setParameter("categoriaId", filtros.categoriaId())
                .setParameter("fechaDesde", filtros.fechaDesde())
                .setParameter("fechaHasta", filtros.fechaHasta())
                .setParameter("zona", filtros.zona())
                .getResultList();
    }

    /**
     * HU-13: todos los reportes registrados, en cualquier estado, para las estadísticas.
     */
    public List<Reporte> listarTodos() {
        return em().createQuery("SELECT r FROM Reporte r", Reporte.class).getResultList();
    }

    public List<Reporte> listarPorAutor(Usuario autor) {
        return em()
                .createQuery("SELECT r FROM Reporte r WHERE r.autor = :autor ORDER BY r.fechaSuceso DESC", Reporte.class)
                .setParameter("autor", autor)
                .getResultList();
    }
}
