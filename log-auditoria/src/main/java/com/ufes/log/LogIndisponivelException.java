package com.ufes.log;

public class LogIndisponivelException extends RuntimeException {

    public LogIndisponivelException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
