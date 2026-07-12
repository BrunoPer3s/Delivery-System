package com.ufes.delivery.model.perfil;

import java.util.ArrayList;
import java.util.List;

public abstract class Perfil {

    private static final List<Perfil> TODOS = new ArrayList<>();

    protected static void todosPerfis(Perfil instancia) {
        TODOS.add(instancia);
    }

    public String getDescricao() {
        return this.getClass().getSimpleName();
    }

    public static Perfil porDescricao(String descricao) {
        for (Perfil perfil : TODOS) {
            if (perfil.getDescricao().equals(descricao)) {
                return perfil;
            }
        }
        throw new IllegalArgumentException("Perfil " + descricao.toUpperCase() + " desconhecido");
    }

    public abstract boolean podeAdministrar();
}
