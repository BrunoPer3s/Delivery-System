package com.ufes.delivery.model.estado;

public final class EmPreparo extends EstadoPedido {

    public static final EmPreparo INSTANCIA = new EmPreparo();

    private EmPreparo() {
        todosEstadosPedido(this);
    }

    @Override
    public EstadoPedido avancar() {
        return AguardandoEntrega.INSTANCIA;
    }

    @Override
    public boolean isConclusivo() {
        return false;
    }
}

