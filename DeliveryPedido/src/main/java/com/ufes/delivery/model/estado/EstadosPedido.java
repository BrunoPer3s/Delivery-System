package com.ufes.delivery.model.estado;

import java.util.List;

public final class EstadosPedido {

    public static final EstadoPedido NOVO = Novo.INSTANCIA;
    public static final EstadoPedido AGUARDANDO_PAGAMENTO = AguardandoPagamento.INSTANCIA;
    public static final EstadoPedido EM_PREPARO = EmPreparo.INSTANCIA;
    public static final EstadoPedido AGUARDANDO_ENTREGA = AguardandoEntrega.INSTANCIA;
    public static final EstadoPedido EM_TRANSITO = EmTransito.INSTANCIA;
    public static final EstadoPedido ENTREGUE = Entregue.INSTANCIA;

    public static final List<EstadoPedido> CICLO = List.of(
            NOVO, AGUARDANDO_PAGAMENTO, EM_PREPARO,
            AGUARDANDO_ENTREGA, EM_TRANSITO, ENTREGUE);

    private EstadosPedido() {}

    public static EstadoPedido porNome(String nome) {
        for (EstadoPedido estado : CICLO) {
            if (estado.getNome().equals(nome)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de pedido desconhecido: " + nome);
    }
}

