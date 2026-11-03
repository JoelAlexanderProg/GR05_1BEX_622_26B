package ec.edu.epn.encuentraepn.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Acuerdo para la entrega de un objeto reclamado.
 * Diagrama de clases: «entity» AcuerdoEntrega.
 */
@Entity
@Table(name = "acuerdo_entrega")
public class AcuerdoEntrega {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String lugar;

    /** HU-21: punto de entrega oficial elegido, o null si se acordó otro lugar. */
    @ManyToOne
    private PuntoEntrega punto;

    @Column(nullable = false)
    private LocalDateTime fechaPropuesta;

    private LocalDateTime fechaConfirmacion;

    /** HU-20: otra fecha que pidió el reclamante y que todavía no acepta quien encontró el objeto. */
    private LocalDateTime fechaSolicitada;

    @OneToOne(optional = false)
    @JoinColumn(name = "reclamacion_id", nullable = false, unique = true)
    private Reclamacion reclamacion;

    protected AcuerdoEntrega() {
    }

    // SEC-04 · 23: «create» AcuerdoEntrega(reclamacion, lugar, fecha)
    public AcuerdoEntrega(Reclamacion reclamacion, String lugar, LocalDateTime fechaPropuesta) {
        this.reclamacion = reclamacion;
        this.lugar = lugar;
        this.fechaPropuesta = fechaPropuesta;
    }

    /** HU-21: acuerdo en un punto de entrega oficial; el lugar es el nombre del punto. */
    public AcuerdoEntrega(Reclamacion reclamacion, PuntoEntrega punto, LocalDateTime fechaPropuesta) {
        this(reclamacion, punto.getNombre(), fechaPropuesta);
        this.punto = punto;
    }

    // SEC-04 · 34: confirmar(fecha)
    public void confirmar(LocalDateTime fecha) {
        if (estaConfirmado()) {
            throw new IllegalStateException("El acuerdo ya está confirmado");
        }
        fechaConfirmacion = fecha;
    }

    /**
     * HU-20: el reclamante no puede asistir y pide otra fecha, futura y distinta de la propuesta.
     * Solo se puede pedir mientras el acuerdo no está confirmado.
     */
    public void solicitarOtraFecha(LocalDateTime fecha, LocalDateTime ahora) {
        if (estaConfirmado()) {
            throw new IllegalStateException("El acuerdo ya está confirmado");
        }
        if (fecha == null || !fecha.isAfter(ahora)) {
            throw new IllegalArgumentException("Indique una fecha futura para la entrega.");
        }
        if (fecha.equals(fechaPropuesta)) {
            throw new IllegalArgumentException("La nueva fecha debe ser distinta de la propuesta.");
        }
        fechaSolicitada = fecha;
    }

    /**
     * HU-20: quien encontró el objeto acepta la fecha que pidió el reclamante. Como la pidió el propio
     * reclamante, el acuerdo queda confirmado con esa fecha.
     */
    public void aceptarOtraFecha(LocalDateTime ahora) {
        if (!hayFechaSolicitada()) {
            throw new IllegalStateException("El reclamante no ha pedido otra fecha");
        }
        fechaPropuesta = fechaSolicitada;
        fechaSolicitada = null;
        confirmar(ahora);
    }

    public boolean hayFechaSolicitada() {
        return fechaSolicitada != null;
    }

    public boolean estaConfirmado() {
        return fechaConfirmacion != null;
    }

    public UUID getId() {
        return id;
    }

    public String getLugar() {
        return lugar;
    }

    public PuntoEntrega getPunto() {
        return punto;
    }

    public LocalDateTime getFechaPropuesta() {
        return fechaPropuesta;
    }

    public LocalDateTime getFechaConfirmacion() {
        return fechaConfirmacion;
    }

    public LocalDateTime getFechaSolicitada() {
        return fechaSolicitada;
    }

    public Reclamacion getReclamacion() {
        return reclamacion;
    }
}
