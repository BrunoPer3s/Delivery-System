package com.ufes.log;

import com.ufes.log.model.MensagemLog;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CsvLogger implements ILogger {
    private static final String FILENAME = "log.csv";

    @Override
    public void registrar(MensagemLog mensagem) {
        boolean fileExists = new java.io.File(FILENAME).exists();
        try (FileWriter fw = new FileWriter(FILENAME, true);
             PrintWriter out = new PrintWriter(fw)) {
            if (!fileExists) {
                out.println("NOME_USUARIO,DATA,HORA,CODIGO_PEDIDO,NOME_OPERACAO,NOME_CLIENTE");
            }
            out.printf("%s,%s,%s,%s,%s,%s%n",
                mensagem.getNomeUsuario(),
                mensagem.getData(),
                mensagem.getHora(),
                mensagem.getCodigoPedido(),
                mensagem.getNomeOperacao(),
                mensagem.getNomeCliente()
            );
        } catch (IOException e) {
            System.err.println("Erro ao escrever log CSV: " + e.getMessage());
        }
    }
}

