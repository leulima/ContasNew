package br.com.gigaspace.contas.model;

public enum TransactionTypeEnum {
    DEBITO("D", "Débito"),
    CREDITO("C", "Crédito"),
    TRANSFERENCIA("T", "Transferência");

    private String id;
    private String descricao;

    private TransactionTypeEnum(String id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static TransactionTypeEnum getById(String id) {
        TransactionTypeEnum e = null;
        for (TransactionTypeEnum item : TransactionTypeEnum.values()) {
            if (id != null && item.getId().compareTo(id) == 0) {
                e = item;
                break;
            }
        }
        return e;
    }
}
