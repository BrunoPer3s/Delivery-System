package com.ufes.delivery.busca;

import com.ufes.delivery.busca.clienteEstrategias.BuscaClientePorCpf;
import com.ufes.delivery.busca.clienteEstrategias.BuscaClientePorNome;
import com.ufes.delivery.busca.produtoEstrategias.BuscaProdutoPorCategoria;
import com.ufes.delivery.busca.produtoEstrategias.BuscaProdutoPorCodigo;
import com.ufes.delivery.busca.produtoEstrategias.BuscaProdutoPorNome;

public class EstrategiasBuscasFactory {

    @SuppressWarnings("unchecked")
    public static <T> EstrategiaBusca<T> porRotulo(String tipo) {
        return (EstrategiaBusca<T>) switch (tipo) {
            case "Cliente Nome" -> new BuscaClientePorNome();
            case "Cliente CPF" -> new BuscaClientePorCpf();
            case "Produto Nome" -> new BuscaProdutoPorNome();
            case "Produto Código" -> new BuscaProdutoPorCodigo();
            case "Produto Categoria" -> new BuscaProdutoPorCategoria();
            default -> throw new BuscaInvalidaException("Não é possivél busca por '" + tipo + "'.");
        };
    }
}
