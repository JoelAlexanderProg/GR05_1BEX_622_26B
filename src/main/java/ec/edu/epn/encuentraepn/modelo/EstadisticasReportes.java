package ec.edu.epn.encuentraepn.modelo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * HU-13: cuántos reportes hay por categoría y por zona del campus, de mayor a menor cantidad.
 */
public class EstadisticasReportes {

    private final List<Reporte> reportes;

    public EstadisticasReportes(List<Reporte> reportes) {
        this.reportes = reportes;
    }

    public int total() {
        return reportes.size();
    }

    public Map<String, Long> porCategoria() {
        return contarPor(reporte -> reporte.getCategoria().getNombre());
    }

    public Map<String, Long> porZona() {
        return contarPor(reporte -> reporte.getUbicacion().getZona());
    }

    /**
     * Cuenta los reportes que tienen cada valor de la clave y los ordena de mayor a menor (y por nombre si empatan).
     */
    private Map<String, Long> contarPor(Function<Reporte, String> clave) {
        Map<String, Long> conteo = reportes.stream()
                .collect(Collectors.groupingBy(clave, Collectors.counting()));
        Map<String, Long> ordenado = new LinkedHashMap<>();
        conteo.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                .forEach(entrada -> ordenado.put(entrada.getKey(), entrada.getValue()));
        return ordenado;
    }
}
