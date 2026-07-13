package com.ufes.delivery.busca.produtoEstrategias;

import com.ufes.delivery.busca.EstrategiaBusca;
import com.ufes.delivery.model.Produto;
import com.ufes.delivery.repository.produto.IProdutoRepository;

import java.util.List;

public class BuscaProdutoPorCategoria extends EstrategiaBusca<Produto> {

    @Override
    public List<Produto> buscar(String valor, Object repositorio) throws RuntimeException {
        IProdutoRepository repo = (IProdutoRepository) repositorio;
        return repo.buscarPorCategoria(valor.trim());
    }
}

