package com.ufes.delivery.model.estado;

public final class EmPreparo implements EstadoPedido {

    public static final EmPreparo INSTANCIA = new EmPreparo();

    private EmPreparo() {}

    @Override
    public String getNome() {
        return "Em preparo";
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

