package ec.edu.epn.encuentraepn;

import ec.edu.epn.encuentraepn.modelo.Campus;
import ec.edu.epn.encuentraepn.modelo.Categoria;
import ec.edu.epn.encuentraepn.modelo.DatosReporte;
import ec.edu.epn.encuentraepn.modelo.Reclamacion;
import ec.edu.epn.encuentraepn.modelo.Reporte;
import ec.edu.epn.encuentraepn.modelo.TipoReporte;
import ec.edu.epn.encuentraepn.modelo.Ubicacion;
import ec.edu.epn.encuentraepn.modelo.Usuario;

import java.time.LocalDate;
import java.util.List;

/**
 * Datos de ejemplo de las pruebas unitarias, tomados de los casos de prueba de la Tarea 1:
 * Bruno Salas encontró una calculadora y Andrea Paz la reclama.
 */
public final class DatosDePrueba {

    public static final String MOTIVO = "Las respuestas no coinciden con el objeto";

    private DatosDePrueba() {
    }

    /**
     * Reclamación recién registrada (PENDIENTE) sobre un objeto encontrado.
     */
    public static Reclamacion reclamacionPendiente() {
        return reclamacionPendiente(new Usuario("Bruno Salas", "bruno.salas@epn.edu.ec"));
    }

    /**
     * Reclamación recién registrada (PENDIENTE) sobre un objeto que registró el encontrante indicado.
     */
    public static Reclamacion reclamacionPendiente(Usuario encontrante) {
        return new Reclamacion(new Usuario("Andrea Paz", "andrea.paz@epn.edu.ec"), reporteEncontrado(encontrante));
    }

    /**
     * Reporte publicado (ACTIVO) de la calculadora que encontró Bruno Salas.
     */
    public static Reporte reporteActivo() {
        return reporteActivo("Calculadora científica negra");
    }

    /**
     * Reporte publicado (ACTIVO) de un objeto que encontró Bruno Salas, con la descripción indicada.
     */
    public static Reporte reporteActivo(String descripcion) {
        Reporte reporte = reporteEncontrado(new Usuario("Bruno Salas", "bruno.salas@epn.edu.ec"), descripcion);
        reporte.publicar();
        return reporte;
    }

    /**
     * Reporte de un objeto encontrado de la categoría y la zona indicadas.
     */
    public static Reporte reporteDe(String categoria, String zona) {
        return reporteEncontrado(new Usuario("Bruno Salas", "bruno.salas@epn.edu.ec"), "Objeto encontrado",
                categoria, zona);
    }

    /** Lugar de la pérdida en los datos de las coincidencias (zona Centro). */
    public static final double LATITUD = -0.2100;
    public static final double LONGITUD = -78.4900;

    /** Metros que hay en un grado de longitud junto a la línea ecuatorial. */
    private static final double METROS_POR_GRADO = 111_195;

    /**
     * HU-14: reporte publicado de la calculadora que Andrea Paz perdió el 1 de octubre de 2026 en (LATITUD, LONGITUD).
     */
    public static Reporte reportePerdido(Categoria categoria) {
        Usuario andrea = new Usuario("Andrea Paz", "andrea.paz@epn.edu.ec");
        DatosReporte datos = new DatosReporte(TipoReporte.PERDIDO, null, "Calculadora Casio gris",
                LocalDate.of(2026, 10, 1), LATITUD, LONGITUD, List.of());
        Reporte reporte = new Reporte(datos, andrea, categoria, new Ubicacion(LATITUD, LONGITUD, Campus.ZONA_CENTRO));
        reporte.publicar();
        return reporte;
    }

    /**
     * HU-14: reporte publicado de un objeto que Bruno Salas encontró los días indicados después del 1 de octubre
     * y a los metros indicados al este del lugar de la pérdida.
     */
    public static Reporte encontradoA(Categoria categoria, double metros, int diasDespues) {
        double longitud = LONGITUD + metros / METROS_POR_GRADO;
        DatosReporte datos = new DatosReporte(TipoReporte.ENCONTRADO, null, "Calculadora encontrada",
                LocalDate.of(2026, 10, 1).plusDays(diasDespues), LATITUD, longitud, List.of("¿De qué color es?"));
        Reporte reporte = new Reporte(datos, new Usuario("Bruno Salas", "bruno.salas@epn.edu.ec"), categoria,
                new Ubicacion(LATITUD, longitud, Campus.ZONA_CENTRO));
        reporte.agregarPregunta("¿De qué color es?");
        reporte.publicar();
        return reporte;
    }

    private static Reporte reporteEncontrado(Usuario encontrante) {
        return reporteEncontrado(encontrante, "Calculadora científica negra");
    }

    private static Reporte reporteEncontrado(Usuario encontrante, String descripcion) {
        return reporteEncontrado(encontrante, descripcion, "Calculadoras", Campus.ZONA_CENTRO);
    }

    private static Reporte reporteEncontrado(Usuario encontrante, String descripcion, String categoria, String zona) {
        DatosReporte datos = new DatosReporte(TipoReporte.ENCONTRADO, null, descripcion,
                LocalDate.of(2026, 9, 28), -0.21045, -78.48920, List.of("¿Qué iniciales tiene detrás?"));
        return new Reporte(datos, encontrante, new Categoria(categoria),
                new Ubicacion(-0.21045, -78.48920, zona));
    }
}
