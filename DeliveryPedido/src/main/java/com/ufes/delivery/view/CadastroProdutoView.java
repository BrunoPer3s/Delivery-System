package com.ufes.delivery.view;

import com.ufes.delivery.model.Produto;
import com.ufes.delivery.presenter.CadastroProdutoPresenter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class CadastroProdutoView extends JFrame implements ICadastroProdutoView {

    private JTextField txtCodigo;
    private JTextField txtNome;
    private JComboBox<String> cmbCategoria;
    private JTextField txtPrecoUnitario;
    private JTextField txtQuantidadeEstoque;
    private JLabel lblEstoque;
    private JLabel lblMensagem;
    private JButton btnSalvar;
    private JButton btnCancelar;

    private CadastroProdutoPresenter presenter;

    public CadastroProdutoView() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setTitle("Produto");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(500, 320));

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelDados = new JPanel(new GridBagLayout());
        painelDados.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Dados do Produto",
                TitledBorder.LEFT, TitledBorder.TOP));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        painelDados.add(new JLabel("Código"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.3;
        txtCodigo = new JTextField(8);
        painelDados.add(txtCodigo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        painelDados.add(new JLabel("Nome"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNome = new JTextField(30);
        painelDados.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        painelDados.add(new JLabel("Categoria"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbCategoria = new JComboBox<>(Produto.CATEGORIAS);
        painelDados.add(cmbCategoria, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        painelDados.add(new JLabel("Preço unitário"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.3;
        txtPrecoUnitario = new JTextField(10);
        painelDados.add(txtPrecoUnitario, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        lblEstoque = new JLabel("Quantidade inicial em estoque");
        painelDados.add(lblEstoque, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.3;
        txtQuantidadeEstoque = new JTextField(10);
        painelDados.add(txtQuantidadeEstoque, gbc);

        painelPrincipal.add(painelDados, BorderLayout.CENTER);

        JPanel painelInferior = new JPanel(new BorderLayout(5, 5));

        lblMensagem = new JLabel(" ");
        lblMensagem.setForeground(Color.RED);
        lblMensagem.setHorizontalAlignment(SwingConstants.CENTER);
        painelInferior.add(lblMensagem, BorderLayout.NORTH);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");
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

    public void setPresenter(CadastroProdutoPresenter presenter) {
        this.presenter = presenter;
    }


    @Override public String getCodigo() { return txtCodigo.getText(); }
    @Override public String getNome() { return txtNome.getText(); }
    @Override public String getCategoria() { return (String) cmbCategoria.getSelectedItem(); }
    @Override public String getPrecoUnitario() { return txtPrecoUnitario.getText(); }
    @Override public String getQuantidadeEstoque() { return txtQuantidadeEstoque.getText(); }

    @Override public void setCodigo(String codigo) { txtCodigo.setText(codigo); }
    @Override public void setNome(String nome) { txtNome.setText(nome); }

    @Override
    public void setCategoria(String categoria) {
        cmbCategoria.setSelectedItem(categoria);
    }

    @Override public void setPrecoUnitario(String preco) { txtPrecoUnitario.setText(preco); }
    @Override public void setQuantidadeEstoque(String quantidade) { txtQuantidadeEstoque.setText(quantidade); }
    @Override public void setCodigoEditavel(boolean editavel) { txtCodigo.setEditable(editavel); }
    @Override public void setEstoqueEditavel(boolean editavel) { txtQuantidadeEstoque.setEditable(editavel); }
    @Override public void setLabelEstoque(String label) { lblEstoque.setText(label); }

    @Override
    public void setModoVisualizacao(boolean visualizacao) {
        if (visualizacao) {
            txtCodigo.setEditable(false);
            txtNome.setEditable(false);
            cmbCategoria.setEnabled(false);
            txtPrecoUnitario.setEditable(false);
            txtQuantidadeEstoque.setEditable(false);
            btnSalvar.setVisible(false);
            btnCancelar.setText("Fechar");
        }
    }

    @Override
    public void exibirMensagemErro(String mensagem) {
        lblMensagem.setText(mensagem);
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
        if (txtCodigo.isEditable()) {
            txtCodigo.requestFocusInWindow();
        } else {
            txtNome.requestFocusInWindow();
        }
    }
}

