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
                "{\"nome_usuario\":\"%s\", \"data\":\"%s\", \"hora\":\"%s\", \"codigo_pedido\":\"%s\", "
                + "\"nome_operacao\":\"%s\", \"nome_cliente\":\"%s\", \"recurso\":\"%s\", "
                + "\"resultado\":\"%s\", \"justificativa\":\"%s\"}",
                escapar(mensagem.getNomeUsuario()),
                escapar(mensagem.getData()),
                escapar(mensagem.getHora()),
                escapar(mensagem.getCodigoPedido()),
                escapar(mensagem.getNomeOperacao()),
                escapar(mensagem.getNomeCliente()),
                escapar(mensagem.getRecurso()),
                escapar(mensagem.getResultado()),
                escapar(mensagem.getJustificativa())
            );
            out.println(json);
        } catch (IOException e) {
            throw new LogIndisponivelException("Falha ao escrever o log JSONL", e);
        }
    }

    private static String escapar(String valor) {
        return valor.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
