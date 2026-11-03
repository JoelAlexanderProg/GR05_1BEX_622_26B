package ec.edu.epn.encuentraepn.controlador;

/**
 * Indica que una operación no cumple una regla del sistema. El mensaje se
 * muestra al usuario.
 */
public class OperacionInvalidaException extends RuntimeException {

    public OperacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
