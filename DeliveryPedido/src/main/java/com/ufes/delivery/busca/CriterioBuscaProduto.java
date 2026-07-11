package com.ufes.delivery.busca;

import com.ufes.delivery.model.Produto;
import com.ufes.delivery.repository.IProdutoRepository;

import java.util.List;

public interface CriterioBuscaProduto {

    String getRotulo();

    List<Produto> buscar(String valor, IProdutoRepository repositorio);
}

