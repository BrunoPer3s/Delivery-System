package com.ufes.log.model;

public class MensagemLog {
    private final String nomeUsuario;
    private final String data;
    private final String hora;
    private final String codigoPedido;
    private final String nomeOperacao;
    private final String nomeCliente;

    public MensagemLog(String nomeUsuario, String data, String hora, String codigoPedido, String nomeOperacao, String nomeCliente) {
        this.nomeUsuario = nomeUsuario;
        this.data = data;
        this.hora = hora;
        this.codigoPedido = codigoPedido;
        this.nomeOperacao = nomeOperacao;
        this.nomeCliente = nomeCliente;
    }

    public String getNomeUsuario() { return nomeUsuario; }
    public String getData() { return data; }
    public String getHora() { return hora; }
    public String getCodigoPedido() { return codigoPedido; }
    public String getNomeOperacao() { return nomeOperacao; }
    public String getNomeCliente() { return nomeCliente; }

    @Override
    public String toString() {
        return "MensagemLog{" +
                "nomeUsuario='" + nomeUsuario + '\'' +
                ", data='" + data + '\'' +
                ", hora='" + hora + '\'' +
                ", codigoPedido='" + codigoPedido + '\'' +
                ", nomeOperacao='" + nomeOperacao + '\'' +
                ", nomeCliente='" + nomeCliente + '\'' +
                '}';
    }
}

