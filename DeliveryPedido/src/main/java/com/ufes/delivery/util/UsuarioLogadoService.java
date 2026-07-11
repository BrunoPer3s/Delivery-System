package com.ufes.delivery.util;

import com.ufes.delivery.service.SessaoService;

public class UsuarioLogadoService {

    private UsuarioLogadoService() {
    }

    public static String getNomeUsuario() {
        String usuario = SessaoService.getInstancia().getNomeUsuarioLogado();
        return usuario != null ? usuario : "sistema";
    }
}

