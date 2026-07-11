package com.ufes.delivery.presenter;

import com.ufes.delivery.log.GerenciadorDeLogAtivo;
import com.ufes.delivery.log.MensagemLogFactory;
import com.ufes.delivery.desconto.pedido.AplicadorCupomPedidoService;
import com.ufes.delivery.desconto.taxa.entrega.CalculadoraTaxaDescontoPedidoService;
import com.ufes.delivery.model.Cliente;
import com.ufes.delivery.model.CupomDescontoPedido;
import com.ufes.delivery.model.Endereco;
import com.ufes.delivery.model.Item;
import com.ufes.delivery.model.Pedido;
import com.ufes.delivery.model.Produto;
import com.ufes.delivery.model.estado.EstadosPedido;
import com.ufes.delivery.repository.IClienteRepository;
import com.ufes.delivery.repository.ICupomRepository;
import com.ufes.delivery.repository.IPedidoRepository;
import com.ufes.delivery.repository.IProdutoRepository;
import com.ufes.delivery.repository.PedidoRegistro;
import com.ufes.delivery.service.FonteAleatoriedadePadrao;
import com.ufes.delivery.service.IFonteAleatoriedade;
import com.ufes.delivery.service.ResultadoPagamento;
import com.ufes.delivery.service.SessaoService;
import com.ufes.delivery.service.SimuladorPagamentoService;
import com.ufes.delivery.view.CadastroClienteView;
import com.ufes.delivery.view.IPedidoView;
import com.ufes.delivery.view.ResultadoPagamentoView;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class PedidoPresenter {

    private final IPedidoView view;
    private final IClienteRepository clienteRepository;
    private final IProdutoRepository produtoRepository;
    private final ICupomRepository cupomRepository;
    private final IPedidoRepository pedidoRepository;
    private final GerenciadorDeLogAtivo logger;
    private final SessaoService sessaoService;
    private final IFonteAleatoriedade fonteAleatoriedade;

    private static int contadorCodigo = 1001;

    private Cliente clienteSelecionado;
    private final List<Produto> produtosAdicionados = new ArrayList<>();
    private final List<Item> itensDoCarrinho = new ArrayList<>();
    private Pedido pedido;

    private static final NumberFormat FORMATO_MOEDA =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public PedidoPresenter(IPedidoView view,
                           IClienteRepository clienteRepository,
                           IProdutoRepository produtoRepository,
                           ICupomRepository cupomRepository,
                           IPedidoRepository pedidoRepository,
                           GerenciadorDeLogAtivo logger,
                           SessaoService sessaoService) {
        this(view, clienteRepository, produtoRepository, cupomRepository, pedidoRepository,
                logger, sessaoService, new FonteAleatoriedadePadrao());
    }

    public PedidoPresenter(IPedidoView view,
                           IClienteRepository clienteRepository,
                           IProdutoRepository produtoRepository,
                           ICupomRepository cupomRepository,
                           IPedidoRepository pedidoRepository,
                           GerenciadorDeLogAtivo logger,
                           SessaoService sessaoService,
                           IFonteAleatoriedade fonteAleatoriedade) {
        this.view = view;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.cupomRepository = cupomRepository;
        this.pedidoRepository = pedidoRepository;
        this.logger = logger;
        this.sessaoService = sessaoService;
        this.fonteAleatoriedade = fonteAleatoriedade;

        atualizarTotais();
    }


    public void onBuscarCliente() {
        String texto = view.getTextoCliente();
        if (texto == null || texto.trim().isEmpty()) {
            view.exibirMensagemErro("Informe o nome do cliente para buscar.");
            return;
        }

        List<Cliente> resultados = clienteRepository.buscarPorNome(texto.trim());

        if (resultados.isEmpty()) {
            view.exibirMensagemErro("Nenhum cliente encontrado com o nome \"" + texto.trim() + "\".");
            return;
        }

        Cliente selecionado;
        if (resultados.size() == 1) {
            selecionado = resultados.get(0);
        } else {
            String[] opcoes = new String[resultados.size()];
            for (int i = 0; i < resultados.size(); i++) {
                Cliente c = resultados.get(i);
                opcoes[i] = c.getNome() + " - " + c.getCpfFormatado();
            }
            Object escolha = view.exibirSelecaoDialog(
                    "Selecione o cliente:", "Clientes encontrados", opcoes);
            if (escolha == null) {
                return;
            }
            String escolhaStr = escolha.toString();
            selecionado = null;
            for (int i = 0; i < opcoes.length; i++) {
                if (opcoes[i].equals(escolhaStr)) {
                    selecionado = resultados.get(i);
                    break;
                }
            }
            if (selecionado == null) {
                return;
            }
        }

        selecionarCliente(selecionado);
    }

    private void selecionarCliente(Cliente cliente) {
        this.clienteSelecionado = cliente;
        view.setTextoCliente(cliente.getNome() + " - " + cliente.getCpfFormatado());

        List<Endereco> enderecos = cliente.getEnderecos();
        List<String> enderecosFormatados = new ArrayList<>();
        int indexPadrao = 0;

        for (int i = 0; i < enderecos.size(); i++) {
            Endereco e = enderecos.get(i);
            enderecosFormatados.add(e.getEnderecoFormatado());
            if (e.isPadrao()) {
                indexPadrao = i;
            }
        }

        view.carregarEnderecos(enderecosFormatados);
        if (!enderecosFormatados.isEmpty()) {
            view.setEnderecoSelecionadoIndex(indexPadrao);
        }

        reconstruirPedido();
    }


    public void onEnderecoAlterado() {
        if (clienteSelecionado == null) return;

        int index = view.getEnderecoSelecionadoIndex();
        List<Endereco> enderecos = clienteSelecionado.getEnderecos();
        if (index < 0 || index >= enderecos.size()) return;

        for (int i = 0; i < enderecos.size(); i++) {
            enderecos.get(i).setPadrao(i == index);
        }

        reconstruirPedido();
    }


    public void onAdicionarItem() {
        String nomeBusca = view.exibirInputDialog("Nome do produto:");
        if (nomeBusca == null || nomeBusca.trim().isEmpty()) {
            return;
        }

        List<Produto> resultados = produtoRepository.buscarPorNome(nomeBusca.trim());
        if (resultados.isEmpty()) {
            view.exibirMensagemErro("Nenhum produto encontrado com o nome \"" + nomeBusca.trim() + "\".");
            return;
        }

        Produto produtoSelecionado;
        if (resultados.size() == 1) {
            produtoSelecionado = resultados.get(0);
        } else {
            String[] opcoes = new String[resultados.size()];
            for (int i = 0; i < resultados.size(); i++) {
                Produto p = resultados.get(i);
                opcoes[i] = p.getNome() + " (" + p.getCategoria() + ") - " + p.getPrecoFormatado()
                        + " [Estoque: " + p.getEstoqueAtual() + "]";
            }
            Object escolha = view.exibirSelecaoDialog(
                    "Selecione o produto:", "Produtos encontrados", opcoes);
            if (escolha == null) {
                return;
            }
            String escolhaStr = escolha.toString();
            produtoSelecionado = null;
            for (int i = 0; i < opcoes.length; i++) {
                if (opcoes[i].equals(escolhaStr)) {
                    produtoSelecionado = resultados.get(i);
                    break;
                }
            }
            if (produtoSelecionado == null) {
                return;
            }
        }

        if (produtoSelecionado.getEstoqueAtual() <= 0) {
            view.exibirMensagemErro("Produto \"" + produtoSelecionado.getNome()
                    + "\" sem estoque disponível.");
            return;
        }

        Item novoItem = new Item(
                produtoSelecionado.getNome(),
                1,
                produtoSelecionado.getPrecoUnitario(),
                produtoSelecionado.getCategoria());

        itensDoCarrinho.add(novoItem);
        produtosAdicionados.add(produtoSelecionado);

        atualizarTabelaItens();
        reconstruirPedido();
    }


    public void onRemoverItem() {
        int linha = view.getLinhaSelecionada();
        if (linha < 0 || linha >= itensDoCarrinho.size()) {
            view.exibirMensagemErro("Selecione um item na tabela para excluir.");
            return;
        }

        itensDoCarrinho.remove(linha);
        produtosAdicionados.remove(linha);

        atualizarTabelaItens();
        reconstruirPedido();
    }


    public void onQuantidadeAlterada(int linha, String novaQuantidadeStr) {
        if (linha < 0 || linha >= itensDoCarrinho.size()) {
            return;
        }

        try {
            int novaQtd = Integer.parseInt(novaQuantidadeStr.trim());
            if (novaQtd <= 0) {
                view.exibirMensagemErro("Quantidade deve ser maior que zero.");
                atualizarTabelaItens();
                return;
            }

            Produto produto = produtosAdicionados.get(linha);
            if (novaQtd > produto.getEstoqueAtual()) {
                view.exibirMensagemErro("Quantidade excede o estoque disponível ("
                        + produto.getEstoqueAtual() + ").");
                atualizarTabelaItens();
                return;
            }

            Item itemAntigo = itensDoCarrinho.get(linha);
            Item itemNovo = new Item(
                    itemAntigo.getNome(),
                    novaQtd,
                    itemAntigo.getValorUnitario(),
                    itemAntigo.getTipo());
            itensDoCarrinho.set(linha, itemNovo);

            atualizarTabelaItens();
            reconstruirPedido();

        } catch (NumberFormatException e) {
            view.exibirMensagemErro("Quantidade deve ser um número inteiro.");
            atualizarTabelaItens();
        }
    }


    public void onAplicarCupom() {
        String codigoCupom = view.getCodigoCupom();
        if (codigoCupom == null || codigoCupom.trim().isEmpty()) {
            view.exibirMensagemErro("Informe o código do cupom.");
            registrarAuditoria("Cupom recusado - código não informado");
            return;
        }

        if (pedido == null) {
            view.exibirMensagemErro("Selecione um cliente e adicione itens antes de aplicar o cupom.");
            return;
        }

        String codigo = codigoCupom.trim().toUpperCase();
        AplicadorCupomPedidoService aplicador =
                new AplicadorCupomPedidoService(cupomRepository, logger);
        try {
            aplicador.aplicarCupom(pedido, codigo, LocalDateTime.now());
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.exibirMensagemErro(e.getMessage());
            registrarAuditoria("Cupom recusado: " + codigo + " - " + e.getMessage());
            return;
        }

        double percentual = pedido.getCupomAplicado()
                .map(CupomDescontoPedido::getPercentual).orElse(0.0);
        view.exibirMensagemSucesso("Cupom \"" + codigo + "\" aplicado com sucesso! "
                + "Desconto de " + percentual + "%.");
        atualizarTotais();
    }


    public void onPagar() {
        if (clienteSelecionado == null) {
            view.exibirMensagemErro("Selecione um cliente antes de pagar.");
            return;
        }

        if (itensDoCarrinho.isEmpty()) {
            view.exibirMensagemErro("Adicione pelo menos um item ao pedido.");
            return;
        }

        reconstruirPedido();

        String resumo = "Confirmar pagamento do pedido #" + pedido.getCodigo() + "?\n\n"
                + "Cliente: " + clienteSelecionado.getNome() + "\n"
                + "Itens: " + itensDoCarrinho.size() + "\n"
                + "Total: " + FORMATO_MOEDA.format(pedido.calcularValorTotal());

        int resposta = view.exibirConfirmDialog(resumo, "Confirmar Pagamento");
        if (resposta != 0) {
            return;
        }

        for (int i = 0; i < itensDoCarrinho.size(); i++) {
            Item item = itensDoCarrinho.get(i);
            Produto produto = produtosAdicionados.get(i);
            if (item.getQuantidade() > produto.getEstoqueAtual()) {
                view.exibirMensagemErro("Estoque insuficiente para \"" + produto.getNome()
                        + "\". Solicitado: " + item.getQuantidade()
                        + ", disponível: " + produto.getEstoqueAtual() + ".");
                registrarAuditoria("Pagamento bloqueado por estoque insuficiente - Pedido #"
                        + pedido.getCodigo() + " - Produto: " + produto.getNome()
                        + " - Solicitado: " + item.getQuantidade()
                        + " - Disponível: " + produto.getEstoqueAtual());
                return;
            }
        }

        SimuladorPagamentoService simulador = new SimuladorPagamentoService(fonteAleatoriedade);
        ResultadoPagamento resultado = simulador.simularPagamento();

        String enderecoEntrega = "";
        int indexEndereco = view.getEnderecoSelecionadoIndex();
        List<Endereco> enderecos = clienteSelecionado.getEnderecos();
        if (indexEndereco >= 0 && indexEndereco < enderecos.size()) {
            enderecoEntrega = enderecos.get(indexEndereco).getEnderecoFormatado();
        }

        String totalFormatado = FORMATO_MOEDA.format(pedido.calcularValorTotal());

        if (resultado.isAprovado()) {
            for (int i = 0; i < itensDoCarrinho.size(); i++) {
                Item item = itensDoCarrinho.get(i);
                Produto produto = produtosAdicionados.get(i);
                produto.ajustarEstoque(-item.getQuantidade());
                produtoRepository.salvar(produto);
                registrarAuditoria("Baixa de estoque - Pedido #" + pedido.getCodigo()
                        + " - Produto: " + produto.getCodigo() + " - " + produto.getNome()
                        + " - Qtd: " + item.getQuantidade()
                        + " - Estoque resultante: " + produto.getEstoqueAtual());
            }

            contadorCodigo++;

            String dataPedidoStr = resultado.getDataHoraPagamento()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            PedidoRegistro registro = new PedidoRegistro(
                    pedido.getCodigo(),
                    clienteSelecionado.getNome(),
                    dataPedidoStr,
                    null,
                    EstadosPedido.AGUARDANDO_ENTREGA,
                    totalFormatado);
            pedidoRepository.registrar(registro);

            registrarAuditoria("Pedido #" + pedido.getCodigo() + " APROVADO via "
                    + resultado.getFormaPagamento() + " - Cliente: "
                    + clienteSelecionado.getNome() + " - Total: " + totalFormatado);
            registrarAuditoria("Transição de estado - Pedido #" + pedido.getCodigo()
                    + " para " + EstadosPedido.AGUARDANDO_ENTREGA.getNome());

            new ResultadoPagamentoView(
                    resultado, pedido.getCodigo(),
                    clienteSelecionado.getNome(), enderecoEntrega,
                    totalFormatado
            ).exibir();

            view.fechar();
        } else {
            registrarAuditoria("Pedido #" + pedido.getCodigo() + " REPROVADO via "
                    + resultado.getFormaPagamento() + " - Cliente: "
                    + clienteSelecionado.getNome() + " - Total: " + totalFormatado);

            new ResultadoPagamentoView(
                    resultado, pedido.getCodigo(),
                    clienteSelecionado.getNome(), enderecoEntrega,
                    totalFormatado
            ).exibir();
        }
    }


    public void onCancelar() {
        boolean temConteudo = !itensDoCarrinho.isEmpty() || clienteSelecionado != null;
        if (temConteudo) {
            int resposta = view.exibirConfirmDialog(
                    "Há dados informados no pedido. Deseja descartar as alterações?",
                    "Cancelar pedido");
            if (resposta != 0) {
                return;
            }
        }
        view.fechar();
    }


    public void onNovoCliente() {
        CadastroClienteView cadastroView = new CadastroClienteView();
        CadastroClientePresenter cadastroPresenter = new CadastroClientePresenter(
                cadastroView, clienteRepository, logger, sessaoService, null);
        cadastroView.setPresenter(cadastroPresenter);
        cadastroView.exibir();
    }


    private void reconstruirPedido() {
        if (clienteSelecionado == null) {
            pedido = null;
            atualizarTotais();
            return;
        }

        pedido = new Pedido(contadorCodigo, LocalDateTime.now(), clienteSelecionado);

        for (Item item : itensDoCarrinho) {
            pedido.adicionarItem(item);
        }

        try {
            CalculadoraTaxaDescontoPedidoService calculadora =
                    new CalculadoraTaxaDescontoPedidoService(logger);
            calculadora.calcularDesconto(pedido);
        } catch (Exception e) {
            System.err.println("Erro ao calcular descontos de taxa: " + e.getMessage());
        }

        String codigoCupom = view.getCodigoCupom();
        if (codigoCupom != null && !codigoCupom.trim().isEmpty()) {
            Optional<CupomDescontoPedido> cupomOpt = cupomRepository.buscarCupom(codigoCupom.trim().toUpperCase());
            if (cupomOpt.isPresent()) {
                CupomDescontoPedido cupom = cupomOpt.get();
                LocalDateTime agora = LocalDateTime.now();
                if (!agora.isBefore(cupom.getDataHoraInicio()) && !agora.isAfter(cupom.getDataHoraFim())) {
                    try {
                        pedido.setCupomAplicado(cupom);
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
        }

        atualizarTotais();
    }

    private void atualizarTabelaItens() {
        List<String[]> dados = new ArrayList<>();
        for (Item item : itensDoCarrinho) {
            dados.add(new String[]{
                item.getTipo(),
                item.getNome(),
                FORMATO_MOEDA.format(item.getValorUnitario()),
                String.valueOf(item.getQuantidade()),
                FORMATO_MOEDA.format(item.valorTotal())
            });
        }
        view.carregarItens(dados);
    }

    private void atualizarTotais() {
        if (pedido == null) {
            view.setTotalDescontos(FORMATO_MOEDA.format(0));
            view.setDescontoTaxaEntrega(FORMATO_MOEDA.format(0));
            view.setTaxaEntregaFinal(FORMATO_MOEDA.format(0));
            view.setTotalPedido(FORMATO_MOEDA.format(0));
            return;
        }

        double valorPedido = pedido.getValorPedido();
        double totalDescontos = 0;

        Optional<CupomDescontoPedido> cupomAplicado = pedido.getCupomAplicado();
        if (cupomAplicado.isPresent()) {
            double valorComTaxa = valorPedido + pedido.getTaxaEntregaComDesconto();
            totalDescontos = valorComTaxa * cupomAplicado.get().getPercentual() / 100;
        }

        view.setTotalDescontos(FORMATO_MOEDA.format(totalDescontos));
        view.setDescontoTaxaEntrega(FORMATO_MOEDA.format(pedido.getTotalDescontosTaxaEntrega()));
        view.setTaxaEntregaFinal(FORMATO_MOEDA.format(pedido.getTaxaEntregaComDesconto()));
        view.setTotalPedido(FORMATO_MOEDA.format(pedido.calcularValorTotal()));
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

