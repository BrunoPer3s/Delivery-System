package com.ufes.delivery.view;

public interface ICadastroUsuarioView {

    String getNome();

    String getNomeUsuario();

    String getSenha();

    String getConfirmarSenha();

    void exibirMensagemErro(String mensagem);

    void exibirMensagemSucesso(String mensagem);

    void limparMensagem();

    void limparCampos();

    void fechar();

    void exibir();
}

