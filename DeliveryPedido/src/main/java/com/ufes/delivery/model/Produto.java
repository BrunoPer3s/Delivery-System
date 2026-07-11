package com.ufes.delivery.model;

import java.text.NumberFormat;
import java.util.Locale;

public class Produto {

    public static final String[] CATEGORIAS = {
        "Papelaria", "Educação", "Lazer", "Entretenimento"
    };

    private int codigo;
    private String nome;
    private String categoria;
    private double precoUnitario;
    private int estoqueAtual;

    private static final NumberFormat FORMATO_MOEDA =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public Produto(int codigo, String nome, String categoria,
                   double precoUnitario, int estoqueInicial) {
        validarCodigo(codigo);
        validarNome(nome);
        validarCategoria(categoria);
        validarPreco(precoUnitario);
        if (estoqueInicial < 0) {
            throw new IllegalArgumentException(
                "Quantidade inicial em estoque não pode ser negativa");
        }

        this.codigo = codigo;
        this.nome = nome.trim();
        this.categoria = categoria;
        this.precoUnitario = precoUnitario;
        this.estoqueAtual = estoqueInicial;
    }


    private void validarCodigo(int codigo) {
        if (codigo <= 0) {
            throw new IllegalArgumentException("Código deve ser um inteiro positivo");
        }
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        String nomeTrimmed = nome.trim();
        if (nomeTrimmed.length() < 2 || nomeTrimmed.length() > 120) {
            throw new IllegalArgumentException("Nome deve conter de 2 a 120 caracteres");
        }
    }

    private void validarCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("Categoria é obrigatória");
        }
        boolean valida = false;
        for (String cat : CATEGORIAS) {
            if (cat.equals(categoria)) {
                valida = true;
                break;
            }
        }
        if (!valida) {
            throw new IllegalArgumentException("Categoria inválida: " + categoria);
        }
    }

    private void validarPreco(double preco) {
        if (preco <= 0) {
            throw new IllegalArgumentException("Preço unitário deve ser maior que R$ 0,00");
        }
    }


    public void ajustarEstoque(int quantidade) {
        int novoEstoque = this.estoqueAtual + quantidade;
        if (novoEstoque < 0) {
            throw new IllegalStateException(
                "Estoque não pode ficar negativo. Atual: " + estoqueAtual +
                ", ajuste: " + quantidade);
        }
        this.estoqueAtual = novoEstoque;
    }


    public int getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
    public double getPrecoUnitario() { return precoUnitario; }
    public int getEstoqueAtual() { return estoqueAtual; }

    public String getPrecoFormatado() {
        return FORMATO_MOEDA.format(precoUnitario);
    }


    public void setNome(String nome) {
        validarNome(nome);
        this.nome = nome.trim();
    }

    public void setCategoria(String categoria) {
        validarCategoria(categoria);
        this.categoria = categoria;
    }

    public void setPrecoUnitario(double precoUnitario) {
        validarPreco(precoUnitario);
        this.precoUnitario = precoUnitario;
    }

    @Override
    public String toString() {
        return "Produto{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", categoria='" + categoria + '\'' +
                ", preco=" + getPrecoFormatado() +
                ", estoque=" + estoqueAtual +
                '}';
    }
}

