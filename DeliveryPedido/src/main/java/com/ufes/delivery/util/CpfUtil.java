package com.ufes.delivery.util;

public class CpfUtil {

    private CpfUtil() {
    }

    public static boolean validar(String cpf) {
        if (cpf == null) return false;

        String somenteDigitos = cpf.replaceAll("\\D", "");

        if (somenteDigitos.length() != 11) return false;

        if (somenteDigitos.chars().distinct().count() == 1) return false;

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(somenteDigitos.charAt(i)) * (10 - i);
        }
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito >= 10) primeiroDigito = 0;
        if (primeiroDigito != Character.getNumericValue(somenteDigitos.charAt(9))) {
            return false;
        }

        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(somenteDigitos.charAt(i)) * (11 - i);
        }
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito >= 10) segundoDigito = 0;
        return segundoDigito == Character.getNumericValue(somenteDigitos.charAt(10));
    }

    public static String formatar(String cpf) {
        if (cpf == null) return "";
        String somenteDigitos = cpf.replaceAll("\\D", "");
        if (somenteDigitos.length() != 11) return cpf;
        return somenteDigitos.substring(0, 3) + "." +
               somenteDigitos.substring(3, 6) + "." +
               somenteDigitos.substring(6, 9) + "-" +
               somenteDigitos.substring(9);
    }

    public static String removerMascara(String cpf) {
        if (cpf == null) return "";
        return cpf.replaceAll("\\D", "");
    }
}

