package br.com.gigaspace.contas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
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

import br.com.gigaspace.contas.helper.CategoryHelper;
import br.com.gigaspace.contas.model.Category;
import br.com.gigaspace.contas.model.Constants;
import br.com.gigaspace.contas.model.TransactionTypeEnum;

public class CategoryFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = CategoryFragment.class.getSimpleName();

    private CategoryHelper service;
    private ListView listview;
    private CategoriaArrayAdapter adapter;
    private List<Category> listItems;
    private static Category selectedItem;

    private int startColor = 0;
    private int indexColor;
    private Map<Long, String> map = new HashMap<Long, String>();

    private TransactionTypeEnum tipoTransacaoEnum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tipoTransacaoEnum = TransactionTypeEnum.getById(getArguments().getString("category"));
        startColor = 0 + (int) (Math.random() * (Constants.CIRCLE_COLORS.length - 1));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_category, container, false);

        indexColor = startColor;
        service = new CategoryHelper(getContext());

        listItems = service.findAllByType(tipoTransacaoEnum);
        adapter = new CategoriaArrayAdapter(getContext(), listItems);

        listview = (ListView) rootView.findViewById(R.id.listview);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);

        registerForContextMenu(listview);

        // Configure Add button
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fabCategory);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                //Write here anything that you wish to do on click of FAB
                // Code to Add an item with default animation
                int index = mAdapter.getItemCount();
                DataObject obj = new DataObject("Some Primary Text " + index,
                        "Secondary " + index);
                ((MyRecyclerViewAdapter) mAdapter).addItem(obj, index);
                //Ends Here
                */
                showInputDialog(null);
            }
        });
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object o = listview.getItemAtPosition(position);
        selectedItem = (Category) o;
        showInputDialog(selectedItem);
        //Toast.makeText(getContext(), item.getDescricao(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        if (view.getId() == R.id.listview) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;

            Object o = listview.getItemAtPosition(info.position);
            selectedItem = (Category) o;

            menu.setHeaderTitle(selectedItem.getDescricao());
            menu.add(Menu.NONE, 0, 0, R.string.edit);
            menu.add(Menu.NONE, 1, 1, R.string.delete);
            menu.add(Menu.FIRST, 2, 2, R.string.subcategories);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        View view = info.targetView;

        //Object o = listview.getItemAtPosition(info.position);
        //Object o = adapter.getItem(info.position);
        Category categoria = selectedItem;

        switch (item.getItemId()) {
            case 0:
                Toast.makeText(getContext(), "Editar index: " + menuItemIndex + "(" + categoria.getDescricao() + ")", Toast.LENGTH_SHORT).show();
                return true;

            case 1:
                confirmDeleteDialog(categoria);
                return true;

            case 2:
                Intent intent = new Intent(getActivity(), SubcategoryActivity.class);
                intent.putExtra("categ_id", categoria.getId());
                intent.putExtra("categ_descricao", categoria.getDescricao());
                startActivity(intent);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    protected void showInputDialog(Category categoria) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View promptsView = inflater.inflate(R.layout.popup_input_text, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final TextView textView = (TextView) promptsView.findViewById(R.id.titleTextView);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        textView.setText(R.string.add_category);
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
                                Category item = (Category) userInput.getTag();

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

        if (categoria != null) {
            textView.setText(R.string.edit_category);
            userInput.setText(categoria.getDescricao());
            userInput.setTag(categoria);

            // Create aditional button
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.delete),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Category item = (Category) userInput.getTag();
                            onDialogDeleteAction(item);
                        }
                    });
        }

        // show it
        alertDialog.show();
    }

    private void refreshListView() {
        listItems = service.findAllByType(tipoTransacaoEnum);
        //listview.invalidateViews();
        adapter.clear();
        adapter.addAll(listItems);
    }

    private void onDialogPositiveAction(Category categoria, String description) {
        if (description != null && !description.equals("")) {

            if (categoria != null && !categoria.getDescricao().equals(description)) {
                Log.i(TAG, "Editando categoria " + description);
                categoria.setDescricao(description);
            }
            else {
                categoria = new Category();
                categoria.setDescricao(description);
                categoria.setTipo(tipoTransacaoEnum.getId());
                Log.i(TAG, "Adicionando categoria " + description);
            }

            if (service.save(categoria) > 0) {
                refreshListView();
                Toast.makeText(getView().getContext(),
                        String.format("%s %s.", description, getResources().getString(R.string.saved)),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onDialogDeleteAction(Category categoria) {
        confirmDeleteDialog(categoria);
    }

    private void confirmDeleteDialog(final Category categoria) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(R.string.delete_category);
        alert.setMessage("Confirma a exclusão da categoria [" + categoria.getDescricao() + "] ?");
        alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCategory(categoria);
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

    private void deleteCategory(Category categoria) {
        service.delete(categoria);
        refreshListView();
        Toast.makeText(getView().getContext(),
                String.format("%s %s.", categoria.getDescricao(), getResources().getString(R.string.deleted)),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    static class CategoryViewHolder {
        TextView textViewIcon;
        TextView textViewText;
        TextView textViewDetail;
    }

    private class CategoriaArrayAdapter extends ArrayAdapter<Category> {

        public CategoriaArrayAdapter(Context context, List<Category> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Category item = getItem(position);

            CategoryViewHolder viewHolder;

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                // inflate the layout
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_row_simple, parent, false);

                // well set up the ViewHolder
                viewHolder = new CategoryViewHolder();
                viewHolder.textViewText = (TextView) convertView.findViewById(R.id.itemText);
                viewHolder.textViewIcon = (TextView) convertView.findViewById(R.id.itemIcone);
                viewHolder.textViewDetail = (TextView) convertView.findViewById(R.id.itemDetail);

                // store the holder with the view.
                convertView.setTag(viewHolder);
            } else {
                // we've just avoided calling findViewById() on resource everytime
                // just use the viewHolder
                viewHolder = (CategoryViewHolder) convertView.getTag();
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
