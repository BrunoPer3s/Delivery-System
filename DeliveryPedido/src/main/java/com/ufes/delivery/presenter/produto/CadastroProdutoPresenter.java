package com.ufes.delivery.presenter.produto;

import com.ufes.delivery.log.GerenciadorDeLogAtivo;
import com.ufes.delivery.log.MensagemLogFactory;
import com.ufes.delivery.model.Produto;
import com.ufes.delivery.repository.produto.IProdutoRepository;
import com.ufes.delivery.service.SessaoService;
import com.ufes.delivery.view.produto.ICadastroProdutoView;

public class CadastroProdutoPresenter {

    private final ICadastroProdutoView view;
    private final IProdutoRepository produtoRepository;
    private final GerenciadorDeLogAtivo logger;
    private final SessaoService sessaoService;
    private final Produto produtoEdicao;

    public CadastroProdutoPresenter(ICadastroProdutoView view,
                                     IProdutoRepository produtoRepository,
                                     GerenciadorDeLogAtivo logger,
                                     SessaoService sessaoService,
                                     Produto produtoEdicao) {
        this.view = view;
        this.produtoRepository = produtoRepository;
        this.logger = logger;
        this.sessaoService = sessaoService;
        this.produtoEdicao = produtoEdicao;

        if (produtoEdicao != null) {
            preencherFormulario();
        }
    }

    private void preencherFormulario() {
        view.setCodigo(String.valueOf(produtoEdicao.getCodigo()));
        view.setNome(produtoEdicao.getNome());
        view.setCategoria(produtoEdicao.getCategoria());
        view.setPrecoUnitario(produtoEdicao.getPrecoFormatado());

        view.setLabelEstoque("Estoque atual");
        view.setQuantidadeEstoque(String.valueOf(produtoEdicao.getEstoqueAtual()));

        view.setModoVisualizacao(true);
    }

    public void onSalvar() {
        if (!sessaoService.isAdministrador()) {
            view.exibirMensagemErro("Cadastro de produto é restrito ao Administrador.");
            registrarAuditoria("Acesso negado - tentativa de cadastro de produto sem perfil Administrador");
            return;
        }

        String codigoStr = view.getCodigo();
        if (codigoStr == null || codigoStr.trim().isEmpty()) {
            view.exibirMensagemErro("Código é obrigatório.");
            return;
        }
        int codigo;
        try {
            codigo = Integer.parseInt(codigoStr.trim());
            if (codigo <= 0) {
                view.exibirMensagemErro("Código deve ser um inteiro positivo.");
                return;
            }
        } catch (NumberFormatException e) {
            view.exibirMensagemErro("Código deve ser um número inteiro.");
            return;
        }

        if (produtoEdicao == null && produtoRepository.buscarPorCodigo(codigo).isPresent()) {
            view.exibirMensagemErro("Já existe um produto com este código.");
            return;
        }

        String nome = view.getNome();
        if (nome == null || nome.trim().isEmpty()) {
            view.exibirMensagemErro("Nome é obrigatório.");
            return;
        }
        if (nome.trim().length() < 2 || nome.trim().length() > 120) {
            view.exibirMensagemErro("Nome deve conter de 2 a 120 caracteres.");
            return;
        }

        String categoria = view.getCategoria();
        if (categoria == null || categoria.trim().isEmpty()) {
            view.exibirMensagemErro("Categoria é obrigatória.");
            return;
        }

        String precoStr = view.getPrecoUnitario();
        if (precoStr == null || precoStr.trim().isEmpty()) {
            view.exibirMensagemErro("Preço unitário é obrigatório.");
            return;
        }
        String precoNormalizado = precoStr.trim().replace(',', '.');
        int posSeparador = precoNormalizado.indexOf('.');
        if (posSeparador >= 0 && precoNormalizado.length() - posSeparador - 1 > 2) {
            view.exibirMensagemErro("Preço unitário deve ter no máximo duas casas decimais.");
            return;
        }
        double preco;
        try {
            preco = Double.parseDouble(precoNormalizado);
            if (preco <= 0) {
                view.exibirMensagemErro("Preço unitário deve ser maior que R$ 0,00.");
                return;
            }
        } catch (NumberFormatException e) {
            view.exibirMensagemErro("Preço unitário inválido. Use formato: 18,50");
            return;
        }

        int estoqueInicial = 0;
        if (produtoEdicao == null) {
            String estoqueStr = view.getQuantidadeEstoque();
            if (estoqueStr == null || estoqueStr.trim().isEmpty()) {
                view.exibirMensagemErro("Quantidade inicial em estoque é obrigatória.");
                return;
            }
            try {
                estoqueInicial = Integer.parseInt(estoqueStr.trim());
                if (estoqueInicial < 0) {
                    view.exibirMensagemErro(
                        "Quantidade em estoque não pode ser negativa.");
                    return;
                }
            } catch (NumberFormatException e) {
                view.exibirMensagemErro(
                    "Quantidade em estoque deve ser um número inteiro.");
                return;
            }
        }

        try {
            Produto produto;
            String operacao;

            if (produtoEdicao != null) {
                produto = produtoEdicao;
                produto.setNome(nome.trim());
                produto.setCategoria(categoria);
                produto.setPrecoUnitario(preco);
                operacao = "Edição de produto: " + produto.getCodigo()
                         + " - " + produto.getNome();
            } else {
                produto = new Produto(codigo, nome.trim(), categoria,
                                       preco, estoqueInicial);
                operacao = "Cadastro de produto: " + produto.getCodigo()
                         + " - " + produto.getNome();
            }
            produtoRepository.salvar(produto);
            registrarAuditoria(operacao);

            view.exibirMensagemSucesso("Produto salvo com sucesso!");
            view.fechar();

        } catch (IllegalArgumentException e) {
            view.exibirMensagemErro(e.getMessage());
        }
    }

    public void onCancelar() {
        view.fechar();
    }

    private void registrarAuditoria(String operacao) {
        if (logger != null) {
            try {
                String usuario = sessaoService.getNomeUsuarioLogado();
                logger.registrar(MensagemLogFactory.criarParaOperacao(
                    usuario != null ? usuario : "sistema", operacao));
            } catch (Exception e) {
                System.err.println("Falha ao registrar auditoria: " + e.getMessage());
            }
        }
    }
}

