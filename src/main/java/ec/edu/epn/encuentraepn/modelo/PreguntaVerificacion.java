package ec.edu.epn.encuentraepn.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Pregunta que define quien encontró un objeto, sobre un detalle que solo
 * el dueño conoce. Diagrama de clases: «entity» PreguntaVerificacion.
 */
@Entity
@Table(name = "pregunta_verificacion")
public class PreguntaVerificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String texto;

    @ManyToOne(optional = false)
    private Reporte reporte;

    protected PreguntaVerificacion() {
    }

    public PreguntaVerificacion(Reporte reporte, String texto) {
        this.reporte = reporte;
        this.texto = texto;
    }

    public UUID getId() {
        return id;
    }

    public String getTexto() {
        return texto;
    }

    public Reporte getReporte() {
        return reporte;
    }
}
