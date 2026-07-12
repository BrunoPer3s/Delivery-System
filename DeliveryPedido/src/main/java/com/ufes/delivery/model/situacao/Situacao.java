package com.ufes.delivery.model.situacao;

import java.util.ArrayList;
import java.util.List;

public abstract class Situacao {

    private static final List<Situacao> TODAS = new ArrayList<>();
    public abstract boolean podeIniciarSessao();

    public  String getDescricao() {
        return this.getClass().getSimpleName();
    }

    protected static void todasSituacoes(Situacao instancia) {
        TODAS.add(instancia);
    }

    public Situacao autorizar() {
        throw new IllegalStateException("Nao eh possivel alterar a situacao de " +
                this.getClass().getSimpleName() +
                "para autorizado");
    }

    public Situacao desautorizar() {
        throw new IllegalStateException("Nao eh possivel alterar a situacao de " +
                                        this.getClass().getSimpleName() +
                                        "para desautorizado");
    }

    public static Situacao porDescricao(String descricao) {
        for (Situacao situacao : TODAS) {
            if (situacao.getDescricao().equals(descricao)) {
                return situacao;
            }
        }
        throw new IllegalArgumentException("Situacao " + descricao.toUpperCase() +"desconhecida.");
    }
}
