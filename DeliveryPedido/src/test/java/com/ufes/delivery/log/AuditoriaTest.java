package com.ufes.delivery.log;

import com.ufes.delivery.apoio.CadastroUsuarioViewStub;
import com.ufes.delivery.apoio.GestaoUsuarioViewStub;
import com.ufes.delivery.apoio.LoggerEmMemoria;
import com.ufes.delivery.persistencia.BancoDados;
import com.ufes.delivery.presenter.usuario.CadastroUsuarioPresenter;
import com.ufes.delivery.presenter.usuario.GestaoUsuarioPresenter;
import com.ufes.delivery.repository.usuario.IUsuarioRepository;
import com.ufes.delivery.repository.usuario.UsuarioRepositoryEmMemoria;
import com.ufes.delivery.repository.usuario.UsuarioRepositorySQLite;
import com.ufes.delivery.service.AutenticacaoService;
import com.ufes.delivery.service.SessaoService;
import com.ufes.log.CsvLogger;
import com.ufes.log.JsonlLogger;
import com.ufes.log.XmlLogger;
import com.ufes.log.model.MensagemLog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("US12 - Registrar eventos de auditoria")
class AuditoriaTest {

    @TempDir
    Path diretorio;

    private LoggerEmMemoria logger;
    private GerenciadorDeLogAtivo gerenciador;
    private SessaoService sessaoService;

    @BeforeEach
    void preparar() {
        logger = new LoggerEmMemoria();
        gerenciador = new GerenciadorDeLogAtivo(logger);
        sessaoService = SessaoService.getInstancia();
    }

    @AfterEach
    void encerrar() {
        sessaoService.encerrarSessao();
        System.clearProperty(ConfiguracaoAuditoria.PROPRIEDADE);
    }

    private MensagemLog ultimo() {
        List<MensagemLog> registros = logger.getRegistros();
        return registros.get(registros.size() - 1);
    }

    @Test
    @DisplayName("Conteúdo mínimo - o registro contém usuário, data, hora, operação, recurso e resultado")
    void registroContemConteudoMinimo() {
        MensagemLog mensagem = MensagemLogFactory.operacao("Cadastro de produto")
                .recurso("Produto 2001 - Caderno")
                .resultado(ResultadoOperacao.SUCESSO)
                .justificativa("Quantidade inicial: 120")
                .paraUsuario("adminmaster");

        assertEquals("adminmaster", mensagem.getNomeUsuario());
        assertTrue(mensagem.getData().matches("\\d{2}/\\d{2}/\\d{4}"));
        assertTrue(mensagem.getHora().matches("\\d{2}:\\d{2}:\\d{2}"));
        assertEquals("Cadastro de produto", mensagem.getNomeOperacao());
        assertEquals("Produto 2001 - Caderno", mensagem.getRecurso());
        assertEquals("Sucesso", mensagem.getResultado());
        assertEquals("Quantidade inicial: 120", mensagem.getJustificativa());
    }

    @Test
    @DisplayName("Operações de pedido registram o código do pedido e o nome do cliente")
    void registroDePedidoContemCodigoECliente() {
        MensagemLog mensagem = MensagemLogFactory.operacao("Resultado do pagamento")
                .pedido(1001, "Fulano de Tal")
                .recurso("Pagamento via PIX Chave")
                .resultado(ResultadoOperacao.SUCESSO)
                .paraUsuario("atendente01");

        assertEquals("1001", mensagem.getCodigoPedido());
        assertEquals("Fulano de Tal", mensagem.getNomeCliente());
    }

    @Test
    @DisplayName("Cenário 6 - Decisão administrativa sobre usuário é registrada")
    void decisaoAdministrativaERegistrada() {
        IUsuarioRepository usuarioRepository = new UsuarioRepositoryEmMemoria();
        sessaoService.iniciarSessao(
                usuarioRepository.buscarPorNomeUsuario("adminmaster").orElseThrow());

        GestaoUsuarioViewStub view = new GestaoUsuarioViewStub();
        GestaoUsuarioPresenter presenter =
                new GestaoUsuarioPresenter(view, usuarioRepository, sessaoService, gerenciador);

        view.setSelecionados(List.of("maria01"));
        presenter.onAutorizar();

        MensagemLog registro = ultimo();
        assertEquals("adminmaster", registro.getNomeUsuario());
        assertEquals("Autorização de usuário", registro.getNomeOperacao());
        assertEquals("Usuário maria01", registro.getRecurso());
        assertEquals("Sucesso", registro.getResultado());
    }

    @Test
    @DisplayName("Autenticação bem-sucedida é registrada com resultado Sucesso")
    void autenticacaoComSucessoERegistrada() {
        IUsuarioRepository usuarioRepository = new UsuarioRepositoryEmMemoria();
        new AutenticacaoService(usuarioRepository, gerenciador)
                .autenticar("adminmaster", "Admin123");

        MensagemLog registro = ultimo();
        assertEquals("Autenticação", registro.getNomeOperacao());
        assertEquals("Sucesso", registro.getResultado());
        assertEquals("Usuário adminmaster", registro.getRecurso());
    }

    @Test
    @DisplayName("Autenticação rejeitada registra o motivo sem expor a senha")
    void autenticacaoRejeitadaNaoExpoeSenha() {
        IUsuarioRepository usuarioRepository = new UsuarioRepositoryEmMemoria();
        new AutenticacaoService(usuarioRepository, gerenciador)
                .autenticar("adminmaster", "SenhaSecreta123");

        MensagemLog registro = ultimo();
        assertEquals("Rejeitado", registro.getResultado());
        assertEquals("Credenciais inválidas", registro.getJustificativa());
        assertFalse(registro.toString().contains("SenhaSecreta123"));
    }

    @Test
    @DisplayName("O cadastro de usuário não registra a senha nem o hash")
    void cadastroDeUsuarioNaoRegistraSenha() {
        BancoDados banco = new BancoDados(diretorio.resolve("teste.db").toString());
        banco.inicializar();
        IUsuarioRepository usuarioRepository = new UsuarioRepositorySQLite(banco);

        CadastroUsuarioViewStub view =
                new CadastroUsuarioViewStub("Bruno Peres", "bruno01", "Senha123");
        new CadastroUsuarioPresenter(view, usuarioRepository, gerenciador).onSalvar();

        MensagemLog registro = ultimo();
        assertEquals("Cadastro de usuário", registro.getNomeOperacao());
        assertFalse(registro.toString().contains("Senha123"));

        String hash = usuarioRepository.buscarPorNomeUsuario("bruno01").orElseThrow().getSenhaHash();
        assertFalse(registro.toString().contains(hash));
    }

    @Test
    @DisplayName("A modalidade de auditoria é única por execução e configurável")
    void modalidadeDeAuditoriaEConfiguravel() {
        assertInstanceOf(JsonlLogger.class, ConfiguracaoAuditoria.loggerDaModalidade("jsonl"));
        assertInstanceOf(CsvLogger.class, ConfiguracaoAuditoria.loggerDaModalidade("csv"));
        assertInstanceOf(XmlLogger.class, ConfiguracaoAuditoria.loggerDaModalidade("xml"));
    }

    @Test
    @DisplayName("A modalidade padrão é JSONL quando nada é configurado")
    void modalidadePadraoEJsonl() {
        assertInstanceOf(JsonlLogger.class, ConfiguracaoAuditoria.loggerConfigurado());
    }

    @Test
    @DisplayName("Uma modalidade desconhecida é rejeitada")
    void modalidadeDesconhecidaERejeitada() {
        assertThrows(IllegalArgumentException.class,
                () -> ConfiguracaoAuditoria.loggerDaModalidade("yaml"));
    }

    @Test
    @DisplayName("A propriedade de configuração seleciona a modalidade da execução")
    void propriedadeSelecionaModalidade() {
        System.setProperty(ConfiguracaoAuditoria.PROPRIEDADE, "csv");

        assertInstanceOf(CsvLogger.class, ConfiguracaoAuditoria.loggerConfigurado());
    }
}
