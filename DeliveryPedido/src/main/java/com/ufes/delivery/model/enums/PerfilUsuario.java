package com.ufes.delivery.model.enums;

public enum PerfilUsuario {
    ADMINISTRADOR("Administrador"),
    ATENDENTE("Atendente");

    private final String descricao;

    PerfilUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

