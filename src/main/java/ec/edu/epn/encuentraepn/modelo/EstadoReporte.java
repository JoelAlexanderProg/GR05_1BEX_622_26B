package ec.edu.epn.encuentraepn.modelo;

/**
 * Estados de un reporte: BORRADOR → ACTIVO → RESERVADO → ENTREGADO.
 * Un reporte ACTIVO también puede quedar RETIRADO por su autor (HU-11).
 */
public enum EstadoReporte {

    BORRADOR("Borrador"),
    ACTIVO("Activo"),
    RESERVADO("Reservado"),
    ENTREGADO("Entregado"),
    RETIRADO("Retirado");

    private final String etiqueta;

    EstadoReporte(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
