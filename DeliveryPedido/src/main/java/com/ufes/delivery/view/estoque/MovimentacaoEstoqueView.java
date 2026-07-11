package com.ufes.delivery.view.estoque;

import com.ufes.delivery.presenter.estoque.MovimentacaoEstoquePresenter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoEstoqueView extends JFrame implements IMovimentacaoEstoqueView {

    private JTextField txtBuscaProduto;
    private JTable tabelaProdutos;
    private ProdutosTableModel tableModel;

    private JTextField txtProdutoSelecionado;
    private JTextField txtEstoqueAtual;

    private JTextField txtDataMovimentacao;
    private JComboBox<String> cmbTipoMovimentacao;
    private JTextField txtQuantidade;
    private JTextField txtMotivo;
    private JTextField txtEstoquePrevia;
    private JTextField txtNotaFiscal;
    private JLabel lblMotivo;
    private JLabel lblNotaFiscal;
    private JButton btnConfirmar;

    private MovimentacaoEstoquePresenter presenter;

    public MovimentacaoEstoqueView() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setTitle("Movimentação de Estoque");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(700, 520));

        JPanel painelPrincipal = new JPanel(new BorderLayout(8, 8));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelBusca = new JPanel(new BorderLayout(5, 5));
        painelBusca.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Busca de Produtos",
                TitledBorder.LEFT, TitledBorder.TOP));

        JPanel painelCampoBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 3));
        painelCampoBusca.add(new JLabel("Buscar produto"));
        txtBuscaProduto = new JTextField(25);
        painelCampoBusca.add(txtBuscaProduto);
        JButton btnBuscar = new JButton("Buscar");
        painelCampoBusca.add(btnBuscar);
        painelBusca.add(painelCampoBusca, BorderLayout.NORTH);

        tableModel = new ProdutosTableModel();
        tabelaProdutos = new JTable(tableModel);
        tabelaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaProdutos.setRowHeight(22);
        tabelaProdutos.getTableHeader().setReorderingAllowed(false);
        tabelaProdutos.getColumnModel().getColumn(0).setPreferredWidth(55);
        tabelaProdutos.getColumnModel().getColumn(0).setMaxWidth(65);
        tabelaProdutos.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabelaProdutos.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabelaProdutos.getColumnModel().getColumn(3).setPreferredWidth(80);

        JScrollPane scrollTabela = new JScrollPane(tabelaProdutos);
        scrollTabela.setPreferredSize(new Dimension(0, 80));
        painelBusca.add(scrollTabela, BorderLayout.CENTER);

        JPanel painelSelecionar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 3));
        JButton btnSelecionar = new JButton("Selecionar");
        painelSelecionar.add(btnSelecionar);
        painelBusca.add(painelSelecionar, BorderLayout.SOUTH);

        painelPrincipal.add(painelBusca, BorderLayout.NORTH);

        JPanel painelCentral = new JPanel(new BorderLayout(5, 5));

        JPanel painelProduto = new JPanel(new GridBagLayout());
        painelProduto.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Produto Selecionado",
                TitledBorder.LEFT, TitledBorder.TOP));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 10, 3, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        painelProduto.add(new JLabel("Produto"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtProdutoSelecionado = new JTextField(25);
        txtProdutoSelecionado.setEditable(false);
        painelProduto.add(txtProdutoSelecionado, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        painelProduto.add(new JLabel("Quantidade atual em estoque"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.3;
        txtEstoqueAtual = new JTextField(8);
        txtEstoqueAtual.setEditable(false);
        painelProduto.add(txtEstoqueAtual, gbc);

        painelCentral.add(painelProduto, BorderLayout.NORTH);

        JPanel painelMov = new JPanel(new GridBagLayout());
        painelMov.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Movimentação",
                TitledBorder.LEFT, TitledBorder.TOP));

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 10, 3, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        painelMov.add(new JLabel("Data da movimentação"), gbc);
        gbc.gridx = 1;
        txtDataMovimentacao = new JTextField(10);
        txtDataMovimentacao.setText(
            LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        painelMov.add(txtDataMovimentacao, gbc);

        gbc.gridx = 2;
        painelMov.add(new JLabel("Tipo de movimentação"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbTipoMovimentacao = new JComboBox<>(
            new String[]{"Ajuste de estoque", "Entrada"});
        painelMov.add(cmbTipoMovimentacao, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        painelMov.add(new JLabel("Quantidade a movimentar"), gbc);
        gbc.gridx = 1;
        txtQuantidade = new JTextField(8);
        painelMov.add(txtQuantidade, gbc);

        gbc.gridx = 2;
        lblMotivo = new JLabel("Motivo do ajuste");
        painelMov.add(lblMotivo, gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtMotivo = new JTextField(20);
        painelMov.add(txtMotivo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        painelMov.add(new JLabel("Estoque após movimentação (prévia)"), gbc);
        gbc.gridx = 1;
        txtEstoquePrevia = new JTextField(8);
        txtEstoquePrevia.setEditable(false);
        painelMov.add(txtEstoquePrevia, gbc);

        gbc.gridx = 2;
        lblNotaFiscal = new JLabel("Nota fiscal de entrada");
        painelMov.add(lblNotaFiscal, gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNotaFiscal = new JTextField(20);
        painelMov.add(txtNotaFiscal, gbc);

        painelCentral.add(painelMov, BorderLayout.CENTER);

        JPanel painelInferior = new JPanel(new BorderLayout(5, 5));
        JLabel lblInfo = new JLabel(
            "<html><i>Pré-visualização, a atualização definitiva ocorrerá " +
            "após a confirmação da movimentação.<br>" +
            "Ajustes de estoque requerem motivo. " +
            "Entradas requerem número da nota fiscal.</i></html>");
        lblInfo.setFont(lblInfo.getFont().deriveFont(Font.PLAIN, 11f));
        lblInfo.setForeground(Color.BLUE);
        painelInferior.add(lblInfo, BorderLayout.NORTH);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnConfirmar = new JButton("Confirmar movimentação");
        JButton btnCancelar = new JButton("Cancelar");
        painelBotoes.add(btnConfirmar);
        painelBotoes.add(btnCancelar);
        painelInferior.add(painelBotoes, BorderLayout.CENTER);

        painelCentral.add(painelInferior, BorderLayout.SOUTH);
        painelPrincipal.add(painelCentral, BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> {
            if (presenter != null) presenter.onBuscarProduto();
        });
        txtBuscaProduto.addActionListener(e -> {
            if (presenter != null) presenter.onBuscarProduto();
        });
        btnSelecionar.addActionListener(e -> {
            if (presenter != null) presenter.onSelecionar();
        });
        tabelaProdutos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && presenter != null) {
                    presenter.onSelecionar();
                }
            }
        });
        cmbTipoMovimentacao.addActionListener(e -> {
            if (presenter != null) {
                presenter.onTipoMovimentacaoAlterado();
                presenter.onQuantidadeAlterada();
            }
        });
        txtQuantidade.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { atualizarPrevia(); }
            @Override public void removeUpdate(DocumentEvent e) { atualizarPrevia(); }
            @Override public void changedUpdate(DocumentEvent e) { atualizarPrevia(); }
        });
        btnConfirmar.addActionListener(e -> {
            if (presenter != null) presenter.onConfirmarMovimentacao();
        });
        btnCancelar.addActionListener(e -> {
            if (presenter != null) presenter.onCancelar();
        });

        setContentPane(painelPrincipal);
        pack();
        setLocationRelativeTo(null);
    }

    private void atualizarPrevia() {
        if (presenter != null) {
            presenter.onQuantidadeAlterada();
        }
    }

    public void setPresenter(MovimentacaoEstoquePresenter presenter) {
        this.presenter = presenter;
    }


    @Override public String getTextoBuscaProduto() { return txtBuscaProduto.getText(); }
    @Override public int getLinhaSelecionadaProduto() { return tabelaProdutos.getSelectedRow(); }

    @Override
    public int getCodigoNaLinha(int linha) {
        return Integer.parseInt((String) tableModel.getValueAt(linha, 0));
    }

    @Override
    public void carregarResultadosProdutos(List<String[]> dados) {
        tableModel.setDados(dados);
    }

    @Override
    public void setProdutoSelecionado(String nome, String estoqueAtual) {
        txtProdutoSelecionado.setText(nome);
        txtEstoqueAtual.setText(estoqueAtual);
    }

    @Override
    public void limparProdutoSelecionado() {
        txtProdutoSelecionado.setText("");
        txtEstoqueAtual.setText("");
    }

    @Override public String getDataMovimentacao() { return txtDataMovimentacao.getText(); }
    @Override public String getTipoMovimentacao() { return (String) cmbTipoMovimentacao.getSelectedItem(); }
    @Override public String getQuantidadeMovimentar() { return txtQuantidade.getText(); }
    @Override public String getMotivoAjuste() { return txtMotivo.getText(); }
    @Override public String getNotaFiscal() { return txtNotaFiscal.getText(); }

    @Override
    public void setEstoquePrevia(String previa) {
        txtEstoquePrevia.setText(previa);
    }

    @Override
    public void habilitarMovimentacao(boolean habilitar) {
        txtDataMovimentacao.setEditable(habilitar);
        cmbTipoMovimentacao.setEnabled(habilitar);
        txtQuantidade.setEditable(habilitar);
        txtMotivo.setEditable(habilitar);
        txtNotaFiscal.setEditable(habilitar);
        btnConfirmar.setEnabled(habilitar);
    }

    @Override
    public void setMotivoObrigatorio(boolean obrigatorio) {
        lblMotivo.setText(obrigatorio ? "Motivo do ajuste" : "Motivo (opcional)");
    }

    @Override
    public void setNotaFiscalObrigatorio(boolean obrigatorio) {
        lblNotaFiscal.setText(obrigatorio
            ? "Nota fiscal de entrada"
            : "Nota fiscal de entrada");
        txtNotaFiscal.setToolTipText(obrigatorio
            ? "Obrigatório para entradas"
            : "Opcional para ajustes");
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

    @Override public void fechar() { dispose(); }

    @Override
    public void exibir() {
        setVisible(true);
        txtBuscaProduto.requestFocusInWindow();
    }

    private static class ProdutosTableModel extends AbstractTableModel {
        private static final String[] COLUNAS = {
            "Código", "Produto", "Categoria", "Estoque atual"
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
        @Override public Object getValueAt(int row, int col) { return dados.get(row)[col]; }
        @Override public boolean isCellEditable(int row, int col) { return false; }
    }
}

