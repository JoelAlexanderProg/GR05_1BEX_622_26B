package ec.edu.epn.encuentraepn.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.UUID;

/**
 * Miembro de la comunidad EPN. Según el caso de uso actúa como reportante,
 * reclamante o encontrante.
 */
@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    public static final String DOMINIO = "@epn.edu.ec";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    protected Usuario() {
    }

    public Usuario(String nombre, String correo) {
        this.nombre = nombre;
        this.correo = correo;
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        return otro instanceof Usuario usuario && id != null && id.equals(usuario.getId());
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}
