package com.ufes.delivery.busca;

import com.ufes.delivery.model.Produto;
import com.ufes.delivery.repository.IProdutoRepository;

import java.util.List;

public class BuscaProdutoPorCategoria implements CriterioBuscaProduto {

    @Override
    public String getRotulo() {
        return "Categoria";
    }

    @Override
    public List<Produto> buscar(String valor, IProdutoRepository repositorio) {
        return repositorio.buscarPorCategoria(valor.trim());
    }
}

