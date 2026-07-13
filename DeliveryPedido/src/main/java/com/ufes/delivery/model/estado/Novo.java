package com.ufes.delivery.model.estado;

public final class Novo extends EstadoPedido {

    public static final Novo INSTANCIA = new Novo();

    private Novo() {
        super("Novo");
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

