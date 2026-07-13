package com.ufes.delivery.model.situacao;

public final class Pendente extends Situacao {

    public static final Pendente INSTANCIA = new Pendente();

    private Pendente() {
        super("Pendente");
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
        return NaoAutorizado.INSTANCIA;
    }
}
