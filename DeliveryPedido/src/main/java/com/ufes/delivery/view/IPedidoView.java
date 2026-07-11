package com.ufes.delivery.view;

import java.util.List;

public interface IPedidoView {


    String getTextoCliente();

    void setTextoCliente(String nome);

    void carregarEnderecos(List<String> enderecos);

    int getEnderecoSelecionadoIndex();

    void setEnderecoSelecionadoIndex(int index);


    void carregarItens(List<String[]> dados);

    int getLinhaSelecionada();

    String getQuantidadeNaLinha(int linha);


    String getCodigoCupom();

    void setCodigoCupom(String codigo);

    void setTotalDescontos(String valor);

    void setDescontoTaxaEntrega(String valor);

    void setTaxaEntregaFinal(String valor);

    void setTotalPedido(String valor);


    void exibirMensagemErro(String mensagem);

    void exibirMensagemSucesso(String mensagem);

    String exibirInputDialog(String mensagem);

    int exibirConfirmDialog(String mensagem, String titulo);

    Object exibirSelecaoDialog(String mensagem, String titulo, Object[] opcoes);


    void fechar();

    void exibir();
}

