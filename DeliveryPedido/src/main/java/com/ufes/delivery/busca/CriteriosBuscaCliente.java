package com.ufes.delivery.busca;

import java.util.LinkedHashMap;
import java.util.Map;

public final class CriteriosBuscaCliente {

    private static final Map<String, CriterioBuscaCliente> POR_ROTULO = new LinkedHashMap<>();
    private static final CriterioBuscaCliente PADRAO = new BuscaClientePorNome();

    static {
        registrar(PADRAO);
        registrar(new BuscaClientePorCpf());
    }

    private CriteriosBuscaCliente() {}

    private static void registrar(CriterioBuscaCliente criterio) {
        POR_ROTULO.put(criterio.getRotulo(), criterio);
    }

    public static CriterioBuscaCliente porRotulo(String rotulo) {
        return POR_ROTULO.getOrDefault(rotulo, PADRAO);
    }
}

