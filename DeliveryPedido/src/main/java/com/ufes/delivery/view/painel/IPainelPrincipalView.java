package com.ufes.delivery.view.painel;

import java.util.List;

public interface IPainelPrincipalView {

    void setDataOperacao(String data);

    void setMetricas(int pedidosDia, int novos, int aguardandoPagamento,
                     int emPreparo, int aguardandoEntrega,
                     int emTransito, int entreguesHoje);

    void carregarPedidos(List<String[]> dados);

    void setInfoUsuario(String nomeUsuario, String loginFormatado, String tipoPerfil);

    void habilitarMenuAdmin(boolean habilitar);

    void exibirMensagemErro(String mensagem);

    void fechar();

    void exibir();
}

