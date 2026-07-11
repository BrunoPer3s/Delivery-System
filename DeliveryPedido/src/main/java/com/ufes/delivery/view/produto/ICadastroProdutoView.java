package com.ufes.delivery.view.produto;

public interface ICadastroProdutoView {

    String getCodigo();

    String getNome();

    String getCategoria();

    String getPrecoUnitario();

    String getQuantidadeEstoque();

    void setCodigo(String codigo);

    void setNome(String nome);

    void setCategoria(String categoria);

    void setPrecoUnitario(String preco);

    void setQuantidadeEstoque(String quantidade);

    void setCodigoEditavel(boolean editavel);

    void setEstoqueEditavel(boolean editavel);

    void setLabelEstoque(String label);

    void setModoVisualizacao(boolean visualizacao);

    void exibirMensagemErro(String mensagem);

    void exibirMensagemSucesso(String mensagem);

    void fechar();

    void exibir();
}

