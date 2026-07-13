package com.ufes.delivery.busca.produtoEstrategias;

import com.ufes.delivery.busca.EstrategiaBusca;
import com.ufes.delivery.model.Produto;
import com.ufes.delivery.repository.produto.IProdutoRepository;

import java.util.List;

public class BuscaProdutoPorNome extends EstrategiaBusca<Produto> {

    @Override
    public List<Produto> buscar(String valor, Object repositorio) {
        IProdutoRepository repo = (IProdutoRepository) repositorio;
        return repo.buscarPorNome(valor.trim());
    }
}

