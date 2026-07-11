package com.ufes.delivery.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class BancoDados {

    private static final String ARQUIVO_PADRAO = "delivery.db";

    private final String url;

    public BancoDados() {
        this(ARQUIVO_PADRAO);
    }

    public BancoDados(String caminhoArquivo) {
        this.url = "jdbc:sqlite:" + caminhoArquivo;
    }

    public Connection abrirConexao() {
        try {
            Connection conexao = DriverManager.getConnection(url);
            try (Statement stmt = conexao.createStatement()) {
                stmt.execute("PRAGMA busy_timeout = 5000");
                stmt.execute("PRAGMA foreign_keys = ON");
            }
            return conexao;
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao abrir conexao com o banco de dados", e);
        }
    }

    public void executarEmTransacao(TransacaoBanco transacao) {
        try (Connection conexao = abrirConexao()) {
            conexao.setAutoCommit(false);
            try {
                transacao.executar(conexao);
                conexao.commit();
            } catch (SQLException e) {
                conexao.rollback();
                throw e;
            } finally {
                conexao.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao executar transacao", e);
        }
    }

    public void inicializar() {
        try (Connection conexao = abrirConexao();
             Statement stmt = conexao.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS usuarios (
                        nome_usuario TEXT PRIMARY KEY,
                        nome TEXT NOT NULL,
                        senha_hash TEXT NOT NULL,
                        perfil TEXT NOT NULL,
                        situacao TEXT NOT NULL
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS clientes (
                        cpf TEXT PRIMARY KEY,
                        nome TEXT NOT NULL,
                        tipo TEXT NOT NULL,
                        fidelidade REAL NOT NULL
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS enderecos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        cliente_cpf TEXT NOT NULL,
                        logradouro TEXT,
                        numero TEXT,
                        complemento TEXT,
                        bairro TEXT,
                        cidade TEXT,
                        uf TEXT,
                        cep TEXT,
                        padrao INTEGER NOT NULL,
                        ordem INTEGER NOT NULL,
                        FOREIGN KEY (cliente_cpf) REFERENCES clientes(cpf) ON DELETE CASCADE
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS produtos (
                        codigo INTEGER PRIMARY KEY,
                        nome TEXT NOT NULL,
                        categoria TEXT NOT NULL,
                        preco_unitario REAL NOT NULL,
                        estoque_atual INTEGER NOT NULL
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS cupons (
                        codigo TEXT PRIMARY KEY,
                        percentual REAL NOT NULL,
                        data_inicio TEXT NOT NULL,
                        data_fim TEXT NOT NULL
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS pedidos (
                        codigo INTEGER PRIMARY KEY,
                        nome_cliente TEXT,
                        data_pedido TEXT,
                        data_conclusao TEXT,
                        estado TEXT NOT NULL,
                        valor_total TEXT
                    )
                    """);
        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao inicializar o esquema do banco de dados", e);
        }
    }
}
