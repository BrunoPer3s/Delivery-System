package com.ufes.delivery.apoio;

import com.ufes.delivery.view.usuario.IGestaoUsuarioView;

import java.util.ArrayList;
import java.util.List;

public class GestaoUsuarioViewStub implements IGestaoUsuarioView {

    private String termoBusca;
    private List<String> selecionados = new ArrayList<>();
    private boolean confirmaExclusao = true;

    private List<String[]> usuariosCarregados = new ArrayList<>();
    private String mensagemErro;
    private String mensagemInfo;

    public void setTermoBusca(String termoBusca) {
        this.termoBusca = termoBusca;
    }

    public void setSelecionados(List<String> selecionados) {
        this.selecionados = selecionados;
    }

    public void setConfirmaExclusao(boolean confirmaExclusao) {
        this.confirmaExclusao = confirmaExclusao;
    }

    public List<String[]> getUsuariosCarregados() {
        return usuariosCarregados;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    public String getMensagemInfo() {
        return mensagemInfo;
    }

    public List<String> getNomesUsuariosNaTabela() {
        List<String> nomes = new ArrayList<>();
        for (String[] linha : usuariosCarregados) {
            nomes.add(linha[0]);
        }
        return nomes;
    }

    public String getSituacaoNaTabela(String nomeUsuario) {
        for (String[] linha : usuariosCarregados) {
            if (linha[0].equals(nomeUsuario)) {
                return linha[4];
            }
        }
        return null;
    }

    @Override
    public String getTermoBusca() {
        return termoBusca;
    }

    @Override
    public List<String> getNomesUsuariosSelecionados() {
        return selecionados;
    }

    @Override
    public void carregarUsuarios(List<String[]> dados) {
        this.usuariosCarregados = dados;
    }

    @Override
    public void exibirMensagemErro(String mensagem) {
        this.mensagemErro = mensagem;
    }

    @Override
    public void exibirMensagemInfo(String mensagem) {
        this.mensagemInfo = mensagem;
    }

    @Override
    public boolean confirmarExclusao(int quantidade) {
        return confirmaExclusao;
    }

    @Override
    public void fechar() {
    }

    @Override
    public void exibir() {
    }
}
