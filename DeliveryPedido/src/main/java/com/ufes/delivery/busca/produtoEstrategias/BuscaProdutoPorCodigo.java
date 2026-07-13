package com.ufes.delivery.busca.produtoEstrategias;

import com.ufes.delivery.busca.BuscaInvalidaException;
import com.ufes.delivery.busca.EstrategiaBusca;
import com.ufes.delivery.model.Produto;
import com.ufes.delivery.repository.produto.IProdutoRepository;

import java.util.List;

public class BuscaProdutoPorCodigo implements EstrategiaBusca<Produto, IProdutoRepository> {

    @Override
    public String getRotulo() {
        return "Código";
    }

    @Override
    public List<Produto> buscar(String valor, IProdutoRepository repositorio) {
        String termo = valor == null ? "" : valor.trim();
        if (termo.isEmpty()) {
            throw new BuscaInvalidaException("O valor da busca é obrigatório.");
        }
        try {
            int codigo = Integer.parseInt(termo);
            return repositorio.buscarPorCodigo(codigo)
                    .map(List::of)
                    .orElse(List.of());
        } catch (NumberFormatException e) {
            throw new BuscaInvalidaException("Código deve ser um número inteiro.");
        }
    }
}
