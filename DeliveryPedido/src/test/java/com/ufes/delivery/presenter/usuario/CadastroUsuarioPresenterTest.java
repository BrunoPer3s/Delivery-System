package com.ufes.delivery.presenter.usuario;

import com.ufes.delivery.apoio.CadastroUsuarioViewStub;
import com.ufes.delivery.model.Usuario;
import com.ufes.delivery.model.perfil.Perfis;
import com.ufes.delivery.model.situacao.Situacoes;
import com.ufes.delivery.persistencia.BancoDados;
import com.ufes.delivery.repository.usuario.IUsuarioRepository;
import com.ufes.delivery.repository.usuario.UsuarioRepositorySQLite;
import com.ufes.delivery.util.SenhaUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("US02 - Cadastrar usuário")
class CadastroUsuarioPresenterTest {

    @TempDir
    Path diretorio;

    private IUsuarioRepository usuarioRepository;

    @BeforeEach
    void preparar() {
        BancoDados banco = new BancoDados(diretorio.resolve("teste.db").toString());
        banco.inicializar();
        usuarioRepository = new UsuarioRepositorySQLite(banco);
    }

    private CadastroUsuarioViewStub cadastrar(String nome, String nomeUsuario, String senha) {
        CadastroUsuarioViewStub view = new CadastroUsuarioViewStub(nome, nomeUsuario, senha);
        new CadastroUsuarioPresenter(view, usuarioRepository, null).onSalvar();
        return view;
    }

    @Test
    @DisplayName("Um banco recém-criado não possui usuário persistido")
    void bancoNovoNaoPossuiUsuario() {
        assertFalse(usuarioRepository.existeUsuario());
    }

    @Test
    @DisplayName("Cenário 1 - O primeiro usuário do sistema recebe perfil Administrador e situação Autorizado")
    void primeiroUsuarioRecebeAdministradorAutorizado() {
        CadastroUsuarioViewStub view = cadastrar("Bruno Peres", "bruno01", "Senha123");

        assertNotNull(view.getMensagemSucesso());

        Usuario primeiro = usuarioRepository.buscarPorNomeUsuario("bruno01").orElseThrow();
        assertEquals(Perfis.ADMINISTRADOR, primeiro.getPerfil());
        assertEquals(Situacoes.AUTORIZADO, primeiro.getSituacao());
        assertTrue(primeiro.isAutorizado());
    }

    @Test
    @DisplayName("Cenário 2 - Cadastro posterior recebe perfil Atendente e situação Pendente")
    void cadastroPosteriorRecebeAtendentePendente() {
        cadastrar("Bruno Peres", "bruno01", "Senha123");

        CadastroUsuarioViewStub view = cadastrar("Maria Souza", "maria02", "Senha456");

        assertNotNull(view.getMensagemSucesso());

        Usuario posterior = usuarioRepository.buscarPorNomeUsuario("maria02").orElseThrow();
        assertEquals(Perfis.ATENDENTE, posterior.getPerfil());
        assertEquals(Situacoes.PENDENTE, posterior.getSituacao());
        assertFalse(posterior.isAutorizado());
    }

    @Test
    @DisplayName("Cenário 3 - Rejeitar nome de usuário duplicado")
    void rejeitaNomeUsuarioDuplicado() {
        cadastrar("Bruno Peres", "fulano123", "Senha123");

        CadastroUsuarioViewStub view = cadastrar("Outro Fulano", "fulano123", "Senha456");

        assertEquals("Nome de usuário já está em uso.", view.getMensagemErro());
        assertEquals("Bruno Peres",
                usuarioRepository.buscarPorNomeUsuario("fulano123").orElseThrow().getNome());
        assertEquals(1, usuarioRepository.listarTodos().size());
    }

    @Test
    @DisplayName("Cenário 3 - Um nome de usuário pendente também bloqueia a duplicidade")
    void nomeUsuarioPendenteBloqueiaDuplicidade() {
        usuarioRepository.salvar(new Usuario("Maria Oliveira", "maria01",
                SenhaUtil.hashSenha("Maria123"), Perfis.ATENDENTE, Situacoes.PENDENTE));

        CadastroUsuarioViewStub view = cadastrar("Maria Souza", "maria01", "Senha456");

        assertEquals("Nome de usuário já está em uso.", view.getMensagemErro());
    }

    @Test
    @DisplayName("Cenário 4 - Rejeitar nome ausente")
    void rejeitaNomeAusente() {
        CadastroUsuarioViewStub view = cadastrar("   ", "bruno01", "Senha123");

        assertEquals("Nome é obrigatório.", view.getMensagemErro());
        assertFalse(usuarioRepository.existeUsuario());
    }

    @Test
    @DisplayName("Cenário 4 - Rejeitar nome de usuário ausente")
    void rejeitaNomeUsuarioAusente() {
        CadastroUsuarioViewStub view = cadastrar("Bruno Peres", "  ", "Senha123");

        assertEquals("Nome de usuário é obrigatório.", view.getMensagemErro());
        assertFalse(usuarioRepository.existeUsuario());
    }

    @Test
    @DisplayName("Cenário 4 - Rejeitar senha ausente")
    void rejeitaSenhaAusente() {
        CadastroUsuarioViewStub view = cadastrar("Bruno Peres", "bruno01", "");

        assertEquals("Senha é obrigatória.", view.getMensagemErro());
        assertFalse(usuarioRepository.existeUsuario());
    }

    @Test
    @DisplayName("Rejeitar senha com menos de 8 caracteres")
    void rejeitaSenhaCurta() {
        CadastroUsuarioViewStub view = cadastrar("Bruno Peres", "bruno01", "Curta1");

        assertEquals("Senha deve conter de 8 a 64 caracteres.", view.getMensagemErro());
        assertFalse(usuarioRepository.existeUsuario());
    }

    @Test
    @DisplayName("Rejeitar nome de usuário com letra maiúscula")
    void rejeitaNomeUsuarioComMaiuscula() {
        CadastroUsuarioViewStub view = cadastrar("Bruno Peres", "Bruno01", "Senha123");

        assertNotNull(view.getMensagemErro());
        assertTrue(view.getMensagemErro().contains("minúsculas"));
        assertFalse(usuarioRepository.existeUsuario());
    }

    @Test
    @DisplayName("Rejeitar confirmação de senha divergente")
    void rejeitaConfirmacaoDivergente() {
        CadastroUsuarioViewStub view =
                new CadastroUsuarioViewStub("Bruno Peres", "bruno01", "Senha123", "Senha999");
        new CadastroUsuarioPresenter(view, usuarioRepository, null).onSalvar();

        assertEquals("Senha e confirmação de senha não conferem.", view.getMensagemErro());
        assertFalse(usuarioRepository.existeUsuario());
    }

    @Test
    @DisplayName("A senha não é persistida em texto aberto")
    void senhaNaoEPersistidaEmTextoAberto() {
        cadastrar("Bruno Peres", "bruno01", "Senha123");

        Usuario usuario = usuarioRepository.buscarPorNomeUsuario("bruno01").orElseThrow();
        assertFalse(usuario.getSenhaHash().contains("Senha123"));
        assertTrue(SenhaUtil.verificarSenha("Senha123", usuario.getSenhaHash()));
    }
}
