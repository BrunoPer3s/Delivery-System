package com.ufes.delivery.desconto.taxa.entrega;

import com.ufes.delivery.log.MensagemLogFactory;
import com.ufes.delivery.log.ResultadoOperacao;
import com.ufes.delivery.util.UsuarioLogadoService;
import com.ufes.delivery.model.CupomDescontoEntrega;
import com.ufes.delivery.model.Pedido;
import com.ufes.log.ILogger;

import java.util.ArrayList;
import java.util.List;

public class CalculadoraTaxaDescontoPedidoService {
    private List<IFormaDescontoTaxaEntrega> metodosDeDesconto;
    private ILogger logger;

    public CalculadoraTaxaDescontoPedidoService(ILogger logger) {
        this.logger = logger;
        metodosDeDesconto = new ArrayList<>();

        metodosDeDesconto.add(new FormaDescontoTaxaPorBairro());
        metodosDeDesconto.add(new FormaDescontoTaxaPorTipoCliente());
        metodosDeDesconto.add(new FormaDescontoTipoItem());
        metodosDeDesconto.add(new FormaDescontoValorPedido());
    }

    public void calcularDesconto(Pedido pedido) {
        pedido.limparCuponsDescontoEntrega();

        double limiteAplicavel = pedido.getTaxaEntrega();

        for (IFormaDescontoTaxaEntrega formaDescontoTaxaEntrega : metodosDeDesconto) {
            double totalDescontos = pedido.getTotalDescontosTaxaEntrega();

            if (formaDescontoTaxaEntrega.seAplica(pedido) && totalDescontos < limiteAplicavel) {
                CupomDescontoEntrega cupom = formaDescontoTaxaEntrega.calcularDesconto(pedido);
                double limiteRestante = limiteAplicavel - totalDescontos;
                double valorAplicado = Math.min(cupom.getValorDesconto(), limiteRestante);
                cupom.aplicar(valorAplicado);

                if (cupom.getValorDesconto() > 0) {
                    pedido.adicionarCupomDescontoEntrega(cupom);
                }
            }
        }
        if (logger != null) {
            double descontoTotal = pedido.getTotalDescontosTaxaEntrega();
            logger.registrar(MensagemLogFactory.operacao("Cálculo de desconto na taxa de entrega")
                    .pedido(pedido)
                    .recurso("Taxa de entrega do pedido " + pedido.getCodigo())
                    .resultado(ResultadoOperacao.SUCESSO)
                    .justificativa(descontoTotal > 0
                            ? String.format("Desconto aplicado: R$ %.2f", descontoTotal)
                            : "Nenhum desconto aplicável")
                    .paraUsuario(UsuarioLogadoService.getNomeUsuario()));
        }
    }
}

