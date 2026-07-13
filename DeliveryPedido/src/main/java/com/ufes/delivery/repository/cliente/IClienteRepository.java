package com.ufes.delivery.repository.cliente;

import com.ufes.delivery.model.Cliente;
import com.ufes.delivery.repository.RepositorioObserver;

import java.util.List;
import java.util.Optional;

public interface IClienteRepository {

    Optional<Cliente> buscarPorCpf(String cpf);

    List<Cliente> buscarPorNome(String nome);

    void salvar(Cliente cliente);

    List<Cliente> listarTodos();


    void adicionarObservador(RepositorioObserver observador);

    void removerObservador(RepositorioObserver observador);
}

