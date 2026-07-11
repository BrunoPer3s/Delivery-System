package com.ufes.delivery.repository.cliente;

import com.ufes.delivery.repository.RepositorioObserver;
import com.ufes.delivery.repository.SuporteObservadores;

import com.ufes.delivery.model.Cliente;
import com.ufes.delivery.model.Endereco;
import com.ufes.delivery.persistencia.BancoDados;
import com.ufes.delivery.persistencia.PersistenciaException;
import com.ufes.delivery.util.CpfUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepositorySQLite implements IClienteRepository {

    private final BancoDados banco;
    private final SuporteObservadores observadores = new SuporteObservadores();

    public ClienteRepositorySQLite(BancoDados banco) {
        this.banco = banco;
        semearSeVazio();
    }

    private void semearSeVazio() {
        if (!listarTodos().isEmpty()) {
            return;
        }
        Cliente c1 = new Cliente("Fulano de Tal", "52998224725");
        c1.adicionarEndereco(new Endereco("Rua Fulano", "123", "Apto 101",
                "Centro", "Cidade Exemplo", "ES", "29000000", true));
        c1.adicionarEndereco(new Endereco("Avenida Sicrano", "456", "Casa",
                "Jardim Modelo", "Cidade Exemplo", "ES", "29010000", false));
        c1.adicionarEndereco(new Endereco("Travessa Beltrano", "789", "Fundos",
                "Bairro Genérico", "Cidade Exemplo", "ES", "29020000", false));
        salvar(c1);

        Cliente c2 = new Cliente("Fulano da Silva", "45783291609");
        c2.adicionarEndereco(new Endereco("Rua da Silva", "100", "",
                "Praia", "Vila Velha", "ES", "29100000", true));
        salvar(c2);
    }

    @Override
    public void salvar(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }
        try (Connection c = banco.abrirConexao()) {
            c.setAutoCommit(false);
            try {
                gravarCliente(c, cliente);
                removerEnderecos(c, cliente.getCpf());
                gravarEnderecos(c, cliente);
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao salvar cliente", e);
        }
        observadores.notificar();
    }

    @Override
    public Optional<Cliente> buscarPorCpf(String cpf) {
        String cpfLimpo = CpfUtil.removerMascara(cpf);
        String sql = "SELECT cpf, nome, tipo, fidelidade FROM clientes WHERE cpf = ?";
        List<Cliente> encontrados = consultarLista(sql, ps -> ps.setString(1, cpfLimpo),
                "Falha ao buscar cliente por CPF");
        return encontrados.isEmpty() ? Optional.empty() : Optional.of(encontrados.get(0));
    }

    @Override
    public List<Cliente> buscarPorNome(String nome) {
        String like = "%" + nome.toLowerCase() + "%";
        String sql = "SELECT cpf, nome, tipo, fidelidade FROM clientes WHERE LOWER(nome) LIKE ?";
        return consultarLista(sql, ps -> ps.setString(1, like), "Falha ao buscar clientes por nome");
    }

    @Override
    public List<Cliente> listarTodos() {
        String sql = "SELECT cpf, nome, tipo, fidelidade FROM clientes";
        return consultarLista(sql, ps -> { }, "Falha ao listar clientes");
    }

    @Override
    public void adicionarObservador(RepositorioObserver observador) {
        observadores.adicionar(observador);
    }

    @Override
    public void removerObservador(RepositorioObserver observador) {
        observadores.remover(observador);
    }

    private void gravarCliente(Connection c, Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (cpf, nome, tipo, fidelidade) VALUES (?, ?, ?, ?) "
                + "ON CONFLICT(cpf) DO UPDATE SET "
                + "nome = excluded.nome, tipo = excluded.tipo, fidelidade = excluded.fidelidade";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cliente.getCpf());
            ps.setString(2, cliente.getNome());
            ps.setString(3, cliente.getTipo());
            ps.setDouble(4, cliente.getFidelidade());
            ps.executeUpdate();
        }
    }

    private void removerEnderecos(Connection c, String cpf) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM enderecos WHERE cliente_cpf = ?")) {
            ps.setString(1, cpf);
            ps.executeUpdate();
        }
    }

    private void gravarEnderecos(Connection c, Cliente cliente) throws SQLException {
        String sql = "INSERT INTO enderecos (cliente_cpf, logradouro, numero, complemento, "
                + "bairro, cidade, uf, cep, padrao, ordem) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        List<Endereco> enderecos = cliente.getEnderecos();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (int ordem = 0; ordem < enderecos.size(); ordem++) {
                Endereco e = enderecos.get(ordem);
                ps.setString(1, cliente.getCpf());
                ps.setString(2, e.getLogradouro());
                ps.setString(3, e.getNumero());
                ps.setString(4, e.getComplemento());
                ps.setString(5, e.getBairro());
                ps.setString(6, e.getCidade());
                ps.setString(7, e.getUf());
                ps.setString(8, e.getCep());
                ps.setInt(9, e.isPadrao() ? 1 : 0);
                ps.setInt(10, ordem);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private List<Cliente> consultarLista(String sql, ParametrizadorConsulta parametros, String erro) {
        List<Cliente> resultado = new ArrayList<>();
        try (Connection c = banco.abrirConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            parametros.aplicar(ps);
            List<DadosCliente> base = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    base.add(new DadosCliente(
                            rs.getString("cpf"),
                            rs.getString("nome"),
                            rs.getDouble("fidelidade")));
                }
            }
            for (DadosCliente dados : base) {
                Cliente cliente = new Cliente(dados.nome(), dados.cpf());
                cliente.setFidelidade(dados.fidelidade());
                cliente.setEnderecos(carregarEnderecos(c, dados.cpf()));
                resultado.add(cliente);
            }
        } catch (SQLException e) {
            throw new PersistenciaException(erro, e);
        }
        return resultado;
    }

    private record DadosCliente(String cpf, String nome, double fidelidade) {
    }

    private List<Endereco> carregarEnderecos(Connection c, String cpf) throws SQLException {
        String sql = "SELECT logradouro, numero, complemento, bairro, cidade, uf, cep, padrao "
                + "FROM enderecos WHERE cliente_cpf = ? ORDER BY ordem";
        List<Endereco> enderecos = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cpf);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    enderecos.add(new Endereco(
                            rs.getString("logradouro"),
                            rs.getString("numero"),
                            rs.getString("complemento"),
                            rs.getString("bairro"),
                            rs.getString("cidade"),
                            rs.getString("uf"),
                            rs.getString("cep"),
                            rs.getInt("padrao") == 1));
                }
            }
        }
        return enderecos;
    }

    private interface ParametrizadorConsulta {
        void aplicar(PreparedStatement ps) throws SQLException;
    }
}
