package com.ufes.delivery.model.estado;

public final class EmTransito extends EstadoPedido {

    public static final EmTransito INSTANCIA = new EmTransito();

    private EmTransito() {
        todosEstadosPedido(this);
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

