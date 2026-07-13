package com.ufes.delivery.busca;

import java.util.List;

public interface EstrategiaBusca<T, R> {

    String getRotulo();

    List<T> buscar(String valor, R repositorio);
}
