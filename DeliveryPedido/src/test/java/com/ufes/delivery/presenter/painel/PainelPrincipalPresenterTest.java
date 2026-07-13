package com.ufes.delivery.presenter.painel;

import com.ufes.delivery.apoio.PainelPrincipalViewStub;
import com.ufes.delivery.model.Usuario;
import com.ufes.delivery.model.estado.*;
import com.ufes.delivery.model.perfil.Administrador;
import com.ufes.delivery.model.perfil.Atendente;
import com.ufes.delivery.model.perfil.Perfil;
import com.ufes.delivery.model.situacao.Autorizado;
import com.ufes.delivery.repository.pedido.IPedidoRepository;
import com.ufes.delivery.repository.pedido.PedidoRegistro;
import com.ufes.delivery.repository.pedido.PedidoRepositoryEmMemoria;
import com.ufes.delivery.service.SessaoService;
import com.ufes.delivery.util.SenhaUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("US04 - Acessar o painel operacional")
class PainelPrincipalPresenterTest {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private IPedidoRepository pedidoRepository;
    private SessaoService sessaoService;
    private PainelPrincipalViewStub view;

    @BeforeEach
    void preparar() {
        pedidoRepository = new PedidoRepositoryEmMemoria();
        sessaoService = SessaoService.getInstancia();
        view = new PainelPrincipalViewStub();
    }

    @AfterEach
    void encerrar() {
        sessaoService.encerrarSessao();
    }

    private String hoje() {
        return LocalDate.now().format(DATA);
    }

    private String ontem() {
        return LocalDate.now().minusDays(1).format(DATA);
    }

    private void registrar(int codigo, String dataPedido, String dataConclusao, EstadoPedido estado) {
        pedidoRepository.registrar(new PedidoRegistro(
                codigo, "Fulano de Tal", dataPedido, dataConclusao, estado, "R$ 100,00"));
    }

    private void logarComo(Perfil perfil) {
        sessaoService.iniciarSessao(new Usuario("Usuario Teste", "usuario01",
                SenhaUtil.hashSenha("Senha123"), perfil, Autorizado.INSTANCIA));
    }

    private void abrirPainel() {
        logarComo(Administrador.INSTANCIA);
        new PainelPrincipalPresenter(view, null, null, null, null,
                pedidoRepository, null, sessaoService, null);
    }

    @Test
    @DisplayName("Cenário 1 - A data de operação é exibida em destaque no formato DD/MM/AAAA")
    void exibeDataDeOperacao() {
        abrirPainel();

        assertEquals(hoje(), view.getDataOperacao());
        assertTrue(view.getDataOperacao().matches("\\d{2}/\\d{2}/\\d{4}"));
    }

    @Test
    @DisplayName("Cenário 1 - A lista de pedidos considera somente a data de operação")
    void listaSomenteOsPedidosDaDataDeOperacao() {
        registrar(1001, hoje(), null, Novo.INSTANCIA);
        registrar(1002, ontem(), null, EmPreparo.INSTANCIA);
        registrar(1003, hoje(), null, EmTransito.INSTANCIA);

        abrirPainel();

        assertEquals(List.of("1001", "1003"), view.getCodigosNaTabela());
    }

    @Test
    @DisplayName("Cenário 1 - As métricas consideram somente a data de operação")
    void metricasIgnoramPedidosDeOutrasDatas() {
        registrar(1001, hoje(), null, Novo.INSTANCIA);
        registrar(1002, ontem(), null, Novo.INSTANCIA);
        registrar(1003, ontem(), null, Novo.INSTANCIA);

        abrirPainel();

        assertEquals(1, view.getPedidosDia());
        assertEquals(1, view.getNovos());
    }

    @Test
    @DisplayName("Cenário 2 - Métricas coerentes com a lista: oito pedidos, dois Novos e dois Entregues")
    void metricasCoerentesComAListaDePedidos() {
        registrar(1001, hoje(), null, Novo.INSTANCIA);
        registrar(1002, hoje(), null, Novo.INSTANCIA);
        registrar(1003, hoje(), null, AguardandoPagamento.INSTANCIA);
        registrar(1004, hoje(), null, EmPreparo.INSTANCIA);
        registrar(1005, hoje(), null, AguardandoEntrega.INSTANCIA);
        registrar(1006, hoje(), null, EmTransito.INSTANCIA);
        registrar(1007, hoje(), hoje(), Entregue.INSTANCIA);
        registrar(1008, hoje(), hoje(), Entregue.INSTANCIA);

        abrirPainel();

        assertEquals(8, view.getPedidosDia());
        assertEquals(2, view.getNovos());
        assertEquals(2, view.getEntreguesHoje());
        assertEquals(8, view.getPedidosCarregados().size());
        assertEquals(1, view.getAguardandoPagamento());
        assertEquals(1, view.getEmPreparo());
        assertEquals(1, view.getAguardandoEntrega());
        assertEquals(1, view.getEmTransito());
    }

    @Test
    @DisplayName("Entregues hoje conta pela data de conclusão, não pela data do pedido")
    void entreguesHojeContaPelaDataDeConclusao() {
        registrar(1001, ontem(), hoje(), Entregue.INSTANCIA);
        registrar(1002, hoje(), ontem(), Entregue.INSTANCIA);

        abrirPainel();

        assertEquals(1, view.getEntreguesHoje());
    }

    @Test
    @DisplayName("Um pedido entregue em outra data não conta em Entregues hoje")
    void pedidoEntregueEmOutraDataNaoContaHoje() {
        registrar(1001, ontem(), ontem(), Entregue.INSTANCIA);

        abrirPainel();

        assertEquals(0, view.getEntreguesHoje());
        assertEquals(0, view.getPedidosDia());
    }

    @Test
    @DisplayName("Cenário 3 - A barra de status apresenta usuário, login e tipo da sessão")
    void barraDeStatusApresentaDadosDaSessao() {
        abrirPainel();

        assertEquals("usuario01", view.getNomeUsuario());
        assertEquals("Administrador", view.getTipoPerfil());
        assertTrue(view.getLoginFormatado().matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}"));
    }

    @Test
    @DisplayName("O menu administrativo é habilitado apenas para o perfil Administrador")
    void menuAdministrativoRestritoAoAdministrador() {
        logarComo(Atendente.INSTANCIA);
        new PainelPrincipalPresenter(view, null, null, null, null,
                pedidoRepository, null, sessaoService, null);

        assertFalse(view.isMenuAdminHabilitado());
    }

    @Test
    @DisplayName("Cenário 4 - Visualizar um pedido inexistente informa o erro")
    void visualizarPedidoInexistenteInformaErro() {
        abrirPainel();

        PainelPrincipalPresenter presenter = new PainelPrincipalPresenter(
                view, null, null, null, null, pedidoRepository, null, sessaoService, null);
        presenter.onVisualizarPedido(9999);

        assertEquals("Pedido #9999 não foi encontrado.", view.getMensagemErro());
    }
}
