package com.ufes.delivery.service;

import com.ufes.delivery.log.GerenciadorDeLogAtivo;
import com.ufes.delivery.log.ResultadoOperacao;
import com.ufes.delivery.log.MensagemLogFactory;
import com.ufes.log.LogIndisponivelException;
import com.ufes.delivery.repository.pedido.IPedidoRepository;
import com.ufes.delivery.repository.pedido.PedidoRegistro;
import com.ufes.delivery.repository.pedido.TransicaoEstadoPedido;

import javax.swing.*;
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

    public void avancarCiclo() {
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
            String nomeCliente = pedidoRepository.buscarPorCodigo(transicao.codigoPedido())
                    .map(PedidoRegistro::getNomeCliente)
                    .orElse("");
            logger.registrar(MensagemLogFactory.operacao("Transição de estado do pedido")
                    .pedido(transicao.codigoPedido(), nomeCliente)
                    .recurso("Pedido " + transicao.codigoPedido())
                    .resultado(ResultadoOperacao.SUCESSO)
                    .justificativa(transicao.estadoAnterior() + " -> " + transicao.estadoNovo())
                    .paraUsuario(usuario != null ? usuario : "sistema"));
        } catch (LogIndisponivelException e) {
            System.err.println("Auditoria indisponível: " + e.getMessage());
        }
    }
}

