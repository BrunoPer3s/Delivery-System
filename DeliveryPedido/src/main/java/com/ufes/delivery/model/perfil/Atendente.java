package com.ufes.delivery.model.perfil;

public final class Atendente extends Perfil {

    public static final Atendente INSTANCIA = new Atendente();

    private Atendente() {
        super("Atendente");
    }

    @Override
    public boolean podeAdministrar() {
        return false;
    }
}
