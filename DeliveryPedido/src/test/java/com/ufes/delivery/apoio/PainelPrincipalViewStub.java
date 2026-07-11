package com.ufes.delivery.apoio;

import com.ufes.delivery.view.painel.IPainelPrincipalView;

import java.util.ArrayList;
import java.util.List;

public class PainelPrincipalViewStub implements IPainelPrincipalView {

    private String dataOperacao;
    private int pedidosDia;
    private int novos;
    private int aguardandoPagamento;
    private int emPreparo;
    private int aguardandoEntrega;
    private int emTransito;
    private int entreguesHoje;

    private List<String[]> pedidosCarregados = new ArrayList<>();
    private String nomeUsuario;
    private String loginFormatado;
    private String tipoPerfil;
    private boolean menuAdminHabilitado;
    private String mensagemErro;

    public String getDataOperacao() {
        return dataOperacao;
    }

    public int getPedidosDia() {
        return pedidosDia;
    }

    public int getNovos() {
        return novos;
    }

    public int getAguardandoPagamento() {
        return aguardandoPagamento;
    }

    public int getEmPreparo() {
        return emPreparo;
    }

    public int getAguardandoEntrega() {
        return aguardandoEntrega;
    }

    public int getEmTransito() {
        return emTransito;
    }

    public int getEntreguesHoje() {
        return entreguesHoje;
    }

    public List<String[]> getPedidosCarregados() {
        return pedidosCarregados;
    }

    public List<String> getCodigosNaTabela() {
        List<String> codigos = new ArrayList<>();
        for (String[] linha : pedidosCarregados) {
            codigos.add(linha[0]);
        }
        return codigos;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public String getLoginFormatado() {
        return loginFormatado;
    }

    public String getTipoPerfil() {
        return tipoPerfil;
    }

    public boolean isMenuAdminHabilitado() {
        return menuAdminHabilitado;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    @Override
    public void setDataOperacao(String data) {
        this.dataOperacao = data;
    }

    @Override
    public void setMetricas(int pedidosDia, int novos, int aguardandoPagamento,
                            int emPreparo, int aguardandoEntrega,
                            int emTransito, int entreguesHoje) {
        this.pedidosDia = pedidosDia;
        this.novos = novos;
        this.aguardandoPagamento = aguardandoPagamento;
        this.emPreparo = emPreparo;
        this.aguardandoEntrega = aguardandoEntrega;
        this.emTransito = emTransito;
        this.entreguesHoje = entreguesHoje;
    }

    @Override
    public void carregarPedidos(List<String[]> dados) {
        this.pedidosCarregados = dados;
    }

    @Override
    public void setInfoUsuario(String nomeUsuario, String loginFormatado, String tipoPerfil) {
        this.nomeUsuario = nomeUsuario;
        this.loginFormatado = loginFormatado;
        this.tipoPerfil = tipoPerfil;
    }

    @Override
    public void habilitarMenuAdmin(boolean habilitar) {
        this.menuAdminHabilitado = habilitar;
    }

    @Override
    public void exibirMensagemErro(String mensagem) {
        this.mensagemErro = mensagem;
    }

    @Override
    public void fechar() {
    }

    @Override
    public void exibir() {
    }
}
