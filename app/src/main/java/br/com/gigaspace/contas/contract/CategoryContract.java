package br.com.gigaspace.contas.contract;

import android.provider.BaseColumns;

public class CategoryContract implements BaseColumns {
    public static final String TABLE_NAME = "CATEGORIAS";

    public static final String COLUMN_NAME_TIPO = "tipo";
    public static final String COLUMN_NAME_DESCRICAO = "descricao";
}
