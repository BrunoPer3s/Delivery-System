package com.ufes.delivery.view;

import com.ufes.delivery.presenter.GestaoUsuarioPresenter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GestaoUsuarioView extends JFrame implements IGestaoUsuarioView {

    private JTextField txtBusca;
    private JTable tabelaUsuarios;
    private UsuariosTableModel tableModel;

    private GestaoUsuarioPresenter presenter;

    public GestaoUsuarioView() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setTitle("Usuários");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(750, 450));

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        painelBusca.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Busca de Usuários",
                TitledBorder.LEFT, TitledBorder.TOP));

        painelBusca.add(new JLabel("Nome"));
        txtBusca = new JTextField(25);
        painelBusca.add(txtBusca);
        JButton btnBuscar = new JButton("Buscar");
        painelBusca.add(btnBuscar);

        painelPrincipal.add(painelBusca, BorderLayout.NORTH);

        tableModel = new UsuariosTableModel();
        tabelaUsuarios = new JTable(tableModel);
        configurarTabela();

        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Usuários",
                TitledBorder.LEFT, TitledBorder.TOP));
        painelTabela.add(new JScrollPane(tabelaUsuarios), BorderLayout.CENTER);

        painelPrincipal.add(painelTabela, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnAutorizar = new JButton("Autorizar");
        JButton btnDesautorizar = new JButton("Desautorizar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnNovo = new JButton("Novo");
        JButton btnFechar = new JButton("Fechar");

        painelBotoes.add(btnAutorizar);
        painelBotoes.add(btnDesautorizar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnNovo);
        painelBotoes.add(btnFechar);

        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        btnBuscar.addActionListener(e -> {
            if (presenter != null) presenter.onBuscar();
        });
        btnAutorizar.addActionListener(e -> {
            if (presenter != null) presenter.onAutorizar();
        });
        btnDesautorizar.addActionListener(e -> {
            if (presenter != null) presenter.onDesautorizar();
        });
        btnExcluir.addActionListener(e -> {
            if (presenter != null) presenter.onExcluir();
        });
        btnNovo.addActionListener(e -> {
            if (presenter != null) presenter.onNovo();
        });
        btnFechar.addActionListener(e -> {
            if (presenter != null) presenter.onFechar();
        });

        txtBusca.addActionListener(e -> {
            if (presenter != null) presenter.onBuscar();
        });

        setContentPane(painelPrincipal);
        pack();
        setLocationRelativeTo(null);
    }

    private void configurarTabela() {
        tabelaUsuarios.getColumnModel().getColumn(0).setPreferredWidth(30);
        tabelaUsuarios.getColumnModel().getColumn(0).setMaxWidth(40);
        tabelaUsuarios.getColumnModel().getColumn(1).setPreferredWidth(120);
        tabelaUsuarios.getColumnModel().getColumn(2).setPreferredWidth(180);
        tabelaUsuarios.getColumnModel().getColumn(3).setPreferredWidth(70);
        tabelaUsuarios.getColumnModel().getColumn(3).setMaxWidth(80);
        tabelaUsuarios.getColumnModel().getColumn(4).setPreferredWidth(120);
        tabelaUsuarios.getColumnModel().getColumn(5).setPreferredWidth(110);

        JComboBox<String> comboPerfil = new JComboBox<>(
                new String[]{"Administrador", "Atendente"});
        tabelaUsuarios.getColumnModel().getColumn(4)
                .setCellEditor(new DefaultCellEditor(comboPerfil));

        tabelaUsuarios.setRowHeight(25);
        tabelaUsuarios.getTableHeader().setReorderingAllowed(false);
    }

    public void setPresenter(GestaoUsuarioPresenter presenter) {
        this.presenter = presenter;
        tableModel.setPresenter(presenter);
    }


    @Override
    public String getTermoBusca() {
        return txtBusca.getText();
    }

    @Override
    public List<String> getNomesUsuariosSelecionados() {
        List<String> selecionados = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean selecionado = (Boolean) tableModel.getValueAt(i, 0);
            if (selecionado != null && selecionado) {
                selecionados.add((String) tableModel.getValueAt(i, 1));
            }
        }
        return selecionados;
    }

    @Override
    public void carregarUsuarios(List<String[]> dados) {
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
    public boolean confirmarExclusao(int quantidade) {
        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir " + quantidade + " usuário(s)?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        return resposta == JOptionPane.YES_OPTION;
    }

    @Override
    public void fechar() {
        dispose();
    }

    @Override
    public void exibir() {
        setVisible(true);
    }


    private static class UsuariosTableModel extends AbstractTableModel {

        private static final String[] COLUNAS = {
            "Sel.", "Nome de usuário", "Nome", "Autorizado", "Perfil", "Situação"
        };

        private final List<Object[]> dados = new ArrayList<>();
        private GestaoUsuarioPresenter presenter;

        void setPresenter(GestaoUsuarioPresenter presenter) {
            this.presenter = presenter;
        }

        void setDados(List<String[]> novosDados) {
            dados.clear();
            for (String[] d : novosDados) {
                dados.add(new Object[]{
                    Boolean.FALSE,
                    d[0],
                    d[1],
                    Boolean.parseBoolean(d[2]),
                    d[3],
                    d[4]
                });
            }
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
        public Class<?> getColumnClass(int col) {
            return switch (col) {
                case 0, 3 -> Boolean.class;
                default -> String.class;
            };
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return col == 0 || col == 4;
        }

        @Override
        public Object getValueAt(int row, int col) {
            return dados.get(row)[col];
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            dados.get(row)[col] = value;
            fireTableCellUpdated(row, col);

            if (col == 4 && presenter != null) {
                String nomeUsuario = (String) dados.get(row)[1];
                presenter.onPerfilAlterado(nomeUsuario, (String) value);
            }
        }
    }
}

