package com.ufes.delivery.repository.usuario;

import com.ufes.delivery.model.Usuario;
import com.ufes.delivery.model.perfil.Administrador;
import com.ufes.delivery.model.perfil.Atendente;
import com.ufes.delivery.model.situacao.Autorizado;
import com.ufes.delivery.model.situacao.NaoAutorizado;
import com.ufes.delivery.model.situacao.Pendente;
import com.ufes.delivery.util.SenhaUtil;

import java.util.*;

public class UsuarioRepositoryEmMemoria implements IUsuarioRepository {

    private final Map<String, Usuario> usuarios = new LinkedHashMap<>();

    public UsuarioRepositoryEmMemoria() {
        carregarUsuariosDeTeste();
    }

    private void carregarUsuariosDeTeste() {
        salvar(new Usuario("Administrador Master", "adminmaster",
                SenhaUtil.hashSenha("Admin123"),
                Administrador.INSTANCIA, Autorizado.INSTANCIA));

        salvar(new Usuario("Carlos Atendente", "atendente01",
                SenhaUtil.hashSenha("Atende01"),
                Atendente.INSTANCIA, Autorizado.INSTANCIA));

        salvar(new Usuario("Maria Oliveira", "maria01",
                SenhaUtil.hashSenha("Maria123"),
                Atendente.INSTANCIA, Pendente.INSTANCIA));

        salvar(new Usuario("Joao Silva", "joaosilva",
                SenhaUtil.hashSenha("Joao1234"),
                Atendente.INSTANCIA, NaoAutorizado.INSTANCIA));
    }

    @Override
    public Optional<Usuario> buscarPorNomeUsuario(String nomeUsuario) {
        return Optional.ofNullable(usuarios.get(nomeUsuario));
    }

    @Override
    public void salvar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo");
        }
        usuarios.put(usuario.getNomeUsuario(), usuario);
    }

    @Override
    public boolean existeUsuario() {
        return !usuarios.isEmpty();
    }

    @Override
    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuarios.values());
    }

    @Override
    public List<Usuario> buscarPorNome(String termo) {
        String termoLower = termo.toLowerCase();
        List<Usuario> resultado = new ArrayList<>();
        for (Usuario u : usuarios.values()) {
            boolean casaNome = u.getNome().toLowerCase().contains(termoLower);
            boolean casaNomeUsuario = u.getNomeUsuario().toLowerCase().contains(termoLower);
            if (casaNome || casaNomeUsuario) {
                resultado.add(u);
            }
        }
        return resultado;
    }

    @Override
    public void remover(String nomeUsuario) {
        usuarios.remove(nomeUsuario);
    }
}

