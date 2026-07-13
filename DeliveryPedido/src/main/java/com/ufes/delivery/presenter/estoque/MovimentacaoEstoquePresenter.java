package com.ufes.delivery.presenter.estoque;

import com.ufes.delivery.log.GerenciadorDeLogAtivo;
import com.ufes.delivery.log.ResultadoOperacao;
import com.ufes.delivery.log.MensagemLogFactory;
import com.ufes.log.LogIndisponivelException;
import com.ufes.delivery.model.Produto;
import com.ufes.delivery.repository.produto.IProdutoRepository;
import com.ufes.delivery.service.SessaoService;
import com.ufes.delivery.view.estoque.IMovimentacaoEstoqueView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoEstoquePresenter {

    private final IMovimentacaoEstoqueView view;
    private final IProdutoRepository produtoRepository;
    private final GerenciadorDeLogAtivo logger;
    private final SessaoService sessaoService;

    private Produto produtoSelecionado;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public MovimentacaoEstoquePresenter(IMovimentacaoEstoqueView view,
                                         IProdutoRepository produtoRepository,
                                         GerenciadorDeLogAtivo logger,
                                         SessaoService sessaoService) {
        this.view = view;
        this.produtoRepository = produtoRepository;
        this.logger = logger;
        this.sessaoService = sessaoService;

        view.habilitarMovimentacao(false);
    }


    public void onBuscarProduto() {
        String texto = view.getTextoBuscaProduto();
        List<Produto> resultados;

        if (texto == null || texto.trim().isEmpty()) {
            resultados = produtoRepository.listarTodos();
        } else {
            try {
                int codigo = Integer.parseInt(texto.trim());
                resultados = produtoRepository.buscarPorCodigo(codigo)
                        .map(List::of)
                        .orElse(List.of());
            } catch (NumberFormatException e) {
                resultados = produtoRepository.buscarPorNome(texto.trim());
            }
        }

        view.carregarResultadosProdutos(converterParaDados(resultados));
    }

    public void onSelecionar() {
        int linha = view.getLinhaSelecionadaProduto();
        if (linha < 0) {
            view.exibirMensagemErro("Selecione um produto na tabela.");
            return;
        }

        int codigo = view.getCodigoNaLinha(linha);
        produtoRepository.buscarPorCodigo(codigo).ifPresentOrElse(
            produto -> {
                this.produtoSelecionado = produto;
                view.setProdutoSelecionado(
                    produto.getNome(),
                    String.valueOf(produto.getEstoqueAtual()));
                view.habilitarMovimentacao(true);
                view.setEstoquePrevia("");
            },
            () -> view.exibirMensagemErro("Produto não encontrado.")
        );
    }


    public void onTipoMovimentacaoAlterado() {
        String tipo = view.getTipoMovimentacao();
        boolean isAjuste = "Ajuste de estoque".equals(tipo);

        view.setMotivoObrigatorio(isAjuste);
        view.setNotaFiscalObrigatorio(!isAjuste);
    }

    public void onQuantidadeAlterada() {
        if (produtoSelecionado == null) return;

        String qtdStr = view.getQuantidadeMovimentar();
        if (qtdStr == null || qtdStr.trim().isEmpty()) {
            view.setEstoquePrevia("");
            return;
        }

        try {
            int quantidade = Integer.parseInt(qtdStr.trim());
            String tipo = view.getTipoMovimentacao();

            int estoqueAtual = produtoSelecionado.getEstoqueAtual();
            int estoquePrevia;

            if ("Ajuste de estoque".equals(tipo)) {
                estoquePrevia = estoqueAtual + quantidade;
            } else {
                estoquePrevia = estoqueAtual + Math.abs(quantidade);
            }

            view.setEstoquePrevia(String.valueOf(estoquePrevia));
        } catch (NumberFormatException e) {
            view.setEstoquePrevia("?");
        }
    }

    public void onConfirmarMovimentacao() {
        if (!sessaoService.isAdministrador()) {
            view.exibirMensagemErro("Movimentação de estoque é restrita ao Administrador.");
            registrarAuditoria("Movimentação de estoque", "Estoque", ResultadoOperacao.REJEITADO,
                    "Acesso negado - perfil sem permissão administrativa");
            return;
        }

        if (produtoSelecionado == null) {
            view.exibirMensagemErro("Selecione um produto primeiro.");
            return;
        }

        String dataStr = view.getDataMovimentacao();
        if (dataStr == null || dataStr.trim().isEmpty()) {
            view.exibirMensagemErro("Data da movimentação é obrigatória.");
            return;
        }
        LocalDate dataMovimentacao;
        try {
            dataMovimentacao = LocalDate.parse(dataStr.trim(), DATE_FORMATTER);
            if (dataMovimentacao.isAfter(LocalDate.now())) {
                view.exibirMensagemErro(
                    "Data da movimentação não pode ser posterior à data atual.");
                return;
            }
        } catch (DateTimeParseException e) {
            view.exibirMensagemErro("Data inválida. Use o formato DD/MM/AAAA.");
            return;
        }

        String tipo = view.getTipoMovimentacao();
        if (tipo == null || tipo.trim().isEmpty()) {
            view.exibirMensagemErro("Tipo de movimentação é obrigatório.");
            return;
        }

        String qtdStr = view.getQuantidadeMovimentar();
        if (qtdStr == null || qtdStr.trim().isEmpty()) {
            view.exibirMensagemErro("Quantidade a movimentar é obrigatória.");
            return;
        }
        int quantidade;
        try {
            quantidade = Integer.parseInt(qtdStr.trim());
            if (quantidade == 0) {
                view.exibirMensagemErro("Quantidade a movimentar deve ser diferente de zero.");
                return;
            }
        } catch (NumberFormatException e) {
            view.exibirMensagemErro("Quantidade deve ser um número inteiro.");
            return;
        }

        boolean isAjuste = "Ajuste de estoque".equals(tipo);

        if (!isAjuste && quantidade < 0) {
            view.exibirMensagemErro(
                "Quantidade de uma Entrada deve ser positiva. "
                + "Para reduzir o estoque, selecione Ajuste de estoque.");
            return;
        }

        String motivo = view.getMotivoAjuste();
        if (isAjuste && (motivo == null || motivo.trim().isEmpty())) {
            view.exibirMensagemErro("Motivo do ajuste é obrigatório.");
            return;
        }

        String notaFiscal = view.getNotaFiscal();
        if (!isAjuste && (notaFiscal == null || notaFiscal.trim().isEmpty())) {
            view.exibirMensagemErro("Nota fiscal de entrada é obrigatória.");
            return;
        }

        int ajusteEfetivo;
        if (isAjuste) {
            ajusteEfetivo = quantidade;
        } else {
            ajusteEfetivo = Math.abs(quantidade);
        }

        Produto produto = produtoRepository
                .buscarPorCodigo(produtoSelecionado.getCodigo())
                .orElse(null);
        if (produto == null) {
            view.exibirMensagemErro("Produto não está mais cadastrado. Selecione-o novamente.");
            return;
        }
        this.produtoSelecionado = produto;

        int estoqueResultante = produto.getEstoqueAtual() + ajusteEfetivo;
        if (estoqueResultante < 0) {
            view.exibirMensagemErro(
                "Movimentação resultaria em estoque negativo (" + estoqueResultante +
                "). Estoque atual: " + produto.getEstoqueAtual() + ".");
            return;
        }

        try {
            produto.ajustarEstoque(ajusteEfetivo);
            produtoRepository.salvar(produto);

            String justificativa = isAjuste
                    ? "Motivo: " + motivo.trim()
                    : "Nota fiscal: " + notaFiscal.trim();
            registrarAuditoria(
                    "Movimentação de estoque - " + tipo,
                    "Produto " + produto.getCodigo() + " - " + produto.getNome(),
                    ResultadoOperacao.SUCESSO,
                    String.format("Data: %s | Quantidade: %d | Estoque: %d -> %d | %s",
                            dataMovimentacao.format(DATE_FORMATTER),
                            ajusteEfetivo,
                            estoqueResultante - ajusteEfetivo,
                            estoqueResultante,
                            justificativa));

            view.exibirMensagemSucesso(
                "Movimentação confirmada com sucesso!\n" +
                "Estoque atualizado: " + estoqueResultante);

            view.setProdutoSelecionado(
                produto.getNome(),
                String.valueOf(produto.getEstoqueAtual()));
            view.setEstoquePrevia("");
            onBuscarProduto();

        } catch (IllegalStateException e) {
            view.exibirMensagemErro(e.getMessage());
        }
    }

    public void onCancelar() {
        view.fechar();
    }


    private List<String[]> converterParaDados(List<Produto> produtos) {
        List<String[]> dados = new ArrayList<>();
        for (Produto p : produtos) {
            dados.add(new String[]{
                String.valueOf(p.getCodigo()),
                p.getNome(),
                p.getCategoria(),
                String.valueOf(p.getEstoqueAtual())
            });
        }
        return dados;
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
                view.exibirMensagemErro(
                        "A operação foi concluída, mas o registro de auditoria falhou.");
            }
        }
    }
}

