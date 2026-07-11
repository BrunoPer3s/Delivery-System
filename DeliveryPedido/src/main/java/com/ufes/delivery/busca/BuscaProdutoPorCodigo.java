package com.ufes.delivery.busca;

import com.ufes.delivery.model.Produto;
import com.ufes.delivery.repository.IProdutoRepository;

import java.util.List;

public class BuscaProdutoPorCodigo implements CriterioBuscaProduto {

    @Override
    public String getRotulo() {
        return "Código";
    }

    @Override
    public List<Produto> buscar(String valor, IProdutoRepository repositorio) {
        try {
            int codigo = Integer.parseInt(valor.trim());
            return repositorio.buscarPorCodigo(codigo)
                    .map(List::of)
                    .orElse(List.of());
        } catch (NumberFormatException e) {
            throw new BuscaInvalidaException("Código deve ser um número inteiro.");
        }
    }
}

