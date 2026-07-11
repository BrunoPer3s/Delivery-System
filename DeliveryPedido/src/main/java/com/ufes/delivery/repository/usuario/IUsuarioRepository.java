package com.ufes.delivery.repository.usuario;

import com.ufes.delivery.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface IUsuarioRepository {

    Optional<Usuario> buscarPorNomeUsuario(String nomeUsuario);

    List<Usuario> buscarPorNome(String nome);

    void salvar(Usuario usuario);

    void remover(String nomeUsuario);

    boolean existeUsuario();

    List<Usuario> listarTodos();
}

