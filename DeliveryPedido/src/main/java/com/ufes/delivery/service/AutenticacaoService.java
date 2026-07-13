package com.ufes.delivery.service;

import com.ufes.delivery.log.GerenciadorDeLogAtivo;
import com.ufes.delivery.log.ResultadoOperacao;
import com.ufes.delivery.log.MensagemLogFactory;
import com.ufes.log.LogIndisponivelException;
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
            registrarAuditoria(nomeUsuario, ResultadoOperacao.REJEITADO, "Credenciais inválidas");
            return ResultadoAutenticacao.credenciaisInvalidas();
        }

        Usuario usuario = optUsuario.get();

        if (!SenhaUtil.verificarSenha(senha, usuario.getSenhaHash())) {
            registrarAuditoria(nomeUsuario, ResultadoOperacao.REJEITADO, "Credenciais inválidas");
            return ResultadoAutenticacao.credenciaisInvalidas();
        }

        if (!usuario.getSituacao().podeIniciarSessao()) {
            registrarAuditoria(nomeUsuario, ResultadoOperacao.REJEITADO,
                "Usuário com situação " + usuario.getSituacao().getDescricao());
            return ResultadoAutenticacao.naoAutorizado();
        }

        registrarAuditoria(nomeUsuario, ResultadoOperacao.SUCESSO, "");
        return ResultadoAutenticacao.sucesso(usuario);
    }

    private void registrarAuditoria(String nomeUsuario, ResultadoOperacao resultado,
                                     String justificativa) {
        if (logger == null) {
            return;
        }
        try {
            logger.registrar(MensagemLogFactory.operacao("Autenticação")
                    .recurso("Usuário " + nomeUsuario)
                    .resultado(resultado)
                    .justificativa(justificativa)
                    .paraUsuario(nomeUsuario));
        } catch (LogIndisponivelException e) {
            System.err.println("Auditoria indisponível: " + e.getMessage());
        }
    }

}

