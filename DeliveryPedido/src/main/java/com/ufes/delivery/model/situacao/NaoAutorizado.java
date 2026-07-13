package com.ufes.delivery.model.situacao;

public final class NaoAutorizado extends Situacao {

    public static final NaoAutorizado INSTANCIA = new NaoAutorizado();

    private NaoAutorizado() {
        todasSituacoes(this);
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
