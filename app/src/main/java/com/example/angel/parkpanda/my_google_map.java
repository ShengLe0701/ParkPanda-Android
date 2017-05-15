package com.example.angel.parkpanda;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;

public class my_google_map extends AppCompatActivity implements View.OnClickListener, LocationListener {
    ImageView iv;
    LatLng target;

    LocationListener mlocListener;
    LocationManager mlocManager;

    LocationManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_google_map);


        WebView webview = (WebView) findViewById(R.id.webView1);
        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setJavaScriptEnabled(true);

        iv = (ImageView) findViewById(R.id.google_map_toolbar_prev);
        iv.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        String exampleString = extras.getString("GPSDATA");

        //intent.putExtra("TARDATALAT",target_pos.latitude);
        //intent.putExtra("TARDATALONG",target_pos.longitude);


        String target_pos_lat = extras.getString("TARDATALAT");
        String target_pos_long = extras.getString("TARDATALONG");

        target = new LatLng(Double.valueOf(target_pos_lat).doubleValue(), Double.valueOf(target_pos_long).doubleValue());


        webview.loadUrl("https://maps.google.com/maps?" + exampleString);


        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);


    }

    private long lastPressedTime;
    private static final int PERIOD = 2000;


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.google_map_toolbar_prev) {


            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            lm.removeUpdates(this);
            gotopayinterface();
        }
    }

    public double getDistanceFromAtoB(LatLng LatLng1, LatLng LatLng2) {
        double distance = 0;
        Location locationA = new Location("A");
        locationA.setLatitude(LatLng1.latitude);
        locationA.setLongitude(LatLng1.longitude);
        Location locationB = new Location("B");
        locationB.setLatitude(LatLng2.latitude);
        locationB.setLongitude(LatLng2.longitude);
        distance = locationA.distanceTo(locationB);
        return distance;
    }


    @Override
    public void onLocationChanged(Location location) {
        double dfdf = getDistanceFromAtoB(new LatLng(location.getLatitude(), location.getLongitude()), target);
        if (dfdf < 100) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            lm.removeUpdates(this);
            finish();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    public void gotopayinterface()
    {
        finish();
    }
}
