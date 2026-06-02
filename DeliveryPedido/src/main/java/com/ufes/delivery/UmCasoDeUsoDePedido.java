package com.ufes.delivery;

import com.ufes.delivery.desconto.pedido.AplicadorCupomPedidoService;
import com.ufes.delivery.desconto.taxa.entrega.CalculadoraTaxaDescontoPedidoService;
import com.ufes.delivery.log.GerenciadorDeLogAtivo;
import com.ufes.delivery.log.MensagemLogFactory;
import com.ufes.log.JsonlLogger;
import com.ufes.log.CsvLogger;
import com.ufes.log.XmlLogger;
import com.ufes.delivery.model.Cliente;
import com.ufes.delivery.model.CupomDescontoPedido;
import com.ufes.delivery.model.Item;
import com.ufes.delivery.model.Pedido;
import com.ufes.delivery.repository.CupomRepositoryEmMemoria;
import java.time.LocalDateTime;

public class UmCasoDeUsoDePedido {

    public static void main(String[] args) {
        System.out.println(">>> INICIANDO EXECUCAO COM LOG: JSONL");
        GerenciadorDeLogAtivo gerenciadorLog = new GerenciadorDeLogAtivo(new JsonlLogger());

        Cliente cliente = new Cliente("Maria", "Ouro", 1, "Limoeiro", "Cidade Maravilhosa", "Castelo");
        LocalDateTime dataPedido = LocalDateTime.now();
        Pedido pedido = new Pedido(123, dataPedido, cliente);

        pedido.adicionarItem(new Item("Caderno", 2, 10.50, "Educacao"));
        pedido.adicionarItem(new Item("Borracha", 5, 4.25, "Educacao"));
        pedido.adicionarItem(new Item("Biscoito", 4, 5.80, "Alimentacao"));
        pedido.adicionarItem(new Item("Pao", 2, 1.50, "Alimentacao"));
        pedido.adicionarItem(new Item("Livro", 2, 40.20, "Lazer"));
        pedido.adicionarItem(new Item("Jogo", 1, 45.90, "Lazer"));

        try {
            CalculadoraTaxaDescontoPedidoService calculadoraDeDesconto = new CalculadoraTaxaDescontoPedidoService(gerenciadorLog);
            calculadoraDeDesconto.calcularDesconto(pedido);
        } catch (Exception ex) {
            System.err.println("Erro ao calcular descontos de taxa: " + ex.getMessage());
            gerenciadorLog.registrar(MensagemLogFactory.criar(pedido, "Falha operacional: " + ex.getMessage(), "calcularDesconto"));
        }

        System.out.println("\n>>> ALTERNANDO DINAMICAMENTE PARA LOG: CSV");
        gerenciadorLog.setLoggerAtivo(new CsvLogger());

        CupomRepositoryEmMemoria cupomRepository = new CupomRepositoryEmMemoria();
        cupomRepository.adicionarCupom(
                new CupomDescontoPedido("VALIDOHOJE", 15.0, dataPedido.minusDays(1), dataPedido.plusDays(1)));

        AplicadorCupomPedidoService aplicadorCupomService = new AplicadorCupomPedidoService(cupomRepository, gerenciadorLog);

        try {
            System.out.println("Tentando aplicar cupom VALIDOHOJE...");
            aplicadorCupomService.aplicarCupom(pedido, "VALIDOHOJE", LocalDateTime.now());
        } catch (Exception ex) {
            System.err.println("Erro inesperado ao aplicar cupom valido: " + ex.getMessage());
            gerenciadorLog.registrar(MensagemLogFactory.criar(pedido, "Falha operacional: " + ex.getMessage(), "aplicarCupom"));
        }

        System.out.println("\n>>> ALTERNANDO DINAMICAMENTE PARA LOG: XML");
        gerenciadorLog.setLoggerAtivo(new XmlLogger());

        try {
            System.out.println("Tentando aplicar cupom CUPOM_INVALIDO...");
            aplicadorCupomService.aplicarCupom(pedido, "CUPOM_INVALIDO", LocalDateTime.now());
        } catch (Exception ex) {
            System.out.println("Erro esperado capturado e auditado: " + ex.getMessage());
            gerenciadorLog.registrar(MensagemLogFactory.criar(pedido, "Falha operacional: " + ex.getMessage(), "aplicarCupom"));
        }

        try {
            pedido.calcularValorTotal();
            gerenciadorLog.registrar(MensagemLogFactory.criar(pedido, "Calculo do valor total do pedido", "calcularValorTotal"));
        } catch (Exception ex) {
            System.err.println("Erro ao finalizar pedido: " + ex.getMessage());
            gerenciadorLog.registrar(MensagemLogFactory.criar(pedido, "Falha operacional: " + ex.getMessage(), "calcularValorTotal"));
        }

        System.out.println("\n--- Resumo do Pedido ---");
        System.out.println(pedido);
        System.out.println("\n>>> EXECUCAO FINALIZADA. Verifique os arquivos log.jsonl, log.csv e log.xml na raiz do projeto.");
    }
}
