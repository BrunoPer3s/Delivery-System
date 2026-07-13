package com.ufes.delivery.model.estado;

public final class AguardandoEntrega extends EstadoPedido {

    public static final AguardandoEntrega INSTANCIA = new AguardandoEntrega();

    private AguardandoEntrega() {
        super("Aguardando entrega");
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

