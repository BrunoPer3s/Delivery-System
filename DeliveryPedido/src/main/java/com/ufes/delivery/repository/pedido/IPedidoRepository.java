package com.ufes.delivery.repository.pedido;

import com.ufes.delivery.repository.RepositorioObserver;

import com.ufes.delivery.model.estado.EstadoPedido;

import java.util.List;
import java.util.Optional;

public interface IPedidoRepository {

    void registrar(PedidoRegistro pedido);

    List<PedidoRegistro> listarTodos();

    List<PedidoRegistro> listarPorData(String dataOperacao);

    Optional<PedidoRegistro> buscarPorCodigo(int codigo);

    int contarPorEstadoNaData(EstadoPedido estado, String dataOperacao);

    int contarEntreguesNaData(String dataOperacao);

    int totalNaData(String dataOperacao);

    int proximoCodigo();

    List<TransicaoEstadoPedido> avancarEstadosPendentes();


    void adicionarObservador(RepositorioObserver observador);

    void removerObservador(RepositorioObserver observador);
}

