package com.ufes.delivery.busca.clienteEstrategias;

import com.ufes.delivery.busca.BuscaInvalidaException;
import com.ufes.delivery.busca.EstrategiaBusca;
import com.ufes.delivery.model.Cliente;
import com.ufes.delivery.repository.cliente.IClienteRepository;

import java.util.List;

public class BuscaClientePorNome extends EstrategiaBusca<Cliente> {

    @Override
    public List<Cliente> buscar(String valor, Object repositorio) throws RuntimeException {
        IClienteRepository repo = (IClienteRepository) repositorio;
        String termo = valor == null ? "" : valor.trim();
        if (termo.isEmpty()) {
            throw new BuscaInvalidaException("O valor da busca é obrigatório.");
        }
        return repo.buscarPorNome(termo);
    }
}

