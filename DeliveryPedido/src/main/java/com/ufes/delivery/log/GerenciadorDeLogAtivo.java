package com.ufes.delivery.log;

import com.ufes.log.ILogger;
import com.ufes.log.model.MensagemLog;

public class GerenciadorDeLogAtivo implements ILogger {
    private ILogger loggerAtivo;

    public GerenciadorDeLogAtivo(ILogger loggerInicial) {
        if (loggerInicial == null) {
            throw new IllegalArgumentException("O logger inicial não pode ser nulo");
        }
        this.loggerAtivo = loggerInicial;
    }

    public void setLoggerAtivo(ILogger novoLogger) {
        if (novoLogger != null) {
            this.loggerAtivo = novoLogger;
        }
    }

    @Override
    public void registrar(MensagemLog mensagem) {
        this.loggerAtivo.registrar(mensagem);
    }
}


