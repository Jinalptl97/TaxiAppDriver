package com.taxibookingdriver.Controller;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by raj on 6/16/2016.
 */
public class GPS_Service extends Service {

    private LocationListener listener;
    private LocationManager locationManager;
    Context context = this;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    Pref_Master pref_master;
    DatabaseReference rootRef, demoRef;
    String car_type = "";
    onLocation onLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    public interface onLocation {
        void onLocationChangsss(Location location);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {

        pref_master = new Pref_Master(context);
        // pref_master.setUID(userid);
        // name = pref_master.getUID();
        car_type = pref_master.getCar_type();


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        getLocation();
        return START_STICKY;
    }

    public void getLocation() {
        getLastLocation();

    }


    public void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    if (pref_master.getDr_status().equals("1")) {

                        mFirebaseInstance = FirebaseDatabase.getInstance();

                        mFirebaseDatabase = mFirebaseInstance.getReference("cars").child(pref_master.getCar_type()).child("driverAvailable");

                        GeoFire geoFire = new GeoFire(mFirebaseDatabase);
                        geoFire.setLocation(pref_master.getUID(), new GeoLocation(location.getLatitude(), location.getLongitude()));

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("fname").setValue(pref_master.getFname());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("lname").setValue(pref_master.getLname());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("mobile").setValue(pref_master.getMobile());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("driverstatus").setValue(pref_master.getDr_status());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("email").setValue(pref_master.getEmail());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("car_number").setValue(pref_master.getreg_num());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("car").setValue(pref_master.getCar_type());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("car_color").setValue(pref_master.getColor());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("carModel").setValue(pref_master.getCar_model());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("lat").setValue(location.getLatitude());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("lng").setValue(location.getLongitude());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("bearing").setValue(location.getBearing());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("speed").setValue(((location.getSpeed() * 3600) / 1000));

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("profile").setValue(pref_master.getBucket_url() + pref_master.getUID() + "/" + "Profile" + "/" + pref_master.getBack_img());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("device_id").setValue(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("device_token").setValue(FirebaseInstanceId.getInstance().getToken());


                        getNewLocation();

                    }
                    getNewLocation();
                } else {
                    getNewLocation();
                }
            }
        });

    }

    public void getNewLocation() {

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {

                    if (pref_master.getDr_status().equals("1")) {

                        mFirebaseInstance = FirebaseDatabase.getInstance();

                        mFirebaseDatabase = mFirebaseInstance.getReference("cars").child(pref_master.getCar_type()).child("driverAvailable");

                        GeoFire geoFire = new GeoFire(mFirebaseDatabase);
                        geoFire.setLocation(pref_master.getUID(), new GeoLocation(location.getLatitude(), location.getLongitude()));

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("fname").setValue(pref_master.getFname());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("lname").setValue(pref_master.getLname());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("mobile").setValue(pref_master.getMobile());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("driverstatus").setValue(pref_master.getDr_status());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("email").setValue(pref_master.getEmail());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("car_number").setValue(pref_master.getreg_num());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("car").setValue(pref_master.getCar_type());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("car_color").setValue(pref_master.getColor());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("carModel").setValue(pref_master.getCar_model());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("lat").setValue(location.getLatitude());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("lng").setValue(location.getLongitude());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("bearing").setValue(location.getBearing());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("speed").setValue(((location.getSpeed() * 3600) / 1000));

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("profile").setValue(pref_master.getBucket_url() + pref_master.getUID() + "/" + "Profile" + "/" + pref_master.getBack_img());

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("device_id").setValue(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

                        mFirebaseInstance.getReference("cars").child("driverDetail").child(pref_master.getUID()).child("device_token").setValue(FirebaseInstanceId.getInstance().getToken());


                    }
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

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, listener);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (locationManager != null) {
//            //noinspection MissingPermission
//            locationManager.removeUpdates(listener);
//        }
    }


}
