package com.ufes.delivery.model;

public class Endereco {

    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;
    private boolean padrao;

    public Endereco() {
        this.padrao = false;
    }

    public Endereco(String logradouro, String numero, String complemento,
                    String bairro, String cidade, String uf, String cep,
                    boolean padrao) {
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.cep = cep;
        this.padrao = padrao;
    }

    public String getLogradouro() { return logradouro; }
    public String getNumero() { return numero; }
    public String getComplemento() { return complemento; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getUf() { return uf; }
    public String getCep() { return cep; }
    public boolean isPadrao() { return padrao; }

    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    public void setNumero(String numero) { this.numero = numero; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public void setUf(String uf) { this.uf = uf; }
    public void setCep(String cep) { this.cep = cep; }
    public void setPadrao(boolean padrao) { this.padrao = padrao; }

    public boolean isPreenchido() {
        return logradouro != null && !logradouro.trim().isEmpty()
            && numero != null && !numero.trim().isEmpty()
            && bairro != null && !bairro.trim().isEmpty()
            && cidade != null && !cidade.trim().isEmpty()
            && uf != null && !uf.trim().isEmpty()
            && cep != null && !cep.trim().isEmpty();
    }

    public String getEnderecoFormatado() {
        StringBuilder sb = new StringBuilder();
        sb.append(logradouro).append(", ").append(numero);
        if (complemento != null && !complemento.trim().isEmpty()) {
            sb.append(" - ").append(complemento);
        }
        sb.append(", ").append(bairro);
        sb.append(", ").append(cidade).append("/").append(uf);
        sb.append(" - CEP: ").append(formatarCep(cep));
        return sb.toString();
    }

    private String formatarCep(String cep) {
        if (cep == null) return "";
        String somenteDigitos = cep.replaceAll("\\D", "");
        if (somenteDigitos.length() == 8) {
            return somenteDigitos.substring(0, 5) + "-" + somenteDigitos.substring(5);
        }
        return cep;
    }

    @Override
    public String toString() {
        return getEnderecoFormatado();
    }
}

