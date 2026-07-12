package com.ufes.delivery;

import com.ufes.delivery.log.GerenciadorDeLogAtivo;
import com.ufes.delivery.persistencia.BancoDados;
import com.ufes.delivery.presenter.login.LoginPresenter;
import com.ufes.delivery.repository.cliente.ClienteRepositorySQLite;
import com.ufes.delivery.repository.cliente.IClienteRepository;
import com.ufes.delivery.repository.cupom.CupomRepositorySQLite;
import com.ufes.delivery.repository.cupom.ICupomRepository;
import com.ufes.delivery.repository.pagamento.ConfirmacaoPagamentoRepositorySQLite;
import com.ufes.delivery.repository.pagamento.IConfirmacaoPagamentoRepository;
import com.ufes.delivery.repository.pedido.PedidoRepositorySQLite;
import com.ufes.delivery.repository.produto.ProdutoRepositorySQLite;
import com.ufes.delivery.repository.usuario.IUsuarioRepository;
import com.ufes.delivery.repository.usuario.UsuarioRepositorySQLite;
import com.ufes.delivery.service.AutenticacaoService;
import com.ufes.delivery.service.SessaoService;
import com.ufes.delivery.service.SimuladorCicloPedidoService;
import com.ufes.delivery.view.login.LoginView;
import com.ufes.log.JsonlLogger;

import javax.swing.*;

public class DeliveryApplication {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Não foi possível configurar o Look and Feel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            GerenciadorDeLogAtivo logger = new GerenciadorDeLogAtivo(new JsonlLogger());

            BancoDados banco = new BancoDados();
            banco.inicializar();

            IUsuarioRepository usuarioRepository = new UsuarioRepositorySQLite(banco);
            IClienteRepository clienteRepository = new ClienteRepositorySQLite(banco);
            ProdutoRepositorySQLite produtoRepository = new ProdutoRepositorySQLite(banco);
            ICupomRepository cupomRepository = new CupomRepositorySQLite(banco);
            PedidoRepositorySQLite pedidoRepository = new PedidoRepositorySQLite(banco);

            IConfirmacaoPagamentoRepository confirmacaoPagamentoRepository =
                    new ConfirmacaoPagamentoRepositorySQLite(
                            banco, produtoRepository, pedidoRepository);

            AutenticacaoService autenticacaoService =
                    new AutenticacaoService(usuarioRepository, logger);
            SessaoService sessaoService = SessaoService.getInstancia();

            SimuladorCicloPedidoService cicloPedidoService =
                    new SimuladorCicloPedidoService(pedidoRepository, logger, sessaoService);
            cicloPedidoService.iniciar();

            LoginView loginView = new LoginView();
            LoginPresenter loginPresenter = new LoginPresenter(
                    loginView, autenticacaoService, sessaoService,
                    usuarioRepository, clienteRepository, produtoRepository,
                    cupomRepository, pedidoRepository, confirmacaoPagamentoRepository,
                    logger);
            loginView.setPresenter(loginPresenter);

            loginView.exibir();
        });
    }
}


