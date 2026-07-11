package com.ufes.delivery.view.produto;

import java.util.List;

public interface IBuscaProdutoView {

    String getTipoBusca();

    String getValorBusca();

    int getLinhaSelecionada();

    int getCodigoNaLinha(int linha);

    void carregarResultados(List<String[]> dados);

    void exibirMensagemErro(String mensagem);

    void exibirMensagemInfo(String mensagem);

    void fechar();

    void exibir();
}

