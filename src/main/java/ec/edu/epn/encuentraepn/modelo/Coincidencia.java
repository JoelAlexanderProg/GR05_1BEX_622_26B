package ec.edu.epn.encuentraepn.modelo;

/**
 * HU-14: objeto encontrado que puede ser el objeto perdido, con la distancia entre los dos lugares.
 *
 * @param encontrado reporte del objeto encontrado
 * @param metros     distancia redondeada, en metros, desde el lugar de la pérdida
 */
public record Coincidencia(Reporte encontrado, long metros) {

    public Coincidencia(Reporte perdido, Reporte encontrado) {
        this(encontrado, Math.round(perdido.distanciaA(encontrado)));
    }
}
