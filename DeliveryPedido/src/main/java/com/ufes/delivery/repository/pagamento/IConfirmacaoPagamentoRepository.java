package com.ufes.delivery.repository.pagamento;

import com.ufes.delivery.model.Produto;
import com.ufes.delivery.repository.pedido.PedidoRegistro;

import java.util.List;

public interface IConfirmacaoPagamentoRepository {

    void confirmar(List<Produto> produtosComBaixa, PedidoRegistro pedido);
}
