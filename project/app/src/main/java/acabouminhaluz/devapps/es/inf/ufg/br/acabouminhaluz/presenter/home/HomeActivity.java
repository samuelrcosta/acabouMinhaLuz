package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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

import java.util.ArrayList;

import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.R;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.data.EasySharedPreferences;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.MapMarker;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.MessageEvent;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.claims.ClaimsActivity;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.login.LoginActivity;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.profile.ProfileActivity;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.reclaim.ReclaimActivity;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.utils.ImageUtil;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.web.WebMarkers;

public class HomeActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location lastKnownLocation;
    private final int LOCATION_PERMISSION_CODE = 200;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Load user avatar image
        loadAvatarImage();
        // Start navigation controls icons
        navigatitionControls();
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

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){

        switch(permsRequestCode){
            case LOCATION_PERMISSION_CODE:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    finish();
                    startActivity(getIntent());
                }else{
                    System.exit(1);
                }
                break;
        }

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

            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback(){
                @Override
                public void onMapLoaded() {
                    LatLng userLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    getMarkers(userLatLng);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
                }
            });

            // Multiline snippet
            setMultilineSinippets();

            // Disable zoom and move
            disableMapMove();
        }
    }

    private void disableMapMove(){
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
    }

    private void setMultilineSinippets(){
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }

    private void getMarkers(LatLng userLatLng){
        double latitude = userLatLng.latitude;
        double longitude = userLatLng.longitude;

        WebMarkers markers = new WebMarkers(String.valueOf(latitude), String.valueOf(longitude));
        markers.call();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ArrayList<MapMarker> mapMarkers) {
        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.map_icon);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        for(MapMarker mapMarker : mapMarkers){
            LatLng userLatLng = new LatLng(Double.parseDouble(mapMarker.getLatitude_problema()),
                    Double.parseDouble(mapMarker.getLongitude_problema()));

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(userLatLng)
                    .title("Reclamação")
                    .snippet(mapMarker.getUsuario() + "\n" + mapMarker.getData_problema() + "\n" + mapMarker.getHora_problema())
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    .flat(true);

            mMap.addMarker(markerOptions);
        }

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

    public void navigatitionControls(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_map);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_map:
                    break;
                case R.id.action_claims:
                    goToClaims();
                    break;
            }
            return true;
            }
        });
    }

    public void goToClaims(){
        Intent intent = new Intent(this, ClaimsActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToProfile(View v){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToReclaim(View v){
        Intent intent = new Intent(this, ReclaimActivity.class);
        startActivity(intent);
        finish();
    }

    public void loadAvatarImage(){
        de.hdodenhof.circleimageview.CircleImageView content = (de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.image);
        // Get base64 image and convert to bitmap
        Bitmap image = ImageUtil.convert(EasySharedPreferences.getStringFromKey(this, EasySharedPreferences.KEY_IMAGE));
        content.setImageBitmap(image);
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
