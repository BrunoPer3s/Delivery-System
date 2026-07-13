package com.ufes.delivery.service;

import com.ufes.delivery.model.Usuario;

public class ResultadoAutenticacao {

    public enum Tipo {
        SUCESSO,
        CREDENCIAIS_INVALIDAS,
        NAO_AUTORIZADO
    }

    private final Tipo tipo;
    private final Usuario usuario;

    private ResultadoAutenticacao(Tipo tipo, Usuario usuario) {
        this.tipo = tipo;
        this.usuario = usuario;
    }

    public static ResultadoAutenticacao sucesso(Usuario usuario) {
        return new ResultadoAutenticacao(Tipo.SUCESSO, usuario);
    }

    public static ResultadoAutenticacao credenciaisInvalidas() {
        return new ResultadoAutenticacao(Tipo.CREDENCIAIS_INVALIDAS, null);
    }

    public static ResultadoAutenticacao naoAutorizado() {
        return new ResultadoAutenticacao(Tipo.NAO_AUTORIZADO, null);
    }

    public Tipo getTipo() { return tipo; }
    public Usuario getUsuario() { return usuario; }
    public boolean isSucesso() { return tipo == Tipo.SUCESSO; }
}
