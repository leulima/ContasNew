package br.com.gigaspace.contas.model;

public enum AccountTypeEnum {
    CONTA_CORRENTE(1, "Conta Corrente"),
    CARTAO_CREDITO(2, "Cartão de Crédito"),
    POUPANCA(3, "Poupança"),
    OUTROS(4, "Outros"),
    INVESTIMENTO(5, "Investimento");

    private Integer id;
    private String descricao;

    private AccountTypeEnum(Integer id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public static AccountTypeEnum getById(Integer id) {
        AccountTypeEnum e = null;
        for (AccountTypeEnum item : AccountTypeEnum.values()) {
            if (id!=null && item.getId().compareTo(id) == 0) {
                e = item;
                break;
            }
        }
        return e;
    }

    public static AccountTypeEnum getByDescricao(String descricao) {
        AccountTypeEnum e = null;
        for (AccountTypeEnum item : AccountTypeEnum.values()) {
            if (descricao != null && item.getDescricao().compareTo(descricao) == 0) {
                e = item;
                break;
            }
        }
        return e;
    }
}
