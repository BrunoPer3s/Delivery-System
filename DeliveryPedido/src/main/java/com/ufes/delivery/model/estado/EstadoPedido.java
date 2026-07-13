package com.ufes.delivery.model.estado;

import java.util.List;

public abstract class EstadoPedido {

    private final String nome;

    protected EstadoPedido(String nome) {
        this.nome = nome;
    }

    public abstract EstadoPedido avancar();

    public abstract boolean isConclusivo();

    public String getNome() {
        return nome;
    }

    public static List<EstadoPedido> todos() {
        return List.of(
                Novo.INSTANCIA,
                AguardandoPagamento.INSTANCIA,
                EmPreparo.INSTANCIA,
                AguardandoEntrega.INSTANCIA,
                EmTransito.INSTANCIA,
                Entregue.INSTANCIA);
    }

    public static EstadoPedido porNome(String nome) {
        for (EstadoPedido estado : todos()) {
            if (estado.getNome().equals(nome)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de pedido desconhecido: " + nome);
    }
}
