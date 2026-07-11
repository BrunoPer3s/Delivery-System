package com.ufes.delivery.presenter.estoque;

import com.ufes.delivery.apoio.MovimentacaoEstoqueViewStub;
import com.ufes.delivery.model.Usuario;
import com.ufes.delivery.model.perfil.Perfis;
import com.ufes.delivery.model.situacao.Situacoes;
import com.ufes.delivery.persistencia.BancoDados;
import com.ufes.delivery.repository.produto.IProdutoRepository;
import com.ufes.delivery.repository.produto.ProdutoRepositorySQLite;
import com.ufes.delivery.service.SessaoService;
import com.ufes.delivery.util.SenhaUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("US08 - Registrar movimentação de estoque")
class MovimentacaoEstoquePresenterTest {

    private static final int CADERNO = 2001;
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @TempDir
    Path diretorio;

    private IProdutoRepository produtoRepository;
    private SessaoService sessaoService;
    private MovimentacaoEstoqueViewStub view;
    private MovimentacaoEstoquePresenter presenter;

    @BeforeEach
    void preparar() {
        BancoDados banco = new BancoDados(diretorio.resolve("teste.db").toString());
        banco.inicializar();
        produtoRepository = new ProdutoRepositorySQLite(banco);

        sessaoService = SessaoService.getInstancia();
        logarComo(Perfis.ADMINISTRADOR);

        view = new MovimentacaoEstoqueViewStub();
        presenter = new MovimentacaoEstoquePresenter(view, produtoRepository, null, sessaoService);
    }

    @AfterEach
    void encerrar() {
        sessaoService.encerrarSessao();
    }

    private void logarComo(com.ufes.delivery.model.perfil.Perfil perfil) {
        sessaoService.iniciarSessao(new Usuario("Usuario Teste", "usuario01",
                SenhaUtil.hashSenha("Senha123"), perfil, Situacoes.AUTORIZADO));
    }

    private String hoje() {
        return LocalDate.now().format(DATA);
    }

    private int estoqueDoCaderno() {
        return produtoRepository.buscarPorCodigo(CADERNO).orElseThrow().getEstoqueAtual();
    }

    private void selecionarCaderno() {
        view.selecionarProduto(CADERNO);
        presenter.onSelecionar();
    }

    @Test
    @DisplayName("Cenário 1 - A prévia do ajuste não persiste alteração antes da confirmação")
    void previaDeAjusteNaoPersiste() {
        selecionarCaderno();
        assertEquals(120, estoqueDoCaderno());
        assertEquals("120", view.getEstoqueAtualExibido());

        view.preencherMovimentacao(hoje(), "Ajuste de estoque", "-15", "Perda por avaria", null);
        presenter.onQuantidadeAlterada();

        assertEquals("105", view.getEstoquePrevia());
        assertEquals(120, estoqueDoCaderno());
    }

    @Test
    @DisplayName("Cenário 2 - Rejeitar ajuste sem motivo")
    void rejeitaAjusteSemMotivo() {
        selecionarCaderno();
        view.preencherMovimentacao(hoje(), "Ajuste de estoque", "-15", "  ", null);

        presenter.onConfirmarMovimentacao();

        assertEquals("Motivo do ajuste é obrigatório.", view.getMensagemErro());
        assertEquals(120, estoqueDoCaderno());
    }

    @Test
    @DisplayName("Cenário 3 - Exigir nota fiscal na entrada")
    void exigeNotaFiscalNaEntrada() {
        selecionarCaderno();
        view.preencherMovimentacao(hoje(), "Entrada", "30", null, "  ");

        presenter.onConfirmarMovimentacao();

        assertEquals("Nota fiscal de entrada é obrigatória.", view.getMensagemErro());
        assertEquals(120, estoqueDoCaderno());
    }

    @Test
    @DisplayName("Cenário 4 - Rejeitar estoque resultante negativo informando a quantidade disponível")
    void rejeitaEstoqueResultanteNegativo() {
        selecionarCaderno();
        view.preencherMovimentacao(hoje(), "Ajuste de estoque", "-130", "Perda total", null);

        presenter.onConfirmarMovimentacao();

        assertNotNull(view.getMensagemErro());
        assertTrue(view.getMensagemErro().contains("120"));
        assertEquals(120, estoqueDoCaderno());
    }

    @Test
    @DisplayName("Cenário 5 - Confirmar ajuste válido atualiza o estoque com o valor da prévia")
    void confirmaAjusteValido() {
        selecionarCaderno();
        view.preencherMovimentacao(hoje(), "Ajuste de estoque", "-15", "Perda por avaria", null);
        presenter.onQuantidadeAlterada();

        presenter.onConfirmarMovimentacao();

        assertNull(view.getMensagemErro());
        assertNotNull(view.getMensagemSucesso());
        assertEquals(105, estoqueDoCaderno());
    }

    @Test
    @DisplayName("Cenário 5 - Confirmar entrada válida com nota fiscal atualiza o estoque")
    void confirmaEntradaValida() {
        selecionarCaderno();
        view.preencherMovimentacao(hoje(), "Entrada", "30", null, "NF-12345");

        presenter.onConfirmarMovimentacao();

        assertNull(view.getMensagemErro());
        assertEquals(150, estoqueDoCaderno());
    }

    @Test
    @DisplayName("Rejeitar data posterior à data operacional vigente")
    void rejeitaDataFutura() {
        selecionarCaderno();
        String amanha = LocalDate.now().plusDays(1).format(DATA);
        view.preencherMovimentacao(amanha, "Entrada", "30", null, "NF-12345");

        presenter.onConfirmarMovimentacao();

        assertNotNull(view.getMensagemErro());
        assertTrue(view.getMensagemErro().contains("posterior"));
        assertEquals(120, estoqueDoCaderno());
    }

    @Test
    @DisplayName("Rejeitar quantidade igual a zero")
    void rejeitaQuantidadeZero() {
        selecionarCaderno();
        view.preencherMovimentacao(hoje(), "Entrada", "0", null, "NF-12345");

        presenter.onConfirmarMovimentacao();

        assertEquals("Quantidade a movimentar deve ser diferente de zero.", view.getMensagemErro());
        assertEquals(120, estoqueDoCaderno());
    }

    @Test
    @DisplayName("Rejeitar entrada com quantidade negativa em vez de convertê-la em positiva")
    void rejeitaEntradaComQuantidadeNegativa() {
        selecionarCaderno();
        view.preencherMovimentacao(hoje(), "Entrada", "-30", null, "NF-12345");

        presenter.onConfirmarMovimentacao();

        assertNotNull(view.getMensagemErro());
        assertTrue(view.getMensagemErro().contains("positiva"));
        assertEquals(120, estoqueDoCaderno());
    }

    @Test
    @DisplayName("A movimentação é restrita ao Administrador")
    void atendenteNaoConfirmaMovimentacao() {
        selecionarCaderno();
        view.preencherMovimentacao(hoje(), "Entrada", "30", null, "NF-12345");

        logarComo(Perfis.ATENDENTE);
        presenter.onConfirmarMovimentacao();

        assertNotNull(view.getMensagemErro());
        assertTrue(view.getMensagemErro().contains("Administrador"));
        assertEquals(120, estoqueDoCaderno());
    }
}
