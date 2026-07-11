package com.ufes.delivery.busca;

import com.ufes.delivery.model.Produto;
import com.ufes.delivery.repository.produto.IProdutoRepository;

import java.util.List;

public class BuscaProdutoPorNome implements CriterioBuscaProduto {

    @Override
    public String getRotulo() {
        return "Nome";
    }

    @Override
    public List<Produto> buscar(String valor, IProdutoRepository repositorio) {
        return repositorio.buscarPorNome(valor.trim());
    }
}

