package com.ufes.delivery.repository.pedido;

import com.ufes.delivery.model.estado.EstadoPedido;
import com.ufes.delivery.repository.RepositorioObserver;

import java.util.*;

public class PedidoRepositoryEmMemoria implements IPedidoRepository {

    private final List<PedidoRegistro> pedidos = new ArrayList<>();
    private final List<RepositorioObserver> observadores = new ArrayList<>();

    @Override
    public void registrar(PedidoRegistro pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido não pode ser nulo");
        }
        pedidos.add(pedido);
        notificarObservadores();
    }

    @Override
    public List<PedidoRegistro> listarTodos() {
        return Collections.unmodifiableList(pedidos);
    }

    @Override
    public Optional<PedidoRegistro> buscarPorCodigo(int codigo) {
        for (PedidoRegistro p : pedidos) {
            if (p.getCodigo() == codigo) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<PedidoRegistro> listarPorData(String dataOperacao) {
        List<PedidoRegistro> resultado = new ArrayList<>();
        for (PedidoRegistro p : pedidos) {
            if (Objects.equals(p.getDataPedido(), dataOperacao)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    @Override
    public int contarPorEstadoNaData(EstadoPedido estado, String dataOperacao) {
        int count = 0;
        for (PedidoRegistro p : pedidos) {
            if (p.getEstado().equals(estado)
                    && Objects.equals(p.getDataPedido(), dataOperacao)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int contarEntreguesNaData(String dataOperacao) {
        int count = 0;
        for (PedidoRegistro p : pedidos) {
            if (p.getDataConclusao() != null
                    && Objects.equals(p.getDataConclusao(), dataOperacao)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int totalNaData(String dataOperacao) {
        return listarPorData(dataOperacao).size();
    }

    @Override
    public int proximoCodigo() {
        int maior = 1000;
        for (PedidoRegistro p : pedidos) {
            if (p.getCodigo() > maior) {
                maior = p.getCodigo();
            }
        }
        return maior + 1;
    }

    @Override
    public List<TransicaoEstadoPedido> avancarEstadosPendentes() {
        List<TransicaoEstadoPedido> transicoes = new ArrayList<>();
        for (PedidoRegistro pedido : pedidos) {
            if (!pedido.getEstado().isConclusivo()) {
                String anterior = pedido.getEstado().getNome();
                pedido.avancarEstado();
                transicoes.add(new TransicaoEstadoPedido(
                        pedido.getCodigo(), anterior, pedido.getEstado().getNome()));
            }
        }
        if (!transicoes.isEmpty()) {
            notificarObservadores();
        }
        return transicoes;
    }


    @Override
    public void adicionarObservador(RepositorioObserver observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    @Override
    public void removerObservador(RepositorioObserver observador) {
        observadores.remove(observador);
    }

    private void notificarObservadores() {
        for (RepositorioObserver observador : observadores) {
            observador.onDadosAlterados();
        }
    }
}

