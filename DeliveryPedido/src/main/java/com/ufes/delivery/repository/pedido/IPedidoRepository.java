package com.ufes.delivery.repository.pedido;

import com.ufes.delivery.repository.RepositorioObserver;

import com.ufes.delivery.model.estado.EstadoPedido;

import java.util.List;
import java.util.Optional;

public interface IPedidoRepository {

    void registrar(PedidoRegistro pedido);

    List<PedidoRegistro> listarTodos();

    Optional<PedidoRegistro> buscarPorCodigo(int codigo);

    int contarPorEstado(EstadoPedido estado);

    int total();

    int proximoCodigo();

    List<TransicaoEstadoPedido> avancarEstadosPendentes();


    void adicionarObservador(RepositorioObserver observador);

    void removerObservador(RepositorioObserver observador);
}

