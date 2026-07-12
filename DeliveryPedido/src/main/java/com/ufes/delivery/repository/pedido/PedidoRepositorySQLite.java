package com.ufes.delivery.repository.pedido;

import com.ufes.delivery.model.estado.EstadoPedido;
import com.ufes.delivery.persistencia.BancoDados;
import com.ufes.delivery.persistencia.PersistenciaException;
import com.ufes.delivery.repository.RepositorioObserver;
import com.ufes.delivery.repository.SuporteObservadores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoRepositorySQLite implements IPedidoRepository {

    private final BancoDados banco;
    private final SuporteObservadores observadores = new SuporteObservadores();

    public PedidoRepositorySQLite(BancoDados banco) {
        this.banco = banco;
    }

    private static final String SQL_REGISTRAR =
            "INSERT INTO pedidos (codigo, nome_cliente, data_pedido, data_conclusao, "
            + "estado, valor_total) VALUES (?, ?, ?, ?, ?, ?) "
            + "ON CONFLICT(codigo) DO UPDATE SET "
            + "nome_cliente = excluded.nome_cliente, data_pedido = excluded.data_pedido, "
            + "data_conclusao = excluded.data_conclusao, estado = excluded.estado, "
            + "valor_total = excluded.valor_total";

    @Override
    public void registrar(PedidoRegistro pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido não pode ser nulo");
        }
        banco.executarEmTransacao(c -> gravar(c, pedido));
        observadores.notificar();
    }

    public void gravar(Connection conexao, PedidoRegistro pedido) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement(SQL_REGISTRAR)) {
            ps.setInt(1, pedido.getCodigo());
            ps.setString(2, pedido.getNomeCliente());
            ps.setString(3, pedido.getDataPedido());
            ps.setString(4, pedido.getDataConclusao());
            ps.setString(5, pedido.getEstado().getNome());
            ps.setString(6, pedido.getValorTotal());
            ps.executeUpdate();
        }
    }

    public void notificarAlteracao() {
        observadores.notificar();
    }

    @Override
    public List<PedidoRegistro> listarTodos() {
        String sql = "SELECT codigo, nome_cliente, data_pedido, data_conclusao, estado, valor_total "
                + "FROM pedidos ORDER BY codigo";
        List<PedidoRegistro> resultado = new ArrayList<>();
        try (Connection c = banco.abrirConexao();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao listar pedidos", e);
        }
        return resultado;
    }

    @Override
    public Optional<PedidoRegistro> buscarPorCodigo(int codigo) {
        String sql = "SELECT codigo, nome_cliente, data_pedido, data_conclusao, estado, valor_total "
                + "FROM pedidos WHERE codigo = ?";
        try (Connection c = banco.abrirConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao buscar pedido", e);
        }
    }

    @Override
    public List<PedidoRegistro> listarPorData(String dataOperacao) {
        String sql = "SELECT codigo, nome_cliente, data_pedido, data_conclusao, estado, valor_total "
                + "FROM pedidos WHERE data_pedido = ? ORDER BY codigo";
        List<PedidoRegistro> resultado = new ArrayList<>();
        try (Connection c = banco.abrirConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, dataOperacao);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao listar pedidos da data de operação", e);
        }
        return resultado;
    }

    @Override
    public int contarPorEstadoNaData(EstadoPedido estado, String dataOperacao) {
        String sql = "SELECT COUNT(*) FROM pedidos WHERE estado = ? AND data_pedido = ?";
        try (Connection c = banco.abrirConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, estado.getNome());
            ps.setString(2, dataOperacao);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao contar pedidos por estado", e);
        }
    }

    @Override
    public int contarEntreguesNaData(String dataOperacao) {
        String sql = "SELECT COUNT(*) FROM pedidos WHERE data_conclusao = ?";
        try (Connection c = banco.abrirConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, dataOperacao);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao contar pedidos entregues", e);
        }
    }

    @Override
    public int totalNaData(String dataOperacao) {
        String sql = "SELECT COUNT(*) FROM pedidos WHERE data_pedido = ?";
        try (Connection c = banco.abrirConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, dataOperacao);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao contar pedidos", e);
        }
    }

    @Override
    public int proximoCodigo() {
        String sql = "SELECT MAX(codigo) FROM pedidos";
        try (Connection c = banco.abrirConexao();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int maior = 1000;
            if (rs.next()) {
                int valor = rs.getInt(1);
                if (!rs.wasNull() && valor > maior) {
                    maior = valor;
                }
            }
            return maior + 1;
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao obter próximo código de pedido", e);
        }
    }

    @Override
    public List<TransicaoEstadoPedido> avancarEstadosPendentes() {
        List<TransicaoEstadoPedido> transicoes = new ArrayList<>();
        List<PedidoRegistro> todos = listarTodos();
        String sql = "UPDATE pedidos SET estado = ?, data_conclusao = ? WHERE codigo = ?";
        try (Connection c = banco.abrirConexao()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                for (PedidoRegistro pedido : todos) {
                    if (pedido.getEstado().isConclusivo()) {
                        continue;
                    }
                    String anterior = pedido.getEstado().getNome();
                    pedido.avancarEstado();
                    ps.setString(1, pedido.getEstado().getNome());
                    ps.setString(2, pedido.getDataConclusao());
                    ps.setInt(3, pedido.getCodigo());
                    ps.executeUpdate();
                    transicoes.add(new TransicaoEstadoPedido(
                            pedido.getCodigo(), anterior, pedido.getEstado().getNome()));
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao avançar estados dos pedidos", e);
        }
        if (!transicoes.isEmpty()) {
            observadores.notificar();
        }
        return transicoes;
    }

    @Override
    public void adicionarObservador(RepositorioObserver observador) {
        observadores.adicionar(observador);
    }

    @Override
    public void removerObservador(RepositorioObserver observador) {
        observadores.remover(observador);
    }

    private PedidoRegistro mapear(ResultSet rs) throws SQLException {
        return new PedidoRegistro(
                rs.getInt("codigo"),
                rs.getString("nome_cliente"),
                rs.getString("data_pedido"),
                rs.getString("data_conclusao"),
                EstadoPedido.porNome(rs.getString("estado")),
                rs.getString("valor_total"));
    }
}
