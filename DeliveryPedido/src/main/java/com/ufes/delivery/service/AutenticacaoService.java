package com.ufes.delivery.service;

import com.ufes.delivery.log.GerenciadorDeLogAtivo;
import com.ufes.delivery.log.MensagemLogFactory;
import com.ufes.delivery.model.Usuario;
import com.ufes.delivery.repository.usuario.IUsuarioRepository;
import com.ufes.delivery.util.SenhaUtil;

import java.util.Optional;

public class AutenticacaoService {

    private final IUsuarioRepository usuarioRepository;
    private final GerenciadorDeLogAtivo logger;

    public AutenticacaoService(IUsuarioRepository usuarioRepository,
                                GerenciadorDeLogAtivo logger) {
        if (usuarioRepository == null) {
            throw new IllegalArgumentException("Repositório de usuários é obrigatório");
        }
        this.usuarioRepository = usuarioRepository;
        this.logger = logger;
    }

    public ResultadoAutenticacao autenticar(String nomeUsuario, String senha) {
        Optional<Usuario> optUsuario = usuarioRepository.buscarPorNomeUsuario(nomeUsuario);

        if (optUsuario.isEmpty()) {
            registrarAuditoria(nomeUsuario, "Autenticação - Falha: credenciais inválidas");
            return ResultadoAutenticacao.credenciaisInvalidas();
        }

        Usuario usuario = optUsuario.get();

        if (!SenhaUtil.verificarSenha(senha, usuario.getSenhaHash())) {
            registrarAuditoria(nomeUsuario, "Autenticação - Falha: credenciais inválidas");
            return ResultadoAutenticacao.credenciaisInvalidas();
        }

        if (!usuario.getSituacao().podeIniciarSessao()) {
            registrarAuditoria(nomeUsuario,
                "Autenticação - Falha: usuário " + usuario.getSituacao().getDescricao());
            return ResultadoAutenticacao.naoAutorizado();
        }

        registrarAuditoria(nomeUsuario, "Autenticação - Sucesso");
        return ResultadoAutenticacao.sucesso(usuario);
    }

    private void registrarAuditoria(String nomeUsuario, String operacao) {
        if (logger != null) {
            try {
                logger.registrar(
                    MensagemLogFactory.criarParaOperacao(nomeUsuario, operacao));
            } catch (Exception e) {
                System.err.println("Falha ao registrar auditoria: " + e.getMessage());
            }
        }
    }

    public static class ResultadoAutenticacao {

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
}

