package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.claims;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v7.widget.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.R;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.data.EasySharedPreferences;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.data.ReclamacaoDAO;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.MapMarker;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.MessageEvent;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.Reclamacao;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.BaseActivity;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.home.HomeActivity;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.web.WebClaims;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.web.WebProfileEdit;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.utils.RecyclerItemClickListener;

public class ClaimsActivity extends AppCompatActivity {
    private ListView mListView;
    private ArrayList<Reclamacao> reclamacoes;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claims);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // Start navigation controls icons
        navigatitionControls();

        getClaims();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void navigatitionControls(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_claims);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_map:
                        goToHome();
                        break;
                    case R.id.action_claims:
                        break;
                }
                return true;
            }
        });
    }

    public void goToHome(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void getClaims(){
        String token = EasySharedPreferences.getStringFromKey(this, EasySharedPreferences.KEY_TOKEN);
        WebClaims webClaims = new WebClaims(token);
        webClaims.call();
    }

    public void createList(){
        String[] listItems = new String[reclamacoes.size()];

        for(int i = 0; i < reclamacoes.size(); i++){
            Reclamacao reclamacao = reclamacoes.get(i);
            listItems[i] = reclamacao.getData();
        }

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        ClaimAdapter mAdapter = new ClaimAdapter(this, reclamacoes);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        final Context context = this;
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Reclamacao selectedItem = reclamacoes.get(position);

                        Intent detailIntent = new Intent(context, ClaimDetailActivity.class);

                        detailIntent.putExtra("id", selectedItem.getId());
                        detailIntent.putExtra("data", selectedItem.getData());
                        detailIntent.putExtra("hora", selectedItem.getHora());
                        detailIntent.putExtra("obs", selectedItem.getObs());
                        detailIntent.putExtra("latitude", selectedItem.getLatitude());
                        detailIntent.putExtra("longitude", selectedItem.getLongitude());

                        startActivity(detailIntent);
                    }
                })
        );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ArrayList<Reclamacao> reclamacoes) {
        this.reclamacoes = new ArrayList<Reclamacao>();
        ReclamacaoDAO dao = new ReclamacaoDAO(this);
        for(int i = 0; i < reclamacoes.size(); i++){
            dao.delete(reclamacoes.get(i));
            dao.create(reclamacoes.get(i));
            this.reclamacoes.add(reclamacoes.get(i));
        }
        createList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Exception exception) {
        Snackbar.make(findViewById(android.R.id.content),exception.getMessage(),
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        goToHome();
    }
}
