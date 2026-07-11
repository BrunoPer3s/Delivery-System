package com.ufes.delivery.model;

import com.ufes.delivery.util.CpfUtil;

import java.util.ArrayList;
import java.util.List;

public class Cliente {

    private String nome;
    private String cpf;
    private final List<Endereco> enderecos;

    private String tipo;
    private double fidelidade;

    public Cliente(String nome, String cpf) {
        validarNome(nome);
        validarCpf(cpf);
        this.nome = nome.trim();
        this.cpf = CpfUtil.removerMascara(cpf);
        this.enderecos = new ArrayList<>();
        this.tipo = "Normal";
        this.fidelidade = 0.0;
    }

    public Cliente(String nome, String tipo, double fidelidade,
                   String logradouro, String bairro, String cidade) {
        validarTextoObrigatorio(nome, "Nome do cliente nao pode ser vazio");
        validarTextoObrigatorio(tipo, "Tipo do cliente nao pode ser vazio");
        validarTextoObrigatorio(logradouro, "Logradouro do cliente nao pode ser vazio");
        validarTextoObrigatorio(bairro, "Bairro do cliente nao pode ser vazio");
        validarTextoObrigatorio(cidade, "Cidade do cliente nao pode ser vazia");
        if (fidelidade < 0) {
            throw new IllegalArgumentException("Fidelidade do cliente nao pode ser negativa");
        }
        this.nome = nome;
        this.tipo = tipo;
        this.fidelidade = fidelidade;
        this.cpf = "";
        this.enderecos = new ArrayList<>();
        this.enderecos.add(new Endereco(logradouro, "", "", bairro, cidade, "", "", true));
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

    private void validarCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF é obrigatório");
        }
        if (!CpfUtil.validar(cpf)) {
            throw new IllegalArgumentException("CPF inválido");
        }
    }

    private void validarTextoObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }
    }


    public void adicionarEndereco(Endereco endereco) {
        if (enderecos.size() >= 3) {
            throw new IllegalStateException("Máximo de 3 endereços por cliente");
        }
        if (endereco.isPadrao()) {
            enderecos.forEach(e -> e.setPadrao(false));
        }
        enderecos.add(endereco);
    }

    public void setEnderecos(List<Endereco> novosEnderecos) {
        this.enderecos.clear();
        if (novosEnderecos != null) {
            for (Endereco e : novosEnderecos) {
                this.enderecos.add(e);
            }
        }
    }

    public Endereco getEnderecoPadrao() {
        return enderecos.stream()
                .filter(Endereco::isPadrao)
                .findFirst()
                .orElse(enderecos.isEmpty() ? null : enderecos.get(0));
    }

    public List<Endereco> getEnderecos() {
        return new ArrayList<>(enderecos);
    }

    public List<Endereco> getEnderecosPreenchidos() {
        return enderecos.stream()
                .filter(Endereco::isPreenchido)
                .toList();
    }


    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getCpfFormatado() { return CpfUtil.formatar(cpf); }

    public void setNome(String nome) {
        validarNome(nome);
        this.nome = nome.trim();
    }

    public String getTipo() { return tipo; }
    public double getFidelidade() { return fidelidade; }

    public String getLogradouro() {
        Endereco padrao = getEnderecoPadrao();
        return padrao != null ? padrao.getLogradouro() : "";
    }

    public String getBairro() {
        Endereco padrao = getEnderecoPadrao();
        return padrao != null ? padrao.getBairro() : "";
    }

    public String getCidade() {
        Endereco padrao = getEnderecoPadrao();
        return padrao != null ? padrao.getCidade() : "";
    }

    public void setFidelidade(double fidelidade) {
        if (fidelidade < 0) {
            throw new IllegalArgumentException("Fidelidade do cliente nao pode ser negativa");
        }
        this.fidelidade = fidelidade;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "nome='" + nome + '\'' +
                ", cpf='" + getCpfFormatado() + '\'' +
                ", enderecos=" + enderecos.size() +
                '}';
    }
}

