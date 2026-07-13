package com.ufes.delivery.apoio;

import com.ufes.log.ILogger;
import com.ufes.log.model.MensagemLog;

import java.util.ArrayList;
import java.util.List;

public class LoggerEmMemoria implements ILogger {

    private final List<MensagemLog> registros = new ArrayList<>();

    @Override
    public void registrar(MensagemLog mensagem) {
        registros.add(mensagem);
    }

    public List<MensagemLog> getRegistros() {
        return registros;
    }

    public String getUltimaOperacao() {
        if (registros.isEmpty()) {
            return null;
        }
        return registros.get(registros.size() - 1).getNomeOperacao();
    }

    public boolean vazio() {
        return registros.isEmpty();
    }
}
