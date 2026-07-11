package com.ufes.delivery.service;

public interface IFonteAleatoriedade {

    boolean sortear(double probabilidade);

    int sortearIndice(int limite);

    int sortearInteiro(int min, int max);
}

