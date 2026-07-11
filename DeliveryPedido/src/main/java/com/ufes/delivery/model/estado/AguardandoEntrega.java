package com.ufes.delivery.model.estado;

public final class AguardandoEntrega implements EstadoPedido {

    public static final AguardandoEntrega INSTANCIA = new AguardandoEntrega();

    private AguardandoEntrega() {}

    @Override
    public String getNome() {
        return "Aguardando entrega";
    }

    @Override
    public EstadoPedido avancar() {
        return EmTransito.INSTANCIA;
    }

    @Override
    public boolean isConclusivo() {
        return false;
    }
}

