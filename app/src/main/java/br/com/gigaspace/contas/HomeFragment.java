package br.com.gigaspace.contas;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.gigaspace.contas.adapter.AccountsListAdapter;
import br.com.gigaspace.contas.helper.AccountHelper;
import br.com.gigaspace.contas.model.Account;
import br.com.gigaspace.contas.model.Constants;
import br.com.gigaspace.contas.model.AccountTypeEnum;

public class HomeFragment extends Fragment {
    private ListView listView;
    private AccountHelper service;

    OnContaSelectedListener mCallback;

    public HomeFragment() {
        // Required empty public constructor
    }

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnContaSelectedListener {
        /** Called by HomeFragment when a list item is selected */
        //public void onContaItemSelected(int position, Bundle args);
        public void onContaItemSelected(int position, Intent intent);
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnContaSelectedListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnContaSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        service = new AccountHelper(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        //contasHelper = new ContaHelper(rootView.getContext());
        ArrayList<Account> items = getAccountsList();

        if (items.size() == 0) {
            items = createTestData();
        }
        Log.i(Constants.LOG, "Numero de items: " + items.size());

        listView = (ListView) rootView.findViewById(R.id.custom_list);
        listView.setAdapter(new AccountsListAdapter(rootView.getContext(), items));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                Account item = (Account) o;

                Intent intent = new Intent(getActivity(), AccountActivity.class);
                intent.putExtra("adding", false);
                intent.putExtra("nome_conta", item.getNomeConta());
                intent.putExtra("tipo_conta", item.getTipoConta());
                intent.putExtra("numero_conta", item.getNumero());
                intent.putExtra("valor_atual", item.getValorAtual());

                //startActivity(intent);
                //editAccount(false);

                // Notify the parent activity of selected item
                //mCallback.onContaItemSelected(position, intent.getExtras());
                mCallback.onContaItemSelected(position, intent);

                // Set the item as checked to be highlighted when in two-pane layout
                listView.setItemChecked(position, true);
            }
        });

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(Constants.LOG, "Atualizando lista de contas...");
        ArrayList<Account> items = getAccountsList();
        listView.setAdapter(new AccountsListAdapter(getView().getContext(), items));
        listView.invalidateViews();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.register_account) {
            Log.i(Constants.LOG, "Adicionando conta...");
            editAccount(true);
            return true;
        }
        else if (id == R.id.remove_all) {
            removeAllData();
        }
        else if (id == R.id.categories) {
            showCategories();
        }
        else if (id == R.id.action_settings) {
            Toast.makeText(getView().getContext(), "Configurações selecionada.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editAccount(boolean adding) {

        Intent intent = new Intent(getView().getContext(), AccountActivity.class);
        intent.putExtra("adding", true);
        //item.setIntent(intent);
        startActivity(intent);


        /*
        // Create fragment and give it an argument for the selected article
        ContaFragment newFragment = new ContaFragment();
        Bundle args = new Bundle();
        args.putBoolean("adding", adding);
        newFragment.setArguments(args);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.frame_container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
        */

    }

    private ArrayList<Account> createTestData() {
        ArrayList<Account> testItems = getListData();

        Log.i(Constants.LOG, "Criando contas de teste...");

        for (int i = 0; i < testItems.size(); i++) {
            Account item = testItems.get(i);
            long id = service.save(item);
            item.setId(id);
        }

        return testItems;
    }

    private ArrayList<Account> getAccountsList() {
        return (ArrayList<Account>) service.findAllConta();
    }

    private void showCategories() {
        Intent intent = new Intent(getView().getContext(), CategoryActivity.class);
        startActivity(intent);
    }

    private void removeAllData() {
        Log.i(Constants.LOG, "Removendo todos registros...");
        service.deleteAll();

        listView.setAdapter(new AccountsListAdapter(getView().getContext(), new ArrayList<Account>()));
        listView.invalidateViews();
        //listView.refreshDrawableState();
    }

    private ArrayList<Account> getListData() {
        ArrayList<Account> results = new ArrayList<Account>();

        Account data = new Account();
        data.setNomeConta("Banco Santander");
        data.setNumero("2269 / 01.023.510-3");
        data.setTipoConta(AccountTypeEnum.CONTA_CORRENTE.getId());
        data.setValorAtual(5600f);
        results.add(data);

        data = new Account();
        data.setNomeConta("Banco Itaú");
        data.setNumero("3311 / 10.875-1");
        data.setTipoConta(AccountTypeEnum.CONTA_CORRENTE.getId());
        data.setValorAtual(7500f);
        results.add(data);

        data = new Account();
        data.setNomeConta("Banco Bradesco");
        data.setNumero("2962 / 0003765-6");
        data.setTipoConta(AccountTypeEnum.CONTA_CORRENTE.getId());
        data.setValorAtual(2600f);
        results.add(data);

        data = new Account();
        data.setNomeConta("Poupança CEF");
        data.setNumero("0012 / 013.847816-0");
        data.setTipoConta(AccountTypeEnum.POUPANCA.getId());
        data.setValorAtual(3100f);
        results.add(data);

        data = new Account();
        data.setNomeConta("Bradesco Mastercard Gold");
        data.setTipoConta(AccountTypeEnum.CARTAO_CREDITO.getId());
        data.setValorAtual(1800f);
        results.add(data);

        data = new Account();
        data.setNomeConta("Santander VISA");
        data.setTipoConta(AccountTypeEnum.CARTAO_CREDITO.getId());
        data.setValorAtual(970f);
        results.add(data);

        data = new Account();
        data.setNomeConta("Santander Mastercard");
        data.setTipoConta(AccountTypeEnum.CARTAO_CREDITO.getId());
        data.setValorAtual(1100f);
        results.add(data);

        data = new Account();
        data.setNomeConta("Citibank VISA");
        data.setTipoConta(AccountTypeEnum.CARTAO_CREDITO.getId());
        data.setValorAtual(600f);
        results.add(data);

        return results;
    }
}
