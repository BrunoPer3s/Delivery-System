package com.ufes.delivery.presenter.usuario;

import com.ufes.delivery.apoio.GestaoUsuarioViewStub;
import com.ufes.delivery.model.Usuario;
import com.ufes.delivery.model.perfil.Administrador;
import com.ufes.delivery.model.perfil.Atendente;
import com.ufes.delivery.model.situacao.Autorizado;
import com.ufes.delivery.model.situacao.NaoAutorizado;
import com.ufes.delivery.model.situacao.Pendente;
import com.ufes.delivery.repository.usuario.IUsuarioRepository;
import com.ufes.delivery.repository.usuario.UsuarioRepositoryEmMemoria;
import com.ufes.delivery.service.SessaoService;
import com.ufes.delivery.util.SenhaUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("US03 - Gerenciar usuários e autorizações")
class GestaoUsuarioPresenterTest {

    private IUsuarioRepository usuarioRepository;
    private SessaoService sessaoService;
    private GestaoUsuarioViewStub view;
    private GestaoUsuarioPresenter presenter;

    @BeforeEach
    void preparar() {
        usuarioRepository = new UsuarioRepositoryEmMemoria();
        sessaoService = SessaoService.getInstancia();
        sessaoService.iniciarSessao(
                usuarioRepository.buscarPorNomeUsuario("adminmaster").orElseThrow());

        view = new GestaoUsuarioViewStub();
        presenter = new GestaoUsuarioPresenter(view, usuarioRepository, sessaoService, null);
    }

    @AfterEach
    void encerrar() {
        sessaoService.encerrarSessao();
    }

    @Test
    @DisplayName("Cenário 2 - Buscar usuário por nome, sem diferenciar maiúsculas de minúsculas")
    void buscaUsuarioPorNome() {
        usuarioRepository.salvar(new Usuario("Fulano de Tal", "fulano123",
                SenhaUtil.hashSenha("Fulano12"), Atendente.INSTANCIA, Pendente.INSTANCIA));

        view.setTermoBusca("FULANO");
        presenter.onBuscar();

        assertEquals(List.of("fulano123"), view.getNomesUsuariosNaTabela());
    }

    @Test
    @DisplayName("Cenário 2 - A busca considera o nome de usuário além do nome civil")
    void buscaConsideraNomeUsuario() {
        view.setTermoBusca("atendente01");
        presenter.onBuscar();

        assertEquals(List.of("atendente01"), view.getNomesUsuariosNaTabela());
    }

    @Test
    @DisplayName("Cenário 3 - Autorizar vários usuários selecionados")
    void autorizaVariosUsuarios() {
        usuarioRepository.salvar(new Usuario("Pedro Alves", "pedro01",
                SenhaUtil.hashSenha("Pedro123"), Atendente.INSTANCIA, Pendente.INSTANCIA));

        view.setSelecionados(List.of("maria01", "pedro01"));
        presenter.onAutorizar();

        assertEquals(Autorizado.INSTANCIA,
                usuarioRepository.buscarPorNomeUsuario("maria01").orElseThrow().getSituacao());
        assertEquals(Autorizado.INSTANCIA,
                usuarioRepository.buscarPorNomeUsuario("pedro01").orElseThrow().getSituacao());
        assertEquals("Autorizado", view.getSituacaoNaTabela("maria01"));
        assertEquals("Autorizado", view.getSituacaoNaTabela("pedro01"));
    }

    @Test
    @DisplayName("Cenário 4 - Desautorizar usuário selecionado impede nova sessão")
    void desautorizaUsuarioSelecionado() {
        view.setSelecionados(List.of("atendente01"));
        presenter.onDesautorizar();

        Usuario atendente = usuarioRepository.buscarPorNomeUsuario("atendente01").orElseThrow();
        assertEquals(NaoAutorizado.INSTANCIA, atendente.getSituacao());
        assertFalse(atendente.isAutorizado());
        assertFalse(atendente.getSituacao().podeIniciarSessao());
    }

    @Test
    @DisplayName("Cenário 5 - Rejeitar Autorizar sem seleção")
    void rejeitaAutorizarSemSelecao() {
        view.setSelecionados(List.of());
        presenter.onAutorizar();

        assertEquals("Selecione ao menos um usuário.", view.getMensagemErro());
        assertEquals(Pendente.INSTANCIA,
                usuarioRepository.buscarPorNomeUsuario("maria01").orElseThrow().getSituacao());
    }

    @Test
    @DisplayName("Cenário 5 - Rejeitar Desautorizar sem seleção")
    void rejeitaDesautorizarSemSelecao() {
        view.setSelecionados(List.of());
        presenter.onDesautorizar();

        assertEquals("Selecione ao menos um usuário.", view.getMensagemErro());
        assertTrue(usuarioRepository.buscarPorNomeUsuario("atendente01").orElseThrow().isAutorizado());
    }

    @Test
    @DisplayName("Cenário 5 - Rejeitar Excluir sem seleção")
    void rejeitaExcluirSemSelecao() {
        view.setSelecionados(List.of());
        presenter.onExcluir();

        assertEquals("Selecione ao menos um usuário.", view.getMensagemErro());
        assertEquals(4, usuarioRepository.listarTodos().size());
    }

    @Test
    @DisplayName("Excluir usuários selecionados após confirmação")
    void excluiUsuariosSelecionados() {
        view.setSelecionados(List.of("joaosilva"));
        view.setConfirmaExclusao(true);
        presenter.onExcluir();

        assertTrue(usuarioRepository.buscarPorNomeUsuario("joaosilva").isEmpty());
    }

    @Test
    @DisplayName("Exclusão cancelada na confirmação não remove o usuário")
    void exclusaoCanceladaNaoRemove() {
        view.setSelecionados(List.of("joaosilva"));
        view.setConfirmaExclusao(false);
        presenter.onExcluir();

        assertTrue(usuarioRepository.buscarPorNomeUsuario("joaosilva").isPresent());
    }

    @Test
    @DisplayName("O administrador logado não pode desautorizar a si mesmo")
    void naoDesautorizaOProprioUsuarioLogado() {
        view.setSelecionados(List.of("adminmaster"));
        presenter.onDesautorizar();

        assertNotNull(view.getMensagemErro());
        assertTrue(usuarioRepository.buscarPorNomeUsuario("adminmaster").orElseThrow().isAutorizado());
    }

    @Test
    @DisplayName("O perfil admite apenas Administrador ou Atendente")
    void alteraPerfilParaValorDoDominio() {
        presenter.onPerfilAlterado("atendente01", "Administrador");

        assertEquals(Administrador.INSTANCIA,
                usuarioRepository.buscarPorNomeUsuario("atendente01").orElseThrow().getPerfil());
    }
}
