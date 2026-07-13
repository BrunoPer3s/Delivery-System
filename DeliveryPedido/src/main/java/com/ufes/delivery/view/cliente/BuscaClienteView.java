package com.ufes.delivery.view.cliente;

import com.ufes.delivery.presenter.cliente.BuscaClientePresenter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BuscaClienteView extends JFrame implements IBuscaClienteView {

    private JComboBox<String> cmbTipoBusca;
    private JTextField txtValor;
    private JTable tabelaResultados;
    private ResultadosTableModel tableModel;

    private BuscaClientePresenter presenter;

    public BuscaClienteView() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setTitle("Clientes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(600, 420));

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        painelBusca.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Busca de Clientes",
                TitledBorder.LEFT, TitledBorder.TOP));

        painelBusca.add(new JLabel("Buscar por"));
        cmbTipoBusca = new JComboBox<>(new String[]{"Nome", "CPF"});
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
        tabelaResultados.setRowHeight(25);
        tabelaResultados.getTableHeader().setReorderingAllowed(false);

        tabelaResultados.getColumnModel().getColumn(0).setPreferredWidth(250);
        tabelaResultados.getColumnModel().getColumn(1).setPreferredWidth(150);

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
        tabelaResultados.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && presenter != null) {
                    presenter.onVisualizar();
                }
            }
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

    public void setPresenter(BuscaClientePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public String getTipoBusca() {
        return "Cliente " + cmbTipoBusca.getSelectedItem();
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
    public String getCpfNaLinha(int linha) {
        return (String) tableModel.getValueAt(linha, 1);
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
        private static final String[] COLUNAS = {"Nome", "CPF"};
        private final List<String[]> dados = new ArrayList<>();

        void setDados(List<String[]> novosDados) {
            dados.clear();
            dados.addAll(novosDados);
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return dados.size(); }
        @Override public int getColumnCount() { return COLUNAS.length; }
        @Override public String getColumnName(int col) { return COLUNAS[col]; }
        @Override public Object getValueAt(int row, int col) { return dados.get(row)[col]; }
    }
}

