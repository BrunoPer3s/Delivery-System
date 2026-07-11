package com.ufes.delivery.repository.produto;

import com.ufes.delivery.repository.RepositorioObserver;
import com.ufes.delivery.repository.SuporteObservadores;

import com.ufes.delivery.model.Produto;
import com.ufes.delivery.persistencia.BancoDados;
import com.ufes.delivery.persistencia.PersistenciaException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdutoRepositorySQLite implements IProdutoRepository {

    private final BancoDados banco;
    private final SuporteObservadores observadores = new SuporteObservadores();

    public ProdutoRepositorySQLite(BancoDados banco) {
        this.banco = banco;
        semearSeVazio();
    }

    private void semearSeVazio() {
        if (!listarTodos().isEmpty()) {
            return;
        }
        salvar(new Produto(2001, "Caderno Universitário", "Papelaria", 18.50, 120));
        salvar(new Produto(2002, "Livro de Matemática Básica", "Educação", 45.00, 35));
        salvar(new Produto(2003, "Jogo de Xadrez", "Lazer", 32.90, 18));
        salvar(new Produto(2004, "Quebra-cabeça 500 peças", "Entretenimento", 27.40, 22));
    }

    @Override
    public Optional<Produto> buscarPorCodigo(int codigo) {
        String sql = "SELECT codigo, nome, categoria, preco_unitario, estoque_atual "
                + "FROM produtos WHERE codigo = ?";
        try (Connection c = banco.abrirConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao buscar produto", e);
        }
    }

    @Override
    public List<Produto> buscarPorNome(String nome) {
        String like = "%" + nome.toLowerCase() + "%";
        String sql = "SELECT codigo, nome, categoria, preco_unitario, estoque_atual "
                + "FROM produtos WHERE LOWER(nome) LIKE ?";
        return consultarLista(sql, ps -> ps.setString(1, like), "Falha ao buscar produtos por nome");
    }

    @Override
    public List<Produto> buscarPorCategoria(String categoria) {
        String sql = "SELECT codigo, nome, categoria, preco_unitario, estoque_atual "
                + "FROM produtos WHERE LOWER(categoria) = LOWER(?)";
        return consultarLista(sql, ps -> ps.setString(1, categoria),
                "Falha ao buscar produtos por categoria");
    }

    @Override
    public void salvar(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo");
        }
        String sql = "INSERT INTO produtos (codigo, nome, categoria, preco_unitario, estoque_atual) "
                + "VALUES (?, ?, ?, ?, ?) "
                + "ON CONFLICT(codigo) DO UPDATE SET "
                + "nome = excluded.nome, categoria = excluded.categoria, "
                + "preco_unitario = excluded.preco_unitario, estoque_atual = excluded.estoque_atual";
        try (Connection c = banco.abrirConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, produto.getCodigo());
            ps.setString(2, produto.getNome());
            ps.setString(3, produto.getCategoria());
            ps.setDouble(4, produto.getPrecoUnitario());
            ps.setInt(5, produto.getEstoqueAtual());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao salvar produto", e);
        }
        observadores.notificar();
    }

    @Override
    public List<Produto> listarTodos() {
        String sql = "SELECT codigo, nome, categoria, preco_unitario, estoque_atual FROM produtos";
        return consultarLista(sql, ps -> { }, "Falha ao listar produtos");
    }

    @Override
    public void adicionarObservador(RepositorioObserver observador) {
        observadores.adicionar(observador);
    }

    @Override
    public void removerObservador(RepositorioObserver observador) {
        observadores.remover(observador);
    }

    private List<Produto> consultarLista(String sql, ParametrizadorConsulta parametros, String erro) {
        List<Produto> resultado = new ArrayList<>();
        try (Connection c = banco.abrirConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            parametros.aplicar(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException(erro, e);
        }
        return resultado;
    }

    private Produto mapear(ResultSet rs) throws SQLException {
        return new Produto(
                rs.getInt("codigo"),
                rs.getString("nome"),
                rs.getString("categoria"),
                rs.getDouble("preco_unitario"),
                rs.getInt("estoque_atual"));
    }

    private interface ParametrizadorConsulta {
        void aplicar(PreparedStatement ps) throws SQLException;
    }
}
