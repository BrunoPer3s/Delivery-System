package com.ufes.delivery.busca.clienteEstrategias;

import com.ufes.delivery.busca.BuscaInvalidaException;
import com.ufes.delivery.busca.EstrategiaBusca;
import com.ufes.delivery.model.Cliente;
import com.ufes.delivery.repository.cliente.IClienteRepository;
import com.ufes.delivery.util.CpfUtil;

import java.util.List;

public class BuscaClientePorCpf extends EstrategiaBusca<Cliente> {

    @Override
    public List<Cliente> buscar(String valor, Object repositorio) {
        IClienteRepository repo = (IClienteRepository) repositorio;
        String termo = valor == null ? "" : valor.trim();
        if (termo.isEmpty()) {
            throw new BuscaInvalidaException("O valor da busca é obrigatório.");
        }
        if (!CpfUtil.validar(termo)) {
            throw new BuscaInvalidaException("CPF inválido.");
        }
        String cpfLimpo = CpfUtil.removerMascara(termo);
        return repo.buscarPorCpf(cpfLimpo)
                .map(List::of)
                .orElse(List.of());
    }
}

