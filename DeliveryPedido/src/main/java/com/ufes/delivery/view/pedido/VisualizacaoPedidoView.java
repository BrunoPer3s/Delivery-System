package com.ufes.delivery.view.pedido;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class VisualizacaoPedidoView extends JFrame {

    public VisualizacaoPedidoView(int codigo, String cliente, String dataPedido,
                                  String dataConclusao, String estado, String valorTotal) {
        setTitle("Pedido #" + codigo);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12),
                BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(),
                        "Dados do Pedido",
                        TitledBorder.LEFT, TitledBorder.TOP)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;

        adicionarLinha(painel, gbc, 0, "Pedido", String.valueOf(codigo));
        adicionarLinha(painel, gbc, 1, "Cliente", cliente);
        adicionarLinha(painel, gbc, 2, "Data do pedido", dataPedido);
        adicionarLinha(painel, gbc, 3, "Data de conclusão",
                dataConclusao != null ? dataConclusao : "-");
        adicionarLinha(painel, gbc, 4, "Estado do pedido", estado);
        adicionarLinha(painel, gbc, 5, "Valor total", valorTotal);

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotao.add(btnFechar);

        JPanel conteudo = new JPanel(new BorderLayout());
        conteudo.add(painel, BorderLayout.CENTER);
        conteudo.add(painelBotao, BorderLayout.SOUTH);

        setContentPane(conteudo);
        pack();
        setMinimumSize(new Dimension(360, 280));
        setLocationRelativeTo(null);
    }

    private void adicionarLinha(JPanel painel, GridBagConstraints gbc, int linha,
                                String rotulo, String valor) {
        gbc.gridx = 0;
        gbc.gridy = linha;
        JLabel lblRotulo = new JLabel(rotulo + ":");
        lblRotulo.setFont(lblRotulo.getFont().deriveFont(Font.BOLD));
        painel.add(lblRotulo, gbc);

        gbc.gridx = 1;
        painel.add(new JLabel(valor != null ? valor : ""), gbc);
    }

    public void exibir() {
        setVisible(true);
    }
}

