package com.ufes.log;

import com.ufes.log.model.MensagemLog;

public interface ILogger {
    void registrar(MensagemLog mensagem);
}

