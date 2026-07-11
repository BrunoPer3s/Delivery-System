package com.ufes.delivery.busca;

import java.util.LinkedHashMap;
import java.util.Map;

public final class CriteriosBuscaProduto {

    private static final Map<String, CriterioBuscaProduto> POR_ROTULO = new LinkedHashMap<>();
    private static final CriterioBuscaProduto PADRAO = new BuscaProdutoPorNome();

    static {
        registrar(new BuscaProdutoPorCodigo());
        registrar(PADRAO);
        registrar(new BuscaProdutoPorCategoria());
    }

    private CriteriosBuscaProduto() {}

    private static void registrar(CriterioBuscaProduto criterio) {
        POR_ROTULO.put(criterio.getRotulo(), criterio);
    }

    public static CriterioBuscaProduto porRotulo(String rotulo) {
        return POR_ROTULO.getOrDefault(rotulo, PADRAO);
    }
}

