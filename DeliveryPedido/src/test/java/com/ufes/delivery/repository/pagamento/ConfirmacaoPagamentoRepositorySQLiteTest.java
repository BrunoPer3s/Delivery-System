package com.ufes.delivery.repository.pagamento;

import com.ufes.delivery.model.Produto;
import com.ufes.delivery.model.estado.EstadosPedido;
import com.ufes.delivery.persistencia.BancoDados;
import com.ufes.delivery.repository.pedido.PedidoRegistro;
import com.ufes.delivery.repository.pedido.PedidoRepositorySQLite;
import com.ufes.delivery.repository.produto.ProdutoRepositorySQLite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("US11 DoD 3 - Aprovação, baixa de estoque e mudança de estado em transação única")
class ConfirmacaoPagamentoRepositorySQLiteTest {

    private static final int CADERNO = 2001;
    private static final int LIVRO = 2002;
    private static final int PEDIDO = 1001;

    @TempDir
    Path diretorio;

    private BancoDados banco;
    private ProdutoRepositorySQLite produtoRepository;
    private PedidoRepositorySQLite pedidoRepository;

    @BeforeEach
    void preparar() {
        banco = new BancoDados(diretorio.resolve("teste.db").toString());
        banco.inicializar();
        produtoRepository = new ProdutoRepositorySQLite(banco);
        pedidoRepository = new PedidoRepositorySQLite(banco);
    }

    private String hoje() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private int estoque(int codigo) {
        return produtoRepository.buscarPorCodigo(codigo).orElseThrow().getEstoqueAtual();
    }

    private PedidoRegistro pedidoAguardandoEntrega() {
        return new PedidoRegistro(PEDIDO, "Fulano de Tal", hoje(), null,
                EstadosPedido.AGUARDANDO_ENTREGA, "R$ 140,30");
    }

    private List<Produto> produtosComBaixa() {
        Produto caderno = produtoRepository.buscarPorCodigo(CADERNO).orElseThrow();
        Produto livro = produtoRepository.buscarPorCodigo(LIVRO).orElseThrow();
        caderno.ajustarEstoque(-10);
        livro.ajustarEstoque(-5);
        return List.of(caderno, livro);
    }

    @Test
    @DisplayName("A confirmação baixa o estoque e grava o pedido em Aguardando entrega")
    void confirmacaoBaixaEstoqueEGravaPedido() {
        IConfirmacaoPagamentoRepository confirmacao = new ConfirmacaoPagamentoRepositorySQLite(
                banco, produtoRepository, pedidoRepository);

        confirmacao.confirmar(produtosComBaixa(), pedidoAguardandoEntrega());

        assertEquals(110, estoque(CADERNO));
        assertEquals(30, estoque(LIVRO));

        PedidoRegistro gravado = pedidoRepository.buscarPorCodigo(PEDIDO).orElseThrow();
        assertEquals(EstadosPedido.AGUARDANDO_ENTREGA, gravado.getEstado());
    }

    @Test
    @DisplayName("Uma falha ao gravar o pedido desfaz a baixa de estoque")
    void falhaAoGravarPedidoDesfazBaixaDeEstoque() {
        PedidoRepositorySQLite pedidoQueFalha = new PedidoRepositorySQLite(banco) {
            @Override
            public void gravar(Connection conexao, PedidoRegistro pedido) throws SQLException {
                throw new SQLException("Falha simulada ao gravar o pedido");
            }
        };

        IConfirmacaoPagamentoRepository confirmacao = new ConfirmacaoPagamentoRepositorySQLite(
                banco, produtoRepository, pedidoQueFalha);

        assertThrows(RuntimeException.class,
                () -> confirmacao.confirmar(produtosComBaixa(), pedidoAguardandoEntrega()));

        assertEquals(120, estoque(CADERNO));
        assertEquals(35, estoque(LIVRO));
        assertTrue(pedidoRepository.buscarPorCodigo(PEDIDO).isEmpty());
    }

    @Test
    @DisplayName("Uma falha ao baixar o estoque não grava o pedido")
    void falhaAoBaixarEstoqueNaoGravaPedido() {
        ProdutoRepositorySQLite produtoQueFalha = new ProdutoRepositorySQLite(banco) {
            @Override
            public void gravarEmLote(Connection conexao, List<Produto> produtos)
                    throws SQLException {
                throw new SQLException("Falha simulada ao baixar o estoque");
            }
        };

        IConfirmacaoPagamentoRepository confirmacao = new ConfirmacaoPagamentoRepositorySQLite(
                banco, produtoQueFalha, pedidoRepository);

        assertThrows(RuntimeException.class,
                () -> confirmacao.confirmar(produtosComBaixa(), pedidoAguardandoEntrega()));

        assertEquals(120, estoque(CADERNO));
        assertTrue(pedidoRepository.buscarPorCodigo(PEDIDO).isEmpty());
    }

    @Test
    @DisplayName("A confirmação exige ao menos um produto para baixa")
    void confirmacaoExigeProdutos() {
        IConfirmacaoPagamentoRepository confirmacao = new ConfirmacaoPagamentoRepositorySQLite(
                banco, produtoRepository, pedidoRepository);

        assertThrows(IllegalArgumentException.class,
                () -> confirmacao.confirmar(List.of(), pedidoAguardandoEntrega()));

        assertTrue(pedidoRepository.buscarPorCodigo(PEDIDO).isEmpty());
    }
}
