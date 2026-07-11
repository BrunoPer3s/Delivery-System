package com.ufes.delivery.model.situacao;

public final class NaoAutorizado implements Situacao {

    public static final NaoAutorizado INSTANCIA = new NaoAutorizado();

    private NaoAutorizado() {}

    @Override
    public String getDescricao() {
        return "Nao autorizado";
    }

    @Override
    public boolean podeIniciarSessao() {
        return false;
    }

    @Override
    public Situacao autorizar() {
        return Autorizado.INSTANCIA;
    }

    @Override
    public Situacao desautorizar() {
        return this;
    }
}
