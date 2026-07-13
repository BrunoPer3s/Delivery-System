package com.ufes.delivery.busca;

import com.ufes.delivery.model.Cliente;
import com.ufes.delivery.model.Produto;
import com.ufes.delivery.repository.cliente.ClienteRepositoryEmMemoria;
import com.ufes.delivery.repository.cliente.IClienteRepository;
import com.ufes.delivery.repository.produto.IProdutoRepository;
import com.ufes.delivery.repository.produto.ProdutoRepositoryEmMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("US05 e US07 - Estratégias de busca")
class EstrategiasBuscasTest {

    private IClienteRepository clienteRepository;
    private IProdutoRepository produtoRepository;

    @BeforeEach
    void preparar() {
        clienteRepository = new ClienteRepositoryEmMemoria();
        produtoRepository = new ProdutoRepositoryEmMemoria();
    }

    private EstrategiaBusca<Cliente, IClienteRepository> cliente(String rotulo) {
        return EstrategiasBuscasFactory.paraCliente(rotulo);
    }

    private EstrategiaBusca<Produto, IProdutoRepository> produto(String rotulo) {
        return EstrategiasBuscasFactory.paraProduto(rotulo);
    }

    @Test
    @DisplayName("A fábrica expõe os rótulos previstos para cliente")
    void rotulosDeCliente() {
        List<String> rotulos = EstrategiasBuscasFactory.estrategiasDeCliente()
                .stream().map(EstrategiaBusca::getRotulo).toList();

        assertEquals(List.of("Nome", "CPF"), rotulos);
    }

    @Test
    @DisplayName("A fábrica expõe os rótulos previstos para produto")
    void rotulosDeProduto() {
        List<String> rotulos = EstrategiasBuscasFactory.estrategiasDeProduto()
                .stream().map(EstrategiaBusca::getRotulo).toList();

        assertEquals(List.of("Nome", "Código", "Categoria"), rotulos);
    }

    @Test
    @DisplayName("Um rótulo desconhecido é rejeitado")
    void rotuloDesconhecidoERejeitado() {
        assertThrows(BuscaInvalidaException.class, () -> cliente("Telefone"));
        assertThrows(BuscaInvalidaException.class, () -> produto("Fornecedor"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Nome", "CPF"})
    @DisplayName("US05 Cenário 4 - Rejeitar valor de busca ausente para cliente")
    void rejeitaValorAusenteNaBuscaDeCliente(String rotulo) {
        BuscaInvalidaException vazio = assertThrows(BuscaInvalidaException.class,
                () -> cliente(rotulo).buscar("   ", clienteRepository));
        BuscaInvalidaException nulo = assertThrows(BuscaInvalidaException.class,
                () -> cliente(rotulo).buscar(null, clienteRepository));

        assertEquals("O valor da busca é obrigatório.", vazio.getMessage());
        assertEquals("O valor da busca é obrigatório.", nulo.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Nome", "Código", "Categoria"})
    @DisplayName("US07 - Rejeitar valor de busca ausente para produto")
    void rejeitaValorAusenteNaBuscaDeProduto(String rotulo) {
        BuscaInvalidaException vazio = assertThrows(BuscaInvalidaException.class,
                () -> produto(rotulo).buscar("  ", produtoRepository));
        BuscaInvalidaException nulo = assertThrows(BuscaInvalidaException.class,
                () -> produto(rotulo).buscar(null, produtoRepository));

        assertEquals("O valor da busca é obrigatório.", vazio.getMessage());
        assertEquals("O valor da busca é obrigatório.", nulo.getMessage());
    }

    @Test
    @DisplayName("US05 Cenário 3 - Rejeitar CPF inválido pelos dígitos verificadores")
    void rejeitaCpfInvalido() {
        BuscaInvalidaException erro = assertThrows(BuscaInvalidaException.class,
                () -> cliente("CPF").buscar("111.111.111-11", clienteRepository));

        assertEquals("CPF inválido.", erro.getMessage());
    }

    @Test
    @DisplayName("US05 Cenário 2 - Buscar cliente por CPF com máscara considera só os 11 dígitos")
    void buscaClientePorCpfComMascara() {
        List<Cliente> comMascara = cliente("CPF").buscar("529.982.247-25", clienteRepository);
        List<Cliente> semMascara = cliente("CPF").buscar("52998224725", clienteRepository);

        assertFalse(comMascara.isEmpty());
        assertEquals(comMascara.get(0).getNome(), semMascara.get(0).getNome());
    }

    @Test
    @DisplayName("US05 Cenário 1 - Buscar cliente por nome parcial")
    void buscaClientePorNomeParcial() {
        List<Cliente> resultados = cliente("Nome").buscar("Ful", clienteRepository);

        assertFalse(resultados.isEmpty());
        assertTrue(resultados.stream().allMatch(c -> c.getNome().contains("Ful")));
    }

    @Test
    @DisplayName("US07 Cenário 1 - Buscar produto por nome parcial")
    void buscaProdutoPorNomeParcial() {
        List<Produto> resultados = produto("Nome").buscar("Caderno", produtoRepository);

        assertEquals(1, resultados.size());
        assertEquals("Caderno Universitário", resultados.get(0).getNome());
    }

    @Test
    @DisplayName("US07 - Buscar produto por código")
    void buscaProdutoPorCodigo() {
        List<Produto> resultados = produto("Código").buscar("2001", produtoRepository);

        assertEquals(1, resultados.size());
        assertEquals(2001, resultados.get(0).getCodigo());
    }

    @Test
    @DisplayName("US07 - Rejeitar código não inteiro")
    void rejeitaCodigoNaoInteiro() {
        BuscaInvalidaException erro = assertThrows(BuscaInvalidaException.class,
                () -> produto("Código").buscar("abc", produtoRepository));

        assertEquals("Código deve ser um número inteiro.", erro.getMessage());
    }

    @Test
    @DisplayName("US07 - Buscar produto por categoria")
    void buscaProdutoPorCategoria() {
        List<Produto> resultados = produto("Categoria").buscar("Lazer", produtoRepository);

        assertEquals(1, resultados.size());
        assertEquals("Jogo de Xadrez", resultados.get(0).getNome());
    }
}
