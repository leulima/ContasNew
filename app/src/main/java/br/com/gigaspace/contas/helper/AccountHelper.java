package br.com.gigaspace.contas.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import br.com.gigaspace.contas.contract.AccountContract;
import br.com.gigaspace.contas.model.Account;
import java.util.ArrayList;
import java.util.List;

//import java.text.SimpleDateFormat;
//import java.util.Locale;

public class AccountHelper extends DataBaseHelper {
    //private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public AccountHelper(Context context) {
        super(context);
    }

    public void deleteAll() {
        getDatabase().delete(AccountContract.TABLE_NAME, null, null);
    }

    public void delete(final Account conta) {
        // Way to delete a record from database
        getDatabase().delete(AccountContract.TABLE_NAME, AccountContract._ID + " = " + conta.getId(), null);
    }

    /**
     * Return object item from cursor
     * @param cursor
     * @return
     */
    private Account fromCursor(Cursor cursor) {
        Account conta = new Account();
        conta.setId(cursor.getLong(cursor.getColumnIndex(AccountContract._ID)));
        conta.setNomeConta(cursor.getString(cursor.getColumnIndex(AccountContract.COLUMN_NAME_NOME)));
        conta.setTipoConta(cursor.getInt(cursor.getColumnIndex(AccountContract.COLUMN_NAME_TIPO)));
        conta.setNumero(cursor.getString(cursor.getColumnIndex(AccountContract.COLUMN_NAME_NUMERO)));
        conta.setValorAtual(cursor.getFloat(cursor.getColumnIndex(AccountContract.COLUMN_NAME_VALOR)));
        return conta;
    }

    /**
     * Return object item by name
     * @param name
     * @return
     */
    public Account findByName(String name) {
        String sql = "SELECT * FROM " + AccountContract.TABLE_NAME +
                " WHERE " + AccountContract.COLUMN_NAME_NOME + "=?";
        Cursor cursor = getDatabase().rawQuery(sql, new String[]{name});
        return cursor.getCount() != 0 ? fromCursor(cursor) : null;
    }

    /**
     * Return object item list
     * @return
     */
    public List<Account> findAllConta() {
        List<Account> list = new ArrayList<Account>();
        String sql = "SELECT * FROM " + AccountContract.TABLE_NAME;
        Cursor cursor = getDatabase().rawQuery(sql, null);

        // Move the Cursor pointer to the first
        cursor.moveToFirst();

        // Iterate over the cursor
        while (!cursor.isAfterLast()) {
            Account conta = fromCursor(cursor);

            // Add the object filled with appropriate data into the list
            list.add(conta);

            // Move the Cursor pointer to next for the next record to fetch
            cursor.moveToNext();
        }

        return list;
    }


    /**
     * Salva a conta e retorna o ID
     * @param item
     * @return
     */
    public long save(Account item) {
        long id;

        ContentValues contentValues = new ContentValues();
        contentValues.put(AccountContract.COLUMN_NAME_NUMERO, item.getNumero());
        contentValues.put(AccountContract.COLUMN_NAME_TIPO, item.getTipoConta());
        contentValues.put(AccountContract.COLUMN_NAME_VALOR, item.getValorAtual());

        Account conta = findByName(item.getNomeConta());

        if (conta == null) { // Record does not exist
            contentValues.put(AccountContract.COLUMN_NAME_NOME, item.getNomeConta());
            id = getDatabase().insert(AccountContract.TABLE_NAME, null, contentValues);
        }
        else { // Record exists
            id = getDatabase().update(AccountContract.TABLE_NAME, contentValues,
                    AccountContract.COLUMN_NAME_NOME + "=?", new String[] { item.getNomeConta() });
        }

        return id;
    }

}
