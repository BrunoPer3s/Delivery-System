package com.ufes.delivery.view;

public interface ILoginView {

    String getNomeUsuario();

    String getSenha();

    void exibirMensagemErro(String mensagem);

    void limparMensagemErro();

    void limparCampos();

    void fechar();

    void exibir();
}

