package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.claims;

import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

public class ClaimsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claims);

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ArrayList<Reclamacao> reclamacoes) {
        ReclamacaoDAO dao = new ReclamacaoDAO(this);
        for(int i = 0; i < reclamacoes.size(); i++){
            dao.delete(reclamacoes.get(i));
            dao.create(reclamacoes.get(i));
        }
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
