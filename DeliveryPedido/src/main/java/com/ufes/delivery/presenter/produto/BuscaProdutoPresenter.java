package com.ufes.delivery.presenter.produto;

import com.ufes.delivery.busca.BuscaInvalidaException;
import com.ufes.delivery.busca.CriterioBuscaProduto;
import com.ufes.delivery.busca.CriteriosBuscaProduto;
import com.ufes.delivery.log.GerenciadorDeLogAtivo;
import com.ufes.delivery.model.Produto;
import com.ufes.delivery.repository.RepositorioObserver;
import com.ufes.delivery.repository.produto.IProdutoRepository;
import com.ufes.delivery.service.SessaoService;
import com.ufes.delivery.view.produto.CadastroProdutoView;
import com.ufes.delivery.view.produto.IBuscaProdutoView;

import java.util.ArrayList;
import java.util.List;

public class BuscaProdutoPresenter implements RepositorioObserver {

    private final IBuscaProdutoView view;
    private final IProdutoRepository produtoRepository;
    private final GerenciadorDeLogAtivo logger;
    private final SessaoService sessaoService;

    public BuscaProdutoPresenter(IBuscaProdutoView view,
                                  IProdutoRepository produtoRepository,
                                  GerenciadorDeLogAtivo logger,
                                  SessaoService sessaoService) {
        this.view = view;
        this.produtoRepository = produtoRepository;
        this.logger = logger;
        this.sessaoService = sessaoService;

        this.produtoRepository.adicionarObservador(this);

        carregarTodos();
    }

    @Override
    public void onDadosAlterados() {
        carregarTodos();
    }

    public void aoFecharJanela() {
        produtoRepository.removerObservador(this);
    }

    public void onBuscar() {
        String valor = view.getValorBusca();

        if (valor == null || valor.trim().isEmpty()) {
            carregarTodos();
            return;
        }

        CriterioBuscaProduto criterio = CriteriosBuscaProduto.porRotulo(view.getTipoBusca());

        try {
            List<Produto> resultados = criterio.buscar(valor, produtoRepository);
            view.carregarResultados(converterParaDados(resultados));
            if (resultados.isEmpty()) {
                view.exibirMensagemInfo("Nenhum produto encontrado.");
            }
        } catch (BuscaInvalidaException e) {
            view.exibirMensagemErro(e.getMessage());
        }
    }

    public void onNovo() {
        CadastroProdutoView cadastroView = new CadastroProdutoView();
        CadastroProdutoPresenter cadastroPresenter = new CadastroProdutoPresenter(
                cadastroView, produtoRepository, logger, sessaoService, null);
        cadastroView.setPresenter(cadastroPresenter);
        cadastroView.exibir();
    }

    public void onVisualizar() {
        int linha = view.getLinhaSelecionada();
        if (linha < 0) {
            view.exibirMensagemErro("Selecione um produto na tabela.");
            return;
        }

        int codigo = view.getCodigoNaLinha(linha);
        produtoRepository.buscarPorCodigo(codigo).ifPresentOrElse(
            produto -> {
                CadastroProdutoView cadastroView = new CadastroProdutoView();
                CadastroProdutoPresenter cadastroPresenter = new CadastroProdutoPresenter(
                        cadastroView, produtoRepository, logger, sessaoService, produto);
                cadastroView.setPresenter(cadastroPresenter);
                cadastroView.exibir();
            },
            () -> view.exibirMensagemErro("Produto não encontrado.")
        );
    }

    public void onFechar() {
        view.fechar();
    }

    private void carregarTodos() {
        List<Produto> todos = produtoRepository.listarTodos();
        view.carregarResultados(converterParaDados(todos));
    }

    private List<String[]> converterParaDados(List<Produto> produtos) {
        List<String[]> dados = new ArrayList<>();
        for (Produto p : produtos) {
            dados.add(new String[]{
                String.valueOf(p.getCodigo()),
                p.getNome(),
                p.getCategoria(),
                p.getPrecoFormatado(),
                String.valueOf(p.getEstoqueAtual())
            });
        }
        return dados;
    }
}

