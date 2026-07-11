package com.ufes.delivery.model.estado;

public interface EstadoPedido {

    String getNome();

    EstadoPedido avancar();

    boolean isConclusivo();
}

