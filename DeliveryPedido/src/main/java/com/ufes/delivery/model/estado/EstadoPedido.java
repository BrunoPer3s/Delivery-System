package com.ufes.delivery.model.estado;

import java.util.ArrayList;
import java.util.List;

public abstract class EstadoPedido {

    private static final List<EstadoPedido> TODOS = new ArrayList<>();

    public abstract EstadoPedido avancar();

    public abstract boolean isConclusivo();

    protected static void todosEstadosPedido(EstadoPedido instancia) {
        TODOS.add(instancia);
    }

    public String getNome() {
        return this.getClass().getSimpleName();
    }

    public static EstadoPedido porNome(String nome) {
        for (EstadoPedido estado : TODOS) {
            if (estado.getNome().equals(nome)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de pedido desconhecido: " + nome);
    }
}

