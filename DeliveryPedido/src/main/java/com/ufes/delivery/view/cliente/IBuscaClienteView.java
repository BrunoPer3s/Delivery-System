package com.ufes.delivery.view.cliente;

import java.util.List;

public interface IBuscaClienteView {

    String getTipoBusca();

    String getValorBusca();

    int getLinhaSelecionada();

    String getCpfNaLinha(int linha);

    void carregarResultados(List<String[]> dados);

    void exibirMensagemErro(String mensagem);

    void exibirMensagemInfo(String mensagem);

    void fechar();

    void exibir();
}

