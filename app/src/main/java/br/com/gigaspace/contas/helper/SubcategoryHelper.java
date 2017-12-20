package br.com.gigaspace.contas.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import br.com.gigaspace.contas.contract.SubcategoryContract;
import br.com.gigaspace.contas.model.Subcategoria;

import java.util.ArrayList;
import java.util.List;

public class SubcategoryHelper extends DataBaseHelper {
    private static String TAG = SubcategoryHelper.class.getSimpleName();

    public SubcategoryHelper(Context context) {
        super(context);
    }

    /**
     * Return object item from cursor
     * @param cursor
     * @return
     */
    private Subcategoria fromCursor(Cursor cursor) {
        Subcategoria item = new Subcategoria();
        item.setId(cursor.getLong(cursor.getColumnIndex(SubcategoryContract._ID)));
        item.setCategId(cursor.getLong(cursor.getColumnIndex(SubcategoryContract.COLUMN_NAME_CATEG_ID)));
        item.setDescricao(cursor.getString(cursor.getColumnIndex(SubcategoryContract.COLUMN_NAME_DESCRICAO)));
        return item;
    }

    /**
     * Return object item list
     * @return
     */
    public List<Subcategoria> findAll(Long categId) {
        List<Subcategoria> list = new ArrayList<Subcategoria>();

        String sql = "SELECT * FROM " + SubcategoryContract.TABLE_NAME +
                " WHERE " + SubcategoryContract.COLUMN_NAME_CATEG_ID + "=?" +
                " ORDER BY " + SubcategoryContract.COLUMN_NAME_DESCRICAO;

        Cursor cursor = getDatabase().rawQuery(sql, new String[]{ String.valueOf(categId) });

        // Move the Cursor pointer to the first
        cursor.moveToFirst();

        // Iterate over the cursor
        while (!cursor.isAfterLast()) {
            Subcategoria item = fromCursor(cursor);

            // Add the object filled with appropriate data into the list
            list.add(item);

            // Move the Cursor pointer to next for the next record to fetch
            cursor.moveToNext();
        }

        return list;
    }

    /**
     * Delete a subcategory
     * @param subcategoria
     */
    public void delete(final Subcategoria subcategoria) {
        getDatabase().delete(SubcategoryContract.TABLE_NAME, SubcategoryContract._ID + " = " + subcategoria.getId(), null);
    }

    /**
     * Save a subcategory and return ID
     * @param item
     * @return
     */
    public long save(Subcategoria item) {
        long id;

        ContentValues contentValues = new ContentValues();
        contentValues.put(SubcategoryContract.COLUMN_NAME_CATEG_ID, item.getCategId());
        contentValues.put(SubcategoryContract.COLUMN_NAME_DESCRICAO, item.getDescricao());

        if (item.getId() == null) { // Record does not exist
            id = getDatabase().insert(SubcategoryContract.TABLE_NAME, null, contentValues);
            item.setId(id);
        }
        else { // Record exists
            id = getDatabase().update(SubcategoryContract.TABLE_NAME, contentValues,
                    SubcategoryContract._ID + "=?", new String[] { String.valueOf(item.getId()) });
        }

        return id;
    }

}
