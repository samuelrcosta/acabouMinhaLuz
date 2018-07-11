package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.reclaim;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;

import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.R;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.data.EasySharedPreferences;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.FormProblemException;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.MessageEvent;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.home.HomeActivity;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.presenter.login.LoginActivity;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.web.WebProfileEdit;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.web.WebReclaim;

public class ReclaimActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location lastKnownLocation;
    private final int LOCATION_PERMISSION_CODE = 200;
    private FusedLocationProviderClient mFusedLocationClient;

    MaterialDialog dialog;

    private String data = "";
    private String hora = "";
    private String obs = "";
    private String latitude = "";
    private String longitude = "";

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

    public void showDatePicker(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePicker(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void saveData(View v){
        hideKeyboard();

        try{
            checkForm();
        } catch (FormProblemException e){
            showAlert(e.getMessage());
            return;
        }

        this.data = getStringFromEdit(R.id.data);
        this.hora = getStringFromEdit(R.id.hour);
        this.obs = getStringFromEdit(R.id.obs);

        showDialogWithMessage(getString(R.string.load));

        trySave(this.data, this.hora, this.latitude, this.longitude, this.obs);
    }

    private void showAlert(String message) {
        Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG).show();
    }

    private void checkForm() throws FormProblemException{
        String data = getStringFromEdit(R.id.data);
        String hora = getStringFromEdit(R.id.hour);

        if("".equals(data)){
            throw new FormProblemException(getString(R.string.error_date_empty));
        }
        if("".equals(hora)){
            throw new FormProblemException(getString(R.string.error_hour_empty));
        }
        if("".equals(this.latitude) || "".equals(this.longitude)){
            throw new FormProblemException(getString(R.string.error_location_empty));
        }
    }

    private void trySave(String data, String hora, String latitude, String longitude, String obs) {
        String token = EasySharedPreferences.getStringFromKey(this, EasySharedPreferences.KEY_TOKEN);
        WebReclaim webReclaim = new WebReclaim(token, data, hora, latitude, longitude, obs);
        webReclaim.call();
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 17));
                //setLatLong(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            }
        });

        // Set on map click listener
        clickMapListener();

        // Disable zoom
        disableMapZoom();
    }

    private void clickMapListener(){
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                // Clean all markers ()
                mMap.clear();

                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(point.latitude, point.longitude)).title("Localização Atual");

                // Set lat and long on global variable
                setLatLong(point.latitude, point.longitude);

                // Add mark on map
                mMap.addMarker(marker);
            }
        });
    }

    private void setLatLong(double latitude, double longitude){
        this.latitude = Double.toString(latitude);
        this.longitude = Double.toString(longitude);
    }

    private void disableMapZoom(){
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
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

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            String txt_data = view.getDayOfMonth() + "/" + (view.getMonth()+1) + "/" + view.getYear();
            EditText input = getActivity().findViewById(R.id.data);
            input.setText(txt_data);
        }
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the date chosen by the user
            String txt_hour =hourOfDay + ":" + minute;
            EditText input = getActivity().findViewById(R.id.hour);
            input.setText(txt_hour);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Exception exception) {
        Snackbar.make(findViewById(android.R.id.content),exception.getMessage(),
                Snackbar.LENGTH_LONG).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent msg) {
        if(msg.getMessage().equals("Ok")) {
            dismissDialog();
            confirmDialog();
        }else if(msg.getMessage().equals("403")){
            logoff();
        }
    }

    private void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reclamação registrada com sucesso!");
        //builder.setMessage("This is an alert dialog message");
        builder.setCancelable(false);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goToHome();
            }
        });
        builder.show();
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

    public void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public String getStringFromEdit(int id){
        EditText input = (EditText) findViewById(id);
        return input.getText().toString();
    }

    public void showDialogWithMessage(String message){
        dialog = new MaterialDialog.Builder(this)
                .content(message)
                .progress(true,0)
                .show();
    }

    public void dismissDialog(){
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        goToHome();
    }
}