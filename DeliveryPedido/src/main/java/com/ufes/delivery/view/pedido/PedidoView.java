package com.ufes.delivery.view.pedido;

import com.ufes.delivery.presenter.pedido.PedidoPresenter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class PedidoView extends JFrame implements IPedidoView {

    private JTextField txtCliente;
    private JComboBox<String> cmbEnderecos;

    private JTable tabelaItens;
    private ItensTableModel tableModel;

    private JTextField txtCupom;
    private JLabel lblTotalDescontos;
    private JLabel lblDescontoTaxaEntrega;
    private JLabel lblTaxaEntregaFinal;
    private JLabel lblTotalPedido;

    private JButton btnPagar;

    private PedidoPresenter presenter;

    public PedidoView() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setTitle("Pedido");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(780, 620));

        JPanel painelPrincipal = new JPanel(new BorderLayout(8, 8));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelDados = new JPanel(new GridBagLayout());
        painelDados.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Dados do Pedido",
                TitledBorder.LEFT, TitledBorder.TOP));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        painelDados.add(new JLabel("Cliente:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtCliente = new JTextField(30);
        painelDados.add(txtCliente, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JButton btnBuscarCliente = new JButton("Buscar");
        painelDados.add(btnBuscarCliente, gbc);

        gbc.gridx = 3;
        JButton btnNovoCliente = new JButton("Novo Cliente");
        painelDados.add(btnNovoCliente, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        painelDados.add(new JLabel("Endereço de entrega:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbEnderecos = new JComboBox<>();
        painelDados.add(cmbEnderecos, gbc);
        gbc.gridwidth = 1;

        painelPrincipal.add(painelDados, BorderLayout.NORTH);

        JPanel painelCentral = new JPanel(new BorderLayout(5, 5));

        JPanel painelTabelaTopo = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        JButton btnAdicionarItem = new JButton("Adicionar Item");
        painelTabelaTopo.add(btnAdicionarItem);
        painelCentral.add(painelTabelaTopo, BorderLayout.NORTH);

        tableModel = new ItensTableModel();
        tabelaItens = new JTable(tableModel);
        tabelaItens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaItens.setRowHeight(24);
        tabelaItens.getTableHeader().setReorderingAllowed(false);

        tabelaItens.getColumnModel().getColumn(0).setPreferredWidth(100);
        tabelaItens.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabelaItens.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabelaItens.getColumnModel().getColumn(3).setPreferredWidth(80);
        tabelaItens.getColumnModel().getColumn(4).setPreferredWidth(100);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tabelaItens.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tabelaItens.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tabelaItens.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        JScrollPane scrollTabela = new JScrollPane(tabelaItens);
        scrollTabela.setPreferredSize(new Dimension(0, 180));
        painelCentral.add(scrollTabela, BorderLayout.CENTER);

        JPanel painelRodape = new JPanel(new GridBagLayout());
        painelRodape.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 8, 3, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        painelRodape.add(new JLabel("Cupom de desconto:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        txtCupom = new JTextField(15);
        painelRodape.add(txtCupom, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JButton btnAplicarCupom = new JButton("Aplicar");
        painelRodape.add(btnAplicarCupom, gbc);

        gbc.gridx = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        painelRodape.add(Box.createHorizontalGlue(), gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        painelRodape.add(new JLabel("Total de descontos:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        lblTotalDescontos = new JLabel("R$ 0,00", SwingConstants.RIGHT);
        painelRodape.add(lblTotalDescontos, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        painelRodape.add(new JLabel("Desconto na taxa de entrega:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        lblDescontoTaxaEntrega = new JLabel("R$ 0,00", SwingConstants.RIGHT);
        painelRodape.add(lblDescontoTaxaEntrega, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        painelRodape.add(new JLabel("Taxa de entrega final:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        lblTaxaEntregaFinal = new JLabel("R$ 0,00", SwingConstants.RIGHT);
        painelRodape.add(lblTaxaEntregaFinal, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        JLabel lblTotalPedidoLabel = new JLabel("Total do pedido:");
        lblTotalPedidoLabel.setFont(lblTotalPedidoLabel.getFont().deriveFont(Font.BOLD, 14f));
        painelRodape.add(lblTotalPedidoLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        lblTotalPedido = new JLabel("R$ 0,00", SwingConstants.RIGHT);
        lblTotalPedido.setFont(lblTotalPedido.getFont().deriveFont(Font.BOLD, 14f));
        painelRodape.add(lblTotalPedido, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(10, 8, 3, 8);
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPagar = new JButton("Pagar");
        JButton btnCancelar = new JButton("Cancelar");
        painelBotoes.add(btnPagar);
        painelBotoes.add(btnCancelar);
        painelRodape.add(painelBotoes, gbc);

        painelCentral.add(painelRodape, BorderLayout.SOUTH);
        painelPrincipal.add(painelCentral, BorderLayout.CENTER);

        txtCliente.addActionListener(e -> {
            if (presenter != null) presenter.onBuscarCliente();
        });
        btnBuscarCliente.addActionListener(e -> {
            if (presenter != null) presenter.onBuscarCliente();
        });
        btnNovoCliente.addActionListener(e -> {
            if (presenter != null) presenter.onNovoCliente();
        });
        cmbEnderecos.addActionListener(e -> {
            if (presenter != null) presenter.onEnderecoAlterado();
        });
        btnAdicionarItem.addActionListener(e -> {
            if (presenter != null) presenter.onAdicionarItem();
        });
        btnAplicarCupom.addActionListener(e -> {
            if (presenter != null) presenter.onAplicarCupom();
        });
        btnPagar.addActionListener(e -> {
            if (presenter != null) presenter.onPagar();
        });
        btnCancelar.addActionListener(e -> {
            if (presenter != null) presenter.onCancelar();
        });

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuExcluir = new JMenuItem("Excluir");
        menuExcluir.addActionListener(e -> {
            if (presenter != null) presenter.onRemoverItem();
        });
        popupMenu.add(menuExcluir);

        tabelaItens.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mostrarPopupSeNecessario(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mostrarPopupSeNecessario(e);
            }

            private void mostrarPopupSeNecessario(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int linha = tabelaItens.rowAtPoint(e.getPoint());
                    if (linha >= 0) {
                        tabelaItens.setRowSelectionInterval(linha, linha);
                        popupMenu.show(tabelaItens, e.getX(), e.getY());
                    }
                }
            }
        });

        tableModel.addTableModelListener(e -> {
            if (e.getColumn() == 3 && presenter != null) {
                int linha = e.getFirstRow();
                String novaQtd = tableModel.getValueAt(linha, 3).toString();
                presenter.onQuantidadeAlterada(linha, novaQtd);
            }
        });

        setContentPane(painelPrincipal);
        pack();
        setLocationRelativeTo(null);
    }

    public void setPresenter(PedidoPresenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public String getTextoCliente() {
        return txtCliente.getText();
    }

    @Override
    public void setTextoCliente(String nome) {
        txtCliente.setText(nome);
    }

    @Override
    public void carregarEnderecos(List<String> enderecos) {
        cmbEnderecos.removeAllItems();
        for (String endereco : enderecos) {
            cmbEnderecos.addItem(endereco);
        }
    }

    @Override
    public int getEnderecoSelecionadoIndex() {
        return cmbEnderecos.getSelectedIndex();
    }

    @Override
    public void setEnderecoSelecionadoIndex(int index) {
        if (index >= 0 && index < cmbEnderecos.getItemCount()) {
            cmbEnderecos.setSelectedIndex(index);
        }
    }

    @Override
    public void carregarItens(List<String[]> dados) {
        tableModel.setDados(dados);
    }

    @Override
    public int getLinhaSelecionada() {
        return tabelaItens.getSelectedRow();
    }

    @Override
    public String getQuantidadeNaLinha(int linha) {
        return tableModel.getValueAt(linha, 3).toString();
    }

    @Override
    public String getCodigoCupom() {
        return txtCupom.getText();
    }

    @Override
    public void setCodigoCupom(String codigo) {
        txtCupom.setText(codigo);
    }

    @Override
    public void setTotalDescontos(String valor) {
        lblTotalDescontos.setText(valor);
    }

    @Override
    public void setDescontoTaxaEntrega(String valor) {
        lblDescontoTaxaEntrega.setText(valor);
    }

    @Override
    public void setTaxaEntregaFinal(String valor) {
        lblTaxaEntregaFinal.setText(valor);
    }

    @Override
    public void setTotalPedido(String valor) {
        lblTotalPedido.setText(valor);
    }

    @Override
    public void exibirMensagemErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void exibirMensagemSucesso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public String exibirInputDialog(String mensagem) {
        return JOptionPane.showInputDialog(this, mensagem);
    }

    @Override
    public int exibirConfirmDialog(String mensagem, String titulo) {
        return JOptionPane.showConfirmDialog(this, mensagem, titulo,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    @Override
    public Object exibirSelecaoDialog(String mensagem, String titulo, Object[] opcoes) {
        return JOptionPane.showInputDialog(this, mensagem, titulo,
                JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);
    }

    @Override
    public void fechar() {
        dispose();
    }

    @Override
    public void exibir() {
        setVisible(true);
        txtCliente.requestFocusInWindow();
    }

    private static class ItensTableModel extends AbstractTableModel {
        private static final String[] COLUNAS = {
            "Categoria", "Item", "Preço unitário", "Quantidade", "Preço total"
        };
        private final List<String[]> dados = new ArrayList<>();

        void setDados(List<String[]> novosDados) {
            dados.clear();
            dados.addAll(novosDados);
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return dados.size();
        }

        @Override
        public int getColumnCount() {
            return COLUNAS.length;
        }

        @Override
        public String getColumnName(int col) {
            return COLUNAS[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            return dados.get(row)[col];
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return col == 3;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            if (col == 3 && row < dados.size()) {
                dados.get(row)[col] = value.toString();
                fireTableCellUpdated(row, col);
            }
        }
    }
}

