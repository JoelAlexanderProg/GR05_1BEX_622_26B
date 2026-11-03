package ec.edu.epn.encuentraepn.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * HU-21: lugar oficial del campus donde se pueden entregar los objetos, con su horario de atención
 * (de lunes a viernes).
 */
@Entity
@Table(name = "punto_entrega")
public class PuntoEntrega {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    /** Indicación para encontrarlo, por ejemplo «Planta baja, mostrador de préstamo». */
    @Column(nullable = false, length = 150)
    private String referencia;

    @Column(nullable = false)
    private LocalTime apertura;

    @Column(nullable = false)
    private LocalTime cierre;

    protected PuntoEntrega() {
    }

    public PuntoEntrega(String nombre, String referencia, LocalTime apertura, LocalTime cierre) {
        this.nombre = nombre;
        this.referencia = referencia;
        this.apertura = apertura;
        this.cierre = cierre;
    }

    /**
     * Indica si el punto atiende en esa fecha y hora: de lunes a viernes, desde la apertura y antes del cierre.
     */
    public boolean atiendeA(LocalDateTime fecha) {
        DayOfWeek dia = fecha.getDayOfWeek();
        LocalTime hora = fecha.toLocalTime();
        return dia != DayOfWeek.SATURDAY && dia != DayOfWeek.SUNDAY
                && !hora.isBefore(apertura) && hora.isBefore(cierre);
    }

    /** Horario para mostrar, por ejemplo «de lunes a viernes, de 07:00 a 19:00». */
    public String getHorario() {
        return "de lunes a viernes, de " + apertura + " a " + cierre;
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getReferencia() {
        return referencia;
    }

    public LocalTime getApertura() {
        return apertura;
    }

    public LocalTime getCierre() {
        return cierre;
    }
}
