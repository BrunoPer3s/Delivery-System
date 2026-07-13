package com.ufes.delivery.view.produto;

import com.ufes.delivery.presenter.produto.BuscaProdutoPresenter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BuscaProdutoView extends JFrame implements IBuscaProdutoView {

    private JComboBox<String> cmbTipoBusca;
    private JTextField txtValor;
    private JTable tabelaResultados;
    private ResultadosTableModel tableModel;

    private BuscaProdutoPresenter presenter;

    public BuscaProdutoView() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setTitle("Produtos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(780, 430));

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        painelBusca.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Busca de Produtos",
                TitledBorder.LEFT, TitledBorder.TOP));

        painelBusca.add(new JLabel("Buscar por"));
        cmbTipoBusca = new JComboBox<>(new String[]{"Nome", "Código", "Categoria"});
        painelBusca.add(cmbTipoBusca);

        painelBusca.add(new JLabel("Valor"));
        txtValor = new JTextField(20);
        painelBusca.add(txtValor);

        JButton btnBuscar = new JButton("Buscar");
        painelBusca.add(btnBuscar);

        painelPrincipal.add(painelBusca, BorderLayout.NORTH);

        tableModel = new ResultadosTableModel();
        tabelaResultados = new JTable(tableModel);
        tabelaResultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaResultados.setRowHeight(28);
        tabelaResultados.getTableHeader().setReorderingAllowed(false);

        tabelaResultados.getColumnModel().getColumn(0).setPreferredWidth(60);
        tabelaResultados.getColumnModel().getColumn(0).setMaxWidth(70);
        tabelaResultados.getColumnModel().getColumn(1).setPreferredWidth(190);
        tabelaResultados.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabelaResultados.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabelaResultados.getColumnModel().getColumn(4).setPreferredWidth(80);
        tabelaResultados.getColumnModel().getColumn(5).setPreferredWidth(90);
        tabelaResultados.getColumnModel().getColumn(5).setMaxWidth(100);

        tabelaResultados.getColumnModel().getColumn(5)
                .setCellRenderer(new ButtonRenderer());
        tabelaResultados.getColumnModel().getColumn(5)
                .setCellEditor(new ButtonEditor());

        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Resultados",
                TitledBorder.LEFT, TitledBorder.TOP));
        painelTabela.add(new JScrollPane(tabelaResultados), BorderLayout.CENTER);

        painelPrincipal.add(painelTabela, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnNovo = new JButton("Novo");
        JButton btnVisualizar = new JButton("Visualizar");
        JButton btnFechar = new JButton("Fechar");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnVisualizar);
        painelBotoes.add(btnFechar);

        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        btnBuscar.addActionListener(e -> {
            if (presenter != null) presenter.onBuscar();
        });
        btnNovo.addActionListener(e -> {
            if (presenter != null) presenter.onNovo();
        });
        btnVisualizar.addActionListener(e -> {
            if (presenter != null) presenter.onVisualizar();
        });
        btnFechar.addActionListener(e -> {
            if (presenter != null) presenter.onFechar();
        });
        txtValor.addActionListener(e -> {
            if (presenter != null) presenter.onBuscar();
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (presenter != null) presenter.aoFecharJanela();
            }
        });

        setContentPane(painelPrincipal);
        pack();
        setLocationRelativeTo(null);
    }

    public void setPresenter(BuscaProdutoPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public String getTipoBusca() {
        return String.valueOf(cmbTipoBusca.getSelectedItem());
    }

    @Override
    public String getValorBusca() {
        return txtValor.getText();
    }

    @Override
    public int getLinhaSelecionada() {
        return tabelaResultados.getSelectedRow();
    }

    @Override
    public int getCodigoNaLinha(int linha) {
        return Integer.parseInt((String) tableModel.getValueAt(linha, 0));
    }

    @Override
    public void carregarResultados(List<String[]> dados) {
        tableModel.setDados(dados);
    }

    @Override
    public void exibirMensagemErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void exibirMensagemInfo(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Informação",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void fechar() { dispose(); }

    @Override
    public void exibir() {
        setVisible(true);
        txtValor.requestFocusInWindow();
    }

    private static class ResultadosTableModel extends AbstractTableModel {
        private static final String[] COLUNAS = {
            "Código", "Nome", "Categoria", "Preço unitário", "Estoque atual", "Ação"
        };
        private final List<String[]> dados = new ArrayList<>();

        void setDados(List<String[]> novosDados) {
            dados.clear();
            dados.addAll(novosDados);
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return dados.size(); }
        @Override public int getColumnCount() { return COLUNAS.length; }
        @Override public String getColumnName(int col) { return COLUNAS[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            if (col == 5) return "Visualizar";
            return dados.get(row)[col];
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return col == 5;
        }
    }

    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            setText(value != null ? value.toString() : "");
            return this;
        }
    }

    private class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton button = new JButton();
        private int linhaAtual;

        ButtonEditor() {
            button.setOpaque(true);
            button.addActionListener(e -> {
                fireEditingStopped();
                if (presenter != null) {
                    tabelaResultados.setRowSelectionInterval(linhaAtual, linhaAtual);
                    presenter.onVisualizar();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int col) {
            linhaAtual = row;
            button.setText(value != null ? value.toString() : "");
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Visualizar";
        }
    }
}

