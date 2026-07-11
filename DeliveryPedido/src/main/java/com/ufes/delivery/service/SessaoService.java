package com.ufes.delivery.service;

import com.ufes.delivery.model.Sessao;
import com.ufes.delivery.model.Usuario;

import java.time.LocalDateTime;

public class SessaoService {

    private static SessaoService instancia;
    private Sessao sessaoAtual;

    private SessaoService() {
    }

    public static SessaoService getInstancia() {
        if (instancia == null) {
            instancia = new SessaoService();
        }
        return instancia;
    }

    public Sessao iniciarSessao(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário é obrigatório para iniciar sessão");
        }
        this.sessaoAtual = new Sessao(usuario, LocalDateTime.now());
        return this.sessaoAtual;
    }

    public Sessao getSessaoAtual() {
        return sessaoAtual;
    }

    public void encerrarSessao() {
        this.sessaoAtual = null;
    }

    public boolean isAutenticado() {
        return sessaoAtual != null;
    }

    public boolean isAdministrador() {
        return sessaoAtual != null
                && sessaoAtual.getUsuario().getPerfil().podeAdministrar();
    }

    public String getNomeUsuarioLogado() {
        if (isAutenticado()) {
            return sessaoAtual.getNomeUsuario();
        }
        return null;
    }
}

