package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.R;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.BaseActivity;

public class home extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location lastKnownLocation;
    private String[] permissions = {getString(R.string.fine_location_permission), getString(R.string.coarse_location_permission)};
    private final int LOCATION_PERMISSION_CODE = 200;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void requestPermissions(){
        //Checking permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Shows a explain dialog to the user, showing the benefits of allowing permissions that are going to be requested
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION ) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                showDialogWithMessage(getString(R.string.requesting_permisisons));
            }
            //request permission
        } else{
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_CODE);
            // requestPermissions calls the callback onRequestPermissionsResult() to handle response
        }
    }

    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult (int requestCode, String permissions[], int[] grantResults ){
        switch (requestCode){
            case LOCATION_PERMISSION_CODE: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location != null) {
                                lastKnownLocation = location;
                            }
                        }
                    });
                }
            }

        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        requestPermissions();
        LatLng userLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(userLatLng).title("TESTE - Acabou minha luz"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 8));

    }





}
