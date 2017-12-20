package br.com.gigaspace.contas.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import br.com.gigaspace.contas.contract.CategoryContract;
import br.com.gigaspace.contas.model.Category;
import br.com.gigaspace.contas.model.TransactionTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe Helper de Category de Lancamentos.
 */
public class CategoryHelper extends DataBaseHelper {
    private static String TAG = CategoryHelper.class.getSimpleName();

    public CategoryHelper(Context context) {
        super(context);
    }

    /**
     * Return object item from cursor
     * @param cursor
     * @return
     */
    private Category fromCursor(Cursor cursor) {
        Category item = new Category();
        item.setId(cursor.getLong(cursor.getColumnIndex(CategoryContract._ID)));
        item.setTipo(cursor.getString(cursor.getColumnIndex(CategoryContract.COLUMN_NAME_TIPO)));
        item.setDescricao(cursor.getString(cursor.getColumnIndex(CategoryContract.COLUMN_NAME_DESCRICAO)));
        return item;
    }


    /**
     * Return object item list
     * @return
     */
    public List<Category> findAllByType(TransactionTypeEnum tipo) {
        List<Category> list = new ArrayList<Category>();

        String sql = "SELECT * FROM " + CategoryContract.TABLE_NAME +
                " WHERE " + CategoryContract.COLUMN_NAME_TIPO + "=?" +
                " ORDER BY " + CategoryContract.COLUMN_NAME_DESCRICAO;

        Cursor cursor = getDatabase().rawQuery(sql, new String[]{ tipo.getId() });

        // Move the Cursor pointer to the first
        cursor.moveToFirst();

        // Iterate over the cursor
        while (!cursor.isAfterLast()) {
            Category item = fromCursor(cursor);

            // Add the object filled with appropriate data into the list
            list.add(item);

            //Log.i(TAG, String.format("%d - %s", item.getId(), item.getDescricao()));

            // Move the Cursor pointer to next for the next record to fetch
            cursor.moveToNext();
        }

        return list;
    }

    /**
     * Delete category
     * @param categoria
     */
    public void delete(final Category categoria) {
        getDatabase().delete(CategoryContract.TABLE_NAME, CategoryContract._ID + " = " + categoria.getId(), null);
    }

    /**
     * Save category and return ID
     * @param item
     * @return
     */
    public long save(Category item) {
        long id;

        ContentValues contentValues = new ContentValues();
        contentValues.put(CategoryContract.COLUMN_NAME_TIPO, item.getTipo());
        contentValues.put(CategoryContract.COLUMN_NAME_DESCRICAO, item.getDescricao());

        if (item.getId() == null) { // Record does not exist
            id = getDatabase().insert(CategoryContract.TABLE_NAME, null, contentValues);
            item.setId(id);
        }
        else { // Record exists
            id = getDatabase().update(CategoryContract.TABLE_NAME, contentValues,
                    CategoryContract._ID + "=?", new String[] { String.valueOf(item.getId()) });
        }

        return id;
    }

}
