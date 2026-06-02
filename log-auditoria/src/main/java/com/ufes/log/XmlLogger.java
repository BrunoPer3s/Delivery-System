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
            out.printf("  <nome_usuario>%s</nome_usuario>%n", mensagem.getNomeUsuario());
            out.printf("  <data>%s</data>%n", mensagem.getData());
            out.printf("  <hora>%s</hora>%n", mensagem.getHora());
            out.printf("  <codigo_pedido>%s</codigo_pedido>%n", mensagem.getCodigoPedido());
            out.printf("  <nome_operacao>%s</nome_operacao>%n", mensagem.getNomeOperacao());
            out.printf("  <nome_cliente>%s</nome_cliente>%n", mensagem.getNomeCliente());
            out.println("</registro>");
        } catch (IOException e) {
            System.err.println("Erro ao escrever log XML: " + e.getMessage());
        }
    }
}

