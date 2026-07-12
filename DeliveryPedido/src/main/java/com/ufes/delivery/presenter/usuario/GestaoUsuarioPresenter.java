package com.ufes.delivery.presenter.usuario;

import com.ufes.delivery.log.GerenciadorDeLogAtivo;
import com.ufes.delivery.log.MensagemLogFactory;
import com.ufes.delivery.model.Usuario;
import com.ufes.delivery.model.perfil.Perfil;
import com.ufes.delivery.repository.usuario.IUsuarioRepository;
import com.ufes.delivery.service.SessaoService;
import com.ufes.delivery.view.usuario.CadastroUsuarioView;
import com.ufes.delivery.view.usuario.IGestaoUsuarioView;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class GestaoUsuarioPresenter {

    private final IGestaoUsuarioView view;
    private final IUsuarioRepository usuarioRepository;
    private final SessaoService sessaoService;
    private final GerenciadorDeLogAtivo logger;

    public GestaoUsuarioPresenter(IGestaoUsuarioView view,
                                   IUsuarioRepository usuarioRepository,
                                   SessaoService sessaoService,
                                   GerenciadorDeLogAtivo logger) {
        this.view = view;
        this.usuarioRepository = usuarioRepository;
        this.sessaoService = sessaoService;
        this.logger = logger;

        carregarTabela();
    }

    public void onBuscar() {
        String termo = view.getTermoBusca();
        List<Usuario> usuarios;

        if (termo == null || termo.trim().isEmpty()) {
            usuarios = usuarioRepository.listarTodos();
        } else {
            usuarios = usuarioRepository.buscarPorNome(termo.trim());
        }

        view.carregarUsuarios(converterParaDadosView(usuarios));
    }

    public void onAutorizar() {
        List<String> selecionados = view.getNomesUsuariosSelecionados();

        if (selecionados.isEmpty()) {
            view.exibirMensagemErro("Selecione ao menos um usuário.");
            return;
        }

        for (String nomeUsuario : selecionados) {
            usuarioRepository.buscarPorNomeUsuario(nomeUsuario).ifPresent(usuario -> {
                usuario.autorizar();
                usuarioRepository.salvar(usuario);
            });
            registrarAuditoria("Autorização de usuário: " + nomeUsuario);
        }

        view.exibirMensagemInfo(selecionados.size() + " usuário(s) autorizado(s).");
        carregarTabela();
    }

    public void onDesautorizar() {
        List<String> selecionados = view.getNomesUsuariosSelecionados();

        if (selecionados.isEmpty()) {
            view.exibirMensagemErro("Selecione ao menos um usuário.");
            return;
        }

        String usuarioLogado = sessaoService.getNomeUsuarioLogado();

        if (selecionados.contains(usuarioLogado)) {
            view.exibirMensagemErro(
                "Não é permitido desautorizar o próprio usuário logado.");
            return;
        }

        for (String nomeUsuario : selecionados) {
            usuarioRepository.buscarPorNomeUsuario(nomeUsuario).ifPresent(usuario -> {
                usuario.desautorizar();
                usuarioRepository.salvar(usuario);
            });
            registrarAuditoria("Desautorização de usuário: " + nomeUsuario);
        }

        view.exibirMensagemInfo(selecionados.size() + " usuário(s) desautorizado(s).");
        carregarTabela();
    }

    public void onExcluir() {
        List<String> selecionados = view.getNomesUsuariosSelecionados();

        if (selecionados.isEmpty()) {
            view.exibirMensagemErro("Selecione ao menos um usuário.");
            return;
        }

        String usuarioLogado = sessaoService.getNomeUsuarioLogado();

        if (selecionados.contains(usuarioLogado)) {
            view.exibirMensagemErro(
                "Não é permitido excluir o próprio usuário logado.");
            return;
        }

        if (!view.confirmarExclusao(selecionados.size())) {
            return;
        }

        for (String nomeUsuario : selecionados) {
            usuarioRepository.remover(nomeUsuario);
            registrarAuditoria("Exclusão de usuário: " + nomeUsuario);
        }

        view.exibirMensagemInfo(selecionados.size() + " usuário(s) excluído(s).");
        carregarTabela();
    }

    public void onNovo() {
        CadastroUsuarioView cadastroView = new CadastroUsuarioView();
        CadastroUsuarioPresenter cadastroPresenter = new CadastroUsuarioPresenter(
                cadastroView, usuarioRepository, logger);
        cadastroView.setPresenter(cadastroPresenter);

        cadastroView.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                carregarTabela();
            }
        });

        cadastroView.exibir();
    }

    public void onFechar() {
        view.fechar();
    }

    public void onPerfilAlterado(String nomeUsuario, String novoPerfil) {
        usuarioRepository.buscarPorNomeUsuario(nomeUsuario).ifPresent(usuario -> {
            Perfil perfil = Perfil.porDescricao(novoPerfil);
            usuario.setPerfil(perfil);
            usuarioRepository.salvar(usuario);
            registrarAuditoria("Alteração de perfil de " + nomeUsuario +
                    " para " + perfil.getDescricao());
        });
    }

    private void carregarTabela() {
        List<Usuario> usuarios = usuarioRepository.listarTodos();
        view.carregarUsuarios(converterParaDadosView(usuarios));
    }

    private List<String[]> converterParaDadosView(List<Usuario> usuarios) {
        List<String[]> dados = new ArrayList<>();
        for (Usuario u : usuarios) {
            dados.add(new String[]{
                u.getNomeUsuario(),
                u.getNome(),
                String.valueOf(u.isAutorizado()),
                u.getPerfil().getDescricao(),
                u.getSituacao().getDescricao()
            });
        }
        return dados;
    }

    private void registrarAuditoria(String operacao) {
        if (logger != null) {
            try {
                String usuario = sessaoService.getNomeUsuarioLogado();
                logger.registrar(
                    MensagemLogFactory.criarParaOperacao(
                        usuario != null ? usuario : "sistema", operacao));
            } catch (Exception e) {
                System.err.println("Falha ao registrar auditoria: " + e.getMessage());
            }
        }
    }
}

