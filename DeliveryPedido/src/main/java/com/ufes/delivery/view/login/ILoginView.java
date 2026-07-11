package com.ufes.delivery.view.login;

public interface ILoginView {

    String getNomeUsuario();

    String getSenha();

    void exibirMensagemErro(String mensagem);

    void limparMensagemErro();

    void limparCampos();

    void fechar();

    void exibir();
}

