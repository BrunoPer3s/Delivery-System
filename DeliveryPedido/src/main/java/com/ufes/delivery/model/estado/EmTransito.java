package com.ufes.delivery.model.estado;

public final class EmTransito implements EstadoPedido {

    public static final EmTransito INSTANCIA = new EmTransito();

    private EmTransito() {}

    @Override
    public String getNome() {
        return "Em trânsito";
    }

    @Override
    public EstadoPedido avancar() {
        return Entregue.INSTANCIA;
    }

    @Override
    public boolean isConclusivo() {
        return false;
    }
}

