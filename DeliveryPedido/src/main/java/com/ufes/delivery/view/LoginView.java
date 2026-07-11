package com.ufes.delivery.view;

import com.ufes.delivery.presenter.LoginPresenter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class LoginView extends JFrame implements ILoginView {

    private JTextField txtNomeUsuario;
    private JPasswordField txtSenha;
    private JLabel lblMensagemErro;
    private JButton btnAcessar;
    private JButton btnCancelar;
    private JButton btnCadastrar;

    private LoginPresenter presenter;

    public LoginView() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel painelDados = new JPanel(new GridBagLayout());
        painelDados.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Dados de Acesso",
                TitledBorder.LEFT,
                TitledBorder.TOP));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        painelDados.add(new JLabel("Nome de usuário"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNomeUsuario = new JTextField(20);
        painelDados.add(txtNomeUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        painelDados.add(new JLabel("Senha"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtSenha = new JPasswordField(20);
        painelDados.add(txtSenha, gbc);

        painelPrincipal.add(painelDados, BorderLayout.CENTER);

        JPanel painelInferior = new JPanel(new BorderLayout(5, 8));

        lblMensagemErro = new JLabel(" ");
        lblMensagemErro.setForeground(Color.RED);
        lblMensagemErro.setHorizontalAlignment(SwingConstants.CENTER);
        painelInferior.add(lblMensagemErro, BorderLayout.NORTH);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnAcessar = new JButton("Acessar");
        btnCancelar = new JButton("Cancelar");
        btnCadastrar = new JButton("Cadastrar usuário");

        painelBotoes.add(btnAcessar);
        painelBotoes.add(btnCancelar);
        painelBotoes.add(btnCadastrar);

        painelInferior.add(painelBotoes, BorderLayout.CENTER);
        painelPrincipal.add(painelInferior, BorderLayout.SOUTH);

        btnAcessar.addActionListener(e -> {
            if (presenter != null) presenter.onAcessar();
        });

        btnCancelar.addActionListener(e -> {
            if (presenter != null) presenter.onCancelar();
        });

        btnCadastrar.addActionListener(e -> {
            if (presenter != null) presenter.onCadastrarUsuario();
        });

        getRootPane().setDefaultButton(btnAcessar);

        setContentPane(painelPrincipal);
        pack();
        setMinimumSize(new Dimension(420, 220));
        setLocationRelativeTo(null);
    }

    public void setPresenter(LoginPresenter presenter) {
        this.presenter = presenter;
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
    public void exibirMensagemErro(String mensagem) {
        lblMensagemErro.setText(mensagem);
    }

    @Override
    public void limparMensagemErro() {
        lblMensagemErro.setText(" ");
    }

    @Override
    public void limparCampos() {
        txtNomeUsuario.setText("");
        txtSenha.setText("");
        limparMensagemErro();
    }

    @Override
    public void fechar() {
        dispose();
    }

    @Override
    public void exibir() {
        setVisible(true);
        txtNomeUsuario.requestFocusInWindow();
    }
}

