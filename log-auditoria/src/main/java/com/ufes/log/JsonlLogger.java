package com.ufes.log;

import com.ufes.log.model.MensagemLog;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class JsonlLogger implements ILogger {
    private static final String FILENAME = "log.jsonl";

    @Override
    public void registrar(MensagemLog mensagem) {
        try (FileWriter fw = new FileWriter(FILENAME, true);
             PrintWriter out = new PrintWriter(fw)) {
            String json = String.format(
                "{\"nome_usuario\":\"%s\", \"data\":\"%s\", \"hora\":\"%s\", \"codigo_pedido\":\"%s\", \"nome_operacao\":\"%s\", \"nome_cliente\":\"%s\"}",
                mensagem.getNomeUsuario(),
                mensagem.getData(),
                mensagem.getHora(),
                mensagem.getCodigoPedido(),
                mensagem.getNomeOperacao(),
                mensagem.getNomeCliente()
            );
            out.println(json);
        } catch (IOException e) {
            System.err.println("Erro ao escrever log JSONL: " + e.getMessage());
        }
    }
}

