package com.ufes.delivery.busca.clienteEstrategias;

import com.ufes.delivery.busca.BuscaInvalidaException;
import com.ufes.delivery.busca.EstrategiaBusca;
import com.ufes.delivery.model.Cliente;
import com.ufes.delivery.repository.cliente.IClienteRepository;

import java.util.List;

public class BuscaClientePorNome implements EstrategiaBusca<Cliente, IClienteRepository> {

    @Override
    public String getRotulo() {
        return "Nome";
    }

    @Override
    public List<Cliente> buscar(String valor, IClienteRepository repositorio) {
        String termo = valor == null ? "" : valor.trim();
        if (termo.isEmpty()) {
            throw new BuscaInvalidaException("O valor da busca é obrigatório.");
        }
        return repositorio.buscarPorNome(termo);
    }
}
