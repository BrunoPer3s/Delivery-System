package com.ufes.delivery.view.estoque;

import java.util.List;

public interface IMovimentacaoEstoqueView {

    String getTextoBuscaProduto();

    int getLinhaSelecionadaProduto();

    int getCodigoNaLinha(int linha);

    void carregarResultadosProdutos(List<String[]> dados);

    void setProdutoSelecionado(String nome, String estoqueAtual);

    void limparProdutoSelecionado();

    String getDataMovimentacao();

    String getTipoMovimentacao();

    String getQuantidadeMovimentar();

    String getMotivoAjuste();

    String getNotaFiscal();

    void setEstoquePrevia(String previa);

    void habilitarMovimentacao(boolean habilitar);

    void setMotivoObrigatorio(boolean obrigatorio);

    void setNotaFiscalObrigatorio(boolean obrigatorio);

    void exibirMensagemErro(String mensagem);

    void exibirMensagemSucesso(String mensagem);

    void fechar();

    void exibir();
}

