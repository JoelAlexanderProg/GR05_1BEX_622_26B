package ec.edu.epn.encuentraepn.modelo;

/**
 * Tipo de un reporte: el objeto se perdió o se encontró.
 */
public enum TipoReporte {

    PERDIDO("Perdido"),
    ENCONTRADO("Encontrado");

    private final String etiqueta;

    TipoReporte(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
