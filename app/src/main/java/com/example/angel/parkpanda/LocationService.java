package com.example.angel.parkpanda;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class LocationService extends Service {
    private Location mDestination;

    private LocationListener locationListener = new LocationListener() {


        @Override
        public void onLocationChanged(Location location) {

            float distance = (mDestination.distanceTo(location))/1000;
            Log.d("######","CHANGE");
            if (distance < 100) {

                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(
                                Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancelAll();

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(i);

                //String str=this.getClass().getSimpleName();

            } else {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(LocationService.this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Distance")
                                .setContentText(Float.toString(distance));
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(
                                Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, mBuilder.build());

            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    public LocationService() {
    }

    public void onDestroy() {
        super.onDestroy();
        Process.killProcess(Process.myPid());

    }

    @Override
    public boolean stopService(Intent name)
    {
        stopSelf();
        return super.stopService(name);


    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MyService", "onStart: " + intent);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mDestination = intent.getParcelableExtra("destination");
        Log.d("######","CHANGE");
        locationManager.removeUpdates(locationListener);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return 0;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                locationListener);

        return START_STICKY;
    }

}