package com.ufes.delivery.busca.produtoEstrategias;

import com.ufes.delivery.busca.BuscaInvalidaException;
import com.ufes.delivery.busca.EstrategiaBusca;
import com.ufes.delivery.model.Produto;
import com.ufes.delivery.repository.produto.IProdutoRepository;

import java.util.List;

public class BuscaProdutoPorNome implements EstrategiaBusca<Produto, IProdutoRepository> {

    @Override
    public String getRotulo() {
        return "Nome";
    }

    @Override
    public List<Produto> buscar(String valor, IProdutoRepository repositorio) {
        String termo = valor == null ? "" : valor.trim();
        if (termo.isEmpty()) {
            throw new BuscaInvalidaException("O valor da busca é obrigatório.");
        }
        return repositorio.buscarPorNome(termo);
    }
}
