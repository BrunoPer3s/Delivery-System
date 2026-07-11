package com.ufes.delivery.apoio;

import com.ufes.delivery.view.cliente.ICadastroClienteView;

import java.util.ArrayList;
import java.util.List;

public class CadastroClienteViewStub implements ICadastroClienteView {

    private String nome;
    private String cpf;
    private List<String[]> enderecos = new ArrayList<>();
    private int enderecoPadraoIndex;

    private boolean cpfEditavel = true;
    private String mensagemErro;
    private String mensagemSucesso;
    private boolean fechada;

    public static String[] endereco(String logradouro, String numero, String complemento,
                                     String bairro, String cidade, String uf, String cep) {
        return new String[]{logradouro, numero, complemento, bairro, cidade, uf, cep};
    }

    public static String[] enderecoValido(String logradouro) {
        return endereco(logradouro, "100", "", "Centro", "Vitória", "ES", "29000000");
    }

    public void preencher(String nome, String cpf, List<String[]> enderecos, int padraoIndex) {
        this.nome = nome;
        this.cpf = cpf;
        this.enderecos = enderecos;
        this.enderecoPadraoIndex = padraoIndex;
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

    public boolean isCpfEditavel() {
        return cpfEditavel;
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public String getCpf() {
        return cpf;
    }

    @Override
    public List<String[]> getEnderecos() {
        return enderecos;
    }

    @Override
    public int getEnderecoPadraoIndex() {
        return enderecoPadraoIndex;
    }

    @Override
    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @Override
    public void setEnderecos(List<String[]> enderecos, int padraoIndex) {
        this.enderecos = enderecos;
        this.enderecoPadraoIndex = padraoIndex;
    }

    @Override
    public void setCpfEditavel(boolean editavel) {
        this.cpfEditavel = editavel;
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
    public void fechar() {
        this.fechada = true;
    }

    @Override
    public void exibir() {
    }
}
