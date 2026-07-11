package com.ufes.delivery.repository.pedido;

public record TransicaoEstadoPedido(int codigoPedido, String estadoAnterior, String estadoNovo) {
}

