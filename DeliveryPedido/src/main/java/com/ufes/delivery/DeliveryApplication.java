package com.ufes.delivery;

import com.ufes.delivery.log.GerenciadorDeLogAtivo;
import com.ufes.delivery.presenter.LoginPresenter;
import com.ufes.delivery.repository.ClienteRepositoryEmMemoria;
import com.ufes.delivery.repository.CupomRepositoryEmMemoria;
import com.ufes.delivery.repository.IClienteRepository;
import com.ufes.delivery.repository.ICupomRepository;
import com.ufes.delivery.repository.IPedidoRepository;
import com.ufes.delivery.repository.IProdutoRepository;
import com.ufes.delivery.repository.IUsuarioRepository;
import com.ufes.delivery.repository.PedidoRepositoryEmMemoria;
import com.ufes.delivery.repository.ProdutoRepositoryEmMemoria;
import com.ufes.delivery.repository.UsuarioRepositoryEmMemoria;
import com.ufes.delivery.service.AutenticacaoService;
import com.ufes.delivery.service.SessaoService;
import com.ufes.delivery.service.SimuladorCicloPedidoService;
import com.ufes.delivery.view.LoginView;
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

            IUsuarioRepository usuarioRepository = new UsuarioRepositoryEmMemoria();
            IClienteRepository clienteRepository = new ClienteRepositoryEmMemoria();
            IProdutoRepository produtoRepository = new ProdutoRepositoryEmMemoria();
            ICupomRepository cupomRepository = new CupomRepositoryEmMemoria();
            IPedidoRepository pedidoRepository = new PedidoRepositoryEmMemoria();

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
                    cupomRepository, pedidoRepository, logger);
            loginView.setPresenter(loginPresenter);

            loginView.exibir();
        });
    }
}


