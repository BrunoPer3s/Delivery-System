package com.ufes.delivery.util;

import java.util.Set;

public final class UfUtil {

    private static final Set<String> UFS_VALIDAS = Set.of(
            "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO",
            "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI",
            "RJ", "RN", "RO", "RR", "RS", "SC", "SP", "SE", "TO");

    private UfUtil() {}

    public static boolean isValida(String uf) {
        if (uf == null) {
            return false;
        }
        return UFS_VALIDAS.contains(uf.trim().toUpperCase());
    }
}

