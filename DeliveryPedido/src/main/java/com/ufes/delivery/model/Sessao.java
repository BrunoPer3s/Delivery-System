package com.ufes.delivery.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Sessao {

    private final Usuario usuario;
    private final LocalDateTime dataHoraLogin;

    private static final DateTimeFormatter FORMATTER_DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public Sessao(Usuario usuario, LocalDateTime dataHoraLogin) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário é obrigatório para iniciar sessão");
        }
        if (dataHoraLogin == null) {
            throw new IllegalArgumentException("Data/hora do login é obrigatória");
        }
        this.usuario = usuario;
        this.dataHoraLogin = dataHoraLogin;
    }

    public Usuario getUsuario() { return usuario; }
    public LocalDateTime getDataHoraLogin() { return dataHoraLogin; }

    public String getNomeUsuario() {
        return usuario.getNomeUsuario();
    }

    public String getPerfilDescricao() {
        return usuario.getPerfil().getDescricao();
    }

    public String getLoginFormatado() {
        return dataHoraLogin.format(FORMATTER_DATA_HORA);
    }

    @Override
    public String toString() {
        return "Sessao{" +
                "usuario=" + usuario.getNomeUsuario() +
                ", login=" + getLoginFormatado() +
                ", tipo=" + getPerfilDescricao() +
                '}';
    }
}

