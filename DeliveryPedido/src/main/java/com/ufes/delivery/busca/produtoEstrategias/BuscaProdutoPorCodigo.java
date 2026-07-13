package com.ufes.delivery.busca.produtoEstrategias;

import com.ufes.delivery.busca.BuscaInvalidaException;
import com.ufes.delivery.busca.EstrategiaBusca;
import com.ufes.delivery.model.Produto;
import com.ufes.delivery.repository.produto.IProdutoRepository;

import java.util.List;

public class BuscaProdutoPorCodigo extends EstrategiaBusca<Produto> {

    @Override
    public List<Produto> buscar(String valor, Object repositorio) throws RuntimeException {
        IProdutoRepository repo = (IProdutoRepository) repositorio;
        try {
            int codigo = Integer.parseInt(valor.trim());
            return repo.buscarPorCodigo(codigo)
                    .map(List::of)
                    .orElse(List.of());
        } catch (NumberFormatException e) {
            throw new BuscaInvalidaException("Código deve ser um número inteiro.");
        }
    }
}

