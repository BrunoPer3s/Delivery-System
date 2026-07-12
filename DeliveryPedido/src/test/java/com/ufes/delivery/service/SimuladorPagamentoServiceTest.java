package com.ufes.delivery.service;

import com.ufes.delivery.apoio.FonteAleatoriedadeFixa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("US11 - Simular resultado do pagamento")
class SimuladorPagamentoServiceTest {

    private ResultadoPagamento simular(boolean aprovado, int indiceForma) {
        return new SimuladorPagamentoService(
                new FonteAleatoriedadeFixa(aprovado, indiceForma)).simularPagamento();
    }

    @Test
    @DisplayName("Cenário 1 - Resultado aprovado registra transação e prazo de entrega")
    void resultadoAprovado() {
        ResultadoPagamento resultado = simular(true, 0);

        assertTrue(resultado.isAprovado());
        assertNotNull(resultado.getIdentificadorTransacao());
        assertNotNull(resultado.getPrevisaoEntrega());
        assertTrue(resultado.getMensagem().contains("aprovado"));
    }

    @Test
    @DisplayName("Cenário 2 - Resultado reprovado não gera prazo de entrega")
    void resultadoReprovado() {
        ResultadoPagamento resultado = simular(false, 0);

        assertFalse(resultado.isAprovado());
        assertNull(resultado.getPrevisaoEntrega());
        assertTrue(resultado.getMensagem().contains("reprovado"));
    }

    @ParameterizedTest
    @CsvSource({
        "0, Open Finance",
        "1, PIX Chave",
        "2, PIX QR Code",
        "3, Cartão de Crédito"
    })
    @DisplayName("Cenário 3 - A forma de pagamento corresponde ao valor da fonte de teste")
    void formaPagamentoVemDaFonteDeTeste(int indice, String formaEsperada) {
        ResultadoPagamento resultado = simular(true, indice);

        assertEquals(formaEsperada, resultado.getFormaPagamento());
    }

    @Test
    @DisplayName("Cenário 3 - A simulação oferece exatamente as quatro formas previstas")
    void asQuatroFormasEstaoDisponiveis() {
        Set<String> formas = new HashSet<>();
        for (int indice = 0; indice < 4; indice++) {
            formas.add(simular(true, indice).getFormaPagamento());
        }

        assertEquals(Set.of("Open Finance", "PIX Chave", "PIX QR Code", "Cartão de Crédito"), formas);
    }

    @Test
    @DisplayName("Cenário 4 - O prazo estimado fica entre a aprovação e o mesmo dia do mês subsequente")
    void prazoEstimadoDentroDoLimite() {
        LocalDateTime antes = LocalDateTime.now();
        ResultadoPagamento resultado = simular(true, 0);
        LocalDateTime limite = resultado.getDataHoraPagamento().plusMonths(1);

        LocalDateTime previsao = resultado.getPrevisaoEntrega();
        assertFalse(previsao.isBefore(antes));
        assertFalse(previsao.isAfter(limite));
    }

    @Test
    @DisplayName("Cenário 4 - O prazo respeita o limite mesmo com a fonte sorteando o máximo")
    void prazoEstimadoRespeitaLimiteNoValorMaximo() {
        ResultadoPagamento resultado = new SimuladorPagamentoService(
                new FonteAleatoriedadeFixa(true, 0, Integer.MAX_VALUE)).simularPagamento();

        LocalDateTime limite = resultado.getDataHoraPagamento().plusMonths(1);
        assertFalse(resultado.getPrevisaoEntrega().isAfter(limite));
    }

    @Test
    @DisplayName("O identificador da transação é único entre tentativas aprovadas")
    void identificadorDaTransacaoEUnico() {
        Set<String> identificadores = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            identificadores.add(simular(true, 0).getIdentificadorTransacao());
        }

        assertEquals(50, identificadores.size());
    }

    @Test
    @DisplayName("O resultado não expõe dados financeiros sensíveis")
    void resultadoNaoExpoeDadosSensiveis() {
        ResultadoPagamento resultado = simular(true, 3);

        assertEquals("Cartão de Crédito", resultado.getFormaPagamento());
        assertFalse(resultado.getMensagem().matches(".*\\d{13,19}.*"));
    }
}
