package com.ufes.delivery.repository.usuario;

import com.ufes.delivery.model.Usuario;
import com.ufes.delivery.model.perfil.Perfis;
import com.ufes.delivery.model.situacao.Situacoes;
import com.ufes.delivery.persistencia.BancoDados;
import com.ufes.delivery.persistencia.PersistenciaException;
import com.ufes.delivery.util.SenhaUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepositorySQLite implements IUsuarioRepository {

    private final BancoDados banco;

    public UsuarioRepositorySQLite(BancoDados banco) {
        this.banco = banco;
        semearSeVazio();
    }

    private void semearSeVazio() {
        if (existeUsuario()) {
            return;
        }
        salvar(new Usuario("Administrador Master", "adminmaster",
                SenhaUtil.hashSenha("Admin123"),
                Perfis.ADMINISTRADOR, Situacoes.AUTORIZADO));
        salvar(new Usuario("Carlos Atendente", "atendente01",
                SenhaUtil.hashSenha("Atende01"),
                Perfis.ATENDENTE, Situacoes.AUTORIZADO));
        salvar(new Usuario("Maria Oliveira", "maria01",
                SenhaUtil.hashSenha("Maria123"),
                Perfis.ATENDENTE, Situacoes.PENDENTE));
        salvar(new Usuario("Joao Silva", "joaosilva",
                SenhaUtil.hashSenha("Joao1234"),
                Perfis.ATENDENTE, Situacoes.NAO_AUTORIZADO));
    }

    @Override
    public void salvar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo");
        }
        String sql = "INSERT INTO usuarios (nome_usuario, nome, senha_hash, perfil, situacao) "
                + "VALUES (?, ?, ?, ?, ?) "
                + "ON CONFLICT(nome_usuario) DO UPDATE SET "
                + "nome = excluded.nome, senha_hash = excluded.senha_hash, "
                + "perfil = excluded.perfil, situacao = excluded.situacao";
        try (Connection c = banco.abrirConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuario.getNomeUsuario());
            ps.setString(2, usuario.getNome());
            ps.setString(3, usuario.getSenhaHash());
            ps.setString(4, usuario.getPerfil().getDescricao());
            ps.setString(5, usuario.getSituacao().getDescricao());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao salvar usuário", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorNomeUsuario(String nomeUsuario) {
        String sql = "SELECT nome_usuario, nome, senha_hash, perfil, situacao "
                + "FROM usuarios WHERE nome_usuario = ?";
        try (Connection c = banco.abrirConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nomeUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao buscar usuário", e);
        }
    }

    @Override
    public List<Usuario> buscarPorNome(String termo) {
        String like = "%" + termo.toLowerCase() + "%";
        String sql = "SELECT nome_usuario, nome, senha_hash, perfil, situacao "
                + "FROM usuarios WHERE LOWER(nome) LIKE ? OR LOWER(nome_usuario) LIKE ?";
        List<Usuario> resultado = new ArrayList<>();
        try (Connection c = banco.abrirConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao buscar usuários", e);
        }
        return resultado;
    }

    @Override
    public void remover(String nomeUsuario) {
        String sql = "DELETE FROM usuarios WHERE nome_usuario = ?";
        try (Connection c = banco.abrirConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nomeUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao remover usuário", e);
        }
    }

    @Override
    public boolean existeUsuario() {
        String sql = "SELECT 1 FROM usuarios LIMIT 1";
        try (Connection c = banco.abrirConexao();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next();
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao verificar existência de usuários", e);
        }
    }

    @Override
    public List<Usuario> listarTodos() {
        String sql = "SELECT nome_usuario, nome, senha_hash, perfil, situacao FROM usuarios";
        List<Usuario> resultado = new ArrayList<>();
        try (Connection c = banco.abrirConexao();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao listar usuários", e);
        }
        return resultado;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getString("nome"),
                rs.getString("nome_usuario"),
                rs.getString("senha_hash"),
                Perfis.porDescricao(rs.getString("perfil")),
                Situacoes.porDescricao(rs.getString("situacao")));
    }
}
