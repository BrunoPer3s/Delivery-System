package com.ufes.delivery.model.enums;

public enum SituacaoUsuario {
    AUTORIZADO("Autorizado"),
    PENDENTE("Pendente"),
    NAO_AUTORIZADO("Nao autorizado");

    private final String descricao;

    SituacaoUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

