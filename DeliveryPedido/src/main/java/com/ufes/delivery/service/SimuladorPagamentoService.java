package com.ufes.delivery.service;

import java.time.LocalDateTime;

public class SimuladorPagamentoService {

    private static final String[] FORMAS_PAGAMENTO = {
        "Open Finance",
        "PIX Chave",
        "PIX QR Code",
        "Cartão de Crédito"
    };

    private static final double PROBABILIDADE_APROVACAO = 0.5;

    private final IFonteAleatoriedade fonteAleatoriedade;

    public SimuladorPagamentoService(IFonteAleatoriedade fonteAleatoriedade) {
        if (fonteAleatoriedade == null) {
            throw new IllegalArgumentException("Fonte de aleatoriedade é obrigatória");
        }
        this.fonteAleatoriedade = fonteAleatoriedade;
    }

    public ResultadoPagamento simularPagamento() {
        LocalDateTime agora = LocalDateTime.now();

        int indicePagamento = fonteAleatoriedade.sortearIndice(FORMAS_PAGAMENTO.length);
        String formaPagamento = FORMAS_PAGAMENTO[indicePagamento];

        boolean aprovado = fonteAleatoriedade.sortear(PROBABILIDADE_APROVACAO);

        if (aprovado) {
            LocalDateTime limiteEntrega = agora.plusMonths(1);
            long minutosAte = java.time.Duration.between(agora, limiteEntrega).toMinutes();
            int minutosAleatorios = fonteAleatoriedade.sortearInteiro(60, (int) Math.min(minutosAte, Integer.MAX_VALUE));
            LocalDateTime previsaoEntrega = agora.plusMinutes(minutosAleatorios);

            return new ResultadoPagamento(
                true,
                formaPagamento,
                agora,
                previsaoEntrega,
                "Pagamento aprovado via " + formaPagamento + "."
            );
        } else {
            return new ResultadoPagamento(
                false,
                formaPagamento,
                agora,
                null,
                "Pagamento reprovado via " + formaPagamento
                    + ". O pedido foi preservado para nova tentativa."
            );
        }
    }
}

