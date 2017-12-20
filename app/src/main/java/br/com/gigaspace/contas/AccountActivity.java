package br.com.gigaspace.contas;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import br.com.gigaspace.contas.helper.AccountHelper;
import br.com.gigaspace.contas.listener.OnBackPressedListener;
import br.com.gigaspace.contas.model.Account;
import br.com.gigaspace.contas.model.AccountTypeEnum;

public class AccountActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener, OnBackPressedListener {
    // Log tag
    private static final String TAG = AccountActivity.class.getSimpleName();

    private Account contaItem;
    private AccountHelper service;
    private boolean adding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        //Bundle args = getArguments();
        Bundle args = intent.getExtras();

        //contasHelper = new ContasHelper(getActivity());
        service = new AccountHelper(this);
        contaItem = new Account();

        contaItem.setNomeConta(args.getString("nome_conta"));
        contaItem.setTipoConta(args.getInt("tipo_conta", AccountTypeEnum.CONTA_CORRENTE.getId()));
        contaItem.setNumero(args.getString("numero_conta"));
        contaItem.setValorAtual(args.getFloat("valor_atual", 0));

        adding = args.getBoolean("adding", false);

        setContentView(R.layout.activity_account);
        //View view = inflater.inflate(R.layout.activity_conta, container, false);

        Log.i(TAG, "Cadastro de conta...");

        //Spinner spinnerTipo = (Spinner) view.findViewById(R.id.spinnerTipo);
        Spinner spinnerTipo = (Spinner) findViewById(R.id.spinnerTipo);

        // set a listener on spinner
        spinnerTipo.setOnItemSelectedListener(this);

        // populate the spinner from data source
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tipos_conta, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapter);

        // Mostra o icone home de voltar na ActionBar
        //ActionBar actionBar = getActivity().getActionBar();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        //actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_close_white_36dp);

        if (!adding) {
            actionBar.setTitle("Editar Conta");
            actionBar.setSubtitle(contaItem.getNomeConta());
        }
        else {
            actionBar.setTitle("Nova Conta");
        }

//        setHasOptionsMenu(true);

        // Configura listaner de botao de voltar
//        FragmentActivity activity = getActivity();
        //((MainActivity)activity).setOnBackPressedListener(new BaseBackPressedListener(activity));
//        ((MainActivity)activity).setOnBackPressedListener(this);

        //return view;
        setUI();
    }
    /*
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            setUI();
        }
    */
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        contaItem.setTipoConta(AccountTypeEnum.getByDescricao(
                parent.getItemAtPosition(position).toString()).getId());
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // An interface callback
    }

    protected void setUI() {
        TextView txtNome = (TextView) findViewById(R.id.txtNome);
        txtNome.setText(contaItem.getNomeConta());

        if (contaItem.getNumero() != null) {
            ((TextView) findViewById(R.id.txtNumber)).setText(contaItem.getNumero());
        }

        if (contaItem.getValorAtual() != null) {
            ((TextView) findViewById(R.id.txtValor)).setText(String.valueOf(contaItem.getValorAtual()));
        }

        Resources resource = getResources();
        String[] tipoContas = resource.getStringArray(R.array.tipos_conta);

        for (int i = 0; i < tipoContas.length; i++) {
            if (tipoContas[i].equals(AccountTypeEnum.getById(contaItem.getTipoConta()).getDescricao())) {
                ((Spinner) findViewById(R.id.spinnerTipo)).setSelection(i);
                break;
            }
        }

    }

    @Override
    //public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.account_form, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isDataChanged()) {
                    confirmExit();
                }
                else {
                    close();
                }
                return true;
            case R.id.action_save:
                saveData();
                return true;
            //case R.id.action_settings:
            //    return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        doBack();
        super.onBackPressed();
    }

    /**
     * Fecha o fragmento
     */
    private void close() {
        Log.i(TAG, "Fechando fragmento...");

        // For activity
        finish();

        // For fragment
        //getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        //getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        //getActivity().getFragmentManager().popBackStack();
    }


    /**
     * Implementa a acao do botao back (voltar)
     */
    @Override
    public void doBack() {
        Log.d(TAG, "Botao voltar acionado...");

        if (isDataChanged()) {
            confirmExit();
            return;
        }
        close();
    }

    /**
     * Salva os dados
     */
    public void saveData() {
        Log.i(TAG, "Salvando dados...");

        String name = this.getNome();

        if (name == null || name.equals("")) {
            //Toast.makeText(getActivity().getApplicationContext(), "Informe o nome da conta!", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Informe o nome da conta!", Toast.LENGTH_LONG).show();
            return;
        }

        contaItem.setNomeConta(name);
        contaItem.setNumero(this.getNumero());
        contaItem.setTipoConta(this.getTipo());
        contaItem.setValorAtual(this.getValorAtual());

        long id = service.save(contaItem);

        if (id != -1) {
            contaItem.setId(id);

            // A small pop up box that contains a message for a limited amount of time
            Toast.makeText(getApplicationContext(), "Informações gravadas!", Toast.LENGTH_LONG).show();

            close();
        }
    }

    //public void save(View view) {
    //    saveData();
    //}

    private boolean isDataChanged() {
        return ((contaItem.getNomeConta() != null && !contaItem.getNomeConta().equals(this.getNome())) ||
                (contaItem.getNumero() != null && !contaItem.getNumero().equals(this.getNumero())) ||
                (contaItem.getTipoConta() != null && !contaItem.getTipoConta().equals(this.getTipo())) ||
                (contaItem.getValorAtual() != null && !contaItem.getValorAtual().equals(this.getValorAtual())));

    }

    /**
     * Mostra mensagem de confirmacao de saida.
     */
    private void confirmExit() {
        //AlertDialog.Builder alertDialog = new AlertDialog.Builder(getView().getContext());
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());

        // Setting Dialog Title
        //alertDialog.setTitle("Sair sem salvar?");

        // Setting Dialog Message
        alertDialog.setMessage("Os dados foram alterados. Sair sem salvar?");

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                close();
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Setting Icon to Dialog
        alertDialog.setIcon(R.mipmap.ic_launcher);

        // Showing Alert Message
        alertDialog.show();
    }

    private String getNome() {
        CharSequence name = ((TextView) findViewById(R.id.txtNome)).getText();
        return (name != null ? name.toString() : null);
    }

    private String getNumero() {
        CharSequence value = ((TextView) findViewById(R.id.txtNumber)).getText();
        return (value != null ? value.toString() : null);
    }

    private Integer getTipo() {
        return AccountTypeEnum.getByDescricao(((Spinner) findViewById(R.id.spinnerTipo)).getSelectedItem().toString()).getId();
    }

    private Float getValorAtual() {
        CharSequence value = ((TextView) findViewById(R.id.txtValor)).getText();
        return (value != null && !"".equals(value.toString()) ? Float.parseFloat(value.toString()) : 0);
    }
}
