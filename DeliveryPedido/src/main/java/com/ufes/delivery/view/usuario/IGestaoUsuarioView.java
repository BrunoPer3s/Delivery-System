package com.ufes.delivery.view.usuario;

import java.util.List;

public interface IGestaoUsuarioView {

    String getTermoBusca();

    List<String> getNomesUsuariosSelecionados();

    void carregarUsuarios(List<String[]> dados);

    void exibirMensagemErro(String mensagem);

    void exibirMensagemInfo(String mensagem);

    boolean confirmarExclusao(int quantidade);

    void fechar();

    void exibir();
}

