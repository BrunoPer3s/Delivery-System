package com.ufes.delivery.model.perfil;

import java.util.List;

public final class Perfis {

    public static final Perfil ADMINISTRADOR = Administrador.INSTANCIA;
    public static final Perfil ATENDENTE = Atendente.INSTANCIA;

    public static final List<Perfil> TODOS = List.of(ADMINISTRADOR, ATENDENTE);

    private Perfis() {}

    public static Perfil porDescricao(String descricao) {
        for (Perfil perfil : TODOS) {
            if (perfil.getDescricao().equals(descricao)) {
                return perfil;
            }
        }
        throw new IllegalArgumentException("Perfil desconhecido: " + descricao);
    }
}
