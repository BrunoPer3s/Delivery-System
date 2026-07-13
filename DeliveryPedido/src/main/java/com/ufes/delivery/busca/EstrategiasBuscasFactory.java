package com.ufes.delivery.busca;

import com.ufes.delivery.busca.clienteEstrategias.BuscaClientePorCpf;
import com.ufes.delivery.busca.clienteEstrategias.BuscaClientePorNome;
import com.ufes.delivery.busca.produtoEstrategias.BuscaProdutoPorCategoria;
import com.ufes.delivery.busca.produtoEstrategias.BuscaProdutoPorCodigo;
import com.ufes.delivery.busca.produtoEstrategias.BuscaProdutoPorNome;
import com.ufes.delivery.model.Cliente;
import com.ufes.delivery.model.Produto;
import com.ufes.delivery.repository.cliente.IClienteRepository;
import com.ufes.delivery.repository.produto.IProdutoRepository;

import java.util.List;

public final class EstrategiasBuscasFactory {

    private EstrategiasBuscasFactory() {
    }

    public static List<EstrategiaBusca<Cliente, IClienteRepository>> estrategiasDeCliente() {
        return List.of(new BuscaClientePorNome(), new BuscaClientePorCpf());
    }

    public static List<EstrategiaBusca<Produto, IProdutoRepository>> estrategiasDeProduto() {
        return List.of(new BuscaProdutoPorNome(), new BuscaProdutoPorCodigo(),
                new BuscaProdutoPorCategoria());
    }

    public static EstrategiaBusca<Cliente, IClienteRepository> paraCliente(String rotulo) {
        return porRotulo(estrategiasDeCliente(), rotulo);
    }

    public static EstrategiaBusca<Produto, IProdutoRepository> paraProduto(String rotulo) {
        return porRotulo(estrategiasDeProduto(), rotulo);
    }

    private static <T, R> EstrategiaBusca<T, R> porRotulo(
            List<EstrategiaBusca<T, R>> estrategias, String rotulo) {
        for (EstrategiaBusca<T, R> estrategia : estrategias) {
            if (estrategia.getRotulo().equals(rotulo)) {
                return estrategia;
            }
        }
        throw new BuscaInvalidaException("Não é possível buscar por '" + rotulo + "'.");
    }
}
