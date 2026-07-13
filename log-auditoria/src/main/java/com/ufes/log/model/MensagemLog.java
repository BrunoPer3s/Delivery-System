package com.ufes.log.model;

public class MensagemLog {
    private final String nomeUsuario;
    private final String data;
    private final String hora;
    private final String codigoPedido;
    private final String nomeOperacao;
    private final String nomeCliente;
    private final String recurso;
    private final String resultado;
    private final String justificativa;

    public MensagemLog(String nomeUsuario, String data, String hora, String codigoPedido, String nomeOperacao, String nomeCliente) {
        this(nomeUsuario, data, hora, codigoPedido, nomeOperacao, nomeCliente, "", "", "");
    }

    public MensagemLog(String nomeUsuario, String data, String hora, String codigoPedido,
                       String nomeOperacao, String nomeCliente,
                       String recurso, String resultado, String justificativa) {
        this.nomeUsuario = texto(nomeUsuario);
        this.data = texto(data);
        this.hora = texto(hora);
        this.codigoPedido = texto(codigoPedido);
        this.nomeOperacao = texto(nomeOperacao);
        this.nomeCliente = texto(nomeCliente);
        this.recurso = texto(recurso);
        this.resultado = texto(resultado);
        this.justificativa = texto(justificativa);
    }

    private static String texto(String valor) {
        return valor == null ? "" : valor;
    }

    public String getNomeUsuario() { return nomeUsuario; }
    public String getData() { return data; }
    public String getHora() { return hora; }
    public String getCodigoPedido() { return codigoPedido; }
    public String getNomeOperacao() { return nomeOperacao; }
    public String getNomeCliente() { return nomeCliente; }
    public String getRecurso() { return recurso; }
    public String getResultado() { return resultado; }
    public String getJustificativa() { return justificativa; }

    @Override
    public String toString() {
        return "MensagemLog{" +
                "nomeUsuario='" + nomeUsuario + '\'' +
                ", data='" + data + '\'' +
                ", hora='" + hora + '\'' +
                ", codigoPedido='" + codigoPedido + '\'' +
                ", nomeOperacao='" + nomeOperacao + '\'' +
                ", nomeCliente='" + nomeCliente + '\'' +
                ", recurso='" + recurso + '\'' +
                ", resultado='" + resultado + '\'' +
                ", justificativa='" + justificativa + '\'' +
                '}';
    }
}
