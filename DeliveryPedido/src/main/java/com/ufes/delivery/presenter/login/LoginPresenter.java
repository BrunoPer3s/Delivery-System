package com.ufes.delivery.presenter.login;

import com.ufes.delivery.presenter.usuario.CadastroUsuarioPresenter;
import com.ufes.delivery.presenter.painel.PainelPrincipalPresenter;

import com.ufes.delivery.log.GerenciadorDeLogAtivo;
import com.ufes.delivery.model.Sessao;
import com.ufes.delivery.repository.cliente.IClienteRepository;
import com.ufes.delivery.repository.cupom.ICupomRepository;
import com.ufes.delivery.repository.pedido.IPedidoRepository;
import com.ufes.delivery.repository.produto.IProdutoRepository;
import com.ufes.delivery.repository.usuario.IUsuarioRepository;
import com.ufes.delivery.service.AutenticacaoService;
import com.ufes.delivery.service.AutenticacaoService.ResultadoAutenticacao;
import com.ufes.delivery.service.SessaoService;
import com.ufes.delivery.view.usuario.CadastroUsuarioView;
import com.ufes.delivery.view.login.ILoginView;
import com.ufes.delivery.view.painel.PainelPrincipalView;

public class LoginPresenter {

    private final ILoginView view;
    private final AutenticacaoService autenticacaoService;
    private final SessaoService sessaoService;
    private final IUsuarioRepository usuarioRepository;
    private final IClienteRepository clienteRepository;
    private final IProdutoRepository produtoRepository;
    private final ICupomRepository cupomRepository;
    private final IPedidoRepository pedidoRepository;
    private final GerenciadorDeLogAtivo logger;

    public LoginPresenter(ILoginView view,
                          AutenticacaoService autenticacaoService,
                          SessaoService sessaoService,
                          IUsuarioRepository usuarioRepository,
                          IClienteRepository clienteRepository,
                          IProdutoRepository produtoRepository,
                          ICupomRepository cupomRepository,
                          IPedidoRepository pedidoRepository,
                          GerenciadorDeLogAtivo logger) {
        this.view = view;
        this.autenticacaoService = autenticacaoService;
        this.sessaoService = sessaoService;
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.cupomRepository = cupomRepository;
        this.pedidoRepository = pedidoRepository;
        this.logger = logger;
    }

    public void onAcessar() {
        view.limparMensagemErro();

        String nomeUsuario = view.getNomeUsuario();
        String senha = view.getSenha();

        if (nomeUsuario == null || nomeUsuario.trim().isEmpty()) {
            view.exibirMensagemErro("Nome de usuário é obrigatório.");
            return;
        }

        if (senha == null || senha.isEmpty()) {
            view.exibirMensagemErro("Senha é obrigatória.");
            return;
        }

        if (!validarFormatoNomeUsuario(nomeUsuario)) {
            view.exibirMensagemErro(
                "Nome de usuário deve conter apenas letras minúsculas e algarismos, sem espaços (3 a 30 caracteres).");
            return;
        }

        if (senha.length() < 8 || senha.length() > 64) {
            view.exibirMensagemErro("Senha deve conter de 8 a 64 caracteres.");
            return;
        }

        ResultadoAutenticacao resultado = autenticacaoService.autenticar(nomeUsuario, senha);

        switch (resultado.getTipo()) {
            case SUCESSO -> {
                Sessao sessao = sessaoService.iniciarSessao(resultado.getUsuario());
                view.fechar();
                abrirPainelPosLogin(sessao);
            }
            case CREDENCIAIS_INVALIDAS -> {
                view.exibirMensagemErro("Credenciais inválidas.");
            }
            case NAO_AUTORIZADO -> {
                view.exibirMensagemErro(
                    "Acesso depende de autorização administrativa.");
            }
        }
    }

    public void onCancelar() {
        view.limparCampos();
        view.fechar();
    }

    public void onCadastrarUsuario() {
        CadastroUsuarioView cadastroView = new CadastroUsuarioView();
        CadastroUsuarioPresenter cadastroPresenter = new CadastroUsuarioPresenter(
                cadastroView, usuarioRepository, logger);
        cadastroView.setPresenter(cadastroPresenter);
        cadastroView.exibir();
    }

    private boolean validarFormatoNomeUsuario(String nomeUsuario) {
        if (nomeUsuario.length() < 3 || nomeUsuario.length() > 30) {
            return false;
        }
        return nomeUsuario.matches("[a-z0-9]+");
    }

    private void abrirPainelPosLogin(Sessao sessao) {
        PainelPrincipalView painelView = new PainelPrincipalView();
        PainelPrincipalPresenter painelPresenter = new PainelPrincipalPresenter(
                painelView, usuarioRepository, clienteRepository, produtoRepository,
                cupomRepository, pedidoRepository, sessaoService, logger);
        painelView.setPresenter(painelPresenter);
        painelView.exibir();
    }
}

