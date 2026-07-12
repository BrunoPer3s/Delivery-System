package com.ufes.delivery.service;

import com.ufes.delivery.model.Usuario;
import com.ufes.delivery.model.perfil.Administrador;
import com.ufes.delivery.model.perfil.Atendente;
import com.ufes.delivery.model.situacao.Autorizado;
import com.ufes.delivery.repository.usuario.IUsuarioRepository;
import com.ufes.delivery.repository.usuario.UsuarioRepositoryEmMemoria;
import com.ufes.delivery.util.SenhaUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
* Optamos pelo uso do Fake Object porque a camada de Service se tratar estritamente das regras
* de negócio e usar o banco em memória garante rapidez nos testes, além de isolamento dos dados
* e proteção aos dados dos usuarios/clientes.
 */

@DisplayName("US01 - Autenticar usuário e iniciar sessão")
class AutenticacaoServiceTest {

    private IUsuarioRepository usuarioRepository;
    private AutenticacaoService autenticacaoService;

    @BeforeEach
    void preparar() {
        usuarioRepository = new UsuarioRepositoryEmMemoria();
        autenticacaoService = new AutenticacaoService(usuarioRepository, null);
    }

    @Test
    @DisplayName("Cenário 1 - Autenticar usuário administrador")
    void autenticaAdministradorAutorizado() {
        ResultadoAutenticacao resultado = autenticacaoService.autenticar("adminmaster", "Admin123");

        assertTrue(resultado.isSucesso());
        assertEquals(Administrador.INSTANCIA, resultado.getUsuario().getPerfil());
        assertTrue(resultado.getUsuario().getPerfil().podeAdministrar());
    }

    @Test
    @DisplayName("Cenário 2 - Autenticar usuário atendente")
    void autenticaAtendenteAutorizado() {
        ResultadoAutenticacao resultado = autenticacaoService.autenticar("atendente01", "Atende01");

        assertTrue(resultado.isSucesso());
        assertEquals(Atendente.INSTANCIA, resultado.getUsuario().getPerfil());
        assertFalse(resultado.getUsuario().getPerfil().podeAdministrar());
    }

    @Test
    @DisplayName("Cenário 4 - Rejeitar senha incorreta sem identificar o dado que falhou")
    void rejeitaSenhaIncorreta() {
        ResultadoAutenticacao resultado = autenticacaoService.autenticar("adminmaster", "SenhaErrada");

        assertFalse(resultado.isSucesso());
        assertEquals(ResultadoAutenticacao.Tipo.CREDENCIAIS_INVALIDAS, resultado.getTipo());
        assertNull(resultado.getUsuario());
    }

    @Test
    @DisplayName("Cenário 4 - Usuário inexistente produz o mesmo resultado de senha incorreta")
    void rejeitaUsuarioInexistenteComOMesmoResultado() {
        ResultadoAutenticacao inexistente = autenticacaoService.autenticar("naoexiste", "Admin123");
        ResultadoAutenticacao senhaErrada = autenticacaoService.autenticar("adminmaster", "SenhaErrada");

        assertEquals(senhaErrada.getTipo(), inexistente.getTipo());
    }

    @Test
    @DisplayName("Cenário 5 - Bloquear usuário pendente")
    void bloqueiaUsuarioPendente() {
        ResultadoAutenticacao resultado = autenticacaoService.autenticar("maria01", "Maria123");

        assertFalse(resultado.isSucesso());
        assertEquals(ResultadoAutenticacao.Tipo.NAO_AUTORIZADO, resultado.getTipo());
    }

    @Test
    @DisplayName("Cenário 5 - Bloquear usuário não autorizado")
    void bloqueiaUsuarioNaoAutorizado() {
        ResultadoAutenticacao resultado = autenticacaoService.autenticar("joaosilva", "Joao1234");

        assertFalse(resultado.isSucesso());
        assertEquals(ResultadoAutenticacao.Tipo.NAO_AUTORIZADO, resultado.getTipo());
    }

    @Test
    @DisplayName("Usuário desautorizado deixa de iniciar sessão")
    void usuarioDesautorizadoNaoIniciaSessao() {
        Usuario usuario = new Usuario("Ana Lima", "analima",
                SenhaUtil.hashSenha("Ana12345"), Atendente.INSTANCIA, Autorizado.INSTANCIA);
        usuarioRepository.salvar(usuario);

        assertTrue(autenticacaoService.autenticar("analima", "Ana12345").isSucesso());

        usuario.desautorizar();
        usuarioRepository.salvar(usuario);

        ResultadoAutenticacao resultado = autenticacaoService.autenticar("analima", "Ana12345");
        assertFalse(resultado.isSucesso());
        assertEquals(ResultadoAutenticacao.Tipo.NAO_AUTORIZADO, resultado.getTipo());
    }

    @Test
    @DisplayName("A senha não é armazenada em texto aberto")
    void senhaNaoEArmazenadaEmTextoAberto() {
        Usuario usuario = usuarioRepository.buscarPorNomeUsuario("adminmaster").orElseThrow();

        assertNotNull(usuario.getSenhaHash());
        assertFalse(usuario.getSenhaHash().contains("Admin123"));
        assertTrue(SenhaUtil.verificarSenha("Admin123", usuario.getSenhaHash()));
    }
}
