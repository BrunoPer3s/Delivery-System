package com.ufes.delivery.model.situacao;

import java.util.List;

public final class Situacoes {

    public static final Situacao AUTORIZADO = Autorizado.INSTANCIA;
    public static final Situacao PENDENTE = Pendente.INSTANCIA;
    public static final Situacao NAO_AUTORIZADO = NaoAutorizado.INSTANCIA;

    public static final List<Situacao> TODOS = List.of(AUTORIZADO, PENDENTE, NAO_AUTORIZADO);

    private Situacoes() {}

    public static Situacao porDescricao(String descricao) {
        for (Situacao situacao : TODOS) {
            if (situacao.getDescricao().equals(descricao)) {
                return situacao;
            }
        }
        throw new IllegalArgumentException("Situacao desconhecida: " + descricao);
    }
}
