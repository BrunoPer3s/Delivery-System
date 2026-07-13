package com.ufes.delivery.busca;

import java.util.List;

public abstract class EstrategiaBusca<T> {
    public abstract List<T> buscar(String valor, Object repositorio) throws RuntimeException;
}