package com.ufes.delivery.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class ResultadoPagamento {

    private static final AtomicInteger SEQUENCIA_TRANSACAO = new AtomicInteger(0);

    private final boolean aprovado;
    private final String formaPagamento;
    private final LocalDateTime dataHoraPagamento;
    private final LocalDateTime previsaoEntrega;
    private final String identificadorTransacao;
    private final String mensagem;

    private static final DateTimeFormatter TXN_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    public ResultadoPagamento(boolean aprovado, String formaPagamento,
                               LocalDateTime dataHoraPagamento,
                               LocalDateTime previsaoEntrega, String mensagem) {
        this.aprovado = aprovado;
        this.formaPagamento = formaPagamento;
        this.dataHoraPagamento = dataHoraPagamento;
        this.previsaoEntrega = previsaoEntrega;
        this.mensagem = mensagem;

        this.identificadorTransacao = "TXN-" + dataHoraPagamento.format(TXN_FORMATTER)
                + "-" + String.format("%04d", SEQUENCIA_TRANSACAO.incrementAndGet());
    }

    public boolean isAprovado() { return aprovado; }
    public String getFormaPagamento() { return formaPagamento; }
    public LocalDateTime getDataHoraPagamento() { return dataHoraPagamento; }
    public LocalDateTime getPrevisaoEntrega() { return previsaoEntrega; }
    public String getIdentificadorTransacao() { return identificadorTransacao; }
    public String getMensagem() { return mensagem; }
}

