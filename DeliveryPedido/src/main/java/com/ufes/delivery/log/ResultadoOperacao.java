package com.ufes.delivery.log;

public enum ResultadoOperacao {

    SUCESSO("Sucesso"),
    REJEITADO("Rejeitado"),
    FALHA("Falha");

    private final String descricao;

    ResultadoOperacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
