package com.ufes.delivery.presenter.painel;

import com.ufes.delivery.log.GerenciadorDeLogAtivo;
import com.ufes.delivery.log.ResultadoOperacao;
import com.ufes.delivery.log.MensagemLogFactory;
import com.ufes.log.LogIndisponivelException;
import com.ufes.delivery.model.Sessao;
import com.ufes.delivery.model.estado.*;
import com.ufes.delivery.presenter.cliente.BuscaClientePresenter;
import com.ufes.delivery.presenter.cliente.CadastroClientePresenter;
import com.ufes.delivery.presenter.estoque.MovimentacaoEstoquePresenter;
import com.ufes.delivery.presenter.pedido.PedidoPresenter;
import com.ufes.delivery.presenter.produto.BuscaProdutoPresenter;
import com.ufes.delivery.presenter.produto.CadastroProdutoPresenter;
import com.ufes.delivery.presenter.usuario.GestaoUsuarioPresenter;
import com.ufes.delivery.repository.RepositorioObserver;
import com.ufes.delivery.repository.cliente.IClienteRepository;
import com.ufes.delivery.repository.cupom.ICupomRepository;
import com.ufes.delivery.repository.pagamento.IConfirmacaoPagamentoRepository;
import com.ufes.delivery.repository.pedido.IPedidoRepository;
import com.ufes.delivery.repository.pedido.PedidoRegistro;
import com.ufes.delivery.repository.produto.IProdutoRepository;
import com.ufes.delivery.repository.usuario.IUsuarioRepository;
import com.ufes.delivery.service.SessaoService;
import com.ufes.delivery.view.cliente.BuscaClienteView;
import com.ufes.delivery.view.cliente.CadastroClienteView;
import com.ufes.delivery.view.estoque.MovimentacaoEstoqueView;
import com.ufes.delivery.view.painel.IPainelPrincipalView;
import com.ufes.delivery.view.pedido.PedidoView;
import com.ufes.delivery.view.pedido.VisualizacaoPedidoView;
import com.ufes.delivery.view.produto.BuscaProdutoView;
import com.ufes.delivery.view.produto.CadastroProdutoView;
import com.ufes.delivery.view.usuario.GestaoUsuarioView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PainelPrincipalPresenter implements RepositorioObserver {

    private final IPainelPrincipalView view;
    private final IUsuarioRepository usuarioRepository;
    private final IClienteRepository clienteRepository;
    private final IProdutoRepository produtoRepository;
    private final ICupomRepository cupomRepository;
    private final IPedidoRepository pedidoRepository;
    private final IConfirmacaoPagamentoRepository confirmacaoPagamentoRepository;
    private final SessaoService sessaoService;
    private final GerenciadorDeLogAtivo logger;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PainelPrincipalPresenter(IPainelPrincipalView view,
                                     IUsuarioRepository usuarioRepository,
                                     IClienteRepository clienteRepository,
                                     IProdutoRepository produtoRepository,
                                     ICupomRepository cupomRepository,
                                     IPedidoRepository pedidoRepository,
                                     IConfirmacaoPagamentoRepository confirmacaoPagamentoRepository,
                                     SessaoService sessaoService,
                                     GerenciadorDeLogAtivo logger) {
        this.view = view;
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.cupomRepository = cupomRepository;
        this.pedidoRepository = pedidoRepository;
        this.confirmacaoPagamentoRepository = confirmacaoPagamentoRepository;
        this.sessaoService = sessaoService;
        this.logger = logger;

        this.pedidoRepository.adicionarObservador(this);

        configurarPainel();
    }

    @Override
    public void onDadosAlterados() {
        atualizarPedidosEMetricas();
    }

    private void configurarPainel() {
        view.setDataOperacao(getDataOperacao());

        Sessao sessao = sessaoService.getSessaoAtual();
        if (sessao != null) {
            view.setInfoUsuario(
                sessao.getNomeUsuario(),
                sessao.getLoginFormatado(),
                sessao.getPerfilDescricao());

            boolean isAdmin = sessao.getUsuario().getPerfil().podeAdministrar();
            view.habilitarMenuAdmin(isAdmin);
        }

        atualizarPedidosEMetricas();
    }

    public void atualizarPedidosEMetricas() {
        String dataOperacao = getDataOperacao();

        List<PedidoRegistro> pedidos = pedidoRepository.listarPorData(dataOperacao);

        List<String[]> dadosTabela = new ArrayList<>();
        for (PedidoRegistro p : pedidos) {
            dadosTabela.add(p.toArrayTabela());
        }
        view.carregarPedidos(dadosTabela);

        int total = pedidoRepository.totalNaData(dataOperacao);
        int novos = pedidoRepository.contarPorEstadoNaData(Novo.INSTANCIA, dataOperacao);
        int aguardandoPagamento = pedidoRepository.contarPorEstadoNaData(
                AguardandoPagamento.INSTANCIA, dataOperacao);
        int emPreparo = pedidoRepository.contarPorEstadoNaData(
                EmPreparo.INSTANCIA, dataOperacao);
        int aguardandoEntrega = pedidoRepository.contarPorEstadoNaData(
                AguardandoEntrega.INSTANCIA, dataOperacao);
        int emTransito = pedidoRepository.contarPorEstadoNaData(
                EmTransito.INSTANCIA, dataOperacao);
        int entregues = pedidoRepository.contarEntreguesNaData(dataOperacao);

        view.setMetricas(total, novos, aguardandoPagamento, emPreparo,
                aguardandoEntrega, emTransito, entregues);
    }

    private String getDataOperacao() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    public void onVisualizarPedido(int codigo) {
        pedidoRepository.buscarPorCodigo(codigo).ifPresentOrElse(
                registro -> new VisualizacaoPedidoView(
                        registro.getCodigo(),
                        registro.getNomeCliente(),
                        registro.getDataPedido(),
                        registro.getDataConclusao(),
                        registro.getEstado().getNome(),
                        registro.getValorTotal()).exibir(),
                () -> view.exibirMensagemErro(
                        "Pedido #" + codigo + " não foi encontrado."));
    }


    public void onNovoPedido() {
        PedidoView pedidoView = new PedidoView();
        PedidoPresenter pedidoPresenter = new PedidoPresenter(
                pedidoView, clienteRepository, produtoRepository,
                cupomRepository, pedidoRepository, confirmacaoPagamentoRepository,
                logger, sessaoService);
        pedidoView.setPresenter(pedidoPresenter);

        pedidoView.exibir();
    }

    public void onBuscarProdutos() {
        BuscaProdutoView buscaView = new BuscaProdutoView();
        BuscaProdutoPresenter buscaPresenter = new BuscaProdutoPresenter(
                buscaView, produtoRepository, logger, sessaoService);
        buscaView.setPresenter(buscaPresenter);
        buscaView.exibir();
    }

    public void onNovoProduto() {
        CadastroProdutoView cadastroView = new CadastroProdutoView();
        CadastroProdutoPresenter cadastroPresenter = new CadastroProdutoPresenter(
                cadastroView, produtoRepository, logger, sessaoService, null);
        cadastroView.setPresenter(cadastroPresenter);
        cadastroView.exibir();
    }

    public void onMovimentacaoEstoque() {
        MovimentacaoEstoqueView movView = new MovimentacaoEstoqueView();
        MovimentacaoEstoquePresenter movPresenter = new MovimentacaoEstoquePresenter(
                movView, produtoRepository, logger, sessaoService);
        movView.setPresenter(movPresenter);
        movView.exibir();
    }

    public void onNovoCliente() {
        CadastroClienteView cadastroView = new CadastroClienteView();
        CadastroClientePresenter cadastroPresenter = new CadastroClientePresenter(
                cadastroView, clienteRepository, logger, sessaoService, null);
        cadastroView.setPresenter(cadastroPresenter);
        cadastroView.exibir();
    }

    public void onBuscarClientes() {
        BuscaClienteView buscaView = new BuscaClienteView();
        BuscaClientePresenter buscaPresenter = new BuscaClientePresenter(
                buscaView, clienteRepository, logger, sessaoService);
        buscaView.setPresenter(buscaPresenter);
        buscaView.exibir();
    }


    public void onGestaoUsuarios() {
        if (!sessaoService.isAdministrador()) {
            view.exibirMensagemErro(
                    "Gestão de usuários é restrita ao perfil Administrador.");
            registrarAuditoria("Abertura da gestão de usuários", "Tela de usuários",
                    ResultadoOperacao.REJEITADO,
                    "Acesso negado - perfil sem permissão administrativa");
            return;
        }

        GestaoUsuarioView gestaoView = new GestaoUsuarioView();
        GestaoUsuarioPresenter gestaoPresenter = new GestaoUsuarioPresenter(
                gestaoView, usuarioRepository, sessaoService, logger);
        gestaoView.setPresenter(gestaoPresenter);
        gestaoView.exibir();
    }

    private void registrarAuditoria(String operacao, String recurso,
                                     ResultadoOperacao resultado, String justificativa) {
        if (logger != null) {
            try {
                String usuario = sessaoService.getNomeUsuarioLogado();
                logger.registrar(MensagemLogFactory.operacao(operacao)
                        .recurso(recurso)
                        .resultado(resultado)
                        .justificativa(justificativa)
                        .paraUsuario(usuario != null ? usuario : "sistema"));
            } catch (LogIndisponivelException e) {
                view.exibirMensagemErro("O registro de auditoria falhou.");
            }
        }
    }
}

