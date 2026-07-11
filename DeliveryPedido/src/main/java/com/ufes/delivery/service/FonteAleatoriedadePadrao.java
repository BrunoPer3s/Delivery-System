package com.ufes.delivery.service;

import java.util.Random;

public class FonteAleatoriedadePadrao implements IFonteAleatoriedade {

    private final Random random = new Random();

    @Override
    public boolean sortear(double probabilidade) {
        return random.nextDouble() < probabilidade;
    }

    @Override
    public int sortearIndice(int limite) {
        return random.nextInt(limite);
    }

    @Override
    public int sortearInteiro(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}

