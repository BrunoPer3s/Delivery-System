package com.ufes.delivery.model.situacao;

import java.util.List;

public abstract class Situacao {

    private final String descricao;

    protected Situacao(String descricao) {
        this.descricao = descricao;
    }

    public abstract boolean podeIniciarSessao();

    public String getDescricao() {
        return descricao;
    }

    public Situacao autorizar() {
        throw new IllegalStateException(
                "Nao eh possivel alterar a situacao de " + descricao + " para autorizado");
    }

    public Situacao desautorizar() {
        throw new IllegalStateException(
                "Nao eh possivel alterar a situacao de " + descricao + " para desautorizado");
    }

    public static List<Situacao> todas() {
        return List.of(
                Autorizado.INSTANCIA,
                Pendente.INSTANCIA,
                NaoAutorizado.INSTANCIA);
    }

    public static Situacao porDescricao(String descricao) {
        for (Situacao situacao : todas()) {
            if (situacao.getDescricao().equals(descricao)) {
                return situacao;
            }
        }
        throw new IllegalArgumentException("Situacao desconhecida: " + descricao);
    }
}
