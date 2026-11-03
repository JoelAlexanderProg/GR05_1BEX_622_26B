package ec.edu.epn.encuentraepn.modelo;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Datos que el reportante ingresa en el formulario de registro
 * (el parámetro «datos» de los diagramas).
 */
public record DatosReporte(
        TipoReporte tipo,
        UUID categoriaId,
        String descripcionPublica,
        LocalDate fechaSuceso,
        Double latitud,
        Double longitud,
        List<String> preguntas) {
}
