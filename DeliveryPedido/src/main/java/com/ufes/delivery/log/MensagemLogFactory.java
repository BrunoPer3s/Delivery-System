package com.ufes.delivery.log;

import com.ufes.delivery.model.Pedido;
import com.ufes.log.model.MensagemLog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class MensagemLogFactory {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private MensagemLogFactory() {
    }

    public static Builder operacao(String nomeOperacao) {
        return new Builder(nomeOperacao);
    }

    public static final class Builder {

        private final String nomeOperacao;
        private String recurso = "";
        private ResultadoOperacao resultado = ResultadoOperacao.SUCESSO;
        private String justificativa = "";
        private String codigoPedido = "";
        private String nomeCliente = "";

        private Builder(String nomeOperacao) {
            this.nomeOperacao = nomeOperacao;
        }

        public Builder recurso(String recurso) {
            this.recurso = recurso;
            return this;
        }

        public Builder resultado(ResultadoOperacao resultado) {
            this.resultado = resultado;
            return this;
        }

        public Builder justificativa(String justificativa) {
            this.justificativa = justificativa;
            return this;
        }

        public Builder pedido(int codigo, String nomeCliente) {
            this.codigoPedido = String.valueOf(codigo);
            this.nomeCliente = nomeCliente;
            return this;
        }

        public Builder pedido(Pedido pedido) {
            this.codigoPedido = String.valueOf(pedido.getCodigo());
            this.nomeCliente = pedido.getCliente().getNome();
            return this;
        }

        public MensagemLog paraUsuario(String nomeUsuario) {
            LocalDateTime agora = LocalDateTime.now();
            return new MensagemLog(
                    nomeUsuario,
                    agora.format(FORMATO_DATA),
                    agora.format(FORMATO_HORA),
                    codigoPedido,
                    nomeOperacao,
                    nomeCliente,
                    recurso,
                    resultado.getDescricao(),
                    justificativa);
        }
    }
}
