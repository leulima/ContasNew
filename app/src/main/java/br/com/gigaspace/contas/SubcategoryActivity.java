package br.com.gigaspace.contas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.gigaspace.contas.helper.SubcategoryHelper;
import br.com.gigaspace.contas.model.Constants;
import br.com.gigaspace.contas.model.Subcategoria;

public class SubcategoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = SubcategoryActivity.class.getSimpleName();

    private ListView listView;
    private SubcategoryArrayAdapter adapter;
    private SubcategoryHelper service;
    private List<Subcategoria> listItems;
    private Long categId;

    private int startColor = 0;
    private int indexColor;
    private Map<Long, String> map = new HashMap<Long, String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_subcategory);
        listView = (ListView) findViewById(R.id.listViewSubcateg);

        startColor = 0 + (int) (Math.random() * (Constants.CIRCLE_COLORS.length - 1));
        indexColor = startColor;
        service = new SubcategoryHelper(this);

        Intent intent = getIntent();
        Bundle args = intent.getExtras();

        categId = args.getLong("categ_id");
        listItems = service.findAll(categId);

        adapter = new SubcategoryArrayAdapter(this, listItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        String categoryName = args.getString("categ_descricao");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.subcategories);
        getSupportActionBar().setSubtitle(categoryName);

        // Configure Add button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabSubcategory);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(null);
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object o = listView.getItemAtPosition(position);
        Subcategoria selectedItem = (Subcategoria) o;
        showInputDialog(selectedItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void showInputDialog(Subcategoria subcategoria) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View promptsView = inflater.inflate(R.layout.popup_input_text, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final TextView textView = (TextView) promptsView.findViewById(R.id.titleTextView);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        textView.setText(R.string.add_subcategory);
        userInput.setHint(R.string.description);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                String resultText = userInput.getText().toString();
                                Subcategoria item = (Subcategoria) userInput.getTag();

                                //Toast.makeText(getView().getContext(), resultText, Toast.LENGTH_SHORT).show();
                                onDialogPositiveAction(item, resultText);
                            }
                        })

                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        if (subcategoria != null) {
            textView.setText(R.string.edit_subcategory);
            userInput.setText(subcategoria.getDescricao());
            userInput.setTag(subcategoria);

            // Create aditional button
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.delete),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Subcategoria item = (Subcategoria) userInput.getTag();
                            onDialogDeleteAction(item);
                        }
                    });
        }

        // show it
        alertDialog.show();
    }

    private void refreshListView() {
        listItems = service.findAll(categId);
        adapter.clear();
        adapter.addAll(listItems);
    }

    private void onDialogPositiveAction(Subcategoria subcategoria, String description) {
        if (description != null && !description.equals("")) {

            if (subcategoria != null && !subcategoria.getDescricao().equals(description)) {
                Log.i(TAG, "Editando subcategoria " + description);
                subcategoria.setDescricao(description);
            }
            else {
                subcategoria = new Subcategoria();
                subcategoria.setDescricao(description);
                subcategoria.setCategId(categId);
                Log.i(TAG, "Adicionando subcategoria " + description);
            }

            if (service.save(subcategoria) > 0) {
                refreshListView();
                Toast.makeText(this,
                        String.format("%s %s.", description, getResources().getString(R.string.saved)),
                        Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void onDialogDeleteAction(Subcategoria subcategoria) {
        confirmDeleteDialog(subcategoria);
    }

    private void confirmDeleteDialog(final Subcategoria subcategoria) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.delete_subcategory);
        alert.setMessage("Confirma a exclusão da subcategoria [" + subcategoria.getDescricao() + "] ?");
        alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteSubCategory(subcategoria);
                dialog.dismiss();
            }
        });
        alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    private void deleteSubCategory(Subcategoria subcategoria) {
        service.delete(subcategoria);
        refreshListView();
        Toast.makeText(this,
                String.format("%s %s.", subcategoria.getDescricao(), getResources().getString(R.string.deleted)),
                Toast.LENGTH_SHORT).show();
    }

    static class SubcategoryViewHolder {
        TextView textViewIcon;
        TextView textViewText;
        TextView textViewDetail;
    }

    private class SubcategoryArrayAdapter extends ArrayAdapter<Subcategoria> {

        public SubcategoryArrayAdapter(Context context, List<Subcategoria> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Subcategoria item = getItem(position);

            SubcategoryViewHolder viewHolder;

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                // inflate the layout
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_row_simple, parent, false);

                // well set up the ViewHolder
                viewHolder = new SubcategoryViewHolder();
                viewHolder.textViewText = (TextView) convertView.findViewById(R.id.itemText);
                viewHolder.textViewIcon = (TextView) convertView.findViewById(R.id.itemIcone);
                viewHolder.textViewDetail = (TextView) convertView.findViewById(R.id.itemDetail);

                // store the holder with the view.
                convertView.setTag(viewHolder);
            } else {
                // we've just avoided calling findViewById() on resource everytime
                // just use the viewHolder
                viewHolder = (SubcategoryViewHolder) convertView.getTag();
            }

            String colorString;

            if (map.get(item.getId()) == null) {
                if (indexColor + 1 > Constants.CIRCLE_COLORS.length - 1)
                    indexColor = (indexColor + 1) - Constants.CIRCLE_COLORS.length;
                else
                    indexColor += 1;

                colorString = Constants.CIRCLE_COLORS[indexColor];
                map.put(item.getId(), colorString);
            } else {
                colorString = map.get(item.getId());
            }

            int color = Color.parseColor(colorString);

            Drawable drawable = getResources().getDrawable(R.drawable.circle);
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

            viewHolder.textViewIcon.setBackground(drawable);
            viewHolder.textViewIcon.setTag(colorString);
            viewHolder.textViewIcon.setText(item.getDescricao().substring(0, 2).toUpperCase());
            viewHolder.textViewText.setText(item.getDescricao());
            viewHolder.textViewDetail.setText((String) viewHolder.textViewIcon.getTag());

            // Return the completed view to render on screen
            return convertView;
        }
    }
}
