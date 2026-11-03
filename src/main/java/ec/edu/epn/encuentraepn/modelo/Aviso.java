package ec.edu.epn.encuentraepn.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * HU-16: aviso que el sistema deja a un usuario cuando pasa algo que le interesa.
 * Lo lee en «Mis avisos» y el enlace lo lleva a la página donde puede actuar.
 */
@Entity
@Table(name = "aviso")
public class Aviso {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    private Usuario destinatario;

    @Column(nullable = false, length = 400)
    private String texto;

    /** Ruta dentro de la aplicación, por ejemplo «/reportes/detalle?id=…». */
    @Column(nullable = false, length = 200)
    private String enlace;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private boolean leido;

    protected Aviso() {
    }

    public Aviso(Usuario destinatario, String texto, String enlace) {
        this.destinatario = destinatario;
        this.texto = texto;
        this.enlace = enlace;
        this.fecha = LocalDateTime.now();
        this.leido = false;
    }

    /**
     * HU-16: avisa al dueño de un objeto perdido que alguien reportó un objeto encontrado que coincide.
     */
    public static Aviso coincidencia(Reporte perdido, Reporte encontrado) {
        long metros = Math.round(perdido.distanciaA(encontrado));
        return new Aviso(perdido.getAutor(),
                "Se reportó un objeto encontrado que coincide con «" + perdido.getDescripcionPublica() + "»: «"
                        + encontrado.getDescripcionPublica() + "», a " + metros + " m del lugar donde lo perdió.",
                "/reportes/detalle?id=" + encontrado.getId());
    }

    /** HU-18: enlace a «Mis reclamaciones» en la página de inicio. */
    public static final String MIS_RECLAMACIONES = "/inicio#reclamaciones";

    /** HU-18: avisa al reclamante que su reclamación fue aceptada. */
    public static Aviso reclamacionAceptada(Reclamacion reclamacion) {
        return new Aviso(reclamacion.getReclamante(), "Su reclamación de «" + objetoDe(reclamacion)
                + "» fue aceptada. Pronto recibirá la propuesta de entrega.", MIS_RECLAMACIONES);
    }

    /** HU-18: avisa al reclamante que su reclamación fue rechazada, con el motivo. */
    public static Aviso reclamacionRechazada(Reclamacion reclamacion) {
        return new Aviso(reclamacion.getReclamante(), "Su reclamación de «" + objetoDe(reclamacion)
                + "» fue rechazada. Motivo: " + reclamacion.getMotivoRechazo(), MIS_RECLAMACIONES);
    }

    /**
     * HU-18: avisa al reclamante el lugar y la fecha propuestos para la entrega. El enlace lo lleva al acuerdo
     * para confirmarlo.
     */
    public static Aviso entregaPropuesta(AcuerdoEntrega acuerdo) {
        Reclamacion reclamacion = acuerdo.getReclamacion();
        return new Aviso(reclamacion.getReclamante(), "Le propusieron entregarle «" + objetoDe(reclamacion) + "» en "
                + acuerdo.getLugar() + " el " + acuerdo.getFechaPropuesta().toString().replace('T', ' ')
                + ". Confirme el acuerdo de entrega.", "/devoluciones?acuerdo=" + reclamacion.getId());
    }

    private static String objetoDe(Reclamacion reclamacion) {
        return reclamacion.getObjeto().getDescripcionPublica();
    }

    /** HU-17: el usuario ya vio el aviso en su bandeja. */
    public void marcarLeido() {
        leido = true;
    }

    public UUID getId() {
        return id;
    }

    public Usuario getDestinatario() {
        return destinatario;
    }

    public String getTexto() {
        return texto;
    }

    public String getEnlace() {
        return enlace;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public boolean isLeido() {
        return leido;
    }
}
