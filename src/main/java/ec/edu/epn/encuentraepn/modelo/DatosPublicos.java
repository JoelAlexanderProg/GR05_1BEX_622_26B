package ec.edu.epn.encuentraepn.modelo;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Información de un reporte que cualquier persona puede ver.
 * No incluye el autor, las preguntas de verificación ni las respuestas.
 */
public final class DatosPublicos {

    private final UUID id;
    private final TipoReporte tipo;
    private final String categoria;
    private final String descripcionPublica;
    private final LocalDate fechaSuceso;
    private final EstadoReporte estado;
    private final double latitud;
    private final double longitud;
    private final String zona;

    // SEC-02 · 18-19: datos del reporte → datosPublicos
    public DatosPublicos(Reporte reporte) {
        this.id = reporte.getId();
        this.tipo = reporte.getTipo();
        this.categoria = reporte.getCategoria().getNombre();
        this.descripcionPublica = reporte.getDescripcionPublica();
        this.fechaSuceso = reporte.getFechaSuceso();
        this.estado = reporte.getEstado();
        this.latitud = reporte.getUbicacion().getLatitud();
        this.longitud = reporte.getUbicacion().getLongitud();
        this.zona = reporte.getUbicacion().getZona();
    }

    public UUID getId() {
        return id;
    }

    public TipoReporte getTipo() {
        return tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getDescripcionPublica() {
        return descripcionPublica;
    }

    public LocalDate getFechaSuceso() {
        return fechaSuceso;
    }

    public EstadoReporte getEstado() {
        return estado;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public String getZona() {
        return zona;
    }
}
