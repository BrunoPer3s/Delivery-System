package com.ufes.delivery.model.estado;

public final class AguardandoPagamento extends EstadoPedido {

    public static final AguardandoPagamento INSTANCIA = new AguardandoPagamento();

    private AguardandoPagamento() {
        super("Aguardando pagamento");
    }

    @Override
    public EstadoPedido avancar() {
        return EmPreparo.INSTANCIA;
    }

    @Override
    public boolean isConclusivo() {
        return false;
    }
}

