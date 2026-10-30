package edu.unisabana.dyas.pos.configuracion;

public class ClaveDesconocidaException extends RuntimeException {

    public ClaveDesconocidaException(String clave) {
        super("Clave de configuración desconocida: " + clave);
    }
}
