package com.ufes.delivery.model;

import com.ufes.delivery.model.perfil.Perfil;
import com.ufes.delivery.model.situacao.Situacao;

public class Usuario {

    private String nome;
    private String nomeUsuario;
    private String senhaHash;
    private Perfil perfil;
    private Situacao situacao;

    public Usuario(String nome, String nomeUsuario, String senhaHash,
                   Perfil perfil, Situacao situacao) {
        validarNome(nome);
        validarNomeUsuario(nomeUsuario);
        if (senhaHash == null || senhaHash.isBlank()) {
            throw new IllegalArgumentException("Hash da senha é obrigatório");
        }
        if (perfil == null) {
            throw new IllegalArgumentException("Perfil é obrigatório");
        }
        if (situacao == null) {
            throw new IllegalArgumentException("Situação é obrigatória");
        }
        this.nome = nome;
        this.nomeUsuario = nomeUsuario;
        this.senhaHash = senhaHash;
        this.perfil = perfil;
        this.situacao = situacao;
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        String nomeTrimmed = nome.trim();
        if (nomeTrimmed.length() < 2 || nomeTrimmed.length() > 120) {
            throw new IllegalArgumentException("Nome deve conter de 2 a 120 caracteres");
        }
        if (!nomeTrimmed.matches("[\\p{L} '\\-]+")) {
            throw new IllegalArgumentException(
                "Nome deve conter apenas letras, espaços, apóstrofos e hífens");
        }
    }

    private void validarNomeUsuario(String nomeUsuario) {
        if (nomeUsuario == null || nomeUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome de usuário é obrigatório");
        }
        if (nomeUsuario.length() < 3 || nomeUsuario.length() > 30) {
            throw new IllegalArgumentException(
                "Nome de usuário deve conter de 3 a 30 caracteres");
        }
        if (!nomeUsuario.matches("[a-z0-9]+")) {
            throw new IllegalArgumentException(
                "Nome de usuário deve conter apenas letras minúsculas e algarismos, sem espaços");
        }
    }

    public String getNome() { return nome; }
    public String getNomeUsuario() { return nomeUsuario; }
    public String getSenhaHash() { return senhaHash; }
    public Perfil getPerfil() { return perfil; }
    public Situacao getSituacao() { return situacao; }

    public void setPerfil(Perfil perfil) {
        if (perfil == null) throw new IllegalArgumentException("Perfil é obrigatório");
        this.perfil = perfil;
    }

    public void autorizar() {
        this.situacao = situacao.autorizar();
    }

    public void desautorizar() {
        this.situacao = situacao.desautorizar();
    }

    public boolean isAutorizado() {
        return this.situacao.podeIniciarSessao();
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nomeUsuario='" + nomeUsuario + '\'' +
                ", nome='" + nome + '\'' +
                ", perfil=" + perfil.getDescricao() +
                ", situacao=" + situacao.getDescricao() +
                '}';
    }
}

