package edu.unisabana.dyas.pos.configuracion;

public class ValorInvalidoException extends RuntimeException {

    public ValorInvalidoException(String clave, String valor, Throwable causa) {
        super("Valor inválido para " + clave + ": " + valor, causa);
    }
}
