package com.ufes.delivery.presenter.login;

import com.ufes.delivery.apoio.LoginViewStub;
import com.ufes.delivery.repository.usuario.IUsuarioRepository;
import com.ufes.delivery.repository.usuario.UsuarioRepositoryEmMemoria;
import com.ufes.delivery.service.AutenticacaoService;
import com.ufes.delivery.service.SessaoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("US01 - Validação de entrada da tela de login")
class LoginPresenterTest {

    private LoginViewStub view;

    private LoginPresenter criarPresenter(String nomeUsuario, String senha) {
        view = new LoginViewStub(nomeUsuario, senha);
        IUsuarioRepository usuarioRepository = new UsuarioRepositoryEmMemoria();
        AutenticacaoService autenticacaoService = new AutenticacaoService(usuarioRepository, null);
        return new LoginPresenter(view, autenticacaoService, SessaoService.getInstancia(),
                usuarioRepository, null, null, null, null, null, null);
    }

    @AfterEach
    void limparSessao() {
        SessaoService.getInstancia().encerrarSessao();
    }

    @Test
    @DisplayName("Cenário 3 - Rejeitar nome de usuário com espaço")
    void rejeitaNomeUsuarioComEspaco() {
        LoginPresenter presenter = criarPresenter("admin master", "Admin123");

        presenter.onAcessar();

        assertNotNull(view.getMensagemErro());
        assertTrue(view.getMensagemErro().contains("minúsculas"));
        assertFalse(SessaoService.getInstancia().isAutenticado());
        assertFalse(view.isFechada());
    }

    @Test
    @DisplayName("Cenário 3 - Rejeitar nome de usuário com letra maiúscula")
    void rejeitaNomeUsuarioComMaiuscula() {
        LoginPresenter presenter = criarPresenter("AdminMaster", "Admin123");

        presenter.onAcessar();

        assertNotNull(view.getMensagemErro());
        assertTrue(view.getMensagemErro().contains("minúsculas"));
        assertFalse(SessaoService.getInstancia().isAutenticado());
    }

    @Test
    @DisplayName("Cenário 4 - Rejeitar credenciais inválidas sem identificar o dado que falhou")
    void rejeitaCredenciaisInvalidas() {
        LoginPresenter presenter = criarPresenter("adminmaster", "SenhaErrada");

        presenter.onAcessar();

        assertEquals("Credenciais inválidas.", view.getMensagemErro());
        assertFalse(SessaoService.getInstancia().isAutenticado());
    }

    @Test
    @DisplayName("Cenário 5 - Usuário pendente recebe mensagem de autorização administrativa")
    void bloqueiaUsuarioPendente() {
        LoginPresenter presenter = criarPresenter("maria01", "Maria123");

        presenter.onAcessar();

        assertNotNull(view.getMensagemErro());
        assertTrue(view.getMensagemErro().contains("autorização administrativa"));
        assertFalse(SessaoService.getInstancia().isAutenticado());
    }

    @Test
    @DisplayName("Nome de usuário obrigatório")
    void exigeNomeUsuario() {
        LoginPresenter presenter = criarPresenter("   ", "Admin123");

        presenter.onAcessar();

        assertEquals("Nome de usuário é obrigatório.", view.getMensagemErro());
    }

    @Test
    @DisplayName("Senha obrigatória")
    void exigeSenha() {
        LoginPresenter presenter = criarPresenter("adminmaster", "");

        presenter.onAcessar();

        assertEquals("Senha é obrigatória.", view.getMensagemErro());
    }

    @Test
    @DisplayName("Cancelar não inicia sessão e limpa os campos")
    void cancelarNaoIniciaSessao() {
        LoginPresenter presenter = criarPresenter("adminmaster", "Admin123");

        presenter.onCancelar();

        assertTrue(view.isCamposLimpos());
        assertFalse(SessaoService.getInstancia().isAutenticado());
    }
}
