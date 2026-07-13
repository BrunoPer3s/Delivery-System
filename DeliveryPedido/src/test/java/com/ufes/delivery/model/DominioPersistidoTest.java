package com.ufes.delivery.model;

import com.ufes.delivery.model.estado.AguardandoEntrega;
import com.ufes.delivery.model.estado.AguardandoPagamento;
import com.ufes.delivery.model.estado.EmPreparo;
import com.ufes.delivery.model.estado.EmTransito;
import com.ufes.delivery.model.estado.Entregue;
import com.ufes.delivery.model.estado.EstadoPedido;
import com.ufes.delivery.model.estado.Novo;
import com.ufes.delivery.model.perfil.Administrador;
import com.ufes.delivery.model.perfil.Atendente;
import com.ufes.delivery.model.perfil.Perfil;
import com.ufes.delivery.model.situacao.Autorizado;
import com.ufes.delivery.model.situacao.NaoAutorizado;
import com.ufes.delivery.model.situacao.Pendente;
import com.ufes.delivery.model.situacao.Situacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@DisplayName("Vocabulário do domínio exibido e persistido")
class DominioPersistidoTest {

    @Test
    @DisplayName("Os estados do pedido usam exatamente os nomes previstos na especificação")
    void nomesDosEstadosSeguemAEspecificacao() {
        assertEquals("Novo", Novo.INSTANCIA.getNome());
        assertEquals("Aguardando pagamento", AguardandoPagamento.INSTANCIA.getNome());
        assertEquals("Em preparo", EmPreparo.INSTANCIA.getNome());
        assertEquals("Aguardando entrega", AguardandoEntrega.INSTANCIA.getNome());
        assertEquals("Em trânsito", EmTransito.INSTANCIA.getNome());
        assertEquals("Entregue", Entregue.INSTANCIA.getNome());
    }

    @Test
    @DisplayName("As situações de usuário usam exatamente as descrições previstas")
    void descricoesDasSituacoesSeguemAEspecificacao() {
        assertEquals("Autorizado", Autorizado.INSTANCIA.getDescricao());
        assertEquals("Pendente", Pendente.INSTANCIA.getDescricao());
        assertEquals("Nao autorizado", NaoAutorizado.INSTANCIA.getDescricao());
    }

    @Test
    @DisplayName("Os perfis usam exatamente as descrições previstas")
    void descricoesDosPerfisSeguemAEspecificacao() {
        assertEquals("Administrador", Administrador.INSTANCIA.getDescricao());
        assertEquals("Atendente", Atendente.INSTANCIA.getDescricao());
    }

    @Test
    @DisplayName("Todo estado gravado pode ser lido de volta")
    void estadoGravadoPodeSerLidoDeVolta() {
        for (EstadoPedido estado : EstadoPedido.todos()) {
            assertSame(estado, EstadoPedido.porNome(estado.getNome()));
        }
    }

    @Test
    @DisplayName("Toda situação gravada pode ser lida de volta")
    void situacaoGravadaPodeSerLidaDeVolta() {
        for (Situacao situacao : Situacao.todas()) {
            assertSame(situacao, Situacao.porDescricao(situacao.getDescricao()));
        }
    }

    @Test
    @DisplayName("Todo perfil gravado pode ser lido de volta")
    void perfilGravadoPodeSerLidoDeVolta() {
        for (Perfil perfil : Perfil.todos()) {
            assertSame(perfil, Perfil.porDescricao(perfil.getDescricao()));
        }
    }

    @Test
    @DisplayName("Um usuário administrador autorizado é reconstruído a partir dos textos persistidos")
    void usuarioEReconstruidoAPartirDosTextosPersistidos() {
        Perfil perfil = Perfil.porDescricao("Administrador");
        Situacao situacao = Situacao.porDescricao("Autorizado");

        assertSame(Administrador.INSTANCIA, perfil);
        assertSame(Autorizado.INSTANCIA, situacao);
    }

    @Test
    @DisplayName("Um pedido em Aguardando entrega é reconstruído a partir do texto persistido")
    void pedidoEReconstruidoAPartirDoTextoPersistido() {
        assertSame(AguardandoEntrega.INSTANCIA, EstadoPedido.porNome("Aguardando entrega"));
    }

    @Test
    @DisplayName("O domínio de estados contém exatamente os seis estados previstos")
    void dominioDeEstadosEstaCompleto() {
        List<String> nomes = EstadoPedido.todos().stream().map(EstadoPedido::getNome).toList();

        assertEquals(List.of("Novo", "Aguardando pagamento", "Em preparo",
                "Aguardando entrega", "Em trânsito", "Entregue"), nomes);
    }
}
