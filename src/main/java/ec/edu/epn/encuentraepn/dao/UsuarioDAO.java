package ec.edu.epn.encuentraepn.dao;

import ec.edu.epn.encuentraepn.modelo.Usuario;

import java.util.List;

public class UsuarioDAO extends GenericDAO<Usuario> {

    public UsuarioDAO() {
        super(Usuario.class);
    }

    public Usuario buscarPorCorreo(String correo) {
        List<Usuario> usuarios = em()
                .createQuery("SELECT u FROM Usuario u WHERE u.correo = :correo", Usuario.class)
                .setParameter("correo", correo)
                .getResultList();
        return usuarios.isEmpty() ? null : usuarios.get(0);
    }

    /**
     * Devuelve el usuario registrado con ese correo o lo registra si no existe.
     */
    public Usuario obtenerORegistrar(String nombre, String correo) {
        Usuario usuario = buscarPorCorreo(correo);
        return usuario != null ? usuario : guardar(new Usuario(nombre, correo));
    }
}
