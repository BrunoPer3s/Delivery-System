package com.ufes.delivery.persistencia;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface TransacaoBanco {

    void executar(Connection conexao) throws SQLException;
}
