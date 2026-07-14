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

    //Teste de visualização da barra de status de informações no painel transferida para PainelPrincipalPresenterTest
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

    /*
    * O cenário 3, "Cenário 3 - Rejeitar nome de usuário com espaço ou letra maiúscula" foi colocado em
    *   LoginPresenterTest por identificar que se trata de validação dos dados inseridos e por tanto
    *   optamos por deixa-lo no presenter
    */

    //A verificação de possibilidade de identificação de qual credencial era inválida foi realizada comparando os retornos de login inválido e senha inválida
    @Test
    @DisplayName("Cenário 4 - Rejeitar usuario inválido")
    void rejeitaUsuarioIncorreto() {
        ResultadoAutenticacao usuarioIncorreto = autenticacaoService.autenticar("incorreto", "Admin123");

        assertFalse(usuarioIncorreto.isSucesso());
        assertEquals(ResultadoAutenticacao.Tipo.CREDENCIAIS_INVALIDAS, usuarioIncorreto.getTipo());
    }

    /*
     *Separamos o Cenário 4 em 4a e 4b para conseguirmos validar a não identificacao de qual dado falhou comparando
     * os retornos com assertEquals
     */

    @Test
    @DisplayName("Cenário 4a - Rejeitar senha inválido")
    void rejeitaSenhaIncorreta() {
        ResultadoAutenticacao senhaIncorreta = autenticacaoService.autenticar("adminmaster", "SenhaErrada");

        assertFalse(senhaIncorreta.isSucesso());
        assertEquals(ResultadoAutenticacao.Tipo.CREDENCIAIS_INVALIDAS, senhaIncorreta.getTipo());
    }

    @Test
    @DisplayName("Cenário 4b - Verificando saida de senha inválida e usuario inválido")
    void verificaSaidaDeSenhaEUsuarioInvalido() {
        ResultadoAutenticacao senhaIncorreta = autenticacaoService.autenticar("adminmaster", "SenhaErrada");
        ResultadoAutenticacao usuarioIncorreto = autenticacaoService.autenticar("incorreto", "Admin123");

        assertEquals(senhaIncorreta.getTipo(), usuarioIncorreto.getTipo());
    }

    @Test
    @DisplayName("Cenário 5 - Bloquear usuário pendente")
    void bloqueiaUsuarioPendentes() {
        ResultadoAutenticacao pendente = autenticacaoService.autenticar("maria01", "Maria123");

        assertFalse(pendente.isSucesso());
        assertEquals(ResultadoAutenticacao.Tipo.NAO_AUTORIZADO, pendente.getTipo());
    }

    @Test
    @DisplayName("Cenário 5 - Bloquear usuárionão autorizado")
    void bloqueiaUsuarioNaoAutorizado() {
        ResultadoAutenticacao naoAutorizado = autenticacaoService.autenticar("joaosilva", "Joao1234");

        assertFalse(naoAutorizado.isSucesso());
        assertEquals(ResultadoAutenticacao.Tipo.NAO_AUTORIZADO, naoAutorizado.getTipo());
    }

    /*
    * Adição de dois testes extras.
    * Um para verificação do processo de retirada do acesso
    * Outro para verificar criptografia de senha
    */

    @Test
    @DisplayName("Verificar desautorização usuario")
    void verificaDesautorizacaoUsuario() {
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
    @DisplayName("Verificar criptografia de senha")
    void verificaCriptografiaSenha() {
        Usuario usuario = usuarioRepository.buscarPorNomeUsuario("adminmaster").orElseThrow();

        assertNotNull(usuario.getSenhaHash());
        assertFalse(usuario.getSenhaHash().contains("Admin123"));
        assertTrue(SenhaUtil.verificarSenha("Admin123", usuario.getSenhaHash()));
    }
}
