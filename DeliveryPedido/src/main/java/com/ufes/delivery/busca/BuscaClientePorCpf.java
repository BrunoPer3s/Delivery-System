package com.ufes.delivery.busca;

import com.ufes.delivery.model.Cliente;
import com.ufes.delivery.repository.cliente.IClienteRepository;
import com.ufes.delivery.util.CpfUtil;

import java.util.List;

public class BuscaClientePorCpf implements CriterioBuscaCliente {

    @Override
    public String getRotulo() {
        return "CPF";
    }

    @Override
    public List<Cliente> buscar(String valor, IClienteRepository repositorio) {
        String termo = valor == null ? "" : valor.trim();
        if (termo.isEmpty()) {
            throw new BuscaInvalidaException("O valor da busca é obrigatório.");
        }
        if (!CpfUtil.validar(termo)) {
            throw new BuscaInvalidaException("CPF inválido.");
        }
        String cpfLimpo = CpfUtil.removerMascara(termo);
        return repositorio.buscarPorCpf(cpfLimpo)
                .map(List::of)
                .orElse(List.of());
    }
}

