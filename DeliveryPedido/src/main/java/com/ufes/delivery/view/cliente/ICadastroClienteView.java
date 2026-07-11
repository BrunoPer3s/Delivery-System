package com.ufes.delivery.view.cliente;

import java.util.List;

public interface ICadastroClienteView {

    String getNome();

    String getCpf();

    List<String[]> getEnderecos();

    int getEnderecoPadraoIndex();

    void setNome(String nome);

    void setCpf(String cpf);

    void setEnderecos(List<String[]> enderecos, int padraoIndex);

    void setCpfEditavel(boolean editavel);

    void exibirMensagemErro(String mensagem);

    void exibirMensagemSucesso(String mensagem);

    void fechar();

    void exibir();
}

