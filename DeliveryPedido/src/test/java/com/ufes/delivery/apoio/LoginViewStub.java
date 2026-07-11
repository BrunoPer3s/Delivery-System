package com.ufes.delivery.apoio;

import com.ufes.delivery.view.login.ILoginView;

public class LoginViewStub implements ILoginView {

    private final String nomeUsuario;
    private final String senha;

    private String mensagemErro;
    private boolean camposLimpos;
    private boolean fechada;

    public LoginViewStub(String nomeUsuario, String senha) {
        this.nomeUsuario = nomeUsuario;
        this.senha = senha;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    public boolean isCamposLimpos() {
        return camposLimpos;
    }

    public boolean isFechada() {
        return fechada;
    }

    @Override
    public String getNomeUsuario() {
        return nomeUsuario;
    }

    @Override
    public String getSenha() {
        return senha;
    }

    @Override
    public void exibirMensagemErro(String mensagem) {
        this.mensagemErro = mensagem;
    }

    @Override
    public void limparMensagemErro() {
        this.mensagemErro = null;
    }

    @Override
    public void limparCampos() {
        this.camposLimpos = true;
    }

    @Override
    public void fechar() {
        this.fechada = true;
    }

    @Override
    public void exibir() {
    }
}
