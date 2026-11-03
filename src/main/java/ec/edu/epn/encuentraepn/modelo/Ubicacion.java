package ec.edu.epn.encuentraepn.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Lugar aproximado del campus donde se perdió o se encontró un objeto.
 * Se guarda dentro de la tabla del reporte al que pertenece.
 */
@Embeddable
public class Ubicacion {

    /** Radio medio de la Tierra, en metros, para la fórmula de Haversine. */
    private static final double RADIO_TIERRA_METROS = 6_371_000;

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

    /**
     * HU-14: distancia en metros hasta otra ubicación, con la fórmula de Haversine
     * (distancia sobre la superficie de una esfera).
     */
    public double distanciaA(Ubicacion otra) {
        double diferenciaLatitud = Math.toRadians(otra.latitud - latitud);
        double diferenciaLongitud = Math.toRadians(otra.longitud - longitud);
        double a = Math.pow(Math.sin(diferenciaLatitud / 2), 2)
                + Math.cos(Math.toRadians(latitud)) * Math.cos(Math.toRadians(otra.latitud))
                * Math.pow(Math.sin(diferenciaLongitud / 2), 2);
        return 2 * RADIO_TIERRA_METROS * Math.asin(Math.sqrt(a));
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
