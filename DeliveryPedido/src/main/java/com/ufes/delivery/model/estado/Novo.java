package com.ufes.delivery.model.estado;

public final class Novo implements EstadoPedido {

    public static final Novo INSTANCIA = new Novo();

    private Novo() {}

    @Override
    public String getNome() {
        return "Novo";
    }

    @Override
    public EstadoPedido avancar() {
        return AguardandoPagamento.INSTANCIA;
    }

    @Override
    public boolean isConclusivo() {
        return false;
    }
}

