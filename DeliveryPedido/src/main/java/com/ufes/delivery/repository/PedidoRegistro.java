package com.ufes.delivery.repository;

import com.ufes.delivery.model.estado.EstadoPedido;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PedidoRegistro {

    private static final DateTimeFormatter DATA_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final int codigo;
    private final String nomeCliente;
    private final String dataPedido;
    private String dataConclusao;
    private EstadoPedido estado;
    private final String valorTotal;

    public PedidoRegistro(int codigo, String nomeCliente, String dataPedido,
                           String dataConclusao, EstadoPedido estado, String valorTotal) {
        if (estado == null) {
            throw new IllegalArgumentException("Estado do pedido deve ser informado");
        }
        this.codigo = codigo;
        this.nomeCliente = nomeCliente;
        this.dataPedido = dataPedido;
        this.dataConclusao = dataConclusao;
        this.estado = estado;
        this.valorTotal = valorTotal;
    }

    public int getCodigo() { return codigo; }
    public String getNomeCliente() { return nomeCliente; }
    public String getDataPedido() { return dataPedido; }
    public String getDataConclusao() { return dataConclusao; }
    public EstadoPedido getEstado() { return estado; }
    public String getValorTotal() { return valorTotal; }

    public void avancarEstado() {
        this.estado = estado.avancar();
        if (estado.isConclusivo() && dataConclusao == null) {
            this.dataConclusao = LocalDate.now().format(DATA_FORMATTER);
        }
    }

    public String[] toArrayTabela() {
        return new String[]{
            String.valueOf(codigo),
            nomeCliente,
            dataPedido,
            dataConclusao != null ? dataConclusao : "-",
            estado.getNome(),
            valorTotal,
            "Visualizar"
        };
    }
}

