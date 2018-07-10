package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.reclaim;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.R;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.data.EasySharedPreferences;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.MapMarker;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.MessageEvent;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.claims.ClaimsActivity;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.home.HomeActivity;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.login.LoginActivity;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.profile.ProfileActivity;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.utils.ImageUtil;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.web.WebMarkers;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.utils.DownloadImageTask;

public class ReclaimActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location lastKnownLocation;
    private final int LOCATION_PERMISSION_CODE = 200;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reclaim);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Add listener to toobar back button
        toolbarListener();
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

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_CODE);
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(checkPermissions()){
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mMap.setMyLocationEnabled(true);

        }

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback(){
            @Override
            public void onMapLoaded() {
                LatLng userLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
            }
        });

        // Disable zoom and move
        disableMapMove();
    }

    private void disableMapMove(){
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
    }

    private void toolbarListener(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHome();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Exception exception) {
        Snackbar.make(findViewById(android.R.id.content),exception.getMessage(),
                Snackbar.LENGTH_LONG).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent msg) {
        if(msg.getMessage().equals("403")){
            logoff();
        }
    }

    public void goToHome(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void logoff(){

        // Delete all key data
        EasySharedPreferences.setStringFromKey(this, EasySharedPreferences.KEY_TOKEN, "");
        EasySharedPreferences.setStringFromKey(this, EasySharedPreferences.KEY_NAME, "");
        EasySharedPreferences.setStringFromKey(this, EasySharedPreferences.KEY_IMAGE, "");
        EasySharedPreferences.setStringFromKey(this, EasySharedPreferences.KEY_CPF, "");
        // Go to login page
        goToLogin();
    }

    private void goToLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
