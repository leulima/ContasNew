package br.com.gigaspace.contas;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import br.com.gigaspace.contas.adapter.ViewPagerAdapter;
import br.com.gigaspace.contas.model.Constants;
import br.com.gigaspace.contas.model.TransactionTypeEnum;

public class CategoryActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final String TAG = CategoryActivity.class.getSimpleName();

    private ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setTheme(R.style.MyMaterialThemeRed);
        setContentView(R.layout.activity_category);

        handleIntent(getIntent());

        // Initilization
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // Configura ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        CategoryFragment despesasFragment = new CategoryFragment();
        CategoryFragment receitasFragment = new CategoryFragment();

        Bundle args = new Bundle();
        args.putString("category", TransactionTypeEnum.DEBITO.getId());
        despesasFragment.setArguments(args);

        args = new Bundle();
        args.putString("category", TransactionTypeEnum.CREDITO.getId());
        receitasFragment.setArguments(args);

        adapter.addFragment(despesasFragment, getResources().getString(R.string.expenses));
        adapter.addFragment(receitasFragment, getResources().getString(R.string.incomes));
        viewPager.setAdapter(adapter);

        //getSupportActionBar().setHomeButtonEnabled(false);
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Specify that the Home button should show an "Up" caret, indicating that touching the
        // button will take the user one step up in the application's hierarchy.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.categories);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Setup events
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i(Constants.LOG, "Tab selecionada: " + tab.getPosition());
                viewPager.setCurrentItem(tab.getPosition());

                //activity.setTheme((tab.getPosition() == 0 ? R.style.MyMaterialThemeRed : R.style.MyMaterialThemeGreen));
                //activity.setContentView(R.layout.activity_categoria);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.categories, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        /*
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        setupSearchView(searchItem);
        */

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed in the action bar.
                // Create a simple intent that starts the hierarchical parent activity and
                // use NavUtils in the Support Package to ensure proper handling of Up.
                Intent upIntent = new Intent(this, MainActivity.class);

                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is not part of the application's task, so create a new task
                    // with a synthesized back stack.
                    TaskStackBuilder.from(this)
                            // If there are ancestor activities, they should be added here.
                            .addNextIntent(upIntent)
                            .startActivities();
                    finish();
                }
                else {
                    // This activity is part of the application's task, so simply
                    // navigate up to the hierarchical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;

            case R.id.action_settings:
                Toast.makeText(this.getApplicationContext(), "Configurações selecionada.", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //mStatusView.setText("Query = " + newText);
        Log.i(TAG, "Query = " + newText);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        //mStatusView.setText("Query = " + query + " : submitted");
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    /**
     * Gerencia a pesquisa no toolbar
     * @param intent
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search
            Log.i(TAG, "Query = " + query);
        }
    }

}
