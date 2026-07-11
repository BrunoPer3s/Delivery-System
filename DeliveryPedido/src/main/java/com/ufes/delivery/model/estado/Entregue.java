package com.ufes.delivery.model.estado;

public final class Entregue implements EstadoPedido {

    public static final Entregue INSTANCIA = new Entregue();

    private Entregue() {}

    @Override
    public String getNome() {
        return "Entregue";
    }

    @Override
    public EstadoPedido avancar() {
        throw new IllegalStateException(
                "Pedido já está \"Entregue\"; não há transição de estado disponível");
    }

    @Override
    public boolean isConclusivo() {
        return true;
    }
}

