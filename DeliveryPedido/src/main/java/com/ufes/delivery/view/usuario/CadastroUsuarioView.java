package com.ufes.delivery.view.usuario;

import com.ufes.delivery.presenter.usuario.CadastroUsuarioPresenter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class CadastroUsuarioView extends JFrame implements ICadastroUsuarioView {

    private JTextField txtNome;
    private JTextField txtNomeUsuario;
    private JPasswordField txtSenha;
    private JPasswordField txtConfirmarSenha;
    private JLabel lblMensagem;
    private JButton btnSalvar;
    private JButton btnCancelar;

    private CadastroUsuarioPresenter presenter;

    public CadastroUsuarioView() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setTitle("Cadastro de Usuário");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel painelDados = new JPanel(new GridBagLayout());
        painelDados.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Dados do Usuário",
                TitledBorder.LEFT,
                TitledBorder.TOP));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        painelDados.add(new JLabel("Nome"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNome = new JTextField(25);
        painelDados.add(txtNome, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        painelDados.add(new JLabel("Nome de usuário"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNomeUsuario = new JTextField(25);
        painelDados.add(txtNomeUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        painelDados.add(new JLabel("Senha"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtSenha = new JPasswordField(25);
        painelDados.add(txtSenha, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        painelDados.add(new JLabel("Confirmar senha"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtConfirmarSenha = new JPasswordField(25);
        painelDados.add(txtConfirmarSenha, gbc);

        painelPrincipal.add(painelDados, BorderLayout.CENTER);

        JPanel painelInferior = new JPanel(new BorderLayout(5, 8));

        lblMensagem = new JLabel(" ");
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

        getRootPane().setDefaultButton(btnSalvar);

        setContentPane(painelPrincipal);
        pack();
        setMinimumSize(new Dimension(460, 280));
        setLocationRelativeTo(null);
    }

    public void setPresenter(CadastroUsuarioPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public String getNome() {
        return txtNome.getText();
    }

    @Override
    public String getNomeUsuario() {
        return txtNomeUsuario.getText();
    }

    @Override
    public String getSenha() {
        return new String(txtSenha.getPassword());
    }

    @Override
    public String getConfirmarSenha() {
        return new String(txtConfirmarSenha.getPassword());
    }

    @Override
    public void exibirMensagemErro(String mensagem) {
        lblMensagem.setForeground(Color.RED);
        lblMensagem.setText(mensagem);
    }

    @Override
    public void exibirMensagemSucesso(String mensagem) {
        lblMensagem.setForeground(new Color(0, 128, 0));
        lblMensagem.setText(mensagem);
        JOptionPane.showMessageDialog(this, mensagem, "Cadastro realizado",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void limparMensagem() {
        lblMensagem.setText(" ");
    }

    @Override
    public void limparCampos() {
        txtNome.setText("");
        txtNomeUsuario.setText("");
        txtSenha.setText("");
        txtConfirmarSenha.setText("");
        limparMensagem();
    }

    @Override
    public void fechar() {
        dispose();
    }

    @Override
    public void exibir() {
        setVisible(true);
        txtNome.requestFocusInWindow();
    }
}

