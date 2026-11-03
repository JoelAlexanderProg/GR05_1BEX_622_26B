package ec.edu.epn.encuentraepn.modelo;

/**
 * Estados de una reclamación: PENDIENTE → ACEPTADA → COMPLETADA, o PENDIENTE → RECHAZADA.
 * Desde la HU-19, una entrega que no se realizó deja la reclamación ACEPTADA → CANCELADA.
 */
public enum EstadoReclamacion {

    PENDIENTE("Pendiente"),
    ACEPTADA("Aceptada"),
    COMPLETADA("Completada"),
    RECHAZADA("Rechazada"),
    CANCELADA("Cancelada");

    private final String etiqueta;

    EstadoReclamacion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
