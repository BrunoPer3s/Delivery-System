package com.ufes.delivery.log;

import com.ufes.log.CsvLogger;
import com.ufes.log.ILogger;
import com.ufes.log.JsonlLogger;
import com.ufes.log.XmlLogger;

public final class ConfiguracaoAuditoria {

    public static final String PROPRIEDADE = "delivery.auditoria";
    private static final String MODALIDADE_PADRAO = "jsonl";

    private ConfiguracaoAuditoria() {
    }

    public static ILogger loggerConfigurado() {
        return loggerDaModalidade(System.getProperty(PROPRIEDADE, MODALIDADE_PADRAO));
    }

    public static ILogger loggerDaModalidade(String modalidade) {
        String escolhida = modalidade == null ? MODALIDADE_PADRAO : modalidade.trim().toLowerCase();
        return switch (escolhida) {
            case "jsonl" -> new JsonlLogger();
            case "csv" -> new CsvLogger();
            case "xml" -> new XmlLogger();
            default -> throw new IllegalArgumentException(
                    "Modalidade de auditoria desconhecida: " + modalidade
                            + ". Use jsonl, csv ou xml.");
        };
    }
}
