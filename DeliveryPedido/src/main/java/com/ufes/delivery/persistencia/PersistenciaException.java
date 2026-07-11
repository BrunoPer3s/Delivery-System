package com.ufes.delivery.persistencia;

public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
