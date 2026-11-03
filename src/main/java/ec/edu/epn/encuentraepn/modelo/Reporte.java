package ec.edu.epn.encuentraepn.modelo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Reporte de un objeto perdido o encontrado en el campus.
 * Diagrama de clases: «entity» Reporte.
 */
@Entity
@Table(name = "reporte")
public class Reporte {

    /** HU-14: distancia máxima, en metros, entre el lugar de la pérdida y el del hallazgo. */
    public static final int RADIO_COINCIDENCIA_METROS = 200;

    /** HU-14: días después de la pérdida en los que un hallazgo todavía puede ser el mismo objeto. */
    public static final int DIAS_COINCIDENCIA = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private TipoReporte tipo;

    @Column(nullable = false, length = 200)
    private String descripcionPublica;

    @Column(nullable = false)
    private LocalDate fechaSuceso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private EstadoReporte estado;

    private LocalDateTime fechaDevolucion;

    @ManyToOne(optional = false)
    private Usuario autor;

    @ManyToOne(optional = false)
    private Categoria categoria;

    @Embedded
    private Ubicacion ubicacion;

    @OneToMany(mappedBy = "reporte", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PreguntaVerificacion> preguntas = new ArrayList<>();

    /** HU-15: en un objeto encontrado, el reporte de pérdida de su dueño, si quien lo encontró lo avisó. */
    @ManyToOne
    private Reporte reportePerdido;

    protected Reporte() {
    }

    // SEC-01 · 8: «create» Reporte(datos, usuario, categoria, ubicacion)
    public Reporte(DatosReporte datos, Usuario autor, Categoria categoria, Ubicacion ubicacion) {
        this.tipo = datos.tipo();
        this.descripcionPublica = datos.descripcionPublica();
        this.fechaSuceso = datos.fechaSuceso();
        this.autor = autor;
        this.categoria = categoria;
        this.ubicacion = ubicacion;
        this.estado = EstadoReporte.BORRADOR;
    }

    // SEC-01 · 9: «create» preguntas asociadas al reporte
    public void agregarPregunta(String texto) {
        preguntas.add(new PreguntaVerificacion(this, texto));
    }

    // SEC-01 · 10: publicar()
    public void publicar() {
        cambiarEstado(EstadoReporte.BORRADOR, EstadoReporte.ACTIVO);
    }

    // SEC-04 · 18: reservar()
    public void reservar() {
        cambiarEstado(EstadoReporte.ACTIVO, EstadoReporte.RESERVADO);
    }

    // SEC-04 · 40: marcarEntregado(fecha)
    public void marcarEntregado(LocalDateTime fecha) {
        cambiarEstado(EstadoReporte.RESERVADO, EstadoReporte.ENTREGADO);
        fechaDevolucion = fecha;
    }

    /**
     * HU-19: si la entrega acordada no se realizó, el objeto reservado vuelve a estar disponible en el mapa.
     */
    public void liberar() {
        cambiarEstado(EstadoReporte.RESERVADO, EstadoReporte.ACTIVO);
    }

    /**
     * HU-11: quien registró el reporte lo retira y deja de aparecer en el mapa.
     */
    public void retirar() {
        cambiarEstado(EstadoReporte.ACTIVO, EstadoReporte.RETIRADO);
    }

    /**
     * HU-12: indica si la descripción del reporte contiene la palabra buscada.
     */
    public boolean contienePalabra(String palabra) {
        return normalizar(descripcionPublica).contains(normalizar(palabra));
    }

    /**
     * HU-14: indica si un objeto encontrado puede ser este objeto perdido. Coincide si está activo,
     * es de la misma categoría, se encontró a {@value #RADIO_COINCIDENCIA_METROS} m o menos y
     * entre la fecha de la pérdida y {@value #DIAS_COINCIDENCIA} días después.
     */
    public boolean coincideCon(Reporte encontrado) {
        LocalDate limite = fechaSuceso.plusDays(DIAS_COINCIDENCIA);
        return tipo == TipoReporte.PERDIDO
                && encontrado.esEncontrado()
                && encontrado.estaActivo()
                && esDeLaMismaCategoria(encontrado)
                && !encontrado.fechaSuceso.isBefore(fechaSuceso)
                && !encontrado.fechaSuceso.isAfter(limite)
                && distanciaA(encontrado) <= RADIO_COINCIDENCIA_METROS;
    }

    /**
     * HU-15: quien encontró este objeto avisa al dueño de un reporte de pérdida que lo tiene.
     * El reporte de pérdida debe estar activo y ser de otra persona.
     */
    public void vincularConPerdido(Reporte perdido) {
        if (!esEncontrado() || perdido.esEncontrado()) {
            throw new IllegalArgumentException("Solo un objeto encontrado se vincula con un reporte de pérdida.");
        }
        if (!perdido.estaActivo()) {
            throw new IllegalStateException("Este reporte de pérdida ya no está activo.");
        }
        if (perdido.esAutor(autor)) {
            throw new IllegalArgumentException("No puede avisar que encontró un objeto que usted mismo reportó.");
        }
        reportePerdido = perdido;
    }

    /** HU-14: distancia en metros entre el lugar de este reporte y el de otro. */
    public double distanciaA(Reporte otro) {
        return ubicacion.distanciaA(otro.ubicacion);
    }

    /**
     * Compara las categorías por su identificador; si todavía no se guardaron, por la misma instancia.
     */
    private boolean esDeLaMismaCategoria(Reporte otro) {
        return categoria == otro.categoria
                || categoria.getId() != null && categoria.getId().equals(otro.categoria.getId());
    }

    /**
     * Texto en minúsculas, sin tildes y sin espacios al inicio ni al final, para comparar búsquedas.
     */
    private static String normalizar(String texto) {
        return Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }

    /**
     * Pasa del estado esperado al nuevo. Si el reporte está en otro estado, lanza IllegalStateException.
     */
    private void cambiarEstado(EstadoReporte desde, EstadoReporte hacia) {
        exigirEstado(desde);
        estado = hacia;
    }

    private void exigirEstado(EstadoReporte esperado) {
        if (estado != esperado) {
            throw new IllegalStateException("El reporte debe estar en estado " + esperado + " y está " + estado);
        }
    }

    public boolean esEncontrado() {
        return tipo == TipoReporte.ENCONTRADO;
    }

    public boolean estaActivo() {
        return estado == EstadoReporte.ACTIVO;
    }

    public boolean esAutor(Usuario usuario) {
        return autor.equals(usuario);
    }

    public UUID getId() {
        return id;
    }

    public TipoReporte getTipo() {
        return tipo;
    }

    public String getDescripcionPublica() {
        return descripcionPublica;
    }

    public LocalDate getFechaSuceso() {
        return fechaSuceso;
    }

    public EstadoReporte getEstado() {
        return estado;
    }

    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    public Usuario getAutor() {
        return autor;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public List<PreguntaVerificacion> getPreguntas() {
        return Collections.unmodifiableList(preguntas);
    }

    public Reporte getReportePerdido() {
        return reportePerdido;
    }
}
