package com.ufes.delivery.presenter.cliente;

import com.ufes.delivery.busca.BuscaInvalidaException;
import com.ufes.delivery.busca.CriterioBuscaCliente;
import com.ufes.delivery.busca.CriteriosBuscaCliente;
import com.ufes.delivery.log.GerenciadorDeLogAtivo;
import com.ufes.delivery.model.Cliente;
import com.ufes.delivery.repository.cliente.IClienteRepository;
import com.ufes.delivery.repository.RepositorioObserver;
import com.ufes.delivery.service.SessaoService;
import com.ufes.delivery.util.CpfUtil;
import com.ufes.delivery.view.cliente.CadastroClienteView;
import com.ufes.delivery.view.cliente.IBuscaClienteView;

import java.util.ArrayList;
import java.util.List;

public class BuscaClientePresenter implements RepositorioObserver {

    private final IBuscaClienteView view;
    private final IClienteRepository clienteRepository;
    private final GerenciadorDeLogAtivo logger;
    private final SessaoService sessaoService;

    public BuscaClientePresenter(IBuscaClienteView view,
                                  IClienteRepository clienteRepository,
                                  GerenciadorDeLogAtivo logger,
                                  SessaoService sessaoService) {
        this.view = view;
        this.clienteRepository = clienteRepository;
        this.logger = logger;
        this.sessaoService = sessaoService;

        this.clienteRepository.adicionarObservador(this);

        carregarTodos();
    }

    @Override
    public void onDadosAlterados() {
        carregarTodos();
    }

    public void aoFecharJanela() {
        clienteRepository.removerObservador(this);
    }

    public void onBuscar() {
        CriterioBuscaCliente criterio = CriteriosBuscaCliente.porRotulo(view.getTipoBusca());

        try {
            List<Cliente> resultados = criterio.buscar(view.getValorBusca(), clienteRepository);
            view.carregarResultados(converterParaDados(resultados));
            if (resultados.isEmpty()) {
                view.exibirMensagemInfo("Nenhum cliente encontrado para o critério informado.");
            }
        } catch (BuscaInvalidaException e) {
            view.exibirMensagemErro(e.getMessage());
        }
    }

    public void onNovo() {
        CadastroClienteView cadastroView = new CadastroClienteView();
        CadastroClientePresenter cadastroPresenter = new CadastroClientePresenter(
                cadastroView, clienteRepository, logger, sessaoService, null);
        cadastroView.setPresenter(cadastroPresenter);
        cadastroView.exibir();
    }

    public void onVisualizar() {
        int linha = view.getLinhaSelecionada();
        if (linha < 0) {
            view.exibirMensagemErro("Selecione um cliente na tabela.");
            return;
        }

        String cpf = view.getCpfNaLinha(linha);
        String cpfLimpo = CpfUtil.removerMascara(cpf);

        clienteRepository.buscarPorCpf(cpfLimpo).ifPresentOrElse(
            cliente -> {
                CadastroClienteView cadastroView = new CadastroClienteView();
                CadastroClientePresenter cadastroPresenter = new CadastroClientePresenter(
                        cadastroView, clienteRepository, logger, sessaoService, cliente);
                cadastroView.setPresenter(cadastroPresenter);
                cadastroView.exibir();
            },
            () -> view.exibirMensagemErro("Cliente não encontrado.")
        );
    }

    public void onFechar() {
        view.fechar();
    }

    private void carregarTodos() {
        List<Cliente> todos = clienteRepository.listarTodos();
        view.carregarResultados(converterParaDados(todos));
    }

    private List<String[]> converterParaDados(List<Cliente> clientes) {
        List<String[]> dados = new ArrayList<>();
        for (Cliente c : clientes) {
            dados.add(new String[]{c.getNome(), c.getCpfFormatado()});
        }
        return dados;
    }
}

