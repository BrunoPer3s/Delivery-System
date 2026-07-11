package com.ufes.delivery.presenter.cliente;

import com.ufes.delivery.apoio.CadastroClienteViewStub;
import com.ufes.delivery.model.Cliente;
import com.ufes.delivery.model.Endereco;
import com.ufes.delivery.persistencia.BancoDados;
import com.ufes.delivery.repository.cliente.ClienteRepositorySQLite;
import com.ufes.delivery.repository.cliente.IClienteRepository;
import com.ufes.delivery.service.SessaoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.ufes.delivery.apoio.CadastroClienteViewStub.enderecoValido;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("US06 - Cadastrar, editar e visualizar cliente")
class CadastroClientePresenterTest {

    private static final String CPF = "111.444.777-35";
    private static final String CPF_LIMPO = "11144477735";

    @TempDir
    Path diretorio;

    private IClienteRepository clienteRepository;
    private SessaoService sessaoService;
    private CadastroClienteViewStub view;

    @BeforeEach
    void preparar() {
        BancoDados banco = new BancoDados(diretorio.resolve("teste.db").toString());
        banco.inicializar();
        clienteRepository = new ClienteRepositorySQLite(banco);

        sessaoService = SessaoService.getInstancia();
        view = new CadastroClienteViewStub();
    }

    @AfterEach
    void encerrar() {
        sessaoService.encerrarSessao();
    }

    private void salvar(Cliente clienteEdicao) {
        new CadastroClientePresenter(view, clienteRepository, null, sessaoService, clienteEdicao)
                .onSalvar();
    }

    private List<String[]> enderecos(int quantidade) {
        List<String[]> lista = new ArrayList<>();
        for (int i = 1; i <= quantidade; i++) {
            lista.add(enderecoValido("Rua " + i));
        }
        return lista;
    }

    @Test
    @DisplayName("Cenário 1 - Salvar cliente com endereço padrão")
    void salvaClienteComEnderecoPadrao() {
        view.preencher("Fulano de Tal", CPF, enderecos(1), 0);

        salvar(null);

        assertNull(view.getMensagemErro());
        Cliente salvo = clienteRepository.buscarPorCpf(CPF_LIMPO).orElseThrow();
        assertEquals("Fulano de Tal", salvo.getNome());
        assertEquals(1, salvo.getEnderecos().size());
        assertNotNull(salvo.getEnderecoPadrao());
        assertTrue(salvo.getEnderecoPadrao().isPadrao());
    }

    @Test
    @DisplayName("Cenário 2 - Rejeitar CPF duplicado")
    void rejeitaCpfDuplicado() {
        view.preencher("Fulano de Tal", CPF, enderecos(1), 0);
        salvar(null);

        CadastroClienteViewStub outraView = new CadastroClienteViewStub();
        outraView.preencher("Sicrano de Tal", CPF, enderecos(1), 0);
        new CadastroClientePresenter(outraView, clienteRepository, null, sessaoService, null)
                .onSalvar();

        assertEquals("Já existe um cliente com este CPF.", outraView.getMensagemErro());
        assertEquals("Fulano de Tal",
                clienteRepository.buscarPorCpf(CPF_LIMPO).orElseThrow().getNome());
    }

    @Test
    @DisplayName("Cenário 3 - Rejeitar ausência de endereço padrão")
    void rejeitaAusenciaDeEnderecoPadrao() {
        view.preencher("Fulano de Tal", CPF, enderecos(2), -1);

        salvar(null);

        assertEquals("Um endereço padrão é obrigatório.", view.getMensagemErro());
        assertTrue(clienteRepository.buscarPorCpf(CPF_LIMPO).isEmpty());
    }

    @Test
    @DisplayName("Cenário 4 - Rejeitar mais de três endereços informando o limite")
    void rejeitaMaisDeTresEnderecos() {
        view.preencher("Fulano de Tal", CPF, enderecos(4), 0);

        salvar(null);

        assertNotNull(view.getMensagemErro());
        assertTrue(view.getMensagemErro().contains("3 endereços"));
        assertTrue(clienteRepository.buscarPorCpf(CPF_LIMPO).isEmpty());
    }

    @Test
    @DisplayName("Cenário 4 - Três endereços continuam sendo aceitos")
    void aceitaTresEnderecos() {
        view.preencher("Fulano de Tal", CPF, enderecos(3), 0);

        salvar(null);

        assertNull(view.getMensagemErro());
        assertEquals(3, clienteRepository.buscarPorCpf(CPF_LIMPO).orElseThrow()
                .getEnderecos().size());
    }

    @Test
    @DisplayName("Cenário 4 - A edição também não pode ultrapassar três endereços")
    void edicaoNaoUltrapassaTresEnderecos() {
        view.preencher("Fulano de Tal", CPF, enderecos(1), 0);
        salvar(null);

        Cliente existente = clienteRepository.buscarPorCpf(CPF_LIMPO).orElseThrow();

        CadastroClienteViewStub edicaoView = new CadastroClienteViewStub();
        CadastroClientePresenter presenter = new CadastroClientePresenter(
                edicaoView, clienteRepository, null, sessaoService, existente);

        edicaoView.preencher("Fulano de Tal", CPF, enderecos(4), 0);
        presenter.onSalvar();

        assertNotNull(edicaoView.getMensagemErro());
        assertTrue(edicaoView.getMensagemErro().contains("3 endereços"));
        assertEquals(1, clienteRepository.buscarPorCpf(CPF_LIMPO).orElseThrow()
                .getEnderecos().size());
    }

    @Test
    @DisplayName("Cenário 5 - Visualizar e editar cliente preservando o CPF")
    void editaClientePreservandoCpf() {
        view.preencher("Fulano de Tal", CPF, enderecos(1), 0);
        salvar(null);

        Cliente existente = clienteRepository.buscarPorCpf(CPF_LIMPO).orElseThrow();
        int totalAntes = clienteRepository.listarTodos().size();

        CadastroClienteViewStub edicaoView = new CadastroClienteViewStub();
        CadastroClientePresenter presenter = new CadastroClientePresenter(
                edicaoView, clienteRepository, null, sessaoService, existente);

        assertEquals("Fulano de Tal", edicaoView.getNome());
        assertFalse(edicaoView.isCpfEditavel());

        edicaoView.preencher("Fulano Editado", CPF, enderecos(2), 1);
        presenter.onSalvar();

        assertNull(edicaoView.getMensagemErro());
        Cliente editado = clienteRepository.buscarPorCpf(CPF_LIMPO).orElseThrow();
        assertEquals("Fulano Editado", editado.getNome());
        assertEquals(2, editado.getEnderecos().size());
        assertEquals(totalAntes, clienteRepository.listarTodos().size());
    }

    @Test
    @DisplayName("Rejeitar CPF inválido pelos dígitos verificadores")
    void rejeitaCpfInvalido() {
        view.preencher("Fulano de Tal", "111.111.111-11", enderecos(1), 0);

        salvar(null);

        assertEquals("CPF inválido.", view.getMensagemErro());
    }

    @Test
    @DisplayName("O domínio impede um quarto endereço mesmo fora da tela")
    void dominioImpedeQuartoEndereco() {
        Cliente cliente = new Cliente("Fulano de Tal", CPF_LIMPO);
        for (int i = 1; i <= 3; i++) {
            cliente.adicionarEndereco(new Endereco("Rua " + i, "100", "",
                    "Centro", "Vitória", "ES", "29000000", i == 1));
        }

        assertThrows(IllegalStateException.class, () -> cliente.adicionarEndereco(
                new Endereco("Rua 4", "100", "", "Centro", "Vitória", "ES", "29000000", false)));
    }

    @Test
    @DisplayName("O domínio impede substituir os endereços por mais de três")
    void dominioImpedeSetEnderecosAcimaDoLimite() {
        Cliente cliente = new Cliente("Fulano de Tal", CPF_LIMPO);

        List<Endereco> quatro = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            quatro.add(new Endereco("Rua " + i, "100", "",
                    "Centro", "Vitória", "ES", "29000000", i == 1));
        }

        assertThrows(IllegalStateException.class, () -> cliente.setEnderecos(quatro));
        assertEquals(0, cliente.getEnderecos().size());
    }
}
