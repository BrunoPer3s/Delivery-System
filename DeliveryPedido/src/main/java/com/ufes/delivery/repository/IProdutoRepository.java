package com.ufes.delivery.repository;

import com.ufes.delivery.model.Produto;

import java.util.List;
import java.util.Optional;

public interface IProdutoRepository {

    Optional<Produto> buscarPorCodigo(int codigo);

    List<Produto> buscarPorNome(String nome);

    List<Produto> buscarPorCategoria(String categoria);

    void salvar(Produto produto);

    List<Produto> listarTodos();


    void adicionarObservador(RepositorioObserver observador);

    void removerObservador(RepositorioObserver observador);
}

