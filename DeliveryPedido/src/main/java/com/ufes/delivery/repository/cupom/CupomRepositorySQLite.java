package com.ufes.delivery.repository.cupom;

import com.ufes.delivery.model.CupomDescontoPedido;
import com.ufes.delivery.persistencia.BancoDados;
import com.ufes.delivery.persistencia.PersistenciaException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

public class CupomRepositorySQLite implements ICupomRepository {

    private final BancoDados banco;

    public CupomRepositorySQLite(BancoDados banco) {
        this.banco = banco;
        semearSeVazio();
    }

    private void semearSeVazio() {
        if (existeAlgum()) {
            return;
        }
        salvar(new CupomDescontoPedido("EDUCAR10", 10.0,
                LocalDateTime.of(2026, 1, 1, 0, 0),
                LocalDateTime.of(2026, 12, 31, 23, 59)));
        salvar(new CupomDescontoPedido("DESC20", 20.0,
                LocalDateTime.of(2026, 1, 1, 0, 0),
                LocalDateTime.of(2026, 12, 31, 23, 59)));
        salvar(new CupomDescontoPedido("NATAL15", 15.0,
                LocalDateTime.of(2026, 12, 1, 0, 0),
                LocalDateTime.of(2026, 12, 31, 23, 59)));
    }

    @Override
    public Optional<CupomDescontoPedido> buscarCupom(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return Optional.empty();
        }
        String sql = "SELECT codigo, percentual, data_inicio, data_fim FROM cupons WHERE codigo = ?";
        try (Connection c = banco.abrirConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao buscar cupom", e);
        }
    }

    private void salvar(CupomDescontoPedido cupom) {
        String sql = "INSERT INTO cupons (codigo, percentual, data_inicio, data_fim) "
                + "VALUES (?, ?, ?, ?) "
                + "ON CONFLICT(codigo) DO UPDATE SET "
                + "percentual = excluded.percentual, data_inicio = excluded.data_inicio, "
                + "data_fim = excluded.data_fim";
        try (Connection c = banco.abrirConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cupom.getCodigo());
            ps.setDouble(2, cupom.getPercentual());
            ps.setString(3, cupom.getDataHoraInicio().toString());
            ps.setString(4, cupom.getDataHoraFim().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao salvar cupom", e);
        }
    }

    private boolean existeAlgum() {
        try (Connection c = banco.abrirConexao();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM cupons LIMIT 1");
             ResultSet rs = ps.executeQuery()) {
            return rs.next();
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao verificar existência de cupons", e);
        }
    }

    private CupomDescontoPedido mapear(ResultSet rs) throws SQLException {
        return new CupomDescontoPedido(
                rs.getString("codigo"),
                rs.getDouble("percentual"),
                LocalDateTime.parse(rs.getString("data_inicio")),
                LocalDateTime.parse(rs.getString("data_fim")));
    }
}
