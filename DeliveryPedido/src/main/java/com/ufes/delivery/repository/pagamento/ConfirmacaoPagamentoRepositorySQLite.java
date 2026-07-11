package com.ufes.delivery.repository.pagamento;

import com.ufes.delivery.model.Produto;
import com.ufes.delivery.persistencia.BancoDados;
import com.ufes.delivery.repository.pedido.PedidoRegistro;
import com.ufes.delivery.repository.pedido.PedidoRepositorySQLite;
import com.ufes.delivery.repository.produto.ProdutoRepositorySQLite;

import java.util.List;

public class ConfirmacaoPagamentoRepositorySQLite implements IConfirmacaoPagamentoRepository {

    private final BancoDados banco;
    private final ProdutoRepositorySQLite produtoRepository;
    private final PedidoRepositorySQLite pedidoRepository;

    public ConfirmacaoPagamentoRepositorySQLite(BancoDados banco,
                                                 ProdutoRepositorySQLite produtoRepository,
                                                 PedidoRepositorySQLite pedidoRepository) {
        this.banco = banco;
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

        banco.executarEmTransacao(conexao -> {
            produtoRepository.gravarEmLote(conexao, produtosComBaixa);
            pedidoRepository.gravar(conexao, pedido);
        });

        produtoRepository.notificarAlteracao();
        pedidoRepository.notificarAlteracao();
    }
}
