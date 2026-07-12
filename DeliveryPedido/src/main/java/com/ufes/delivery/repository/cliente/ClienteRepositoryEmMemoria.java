package com.ufes.delivery.repository.cliente;

import com.ufes.delivery.model.Cliente;
import com.ufes.delivery.model.Endereco;
import com.ufes.delivery.repository.RepositorioObserver;
import com.ufes.delivery.util.CpfUtil;

import java.util.*;

public class ClienteRepositoryEmMemoria implements IClienteRepository {

    private final Map<String, Cliente> clientes = new LinkedHashMap<>();
    private final List<RepositorioObserver> observadores = new ArrayList<>();

    public ClienteRepositoryEmMemoria() {
        carregarClientesDeTeste();
    }

    private void carregarClientesDeTeste() {
        Cliente c1 = new Cliente("Fulano de Tal", "52998224725");
        c1.adicionarEndereco(new Endereco("Rua Fulano", "123", "Apto 101",
                "Centro", "Cidade Exemplo", "ES", "29000000", true));
        c1.adicionarEndereco(new Endereco("Avenida Sicrano", "456", "Casa",
                "Jardim Modelo", "Cidade Exemplo", "ES", "29010000", false));
        c1.adicionarEndereco(new Endereco("Travessa Beltrano", "789", "Fundos",
                "Bairro Genérico", "Cidade Exemplo", "ES", "29020000", false));
        salvar(c1);

        Cliente c2 = new Cliente("Fulano da Silva", "45783291609");
        c2.adicionarEndereco(new Endereco("Rua da Silva", "100", "",
                "Praia", "Vila Velha", "ES", "29100000", true));
        salvar(c2);
    }

    @Override
    public Optional<Cliente> buscarPorCpf(String cpf) {
        String cpfLimpo = CpfUtil.removerMascara(cpf);
        return Optional.ofNullable(clientes.get(cpfLimpo));
    }

    @Override
    public List<Cliente> buscarPorNome(String nome) {
        String nomeLower = nome.toLowerCase();
        List<Cliente> resultado = new ArrayList<>();
        for (Cliente c : clientes.values()) {
            if (c.getNome().toLowerCase().contains(nomeLower)) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    @Override
    public void salvar(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }
        clientes.put(cliente.getCpf(), cliente);
        notificarObservadores();
    }

    @Override
    public List<Cliente> listarTodos() {
        return new ArrayList<>(clientes.values());
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

