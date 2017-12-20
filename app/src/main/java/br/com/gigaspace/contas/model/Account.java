package br.com.gigaspace.contas.model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Account {
    private Long id;
    private String nomeConta;
    private Integer tipoConta;
    private String numero;
    private Float valorAtual;

    public Account() {
        this.tipoConta = AccountTypeEnum.CONTA_CORRENTE.getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeConta() {
        return nomeConta;
    }

    public void setNomeConta(String nomeConta) {
        this.nomeConta = nomeConta;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Integer getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(Integer tipoConta) {
        this.tipoConta = tipoConta;
    }

    public Float getValorAtual() {
        return valorAtual;
    }

    public void setValorAtual(Float valorAtual) {
        this.valorAtual = valorAtual;
    }

    public String getValorAtualAsString() {
        DecimalFormat df = new DecimalFormat(Constants.CURRENCY_FORMAT, new DecimalFormatSymbols(Locale.getDefault()));
        return df.format(valorAtual);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj.getClass().isInstance(new Account())) {
            final Account bl = (Account) obj;

            if (bl.id.equals(this.id))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "[ Nome Conta=" + nomeConta + ", Tipo Conta=" +
                tipoConta + " , Valor Atual=" + valorAtual + "]";
    }
}
