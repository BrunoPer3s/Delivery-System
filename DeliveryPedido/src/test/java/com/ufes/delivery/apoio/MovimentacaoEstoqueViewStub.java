package com.ufes.delivery.apoio;

import com.ufes.delivery.view.estoque.IMovimentacaoEstoqueView;

import java.util.ArrayList;
import java.util.List;

public class MovimentacaoEstoqueViewStub implements IMovimentacaoEstoqueView {

    private String textoBuscaProduto;
    private int linhaSelecionadaProduto = -1;
    private int codigoNaLinha;
    private String dataMovimentacao;
    private String tipoMovimentacao;
    private String quantidadeMovimentar;
    private String motivoAjuste;
    private String notaFiscal;

    private List<String[]> resultadosProdutos = new ArrayList<>();
    private String nomeProdutoSelecionado;
    private String estoqueAtualExibido;
    private String estoquePrevia;
    private String mensagemErro;
    private String mensagemSucesso;

    public void selecionarProduto(int codigo) {
        this.linhaSelecionadaProduto = 0;
        this.codigoNaLinha = codigo;
    }

    public void preencherMovimentacao(String data, String tipo, String quantidade,
                                       String motivo, String nota) {
        this.dataMovimentacao = data;
        this.tipoMovimentacao = tipo;
        this.quantidadeMovimentar = quantidade;
        this.motivoAjuste = motivo;
        this.notaFiscal = nota;
    }

    public String getEstoquePrevia() {
        return estoquePrevia;
    }

    public String getEstoqueAtualExibido() {
        return estoqueAtualExibido;
    }

    public String getNomeProdutoSelecionado() {
        return nomeProdutoSelecionado;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    public String getMensagemSucesso() {
        return mensagemSucesso;
    }

    public List<String[]> getResultadosProdutos() {
        return resultadosProdutos;
    }

    @Override
    public String getTextoBuscaProduto() {
        return textoBuscaProduto;
    }

    @Override
    public int getLinhaSelecionadaProduto() {
        return linhaSelecionadaProduto;
    }

    @Override
    public int getCodigoNaLinha(int linha) {
        return codigoNaLinha;
    }

    @Override
    public void carregarResultadosProdutos(List<String[]> dados) {
        this.resultadosProdutos = dados;
    }

    @Override
    public void setProdutoSelecionado(String nome, String estoqueAtual) {
        this.nomeProdutoSelecionado = nome;
        this.estoqueAtualExibido = estoqueAtual;
    }

    @Override
    public void limparProdutoSelecionado() {
        this.nomeProdutoSelecionado = null;
        this.estoqueAtualExibido = null;
    }

    @Override
    public String getDataMovimentacao() {
        return dataMovimentacao;
    }

    @Override
    public String getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    @Override
    public String getQuantidadeMovimentar() {
        return quantidadeMovimentar;
    }

    @Override
    public String getMotivoAjuste() {
        return motivoAjuste;
    }

    @Override
    public String getNotaFiscal() {
        return notaFiscal;
    }

    @Override
    public void setEstoquePrevia(String previa) {
        this.estoquePrevia = previa;
    }

    @Override
    public void habilitarMovimentacao(boolean habilitar) {
    }

    @Override
    public void setMotivoObrigatorio(boolean obrigatorio) {
    }

    @Override
    public void setNotaFiscalObrigatorio(boolean obrigatorio) {
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
    }

    @Override
    public void exibir() {
    }
}
