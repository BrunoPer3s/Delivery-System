package com.ufes.delivery.model.estado;

public final class AguardandoPagamento implements EstadoPedido {

    public static final AguardandoPagamento INSTANCIA = new AguardandoPagamento();

    private AguardandoPagamento() {}

    @Override
    public String getNome() {
        return "Aguardando pagamento";
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

