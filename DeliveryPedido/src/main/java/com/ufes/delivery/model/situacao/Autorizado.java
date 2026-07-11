package com.ufes.delivery.model.situacao;

public final class Autorizado implements Situacao {

    public static final Autorizado INSTANCIA = new Autorizado();

    private Autorizado() {}

    @Override
    public String getDescricao() {
        return "Autorizado";
    }

    @Override
    public boolean podeIniciarSessao() {
        return true;
    }

    @Override
    public Situacao autorizar() {
        return this;
    }

    @Override
    public Situacao desautorizar() {
        return NaoAutorizado.INSTANCIA;
    }
}
