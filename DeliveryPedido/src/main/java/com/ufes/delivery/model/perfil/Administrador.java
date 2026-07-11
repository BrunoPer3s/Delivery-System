package com.ufes.delivery.model.perfil;

public final class Administrador implements Perfil {

    public static final Administrador INSTANCIA = new Administrador();

    private Administrador() {}

    @Override
    public String getDescricao() {
        return "Administrador";
    }

    @Override
    public boolean podeAdministrar() {
        return true;
    }
}
