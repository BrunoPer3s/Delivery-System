package com.ufes.delivery.view.painel;

import com.ufes.delivery.presenter.painel.PainelPrincipalPresenter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PainelPrincipalView extends JFrame implements IPainelPrincipalView {

    private JLabel lblDataOperacao;
    private final JLabel[] lblMetricaValores = new JLabel[7];
    private JTable tabelaPedidos;
    private PedidosTableModel tableModel;
    private JLabel lblUsuarioLogado;
    private JLabel lblLoginInfo;
    private JLabel lblTipoPerfil;
    private JMenu menuAdmin;

    private PainelPrincipalPresenter presenter;

    public PainelPrincipalView() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setTitle("Início");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(820, 580));

        JPanel painelPrincipal = new JPanel(new BorderLayout(0, 8));

        setJMenuBar(criarMenuBar());

        JPanel painelSuperior = new JPanel(new BorderLayout(0, 10));
        painelSuperior.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 10));

        lblDataOperacao = new JLabel("Data de operação: --/--/----", SwingConstants.CENTER);
        lblDataOperacao.setFont(lblDataOperacao.getFont().deriveFont(Font.BOLD, 14f));
        lblDataOperacao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        lblDataOperacao.setOpaque(true);
        lblDataOperacao.setBackground(new Color(245, 245, 220));

        JPanel painelData = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelData.add(lblDataOperacao);
        painelSuperior.add(painelData, BorderLayout.NORTH);

        painelSuperior.add(criarPainelCards(), BorderLayout.CENTER);

        painelPrincipal.add(painelSuperior, BorderLayout.NORTH);

        tableModel = new PedidosTableModel();
        tabelaPedidos = new JTable(tableModel);
        configurarTabelaPedidos();

        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 10, 5, 10),
                BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(),
                        "Lista de Pedidos",
                        TitledBorder.LEFT, TitledBorder.TOP)));
        painelTabela.add(new JScrollPane(tabelaPedidos), BorderLayout.CENTER);

        painelPrincipal.add(painelTabela, BorderLayout.CENTER);

        painelPrincipal.add(criarBarraStatus(), BorderLayout.SOUTH);

        setContentPane(painelPrincipal);
        pack();
        setLocationRelativeTo(null);
    }


    private JMenuBar criarMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuOperacao = new JMenu("Operação");

        JMenuItem miNovoPedido = new JMenuItem("Novo pedido");
        JMenuItem miBuscarProdutos = new JMenuItem("Buscar produtos");
        JMenuItem miNovoProduto = new JMenuItem("Novo produto");
        JMenuItem miMovEstoque = new JMenuItem("Movimentação de estoque");
        JMenuItem miNovoCliente = new JMenuItem("Novo cliente");
        JMenuItem miBuscarClientes = new JMenuItem("Buscar clientes");

        miNovoPedido.addActionListener(e -> {
            if (presenter != null) presenter.onNovoPedido();
        });
        miBuscarProdutos.addActionListener(e -> {
            if (presenter != null) presenter.onBuscarProdutos();
        });
        miNovoProduto.addActionListener(e -> {
            if (presenter != null) presenter.onNovoProduto();
        });
        miMovEstoque.addActionListener(e -> {
            if (presenter != null) presenter.onMovimentacaoEstoque();
        });
        miNovoCliente.addActionListener(e -> {
            if (presenter != null) presenter.onNovoCliente();
        });
        miBuscarClientes.addActionListener(e -> {
            if (presenter != null) presenter.onBuscarClientes();
        });

        menuOperacao.add(miNovoPedido);
        menuOperacao.add(miBuscarProdutos);
        menuOperacao.add(miNovoProduto);
        menuOperacao.add(miMovEstoque);
        menuOperacao.add(miNovoCliente);
        menuOperacao.add(miBuscarClientes);

        menuBar.add(menuOperacao);

        menuAdmin = new JMenu("Administração");
        JMenuItem miGestaoUsuarios = new JMenuItem("Gestão de usuários");
        miGestaoUsuarios.addActionListener(e -> {
            if (presenter != null) presenter.onGestaoUsuarios();
        });
        menuAdmin.add(miGestaoUsuarios);
        menuAdmin.setVisible(false);

        menuBar.add(menuAdmin);

        return menuBar;
    }


    private JPanel criarPainelCards() {
        String[] titulos = {
            "Pedidos do dia", "Novos", "Aguardando pagamento", "Em preparo",
            "Aguardando entrega", "Em trânsito", "Entregues hoje"
        };

        JPanel painelCards = new JPanel(new GridLayout(2, 4, 8, 8));
        painelCards.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        for (int i = 0; i < 7; i++) {
            lblMetricaValores[i] = new JLabel("0", SwingConstants.CENTER);
            lblMetricaValores[i].setFont(
                    lblMetricaValores[i].getFont().deriveFont(Font.BOLD, 22f));
            painelCards.add(criarCard(titulos[i], lblMetricaValores[i]));
        }

        painelCards.add(new JLabel());

        return painelCards;
    }

    private JPanel criarCard(String titulo, JLabel valorLabel) {
        JPanel card = new JPanel(new BorderLayout(0, 2));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        card.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.PLAIN, 11f));
        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(valorLabel, BorderLayout.CENTER);

        return card;
    }


    private static final int COLUNA_PEDIDO = 0;
    private static final int COLUNA_ACAO = 6;

    private void configurarTabelaPedidos() {
        tabelaPedidos.getColumnModel().getColumn(0).setPreferredWidth(60);
        tabelaPedidos.getColumnModel().getColumn(1).setPreferredWidth(140);
        tabelaPedidos.getColumnModel().getColumn(2).setPreferredWidth(90);
        tabelaPedidos.getColumnModel().getColumn(3).setPreferredWidth(110);
        tabelaPedidos.getColumnModel().getColumn(4).setPreferredWidth(130);
        tabelaPedidos.getColumnModel().getColumn(5).setPreferredWidth(90);
        tabelaPedidos.getColumnModel().getColumn(6).setPreferredWidth(80);
        tabelaPedidos.setRowHeight(25);
        tabelaPedidos.getTableHeader().setReorderingAllowed(false);

        tabelaPedidos.getColumnModel().getColumn(COLUNA_ACAO)
                .setCellRenderer(new AcaoLinkRenderer());

        tabelaPedidos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int linha = tabelaPedidos.rowAtPoint(e.getPoint());
                int coluna = tabelaPedidos.columnAtPoint(e.getPoint());
                if (linha < 0 || coluna != COLUNA_ACAO || presenter == null) {
                    return;
                }
                Object valorCodigo = tableModel.getValueAt(linha, COLUNA_PEDIDO);
                try {
                    presenter.onVisualizarPedido(Integer.parseInt(valorCodigo.toString()));
                } catch (NumberFormatException ignored) {
                }
            }
        });

        tabelaPedidos.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int coluna = tabelaPedidos.columnAtPoint(e.getPoint());
                tabelaPedidos.setCursor(coluna == COLUNA_ACAO
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : Cursor.getDefaultCursor());
            }
        });
    }


    private JPanel criarBarraStatus() {
        JPanel statusBar = new JPanel(new GridLayout(1, 3));
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        statusBar.setBackground(new Color(240, 240, 240));

        lblUsuarioLogado = new JLabel("Usuário logado: —");
        lblUsuarioLogado.setFont(lblUsuarioLogado.getFont().deriveFont(Font.PLAIN, 11f));

        lblLoginInfo = new JLabel("Login: —");
        lblLoginInfo.setFont(lblLoginInfo.getFont().deriveFont(Font.PLAIN, 11f));
        lblLoginInfo.setHorizontalAlignment(SwingConstants.CENTER);

        lblTipoPerfil = new JLabel("Tipo: —");
        lblTipoPerfil.setFont(lblTipoPerfil.getFont().deriveFont(Font.PLAIN, 11f));
        lblTipoPerfil.setHorizontalAlignment(SwingConstants.RIGHT);

        statusBar.add(lblUsuarioLogado);
        statusBar.add(lblLoginInfo);
        statusBar.add(lblTipoPerfil);

        return statusBar;
    }


    public void setPresenter(PainelPrincipalPresenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public void setDataOperacao(String data) {
        lblDataOperacao.setText("Data de operação: " + data);
    }

    @Override
    public void setMetricas(int pedidosDia, int novos, int aguardandoPagamento,
                            int emPreparo, int aguardandoEntrega,
                            int emTransito, int entreguesHoje) {
        lblMetricaValores[0].setText(String.valueOf(pedidosDia));
        lblMetricaValores[1].setText(String.valueOf(novos));
        lblMetricaValores[2].setText(String.valueOf(aguardandoPagamento));
        lblMetricaValores[3].setText(String.valueOf(emPreparo));
        lblMetricaValores[4].setText(String.valueOf(aguardandoEntrega));
        lblMetricaValores[5].setText(String.valueOf(emTransito));
        lblMetricaValores[6].setText(String.valueOf(entreguesHoje));
    }

    @Override
    public void carregarPedidos(List<String[]> dados) {
        tableModel.setDados(dados);
    }

    @Override
    public void setInfoUsuario(String nomeUsuario, String loginFormatado,
                               String tipoPerfil) {
        lblUsuarioLogado.setText("Usuário logado: " + nomeUsuario);
        lblLoginInfo.setText("Login: " + loginFormatado);
        lblTipoPerfil.setText("Tipo: " + tipoPerfil);
    }

    @Override
    public void habilitarMenuAdmin(boolean habilitar) {
        menuAdmin.setVisible(habilitar);
    }

    @Override
    public void exibirMensagemErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void fechar() {
        dispose();
    }

    @Override
    public void exibir() {
        setVisible(true);
    }


    private static class AcaoLinkRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setForeground(new Color(0, 102, 204));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            return label;
        }
    }


    private static class PedidosTableModel extends AbstractTableModel {

        private static final String[] COLUNAS = {
            "Pedido", "Cliente", "Data do pedido", "Data de conclusão",
            "Estado do pedido", "Valor total", "Ação"
        };

        private final List<String[]> dados = new ArrayList<>();

        void setDados(List<String[]> novosDados) {
            dados.clear();
            dados.addAll(novosDados);
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() { return dados.size(); }

        @Override
        public int getColumnCount() { return COLUNAS.length; }

        @Override
        public String getColumnName(int col) { return COLUNAS[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            if (col < dados.get(row).length) {
                return dados.get(row)[col];
            }
            return "";
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    }
}

