package ec.edu.epn.encuentraepn.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Lugar aproximado del campus donde se perdió o se encontró un objeto.
 * Se guarda dentro de la tabla del reporte al que pertenece.
 */
@Embeddable
public class Ubicacion {

    @Column(nullable = false)
    private double latitud;

    @Column(nullable = false)
    private double longitud;

    @Column(nullable = false, length = 20)
    private String zona;

    // JPA exige un constructor sin argumentos
    protected Ubicacion() {
    }

    public Ubicacion(double latitud, double longitud, String zona) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.zona = zona;
    }

    public boolean estaDentroDelCampus() {
        return Campus.contiene(latitud, longitud);
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
