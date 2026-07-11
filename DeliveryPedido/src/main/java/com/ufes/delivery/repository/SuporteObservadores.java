package com.ufes.delivery.repository;

import java.util.ArrayList;
import java.util.List;

public class SuporteObservadores {

    private final List<RepositorioObserver> observadores = new ArrayList<>();

    public void adicionar(RepositorioObserver observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void remover(RepositorioObserver observador) {
        observadores.remove(observador);
    }

    public void notificar() {
        for (RepositorioObserver observador : observadores) {
            observador.onDadosAlterados();
        }
    }
}
