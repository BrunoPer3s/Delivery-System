package com.ufes.delivery.model.perfil;

import java.util.List;

public abstract class Perfil {

    private final String descricao;

    protected Perfil(String descricao) {
        this.descricao = descricao;
    }

    public abstract boolean podeAdministrar();

    public String getDescricao() {
        return descricao;
    }

    public static List<Perfil> todos() {
        return List.of(
                Administrador.INSTANCIA,
                Atendente.INSTANCIA);
    }

    public static Perfil porDescricao(String descricao) {
        for (Perfil perfil : todos()) {
            if (perfil.getDescricao().equals(descricao)) {
                return perfil;
            }
        }
        throw new IllegalArgumentException("Perfil desconhecido: " + descricao);
    }
}
