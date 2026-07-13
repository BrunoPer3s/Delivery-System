package com.ufes.delivery.model.perfil;

public final class Administrador extends Perfil {

    public static final Administrador INSTANCIA = new Administrador();

    private Administrador() {
        super("Administrador");
    }

    @Override
    public boolean podeAdministrar() {
        return true;
    }
}
