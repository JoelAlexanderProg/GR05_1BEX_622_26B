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
 * Respuesta privada del reclamante a una pregunta de verificación.
 * Solo la ve quien registró el objeto. Diagrama de clases: «entity» RespuestaVerificacion.
 */
@Entity
@Table(name = "respuesta_verificacion")
public class RespuestaVerificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 300)
    private String textoPrivado;

    @ManyToOne(optional = false)
    private Reclamacion reclamacion;

    @ManyToOne(optional = false)
    private PreguntaVerificacion pregunta;

    protected RespuestaVerificacion() {
    }

    public RespuestaVerificacion(Reclamacion reclamacion, PreguntaVerificacion pregunta, String textoPrivado) {
        this.reclamacion = reclamacion;
        this.pregunta = pregunta;
        this.textoPrivado = textoPrivado;
    }

    public UUID getId() {
        return id;
    }

    public String getTextoPrivado() {
        return textoPrivado;
    }

    public Reclamacion getReclamacion() {
        return reclamacion;
    }

    public PreguntaVerificacion getPregunta() {
        return pregunta;
    }
}
