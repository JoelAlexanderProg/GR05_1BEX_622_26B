package ec.edu.epn.encuentraepn.modelo;

/**
 * Estados de una reclamación: PENDIENTE → ACEPTADA → COMPLETADA, o PENDIENTE → RECHAZADA.
 */
public enum EstadoReclamacion {

    PENDIENTE("Pendiente"),
    ACEPTADA("Aceptada"),
    COMPLETADA("Completada"),
    RECHAZADA("Rechazada");

    private final String etiqueta;

    EstadoReclamacion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
