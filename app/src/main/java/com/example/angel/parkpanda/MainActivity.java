package com.example.angel.parkpanda;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;

import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.braintreepayments.api.BraintreePaymentActivity;
import com.braintreepayments.api.PaymentRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.security.token.TokenGenerator;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity implements LocationListener, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMarkerClickListener, OnMapReadyCallback, GoogleMap.OnMapClickListener {

    DrawerLayout drawer;
    GoogleMap mMap;
    PopupWindow pw;
    TextView tv;
    Intent google_map_intent;
    SupportMapFragment supportMapFragment;
    LatLng origional_pos, target_pos;
    String FIRE_URL = "https://parkpanda-1372.firebaseio.com/";
    Firebase fireref;
    FirebaseAuth fireauth;
    ProgressDialog progress;
    ImageView iv_bottm_toolbar_control;
    LatLng tempCameraPos;
    private HashMap<Marker, MyMarker> mMarkersHashMap;
    BottomSheetBehavior mBottomSheetBehavior;
    private ArrayList<MyMarker> mMyMarkersArray = new ArrayList<MyMarker>();
    String BRAINTREE_SERVER="http://ec2-54-254-198-176.ap-southeast-1.compute.amazonaws.com:3000";
    String BRAINTREE_TOKEN;
    String BRAINTREE_AMOUNT;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private GoogleApiClient google_client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Firebase.setAndroidContext(getApplicationContext());

        fireref = new Firebase(FIRE_URL);
        fireauth = FirebaseAuth.getInstance();
        if (checkAlradyLogin() == 1) {
            progress = ProgressDialog.show(this, "",
                    "Wating....", true);

            DatabaseReference mDatabase;
            mDatabase = FirebaseDatabase.getInstance().getReference();

            String userId = fireauth.getCurrentUser().getUid().toString();
            mDatabase.child("profiles").child(userId).addListenerForSingleValueEvent(
                    new ValueEventListener() {

                        @Override
                        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

                            FIRE_USER user = dataSnapshot.getValue(FIRE_USER.class);
                            if ((user.getFirstname() != null) && (user.getLastname() != null)) {
                                LOGININFO.email = fireauth.getCurrentUser().getEmail().toString();
                                LOGININFO.firstname = user.getFirstname();
                                LOGININFO.lastname = user.getLastname();
                                LOGININFO.carlicensenumber = user.getcarlicensenumber();
                                LOGININFO.flag = 10;
                                setNavBarUserInfo();
                                progress.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // et_password.setText("");
                            progress.dismiss();
                        }

                    });
        }
        google_client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        initGUI();
        initGoogleMap();
        plotMarkers(mMyMarkersArray);
        initViewSetting(true);

    }

    void initNavWindow() {
        Toolbar action_my_bar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(action_my_bar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, action_my_bar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                setNavBarUserInfo();
                super.onDrawerOpened(drawerView);

            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
    }

    public void initViewSetting(boolean condition) {

        ImageView iv_search = (ImageView) findViewById(R.id.toolbar_search);
        ImageView iv_control = (ImageView) findViewById(R.id.toolbar_control);
        ImageView iv_prev = (ImageView) findViewById(R.id.toolbar_prev);
        /*1:contorl,2:prev,3:search*/
        if (condition == true) {
            iv_search.setVisibility(View.VISIBLE);
            iv_control.setVisibility(View.VISIBLE);
            iv_prev.setVisibility(View.GONE);
        } else {
            iv_search.setVisibility(View.GONE);
            iv_control.setVisibility(View.GONE);
            iv_prev.setVisibility(View.VISIBLE);
        }


    }

    private void plotMarkers(ArrayList<MyMarker> markers) {
        if (markers.size() > 0) {
            for (MyMarker myMarker : markers) {
                String string_subtitle;
                int freespot;
                MarkerOptions markerOption = new MarkerOptions().position(new LatLng(Double.valueOf(myMarker.getmLat()), Double.valueOf(myMarker.getmLon())));
                markerOption.title(myMarker.getmName());
                String pound = "\u00a3";

                string_subtitle = "Price: " + myMarker.getmPrice() + pound+"/h, FreeSpots: " + myMarker.getmFreeSpace();
                markerOption.snippet(string_subtitle);
                freespot = Integer.parseInt(myMarker.getmFreeSpace());

                if (freespot < 10) {
                    Bitmap icon = drawTextToBitmap(R.drawable.markerred, myMarker.getmFreeSpace());
                    markerOption.icon(BitmapDescriptorFactory.fromBitmap(icon));
                } else if (freespot < 50) {
                    Bitmap icon = drawTextToBitmap(R.drawable.markeryellow, myMarker.getmFreeSpace());
                    markerOption.icon(BitmapDescriptorFactory.fromBitmap(icon));
                } else {
                    Bitmap icon = drawTextToBitmap(R.drawable.markergreen, myMarker.getmFreeSpace());
                    markerOption.icon(BitmapDescriptorFactory.fromBitmap(icon));
                }
                //markerOption.
                Marker currentMarker = mMap.addMarker(markerOption);
                mMarkersHashMap.put(currentMarker, myMarker);

            }
        }
    }

    public void showTempLocation(GoogleMap googleMap) {


//        public MyMarker(String label, String freeSpot, String totalSpot,String price, Double latitude, Double longitude)
        //mMyMarkersArray.add(new MyMarker("NCP Car Park London", "88","150","4", Double.parseDouble("51.508405"), Double.parseDouble("-0.131460")));
        //mMyMarkersArray.add(new MyMarker("Forum Magnum Square", "78","100","5", Double.parseDouble("51.510410"), Double.parseDouble("-0.129685")));
        //mMyMarkersArray.add(new MyMarker("Albert Embankment, SE1", "68","110","6", Double.parseDouble("51.510434"), Double.parseDouble("-0.128046")));
        //mMyMarkersArray.add(new MyMarker("Elverton Street, SW1P2QG", "8","130","7", Double.parseDouble("51.507436"), Double.parseDouble("-0.125943")));
        //mMyMarkersArray.add(new MyMarker("Gillingham Street, SW1V", "18","180","2", Double.parseDouble("51.509593"), Double.parseDouble("-0.132981")));

        plotMarkers(mMyMarkersArray);


    }

    public void initGoogleMap() {
        supportMapFragment = supportMapFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction FT = fragmentManager.beginTransaction();
        FT.replace(R.id.flContent, supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);
    }


    void showhideFootWindow(int condition) {
        View bottomSheet = findViewById(R.id.bottom_sheet);
        View footer_view = findViewById(R.id.footer);
        if (condition == 1) {
            bottomSheet = findViewById(R.id.bottom_sheet);
            mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            mBottomSheetBehavior.setPeekHeight(205);


            footer_view.setVisibility(View.VISIBLE);
            bottomSheet.setVisibility(View.VISIBLE);


        } else {

            footer_view.setVisibility(View.GONE);
            bottomSheet.setVisibility(View.GONE);
        }
    }

    void setNavBarUserInfo() {
        TextView tv_name = (TextView) findViewById(R.id.nav_bar_header_name);
        TextView tv_email = (TextView) findViewById(R.id.nav_bar_header_email);
        tv_name.setText(LOGININFO.firstname + "   " + LOGININFO.lastname);
        tv_email.setText(LOGININFO.email);

    }

    void initGUI() {
        initNavWindow();
        initViewSetting(true);
        showhideFootWindow(0);

        tv = (TextView) findViewById(R.id.txt_error_text);
        tv.setText("");

        ImageView iv_search = (ImageView) findViewById(R.id.toolbar_search);
        ImageView iv_control = (ImageView) findViewById(R.id.toolbar_control);
        ImageView iv_prev = (ImageView) findViewById(R.id.toolbar_prev);
        ImageView iv_nav = (ImageView) findViewById(R.id.bottom_toolbar_nav);

        Button btn_payment = (Button) findViewById(R.id.reserve_button);
        if (checkAlradyLogin() == 1) {
            btn_payment.setEnabled(true);
        } else {
            btn_payment.setEnabled(false);
        }

        // iv_bottm_toolbar_control=(ImageView)findViewById(R.id.image_bottom_toolbar_control);
        // iv_bottm_toolbar_control.setOnClickListener(this);
        btn_payment.setOnClickListener(this);
        iv_control.setOnClickListener(this);
        iv_prev.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        iv_nav.setOnClickListener(this);

        FacebookSdk.sdkInitialize(getApplicationContext());


    }

    public int checkAlradyLogin() {
        int result = 0;
        if (fireauth.getCurrentUser() != null) {
            String ad = fireauth.getCurrentUser().getUid();
            if (ad != null) {
                result = 1;
                LOGININFO.flag = 10;
            }
            else {
                result = 2;
                LOGININFO.carlicensenumber = " ";
                LOGININFO.firstname = " ";
                LOGININFO.email = " ";
                LOGININFO.flag = 1;
                LOGININFO.lastname = " ";
            }
        } else {
            result = 3;
            LOGININFO.carlicensenumber = " ";
            LOGININFO.firstname = " ";
            LOGININFO.email = " ";
            LOGININFO.flag = 1;
            LOGININFO.lastname = " ";
        }
        return result;
    }

    private long lastPressedTime;
    private static final int PERIOD = 2000;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            switch (event.getAction()) {
                case KeyEvent.ACTION_DOWN:
                    if (event.getDownTime() - lastPressedTime < PERIOD) {
                        System.exit(0);
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), "Press again to exit.",
                                Toast.LENGTH_SHORT).show();
                        lastPressedTime = event.getEventTime();
                    }
                    return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        showhideFootWindow(0);
        if (id == R.id.reserve_button) {


            if (checkAlradyLogin() == 1) {

                setPaymentForXX(BRAINTREE_AMOUNT);
            }
        }
        if (id == R.id.toolbar_search) {
            initViewSetting(false);
            Intent intent = new Intent(getApplicationContext(), Find_Park.class);
            startActivityForResult(intent, 2);// Activity is started with requestCode 2
            // finish();


        } else if (id == R.id.toolbar_control) {
            setNavBarUserInfo();
            drawer.openDrawer(findViewById(R.id.nvView));
        } else if (id == R.id.toolbar_prev) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.flContent);
            final Class<? extends Fragment> aClass = currentFragment.getClass();
            String str = aClass.getName();
            doPrevAction(str);

            //  Toast.makeText(getApplicationContext(), "dsadsa", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.bottom_toolbar_nav) {
/*
            Fragment fragment = new mywebview();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction FT = fragmentManager.beginTransaction();
*/


            /*
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");


            Location loc = new Location("dummyprovider");
            loc.setLatitude(target_pos.latitude);
            loc.setLongitude(target_pos.longitude);

            launchTestService(loc);
            startActivity(mapIntent);*/


            // finish();


        }

    }

    public void launchTestService(Location loc) {
        Intent intent_location_service = new Intent(this, LocationService.class);
        intent_location_service.putExtra("destination", loc);
        startService(intent_location_service);
    }

    public void doPrevAction(String strClass) {

        if (strClass.contains("change_password")) {
            Fragment fragment = new my_account();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction FT = fragmentManager.beginTransaction();
            FT.setCustomAnimations(R.anim.enter_from_left,
                    R.anim.exit_to_right);
            FT.replace(R.id.flContent, fragment).commit();
        }
        if (strClass.contains("my_account")) {
            initGoogleMap();

           /* supportMapFragment = supportMapFragment.newInstance();
            supportMapFragment.getMapAsync(this);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction FT = fragmentManager.beginTransaction();
            FT.replace(R.id.flContent, supportMapFragment).commit();
            */


            plotMarkers(mMyMarkersArray);
            initViewSetting(true);

        } else if (strClass.contains("log_in")) {


           /* supportMapFragment = supportMapFragment.newInstance();
            supportMapFragment.getMapAsync(this);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction FT = fragmentManager.beginTransaction();
            FT.replace(R.id.flContent, supportMapFragment).commit();*/
            initGoogleMap();
            plotMarkers(mMyMarkersArray);
            initViewSetting(true);

        } else if (strClass.contains("create_new_account")) {
            Fragment fragment = new log_in();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction FT = fragmentManager.beginTransaction();
            FT.setCustomAnimations(R.anim.enter_from_left,
                    R.anim.exit_to_right);
            FT.replace(R.id.flContent, fragment).commit();
        } else if (strClass.contains("help_dialog")) {
            // pw.dismiss();
            /*supportMapFragment = supportMapFragment.newInstance();
            supportMapFragment.getMapAsync(this);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction FT = fragmentManager.beginTransaction();
            FT.replace(R.id.flContent, supportMapFragment).commit();


            plotMarkers(mMyMarkersArray);


            initViewSetting(true);*/
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        showhideFootWindow(0);
        if (id == R.id.nav_menu_findparking) {
            Intent intent = new Intent(getApplicationContext(), Find_Park.class);
            startActivityForResult(intent, 2);// Activity is started with requestCode 2
            // finish();
        }
        if (id == R.id.nav_menu_account) {
            initViewSetting(false);
            if (LOGININFO.flag == 10) {
                Fragment fragment = new my_account();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction FT = fragmentManager.beginTransaction();
                FT.setCustomAnimations(R.anim.enter_from_right,
                        R.anim.exit_to_left);
                FT.replace(R.id.flContent, fragment).commit();
            } else {
                Fragment fragment = new log_in();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction FT = fragmentManager.beginTransaction();
                FT.setCustomAnimations(R.anim.enter_from_right,
                        R.anim.exit_to_left);
                FT.replace(R.id.flContent, fragment).commit();
            }

        }
        if (id == R.id.nav_menu_help) {
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;

            if (currentapiVersion > 21) {
                showhideFootWindow(0);
                initViewSetting(true);

                LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.ly_my_help, null);
                layout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popupanim));
                PopupWindow optionspu = new PopupWindow(layout, DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT);

                optionspu.setFocusable(true);
                optionspu.showAtLocation(layout, Gravity.CENTER, 0, 0);
                optionspu.update(0, 0, DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT);
                optionspu.setAnimationStyle(R.anim.popupanim);
            } else {

                // initViewSetting(false);

/*                Fragment fragment = new help_dialog();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction FT = fragmentManager.beginTransaction();
                FT.setCustomAnimations(R.anim.enter_from_right,
                        R.anim.exit_to_left);
                FT.replace(R.id.flContent, fragment).commit();*/

                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.fragment_help_dialog, null);
                final PopupWindow optionspu = new PopupWindow(popupView, DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT);

                optionspu.setFocusable(true);
                optionspu.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                Button btnDismiss = (Button) popupView
                        .findViewById(R.id.btn_help_close);

                btnDismiss.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Toast.makeText(getApplicationContext(), "dsdsa", Toast.LENGTH_SHORT).show();
                        optionspu.dismiss();
                        plotMarkers(mMyMarkersArray);
                        initViewSetting(true);
                    }

                });
            }

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    public Location getLocation() {
        Location location = null;
        LocationManager locationManager;
        boolean isGPSEnabled = false;
        boolean isNetworkEnabled = false;
        final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
        double longitude, latitude;
        long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return null;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return location;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showTempLocation(googleMap);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        View mapView = supportMapFragment.getView();
        if (mapView != null &&
                mapView.findViewById(1) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }

        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener()
        {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                initViewSetting(false);
                MyMarker myMarker = mMarkersHashMap.get(marker);
                Intent intent = new Intent(getApplicationContext(), paymentInterface.class);
                intent.putExtra("infokey",myMarker);
                startActivityForResult(intent, 35);// Activity is started with requestCode 2
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                initViewSetting(false);
                MyMarker myMarker = mMarkersHashMap.get(marker);
                Intent intent = new Intent(getApplicationContext(), paymentInterface.class);
                intent.putExtra("infokey",myMarker);
                startActivityForResult(intent, 35);// Activity is started with requestCode 2


            }
        });


        if (tempCameraPos != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tempCameraPos, 15.0f));
        else {
            Location df = getLocation();
           // df = mMap.getMyLocation();

            if (df != null)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(df.getLatitude(), df.getLongitude()), 15.0f));
                TRACKPOSITION.src_Pos = new LatLng(getLocation().getLatitude(),getLocation().getLongitude());
        }

    }

    private Location MMgetMyLocation() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        Location location = service.getLastKnownLocation(provider);

        return location;
    }


    public Location moveMyPosCamera() {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (myLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            String provider = lm.getBestProvider(criteria, true);
            myLocation = lm.getLastKnownLocation(provider);
        }
        return myLocation;
    }
    @Override
    public boolean onMarkerClick(Marker marker) {

        ///marker.get

        MyMarker myMarker = mMarkersHashMap.get(marker);
        double ff1 = mMap.getMyLocation().getLatitude();
        double ff2 = mMap.getMyLocation().getLongitude();
        origional_pos = new LatLng(ff1, ff2);
        target_pos = marker.getPosition();
        double kmdistance;
        double temp_distacne = getDistanceFromAtoB(origional_pos, target_pos);

        Button btn_payment = (Button) findViewById(R.id.reserve_button);

        if(checkAlradyLogin()==1)
        {
            tv.setText("");
            btn_payment.setEnabled(true);
        }
        else
        {
            tv.setText("You must login first!");
            btn_payment.setEnabled(false);
        }
        TextView tv_distance = (TextView) findViewById(R.id.bottom_toolbar_distance);
        if(temp_distacne<1000) {
            kmdistance = temp_distacne;
            tv_distance.setText("" + String.format( "%.1f", kmdistance)+" m");
        }
        else {
            kmdistance=temp_distacne/1000;
            tv_distance.setText("" + String.format( "%.1f", kmdistance)+" km");
        }


        TextView tv_name=(TextView)findViewById(R.id.bottom_toolbar_name);
        tv_name.setText(marker.getTitle());

        TextView tv_deatil_name=(TextView)findViewById(R.id.detail_address);


        TextView tv_money=(TextView)findViewById(R.id.detail_money);
        BRAINTREE_AMOUNT=myMarker.getmPrice();
        String pound = "\u00a3";

        tv_money.setText(pound+" "+myMarker.getmPrice()+" / hour");

        TextView tv_time=(TextView)findViewById(R.id.detail_time);
        tv_time.setText(myMarker.getmTime());

        TextView tv_about=(TextView)findViewById(R.id.detail_about);
        tv_about.setText(myMarker.getmDesc());

        showhideFootWindow(0);
        return false;
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1003) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentMethodNonce paymentMethodNonce = data.getParcelableExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
                //
                String nonce = paymentMethodNonce.getNonce();
                postNonceToServer(nonce);
                // Send the nonce to your server.
            }
        }

        else if (requestCode == 2) {
            showGoogleSelectMarker(data);
            initViewSetting(true);
        }
        else if (requestCode == 35) {
            //showGoogleSelectMarker(data);
            initViewSetting(true);
        }
    }
    public void showGoogleSelectMarker(Intent data)
    {
        String message = data.getStringExtra("MESSAGE");
        String str_lat=data.getStringExtra("Lat");
        String str_lon=data.getStringExtra("Lon");



        mMap.clear();

        if(!(message.equals("null"))) {
            tempCameraPos = new LatLng(Double.valueOf(str_lat).doubleValue(), Double.valueOf(str_lon).doubleValue());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tempCameraPos, 15.0f));
        }
        mMyMarkersArray.clear();
        fireref=new Firebase("https://parkpanda-1372.firebaseio.com/parkinginfos");
        fireref.child(message).child("parkinginfos").addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mMarkersHashMap = new HashMap<Marker, MyMarker>();

                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    FIREPARKINFO newinfo=messageSnapshot.getValue(FIREPARKINFO.class);
                    mMyMarkersArray.add(new MyMarker(newinfo.getaddress(),newinfo.getdesc(),newinfo.getfreeSpace(),newinfo.getlat(),newinfo.getlon(),newinfo.getname(),newinfo.getprice(),newinfo.gettime(),newinfo.gettotalSpace(),newinfo.getImage()));
                }
                plotMarkers(mMyMarkersArray);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void postNonceToServer(String nonce) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("amount", BRAINTREE_AMOUNT);
        params.put("payment_method_nonce", nonce);
        client.post(BRAINTREE_SERVER+"/payment", params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(getApplicationContext(), "Payment Failure", Toast.LENGTH_LONG).show();
                    }
                }
        );
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
    public void onStart() {
        super.onStart();
        google_client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.example.angel.parkpanda/http/host/path")
        );
        AppIndex.AppIndexApi.start(google_client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();


        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.

                Uri.parse("http://host/path"),

                Uri.parse("android-app://com.example.angel.parkpanda/http/host/path")
        );
        AppIndex.AppIndexApi.end(google_client, viewAction);
        google_client.disconnect();
    }




    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    @Override
    public void onBackPressed()
    {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed();
            return;
        }
        else { Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show(); }

        mBackPressed = System.currentTimeMillis();
    }
    public void setPaymentForXX(final String strAmount)
    {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(BRAINTREE_SERVER+"/token", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String clientToken) {

                Log.d("######",clientToken);
                JSONObject mainObject = null;
                try {
                        mainObject = new JSONObject(clientToken);
                    BRAINTREE_TOKEN = mainObject.getString("clientToken");
                        //BRAINTREE_TOKEN=uniObject.toString();
                        Log.d("######",BRAINTREE_TOKEN);

                    PaymentRequest paymentRequest = new PaymentRequest()
                            .clientToken(BRAINTREE_TOKEN);
                    String pound = "\u00a3";

                    paymentRequest.primaryDescription("Amount: "+pound+" "+strAmount+"/h");
                   // paymentRequest.amount("$"+strAmount);
                  //  paymentRequest.actionBarTitle("Pay For ParkPanda");

                    paymentRequest.submitButtonText("Pay "+strAmount+" "+pound+" PURCHASE");
                    startActivityForResult(paymentRequest.getIntent(getApplicationContext()), 1003);


                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }




    public Bitmap drawTextToBitmap(int gResId, String gText) {
        Resources resources = getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, gResId);
        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();

        if ( bitmapConfig == null ) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(0, 0, 0));
        paint.setTextSize((int) (15 * scale));
        paint.setShadowLayer(1f, 0f, 1f, Color.BLACK);

        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width())/2;
        int y = (bitmap.getHeight() + bounds.height())/2-10;
        canvas.drawText(gText, x, y, paint);

        return bitmap;
    }

    @Override
    public void onLocationChanged(Location location) {

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
}
