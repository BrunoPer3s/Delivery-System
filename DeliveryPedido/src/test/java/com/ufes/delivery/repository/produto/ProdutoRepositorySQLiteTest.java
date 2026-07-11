package com.ufes.delivery.repository.produto;

import com.ufes.delivery.model.Produto;
import com.ufes.delivery.persistencia.BancoDados;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Baixa de estoque em transação única")
class ProdutoRepositorySQLiteTest {

    private static final int CADERNO = 2001;
    private static final int LIVRO = 2002;

    @TempDir
    Path diretorio;

    private IProdutoRepository produtoRepository;

    @BeforeEach
    void preparar() {
        BancoDados banco = new BancoDados(diretorio.resolve("teste.db").toString());
        banco.inicializar();
        produtoRepository = new ProdutoRepositorySQLite(banco);
    }

    private int estoque(int codigo) {
        return produtoRepository.buscarPorCodigo(codigo).orElseThrow().getEstoqueAtual();
    }

    @Test
    @DisplayName("salvarEmLote persiste a baixa de todos os produtos do pedido")
    void salvarEmLotePersisteTodosOsProdutos() {
        Produto caderno = produtoRepository.buscarPorCodigo(CADERNO).orElseThrow();
        Produto livro = produtoRepository.buscarPorCodigo(LIVRO).orElseThrow();

        caderno.ajustarEstoque(-10);
        livro.ajustarEstoque(-5);
        produtoRepository.salvarEmLote(List.of(caderno, livro));

        assertEquals(110, estoque(CADERNO));
        assertEquals(30, estoque(LIVRO));
    }

    @Test
    @DisplayName("Um lote inválido não persiste alteração parcial")
    void loteInvalidoNaoPersisteAlteracaoParcial() {
        Produto caderno = produtoRepository.buscarPorCodigo(CADERNO).orElseThrow();
        caderno.ajustarEstoque(-10);

        List<Produto> lote = new ArrayList<>(Arrays.asList(caderno, null));

        assertThrows(IllegalArgumentException.class, () -> produtoRepository.salvarEmLote(lote));

        assertEquals(120, estoque(CADERNO));
    }

    @Test
    @DisplayName("Um lote vazio não altera o estoque")
    void loteVazioNaoAlteraEstoque() {
        produtoRepository.salvarEmLote(List.of());

        assertEquals(120, estoque(CADERNO));
        assertEquals(35, estoque(LIVRO));
    }
}
