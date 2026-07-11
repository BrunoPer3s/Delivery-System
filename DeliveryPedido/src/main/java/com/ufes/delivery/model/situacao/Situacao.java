package com.ufes.delivery.model.situacao;

public interface Situacao {

    String getDescricao();

    boolean podeIniciarSessao();

    Situacao autorizar();

    Situacao desautorizar();
}
