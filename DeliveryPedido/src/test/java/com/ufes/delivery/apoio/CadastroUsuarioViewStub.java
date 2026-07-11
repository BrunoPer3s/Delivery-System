package com.ufes.delivery.apoio;

import com.ufes.delivery.view.usuario.ICadastroUsuarioView;

public class CadastroUsuarioViewStub implements ICadastroUsuarioView {

    private final String nome;
    private final String nomeUsuario;
    private final String senha;
    private final String confirmarSenha;

    private String mensagemErro;
    private String mensagemSucesso;
    private boolean fechada;

    public CadastroUsuarioViewStub(String nome, String nomeUsuario, String senha) {
        this(nome, nomeUsuario, senha, senha);
    }

    public CadastroUsuarioViewStub(String nome, String nomeUsuario,
                                    String senha, String confirmarSenha) {
        this.nome = nome;
        this.nomeUsuario = nomeUsuario;
        this.senha = senha;
        this.confirmarSenha = confirmarSenha;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    public String getMensagemSucesso() {
        return mensagemSucesso;
    }

    public boolean isFechada() {
        return fechada;
    }

    @Override
    public String getNome() {
        return nome;
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
    public String getConfirmarSenha() {
        return confirmarSenha;
    }

    @Override
    public void exibirMensagemErro(String mensagem) {
        this.mensagemErro = mensagem;
    }

    @Override
    public void exibirMensagemSucesso(String mensagem) {
        this.mensagemSucesso = mensagem;
    }

    @Override
    public void limparMensagem() {
        this.mensagemErro = null;
        this.mensagemSucesso = null;
    }

    @Override
    public void limparCampos() {
    }

    @Override
    public void fechar() {
        this.fechada = true;
    }

    @Override
    public void exibir() {
    }
}
