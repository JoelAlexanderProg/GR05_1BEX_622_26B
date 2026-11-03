package ec.edu.epn.encuentraepn.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

    @Column(nullable = false)
    private LocalDateTime fechaPropuesta;

    private LocalDateTime fechaConfirmacion;

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

    // SEC-04 · 34: confirmar(fecha)
    public void confirmar(LocalDateTime fecha) {
        if (estaConfirmado()) {
            throw new IllegalStateException("El acuerdo ya está confirmado");
        }
        fechaConfirmacion = fecha;
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

    public LocalDateTime getFechaPropuesta() {
        return fechaPropuesta;
    }

    public LocalDateTime getFechaConfirmacion() {
        return fechaConfirmacion;
    }

    public Reclamacion getReclamacion() {
        return reclamacion;
    }
}
