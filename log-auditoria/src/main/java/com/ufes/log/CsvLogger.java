package com.ufes.log;

import com.ufes.log.model.MensagemLog;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CsvLogger implements ILogger {
    private static final String FILENAME = "log.csv";
    private static final String CABECALHO =
            "NOME_USUARIO,DATA,HORA,CODIGO_PEDIDO,NOME_OPERACAO,NOME_CLIENTE,RECURSO,RESULTADO,JUSTIFICATIVA";

    @Override
    public void registrar(MensagemLog mensagem) {
        boolean arquivoExiste = new File(FILENAME).exists();
        try (FileWriter fw = new FileWriter(FILENAME, true);
             PrintWriter out = new PrintWriter(fw)) {
            if (!arquivoExiste) {
                out.println(CABECALHO);
            }
            out.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
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
        } catch (IOException e) {
            throw new LogIndisponivelException("Falha ao escrever o log CSV", e);
        }
    }

    private static String escapar(String valor) {
        String semQuebras = valor.replace("\r", " ").replace("\n", " ");
        if (semQuebras.contains(",") || semQuebras.contains("\"")) {
            return "\"" + semQuebras.replace("\"", "\"\"") + "\"";
        }
        return semQuebras;
    }
}
