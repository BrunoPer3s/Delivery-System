package com.ufes.delivery.apoio;

import com.ufes.delivery.service.IFonteAleatoriedade;

public class FonteAleatoriedadeFixa implements IFonteAleatoriedade {

    private final boolean aprovado;
    private final int indiceFormaPagamento;
    private final int inteiroSorteado;

    public FonteAleatoriedadeFixa(boolean aprovado, int indiceFormaPagamento) {
        this(aprovado, indiceFormaPagamento, 60);
    }

    public FonteAleatoriedadeFixa(boolean aprovado, int indiceFormaPagamento, int inteiroSorteado) {
        this.aprovado = aprovado;
        this.indiceFormaPagamento = indiceFormaPagamento;
        this.inteiroSorteado = inteiroSorteado;
    }

    @Override
    public boolean sortear(double probabilidade) {
        return aprovado;
    }

    @Override
    public int sortearIndice(int limite) {
        return indiceFormaPagamento;
    }

    @Override
    public int sortearInteiro(int min, int max) {
        return Math.max(min, Math.min(max, inteiroSorteado));
    }
}
