package com.ufes.delivery.repository;

import com.ufes.delivery.model.Produto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProdutoRepositoryEmMemoria implements IProdutoRepository {

    private final Map<Integer, Produto> produtos = new LinkedHashMap<>();
    private final List<RepositorioObserver> observadores = new ArrayList<>();

    public ProdutoRepositoryEmMemoria() {
        carregarProdutosDeTeste();
    }

    private void carregarProdutosDeTeste() {
        salvar(new Produto(2001, "Caderno Universitário", "Papelaria", 18.50, 120));
        salvar(new Produto(2002, "Livro de Matemática Básica", "Educação", 45.00, 35));
        salvar(new Produto(2003, "Jogo de Xadrez", "Lazer", 32.90, 18));
        salvar(new Produto(2004, "Quebra-cabeça 500 peças", "Entretenimento", 27.40, 22));
    }

    @Override
    public Optional<Produto> buscarPorCodigo(int codigo) {
        return Optional.ofNullable(produtos.get(codigo));
    }

    @Override
    public List<Produto> buscarPorNome(String nome) {
        String nomeLower = nome.toLowerCase();
        List<Produto> resultado = new ArrayList<>();
        for (Produto p : produtos.values()) {
            if (p.getNome().toLowerCase().contains(nomeLower)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    @Override
    public List<Produto> buscarPorCategoria(String categoria) {
        List<Produto> resultado = new ArrayList<>();
        for (Produto p : produtos.values()) {
            if (p.getCategoria().equalsIgnoreCase(categoria)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    @Override
    public void salvar(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo");
        }
        produtos.put(produto.getCodigo(), produto);
        notificarObservadores();
    }

    @Override
    public List<Produto> listarTodos() {
        return new ArrayList<>(produtos.values());
    }


    @Override
    public void adicionarObservador(RepositorioObserver observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    @Override
    public void removerObservador(RepositorioObserver observador) {
        observadores.remove(observador);
    }

    private void notificarObservadores() {
        for (RepositorioObserver observador : observadores) {
            observador.onDadosAlterados();
        }
    }
}

