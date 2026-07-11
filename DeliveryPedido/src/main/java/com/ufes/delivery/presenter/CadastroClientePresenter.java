package com.ufes.delivery.presenter;

import com.ufes.delivery.log.GerenciadorDeLogAtivo;
import com.ufes.delivery.log.MensagemLogFactory;
import com.ufes.delivery.model.Cliente;
import com.ufes.delivery.model.Endereco;
import com.ufes.delivery.repository.IClienteRepository;
import com.ufes.delivery.service.SessaoService;
import com.ufes.delivery.util.CpfUtil;
import com.ufes.delivery.util.UfUtil;
import com.ufes.delivery.view.ICadastroClienteView;

import java.util.ArrayList;
import java.util.List;

public class CadastroClientePresenter {

    private final ICadastroClienteView view;
    private final IClienteRepository clienteRepository;
    private final GerenciadorDeLogAtivo logger;
    private final SessaoService sessaoService;
    private final Cliente clienteEdicao;

    public CadastroClientePresenter(ICadastroClienteView view,
                                     IClienteRepository clienteRepository,
                                     GerenciadorDeLogAtivo logger,
                                     SessaoService sessaoService,
                                     Cliente clienteEdicao) {
        this.view = view;
        this.clienteRepository = clienteRepository;
        this.logger = logger;
        this.sessaoService = sessaoService;
        this.clienteEdicao = clienteEdicao;

        if (clienteEdicao != null) {
            preencherFormulario();
        }
    }

    private void preencherFormulario() {
        view.setNome(clienteEdicao.getNome());
        view.setCpf(clienteEdicao.getCpfFormatado());
        view.setCpfEditavel(false);

        List<Endereco> enderecos = clienteEdicao.getEnderecos();
        List<String[]> dadosEnderecos = new ArrayList<>();
        int padraoIndex = 0;

        for (int i = 0; i < enderecos.size(); i++) {
            Endereco e = enderecos.get(i);
            dadosEnderecos.add(new String[]{
                e.getLogradouro(), e.getNumero(), e.getComplemento(),
                e.getBairro(), e.getCidade(), e.getUf(), e.getCep()
            });
            if (e.isPadrao()) padraoIndex = i;
        }

        view.setEnderecos(dadosEnderecos, padraoIndex);
    }

    public void onSalvar() {
        String nome = view.getNome();
        String cpf = view.getCpf();

        if (nome == null || nome.trim().isEmpty()) {
            view.exibirMensagemErro("Nome é obrigatório.");
            return;
        }
        String nomeTrimmed = nome.trim();
        if (nomeTrimmed.length() < 2 || nomeTrimmed.length() > 120) {
            view.exibirMensagemErro("Nome deve conter de 2 a 120 caracteres.");
            return;
        }
        if (!nomeTrimmed.matches("[\\p{L} '\\-]+")) {
            view.exibirMensagemErro(
                "Nome deve conter apenas letras, espaços, apóstrofos e hífens.");
            return;
        }

        if (cpf == null || cpf.trim().isEmpty()) {
            view.exibirMensagemErro("CPF é obrigatório.");
            return;
        }
        if (!CpfUtil.validar(cpf)) {
            view.exibirMensagemErro("CPF inválido.");
            return;
        }

        String cpfLimpo = CpfUtil.removerMascara(cpf);
        if (clienteEdicao == null) {
            if (clienteRepository.buscarPorCpf(cpfLimpo).isPresent()) {
                view.exibirMensagemErro("Já existe um cliente com este CPF.");
                return;
            }
        }

        List<String[]> dadosEnderecos = view.getEnderecos();
        int padraoIndex = view.getEnderecoPadraoIndex();
        List<Endereco> enderecos = new ArrayList<>();
        boolean temEnderecoPreenchido = false;

        for (int i = 0; i < dadosEnderecos.size(); i++) {
            String[] d = dadosEnderecos.get(i);
            boolean linhaPreenchida = (d[0] != null && !d[0].trim().isEmpty());

            if (linhaPreenchida) {
                if (d[1] == null || d[1].trim().isEmpty()) {
                    view.exibirMensagemErro("Número é obrigatório no endereço " + (i + 1) + ".");
                    return;
                }
                if (d[3] == null || d[3].trim().isEmpty()) {
                    view.exibirMensagemErro("Bairro é obrigatório no endereço " + (i + 1) + ".");
                    return;
                }
                if (d[4] == null || d[4].trim().isEmpty()) {
                    view.exibirMensagemErro("Cidade é obrigatória no endereço " + (i + 1) + ".");
                    return;
                }
                if (!UfUtil.isValida(d[5])) {
                    view.exibirMensagemErro("UF inválida no endereço " + (i + 1)
                            + ". Informe uma sigla válida (ex.: ES, SP).");
                    return;
                }
                if (d[6] == null || d[6].replaceAll("\\D", "").length() != 8) {
                    view.exibirMensagemErro("CEP deve conter 8 dígitos no endereço " + (i + 1) + ".");
                    return;
                }

                Endereco endereco = new Endereco(
                    d[0].trim(), d[1].trim(),
                    d[2] != null ? d[2].trim() : "",
                    d[3].trim(), d[4].trim(),
                    d[5].trim().toUpperCase(), d[6].replaceAll("\\D", ""),
                    i == padraoIndex);
                enderecos.add(endereco);
                temEnderecoPreenchido = true;
            }
        }

        if (!temEnderecoPreenchido) {
            view.exibirMensagemErro("Pelo menos um endereço de entrega é obrigatório.");
            return;
        }

        boolean temPadrao = enderecos.stream().anyMatch(Endereco::isPadrao);
        if (!temPadrao) {
            view.exibirMensagemErro("Um endereço padrão é obrigatório.");
            return;
        }

        try {
            Cliente cliente;
            if (clienteEdicao != null) {
                cliente = clienteEdicao;
                cliente.setNome(nomeTrimmed);
                cliente.setEnderecos(enderecos);
            } else {
                cliente = new Cliente(nomeTrimmed, cpfLimpo);
                for (Endereco e : enderecos) {
                    cliente.adicionarEndereco(e);
                }
            }
            clienteRepository.salvar(cliente);

            String operacao = clienteEdicao != null
                    ? "Edição de cliente: " + cliente.getNome()
                    : "Cadastro de cliente: " + cliente.getNome();
            registrarAuditoria(operacao);

            view.exibirMensagemSucesso("Cliente salvo com sucesso!");
            view.fechar();

        } catch (IllegalArgumentException e) {
            view.exibirMensagemErro(e.getMessage());
        }
    }

    public void onCancelar() {
        view.fechar();
    }

    private void registrarAuditoria(String operacao) {
        if (logger != null) {
            try {
                String usuario = sessaoService.getNomeUsuarioLogado();
                logger.registrar(MensagemLogFactory.criarParaOperacao(
                    usuario != null ? usuario : "sistema", operacao));
            } catch (Exception e) {
                System.err.println("Falha ao registrar auditoria: " + e.getMessage());
            }
        }
    }
}

