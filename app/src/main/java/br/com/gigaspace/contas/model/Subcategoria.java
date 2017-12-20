package br.com.gigaspace.contas.model;

public class Subcategoria {
    private Long id;
    private Long categId;
    private String descricao;

    public Subcategoria() {

    }

    public Subcategoria(Long id, Long categId, String descricao) {
        this.id = id;
        this.categId = categId;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategId() {
        return categId;
    }

    public void setCategId(Long categId) {
        this.categId = categId;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
