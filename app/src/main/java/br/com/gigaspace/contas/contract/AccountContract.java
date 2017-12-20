package br.com.gigaspace.contas.contract;

import android.provider.BaseColumns;

public class AccountContract implements BaseColumns {
    public static final String TABLE_NAME = "CONTAS";

    public static final String COLUMN_NAME_NOME = "nome";
    public static final String COLUMN_NAME_NUMERO = "numero";
    public static final String COLUMN_NAME_TIPO = "tipo";
    public static final String COLUMN_NAME_VALOR = "valor_atual";
}
