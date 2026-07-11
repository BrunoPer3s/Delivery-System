package com.ufes.delivery.log;

import com.ufes.delivery.model.Pedido;
import com.ufes.log.model.MensagemLog;
import com.ufes.delivery.util.UsuarioLogadoService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MensagemLogFactory {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static MensagemLog criar(Pedido pedido, String descricaoOperacao, String nomeMetodo) {
        LocalDateTime agora = LocalDateTime.now();

        String nomeUsuario = UsuarioLogadoService.getNomeUsuario();
        String data = agora.format(dateFormatter);
        String hora = agora.format(timeFormatter);
        String codigoPedido = String.valueOf(pedido.getCodigo());
        String nomeOperacao = descricaoOperacao + " (" + nomeMetodo + ")";
        String nomeCliente = pedido.getCliente().getNome();

        return new MensagemLog(
            nomeUsuario,
            data,
            hora,
            codigoPedido,
            nomeOperacao,
            nomeCliente
        );
    }

    public static MensagemLog criarParaOperacao(String nomeUsuario, String descricaoOperacao) {
        LocalDateTime agora = LocalDateTime.now();
        String data = agora.format(dateFormatter);
        String hora = agora.format(timeFormatter);

        return new MensagemLog(
            nomeUsuario,
            data,
            hora,
            "",
            descricaoOperacao,
            ""
        );
    }
}


