package com.ufes.delivery.model.perfil;

public final class Atendente implements Perfil {

    public static final Atendente INSTANCIA = new Atendente();

    private Atendente() {}

    @Override
    public String getDescricao() {
        return "Atendente";
    }

    @Override
    public boolean podeAdministrar() {
        return false;
    }
}
