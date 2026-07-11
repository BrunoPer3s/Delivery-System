package com.ufes.delivery.service;

import com.ufes.delivery.log.GerenciadorDeLogAtivo;
import com.ufes.delivery.log.MensagemLogFactory;
import com.ufes.delivery.repository.pedido.IPedidoRepository;
import com.ufes.delivery.repository.pedido.TransicaoEstadoPedido;

import javax.swing.Timer;
import java.util.List;

public class SimuladorCicloPedidoService {

    private static final int INTERVALO_MS = 30_000;

    private final IPedidoRepository pedidoRepository;
    private final GerenciadorDeLogAtivo logger;
    private final SessaoService sessaoService;
    private final Timer timer;

    public SimuladorCicloPedidoService(IPedidoRepository pedidoRepository,
                                       GerenciadorDeLogAtivo logger,
                                       SessaoService sessaoService) {
        this.pedidoRepository = pedidoRepository;
        this.logger = logger;
        this.sessaoService = sessaoService;
        this.timer = new Timer(INTERVALO_MS, e -> avancarCiclo());
    }

    public void iniciar() {
        timer.start();
    }

    public void parar() {
        timer.stop();
    }

    private void avancarCiclo() {
        List<TransicaoEstadoPedido> transicoes = pedidoRepository.avancarEstadosPendentes();
        for (TransicaoEstadoPedido transicao : transicoes) {
            registrarAuditoria(transicao);
        }
    }

    private void registrarAuditoria(TransicaoEstadoPedido transicao) {
        if (logger == null) {
            return;
        }
        try {
            String usuario = sessaoService.getNomeUsuarioLogado();
            logger.registrar(MensagemLogFactory.criarParaOperacao(
                    usuario != null ? usuario : "sistema",
                    "Transição automática de estado - Pedido #" + transicao.codigoPedido()
                            + ": " + transicao.estadoAnterior() + " → " + transicao.estadoNovo()));
        } catch (Exception e) {
            System.err.println("Falha ao registrar auditoria: " + e.getMessage());
        }
    }
}

