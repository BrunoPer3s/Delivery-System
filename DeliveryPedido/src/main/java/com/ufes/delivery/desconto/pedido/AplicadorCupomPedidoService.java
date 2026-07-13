package com.ufes.delivery.desconto.pedido;

import com.ufes.delivery.log.MensagemLogFactory;
import com.ufes.delivery.log.ResultadoOperacao;
import com.ufes.delivery.util.UsuarioLogadoService;
import com.ufes.delivery.model.CupomDescontoPedido;
import com.ufes.delivery.model.Pedido;
import com.ufes.delivery.repository.cupom.ICupomRepository;
import com.ufes.log.ILogger;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class AplicadorCupomPedidoService {
    private ICupomRepository cupomRepository;
    private ILogger logger;

    public AplicadorCupomPedidoService(ICupomRepository cupomRepository, ILogger logger) {
        this.cupomRepository = Objects.requireNonNull(cupomRepository, "Repositorio de cupons nao pode ser nulo");
        this.logger = logger;
    }

    public void aplicarCupom(Pedido pedido, String codigoCupom, LocalDateTime dataHoraAplicacao) {
        Objects.requireNonNull(pedido, "Pedido nao pode ser nulo");
        Objects.requireNonNull(dataHoraAplicacao, "Data e hora de aplicacao nao podem ser nulas");

        if (codigoCupom == null || codigoCupom.isBlank()) {
            throw new IllegalArgumentException("Codigo do cupom nao pode ser vazio");
        }

        Optional<CupomDescontoPedido> cupomEncontrado = cupomRepository.buscarCupom(codigoCupom);

        if (cupomEncontrado.isEmpty()) {
            throw new IllegalArgumentException("Cupom inexistente: " + codigoCupom);
        }

        CupomDescontoPedido cupom = cupomEncontrado.get();

        if (dataHoraAplicacao.isBefore(cupom.getDataHoraInicio())
                || dataHoraAplicacao.isAfter(cupom.getDataHoraFim())) {
            throw new IllegalStateException("O pedido nao esta dentro da validade do cupom");
        }

        Optional<CupomDescontoPedido> cupomAtual = pedido.getCupomAplicado();

        if (cupomAtual.isPresent()) {
            if (cupom.getPercentual() <= cupomAtual.get().getPercentual()) {
                throw new IllegalStateException(
                        "O cupom " + codigoCupom + " nao tem um percentual maior que o cupom atual");
            }
        }

        pedido.setCupomAplicado(cupom);

        if (logger != null) {
            logger.registrar(MensagemLogFactory.operacao("Aplicação de cupom")
                    .pedido(pedido)
                    .recurso("Cupom " + codigoCupom)
                    .resultado(ResultadoOperacao.SUCESSO)
                    .justificativa("Desconto de " + cupom.getPercentual() + "% no total do pedido")
                    .paraUsuario(UsuarioLogadoService.getNomeUsuario()));
        }
    }
}

