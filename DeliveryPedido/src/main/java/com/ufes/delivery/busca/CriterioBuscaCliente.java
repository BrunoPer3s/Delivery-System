package com.ufes.delivery.busca;

import com.ufes.delivery.model.Cliente;
import com.ufes.delivery.repository.IClienteRepository;

import java.util.List;

public interface CriterioBuscaCliente {

    String getRotulo();

    List<Cliente> buscar(String valor, IClienteRepository repositorio);
}

