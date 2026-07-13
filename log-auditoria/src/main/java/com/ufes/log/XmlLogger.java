package com.ufes.log;

import com.ufes.log.model.MensagemLog;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class XmlLogger implements ILogger {
    private static final String FILENAME = "log.xml";

    @Override
    public void registrar(MensagemLog mensagem) {
        try (FileWriter fw = new FileWriter(FILENAME, true);
             PrintWriter out = new PrintWriter(fw)) {
            out.println("<registro>");
            out.printf("  <nome_usuario>%s</nome_usuario>%n", escapar(mensagem.getNomeUsuario()));
            out.printf("  <data>%s</data>%n", escapar(mensagem.getData()));
            out.printf("  <hora>%s</hora>%n", escapar(mensagem.getHora()));
            out.printf("  <codigo_pedido>%s</codigo_pedido>%n", escapar(mensagem.getCodigoPedido()));
            out.printf("  <nome_operacao>%s</nome_operacao>%n", escapar(mensagem.getNomeOperacao()));
            out.printf("  <nome_cliente>%s</nome_cliente>%n", escapar(mensagem.getNomeCliente()));
            out.printf("  <recurso>%s</recurso>%n", escapar(mensagem.getRecurso()));
            out.printf("  <resultado>%s</resultado>%n", escapar(mensagem.getResultado()));
            out.printf("  <justificativa>%s</justificativa>%n", escapar(mensagem.getJustificativa()));
            out.println("</registro>");
        } catch (IOException e) {
            throw new LogIndisponivelException("Falha ao escrever o log XML", e);
        }
    }

    private static String escapar(String valor) {
        return valor.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
