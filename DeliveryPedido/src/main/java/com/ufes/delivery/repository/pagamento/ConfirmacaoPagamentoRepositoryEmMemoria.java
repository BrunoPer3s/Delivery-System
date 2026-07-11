package com.ufes.delivery.repository.pagamento;

import com.ufes.delivery.model.Produto;
import com.ufes.delivery.repository.pedido.IPedidoRepository;
import com.ufes.delivery.repository.pedido.PedidoRegistro;
import com.ufes.delivery.repository.produto.IProdutoRepository;

import java.util.List;

public class ConfirmacaoPagamentoRepositoryEmMemoria implements IConfirmacaoPagamentoRepository {

    private final IProdutoRepository produtoRepository;
    private final IPedidoRepository pedidoRepository;

    public ConfirmacaoPagamentoRepositoryEmMemoria(IProdutoRepository produtoRepository,
                                                    IPedidoRepository pedidoRepository) {
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public void confirmar(List<Produto> produtosComBaixa, PedidoRegistro pedido) {
        if (produtosComBaixa == null || produtosComBaixa.isEmpty()) {
            throw new IllegalArgumentException(
                "A confirmação do pagamento exige ao menos um produto para baixa");
        }
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido não pode ser nulo");
        }

        produtoRepository.salvarEmLote(produtosComBaixa);
        pedidoRepository.registrar(pedido);
    }
}
