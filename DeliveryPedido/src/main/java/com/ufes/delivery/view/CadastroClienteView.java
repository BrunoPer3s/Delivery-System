package com.ufes.delivery.view;

import com.ufes.delivery.presenter.CadastroClientePresenter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CadastroClienteView extends JFrame implements ICadastroClienteView {

    private JTextField txtNome;
    private JTextField txtCpf;
    private JTable tabelaEnderecos;
    private EnderecosTableModel tableModel;
    private JLabel lblMensagem;

    private CadastroClientePresenter presenter;

    public CadastroClienteView() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setTitle("Cliente");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(700, 420));

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelDados = new JPanel(new GridBagLayout());
        painelDados.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Dados do Cliente",
                TitledBorder.LEFT, TitledBorder.TOP));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        painelDados.add(new JLabel("Nome"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNome = new JTextField(30);
        painelDados.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        painelDados.add(new JLabel("CPF"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtCpf = new JTextField(30);
        painelDados.add(txtCpf, gbc);

        painelPrincipal.add(painelDados, BorderLayout.NORTH);

        tableModel = new EnderecosTableModel();
        tabelaEnderecos = new JTable(tableModel);
        configurarTabelaEnderecos();

        JPanel painelEnderecos = new JPanel(new BorderLayout());
        painelEnderecos.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Endereços de Entrega",
                TitledBorder.LEFT, TitledBorder.TOP));
        painelEnderecos.add(new JScrollPane(tabelaEnderecos), BorderLayout.CENTER);

        painelPrincipal.add(painelEnderecos, BorderLayout.CENTER);

        JPanel painelInferior = new JPanel(new BorderLayout(5, 5));

        lblMensagem = new JLabel(" ");
        lblMensagem.setForeground(Color.RED);
        lblMensagem.setHorizontalAlignment(SwingConstants.CENTER);
        painelInferior.add(lblMensagem, BorderLayout.NORTH);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);
        painelInferior.add(painelBotoes, BorderLayout.CENTER);

        painelPrincipal.add(painelInferior, BorderLayout.SOUTH);

        btnSalvar.addActionListener(e -> {
            if (presenter != null) presenter.onSalvar();
        });
        btnCancelar.addActionListener(e -> {
            if (presenter != null) presenter.onCancelar();
        });

        setContentPane(painelPrincipal);
        pack();
        setLocationRelativeTo(null);
    }

    private void configurarTabelaEnderecos() {
        tabelaEnderecos.setRowHeight(25);
        tabelaEnderecos.getTableHeader().setReorderingAllowed(false);

        tabelaEnderecos.getColumnModel().getColumn(0).setPreferredWidth(45);
        tabelaEnderecos.getColumnModel().getColumn(0).setMaxWidth(55);
        tabelaEnderecos.getColumnModel().getColumn(1).setPreferredWidth(110);
        tabelaEnderecos.getColumnModel().getColumn(2).setPreferredWidth(50);
        tabelaEnderecos.getColumnModel().getColumn(3).setPreferredWidth(90);
        tabelaEnderecos.getColumnModel().getColumn(4).setPreferredWidth(80);
        tabelaEnderecos.getColumnModel().getColumn(5).setPreferredWidth(90);
        tabelaEnderecos.getColumnModel().getColumn(6).setPreferredWidth(30);
        tabelaEnderecos.getColumnModel().getColumn(7).setPreferredWidth(70);

        tabelaEnderecos.getColumnModel().getColumn(0).setCellRenderer(new RadioRenderer());
        tabelaEnderecos.getColumnModel().getColumn(0).setCellEditor(new RadioEditor());
    }

    public void setPresenter(CadastroClientePresenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public String getNome() { return txtNome.getText(); }

    @Override
    public String getCpf() { return txtCpf.getText(); }

    @Override
    public List<String[]> getEnderecos() {
        if (tabelaEnderecos.isEditing()) {
            tabelaEnderecos.getCellEditor().stopCellEditing();
        }
        List<String[]> enderecos = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            enderecos.add(new String[]{
                str(tableModel.getValueAt(i, 1)),
                str(tableModel.getValueAt(i, 2)),
                str(tableModel.getValueAt(i, 3)),
                str(tableModel.getValueAt(i, 4)),
                str(tableModel.getValueAt(i, 5)),
                str(tableModel.getValueAt(i, 6)),
                str(tableModel.getValueAt(i, 7))
            });
        }
        return enderecos;
    }

    private String str(Object obj) {
        return obj != null ? obj.toString() : "";
    }

    @Override
    public int getEnderecoPadraoIndex() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (Boolean.TRUE.equals(tableModel.getValueAt(i, 0))) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void setNome(String nome) { txtNome.setText(nome); }

    @Override
    public void setCpf(String cpf) { txtCpf.setText(cpf); }

    @Override
    public void setEnderecos(List<String[]> enderecos, int padraoIndex) {
        tableModel.setDados(enderecos, padraoIndex);
    }

    @Override
    public void setCpfEditavel(boolean editavel) { txtCpf.setEditable(editavel); }

    @Override
    public void exibirMensagemErro(String mensagem) {
        lblMensagem.setText(mensagem);
    }

    @Override
    public void exibirMensagemSucesso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void fechar() { dispose(); }

    @Override
    public void exibir() {
        setVisible(true);
        txtNome.requestFocusInWindow();
    }

    private class EnderecosTableModel extends AbstractTableModel {
        private static final String[] COLUNAS = {
            "Padrão", "Logradouro", "Número", "Complemento",
            "Bairro", "Cidade", "UF", "CEP"
        };
        private final Object[][] dados = new Object[3][8];

        EnderecosTableModel() {
            for (int i = 0; i < 3; i++) {
                dados[i][0] = (i == 0);
                for (int j = 1; j < 8; j++) dados[i][j] = "";
            }
        }

        void setDados(List<String[]> enderecos, int padraoIndex) {
            for (int i = 0; i < 3; i++) {
                dados[i][0] = (i == padraoIndex);
                if (i < enderecos.size()) {
                    String[] e = enderecos.get(i);
                    for (int j = 0; j < e.length && j < 7; j++) {
                        dados[i][j + 1] = e[j] != null ? e[j] : "";
                    }
                } else {
                    for (int j = 1; j < 8; j++) dados[i][j] = "";
                }
            }
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return 3; }
        @Override public int getColumnCount() { return COLUNAS.length; }
        @Override public String getColumnName(int col) { return COLUNAS[col]; }

        @Override
        public Class<?> getColumnClass(int col) {
            return col == 0 ? Boolean.class : String.class;
        }

        @Override
        public boolean isCellEditable(int row, int col) { return true; }

        @Override
        public Object getValueAt(int row, int col) { return dados[row][col]; }

        @Override
        public void setValueAt(Object value, int row, int col) {
            if (col == 0 && Boolean.TRUE.equals(value)) {
                for (int i = 0; i < 3; i++) dados[i][0] = false;
                dados[row][0] = true;
                fireTableDataChanged();
            } else {
                dados[row][col] = value;
                fireTableCellUpdated(row, col);
            }
        }
    }

    private static class RadioRenderer extends JRadioButton implements TableCellRenderer {
        RadioRenderer() {
            setHorizontalAlignment(CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            setSelected(Boolean.TRUE.equals(value));
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return this;
        }
    }

    private class RadioEditor extends AbstractCellEditor implements TableCellEditor {
        private final JRadioButton radioButton = new JRadioButton();

        RadioEditor() {
            radioButton.setHorizontalAlignment(SwingConstants.CENTER);
            radioButton.addActionListener(e -> {
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int col) {
            radioButton.setSelected(true);
            return radioButton;
        }

        @Override
        public Object getCellEditorValue() {
            return true;
        }
    }
}

