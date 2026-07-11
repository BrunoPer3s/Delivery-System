package com.ufes.delivery.presenter.usuario;

import com.ufes.delivery.log.GerenciadorDeLogAtivo;
import com.ufes.delivery.log.MensagemLogFactory;
import com.ufes.delivery.model.Usuario;
import com.ufes.delivery.model.perfil.Perfil;
import com.ufes.delivery.model.perfil.Perfis;
import com.ufes.delivery.model.situacao.Situacao;
import com.ufes.delivery.model.situacao.Situacoes;
import com.ufes.delivery.repository.usuario.IUsuarioRepository;
import com.ufes.delivery.util.SenhaUtil;
import com.ufes.delivery.view.usuario.ICadastroUsuarioView;

public class CadastroUsuarioPresenter {

    private final ICadastroUsuarioView view;
    private final IUsuarioRepository usuarioRepository;
    private final GerenciadorDeLogAtivo logger;

    public CadastroUsuarioPresenter(ICadastroUsuarioView view,
                                     IUsuarioRepository usuarioRepository,
                                     GerenciadorDeLogAtivo logger) {
        this.view = view;
        this.usuarioRepository = usuarioRepository;
        this.logger = logger;
    }

    public void onSalvar() {
        view.limparMensagem();

        String nome = view.getNome();
        String nomeUsuario = view.getNomeUsuario();
        String senha = view.getSenha();
        String confirmarSenha = view.getConfirmarSenha();

        if (nome == null || nome.trim().isEmpty()) {
            view.exibirMensagemErro("Nome é obrigatório.");
            return;
        }

        if (nomeUsuario == null || nomeUsuario.trim().isEmpty()) {
            view.exibirMensagemErro("Nome de usuário é obrigatório.");
            return;
        }

        if (senha == null || senha.isEmpty()) {
            view.exibirMensagemErro("Senha é obrigatória.");
            return;
        }

        if (confirmarSenha == null || confirmarSenha.isEmpty()) {
            view.exibirMensagemErro("Confirmação de senha é obrigatória.");
            return;
        }

        String nomeTrimmed = nome.trim();
        if (nomeTrimmed.length() < 2 || nomeTrimmed.length() > 120) {
            view.exibirMensagemErro("Nome deve conter de 2 a 120 caracteres.");
            return;
        }
        if (!nomeTrimmed.matches("[\\p{L} '\\-]+")) {
            view.exibirMensagemErro(
                "Nome deve conter apenas letras, espaços, apóstrofos e hífens.");
            return;
        }

        if (nomeUsuario.length() < 3 || nomeUsuario.length() > 30) {
            view.exibirMensagemErro(
                "Nome de usuário deve conter de 3 a 30 caracteres.");
            return;
        }
        if (!nomeUsuario.matches("[a-z0-9]+")) {
            view.exibirMensagemErro(
                "Nome de usuário deve conter apenas letras minúsculas e algarismos, sem espaços.");
            return;
        }

        if (usuarioRepository.buscarPorNomeUsuario(nomeUsuario).isPresent()) {
            view.exibirMensagemErro("Nome de usuário já está em uso.");
            return;
        }

        if (senha.length() < 8 || senha.length() > 64) {
            view.exibirMensagemErro("Senha deve conter de 8 a 64 caracteres.");
            return;
        }

        if (!senha.equals(confirmarSenha)) {
            view.exibirMensagemErro("Senha e confirmação de senha não conferem.");
            return;
        }

        Perfil perfil;
        Situacao situacao;

        if (!usuarioRepository.existeUsuario()) {
            perfil = Perfis.ADMINISTRADOR;
            situacao = Situacoes.AUTORIZADO;
        } else {
            perfil = Perfis.ATENDENTE;
            situacao = Situacoes.PENDENTE;
        }

        String senhaHash = SenhaUtil.hashSenha(senha);
        Usuario novoUsuario = new Usuario(nomeTrimmed, nomeUsuario, senhaHash, perfil, situacao);
        usuarioRepository.salvar(novoUsuario);

        registrarAuditoria(nomeUsuario,
            "Cadastro de usuário - Perfil: " + perfil.getDescricao() +
            ", Situação: " + situacao.getDescricao());

        String mensagemSucesso;
        if (situacao.podeIniciarSessao()) {
            mensagemSucesso = "Usuário cadastrado com sucesso! " +
                "Perfil: " + perfil.getDescricao() + ". " +
                "Você já pode fazer login.";
        } else {
            mensagemSucesso = "Usuário cadastrado com sucesso! " +
                "Perfil: " + perfil.getDescricao() + ". " +
                "Situação: " + situacao.getDescricao() + " — " +
                "aguarde autorização de um administrador para acessar o sistema.";
        }

        view.exibirMensagemSucesso(mensagemSucesso);
        view.fechar();
    }

    public void onCancelar() {
        view.limparCampos();
        view.fechar();
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
}

