package com.ufes.delivery.view.pedido;

import com.ufes.delivery.service.ResultadoPagamento;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class ResultadoPagamentoView extends JFrame {

    private static final DateTimeFormatter FORMATTER_DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ResultadoPagamentoView(ResultadoPagamento resultado, int codigoPedido,
                                   String nomeCliente, String enderecoEntrega,
                                   String totalFormatado) {
        inicializarComponentes(resultado, codigoPedido, nomeCliente,
                enderecoEntrega, totalFormatado);
    }

    private void inicializarComponentes(ResultadoPagamento resultado, int codigoPedido,
                                         String nomeCliente, String enderecoEntrega,
                                         String totalFormatado) {
        setTitle("Pagamento");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        Color corBanner;
        Color corBannerTexto;
        String textoBanner;
        String textoSubtitulo;

        if (resultado.isAprovado()) {
            corBanner = new Color(76, 175, 80);
            corBannerTexto = new Color(27, 94, 32);
            textoBanner = "Pagamento aprovado";
            textoSubtitulo = "Pedido pronto para entrega";
        } else {
            corBanner = new Color(244, 67, 54);
            corBannerTexto = new Color(183, 28, 28);
            textoBanner = "Pagamento reprovado";
            textoSubtitulo = "Pagamento não autorizado";
        }

        JPanel painelBanner = new JPanel();
        painelBanner.setLayout(new BoxLayout(painelBanner, BoxLayout.Y_AXIS));
        painelBanner.setBackground(corBanner);
        painelBanner.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        painelBanner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        painelBanner.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblBanner = new JLabel(textoBanner, SwingConstants.CENTER);
        lblBanner.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblBanner.setForeground(Color.WHITE);
        lblBanner.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelBanner.add(lblBanner);

        painelPrincipal.add(painelBanner);

        JPanel painelSubtitulo = new JPanel();
        painelSubtitulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        painelSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelSubtitulo.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel lblSubtitulo = new JLabel(textoSubtitulo, SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblSubtitulo.setForeground(Color.BLACK);
        painelSubtitulo.add(lblSubtitulo);

        painelPrincipal.add(Box.createVerticalStrut(5));
        painelPrincipal.add(painelSubtitulo);
        painelPrincipal.add(Box.createVerticalStrut(8));

        JPanel painelResumo = criarPainelSecao("Resumo do Pedido");

        adicionarCampo(painelResumo, "Pedido:", String.valueOf(codigoPedigo(codigoPedido)), null, false);
        adicionarCampo(painelResumo, "Cliente:", nomeCliente, null, false);
        adicionarCampo(painelResumo, "Endereço de entrega:", enderecoEntrega, null, false);
        adicionarCampo(painelResumo, "Total do pedido:", totalFormatado, null, true);

        painelPrincipal.add(painelResumo);
        painelPrincipal.add(Box.createVerticalStrut(5));

        JPanel painelPagamento = criarPainelSecao("Informações do Pagamento");

        String situacaoPagamento = resultado.isAprovado() ? "Aprovado" : "Reprovado";
        adicionarCampo(painelPagamento, "Situação do pagamento:",
                situacaoPagamento, corBannerTexto, false);
        adicionarCampo(painelPagamento, "Forma de pagamento:",
                resultado.getFormaPagamento(), null, false);
        adicionarCampo(painelPagamento, "Data e hora do pagamento:",
                resultado.getDataHoraPagamento().format(FORMATTER_DATA_HORA), null, false);
        adicionarCampo(painelPagamento, "Identificador da transação:",
                resultado.getIdentificadorTransacao(), null, false);

        adicionarCampo(painelPagamento, "Valor pago:", totalFormatado, corBannerTexto, true);

        painelPrincipal.add(painelPagamento);
        painelPrincipal.add(Box.createVerticalStrut(5));

        JPanel painelEntrega = criarPainelSecao("Entrega");

        if (resultado.isAprovado()) {
            adicionarCampo(painelEntrega, "Situação do pedido:",
                    "Pronto para entrega", corBannerTexto, true);
            if (resultado.getPrevisaoEntrega() != null) {
                adicionarCampo(painelEntrega, "Prazo estimado de entrega:",
                        resultado.getPrevisaoEntrega().format(FORMATTER_DATA_HORA),
                        null, true);
            }
            adicionarCampo(painelEntrega, "Observação:",
                    "Prazo gerado de forma simulada para o MVP", null, false);
        } else {
            adicionarCampo(painelEntrega, "Situação do pedido:",
                    "Aguardando pagamento", corBannerTexto, true);
            adicionarCampo(painelEntrega, "Observação:",
                    "O pedido foi preservado para nova tentativa de pagamento.", null, false);
        }

        painelPrincipal.add(painelEntrega);
        painelPrincipal.add(Box.createVerticalStrut(10));

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        painelBotao.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelBotao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        painelBotao.add(btnFechar);
        painelPrincipal.add(painelBotao);

        JScrollPane scrollPane = new JScrollPane(painelPrincipal);
        scrollPane.setBorder(null);
        setContentPane(scrollPane);
        setPreferredSize(new Dimension(620, 480));
        pack();
        setLocationRelativeTo(null);
    }

    private String codigoPedigo(int codigo) {
        return String.valueOf(codigo);
    }

    private JPanel criarPainelSecao(String titulo) {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                titulo,
                TitledBorder.LEFT, TitledBorder.TOP));
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        return painel;
    }

    private void adicionarCampo(JPanel painel, String label, String valor,
                                 Color corValor, boolean negrito) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 8, 2, 8);
        gbc.anchor = GridBagConstraints.WEST;

        int linhaAtual = painel.getComponentCount() / 2;

        gbc.gridx = 0;
        gbc.gridy = linhaAtual;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel lblLabel = new JLabel(label);
        if (corValor != null) {
            lblLabel.setForeground(corValor);
        }
        painel.add(lblLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblValor = new JLabel(valor);
        if (negrito) {
            lblValor.setFont(lblValor.getFont().deriveFont(Font.BOLD));
        }
        if (corValor != null) {
            lblValor.setForeground(corValor);
            lblValor.setFont(lblValor.getFont().deriveFont(Font.BOLD));
        }
        painel.add(lblValor, gbc);
    }

    public void exibir() {
        setVisible(true);
    }
}

