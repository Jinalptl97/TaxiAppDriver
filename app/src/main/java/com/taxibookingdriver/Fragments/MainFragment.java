package com.taxibookingdriver.Fragments;


import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.taxibookingdriver.Controller.GPS_Service;
import com.taxibookingdriver.Controller.GraduallyTextView;
import com.taxibookingdriver.Controller.Pref_Master;
import com.taxibookingdriver.Controller.Progress_dialog;
import com.taxibookingdriver.MyApplication;
import com.taxibookingdriver.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    SupportMapFragment mapFragment;
    GoogleMap getMap;
    LatLng latlngOne;
    private int markerCount;
    Location locationn;
    int center = 0;
    GoogleMap map;
    Marker mk = null;
    int STORAGE_PERMISSION_CODE_LOCATION = 1243;

    android.support.v7.app.AlertDialog dialog;
    RelativeLayout rr_map;
    Pref_Master pref;
    Context context;
    ImageView myLocationButton;
    private FusedLocationProviderClient mFusedLocationClient;
    LocationListener locationListener;
    LocationManager locationManager;

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        context = getActivity();
        pref = new Pref_Master(context);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
//        RelativeLayout appdrawer = getActivity().findViewById(R.id.appdrawer);
//        appdrawer.setVisibility(View.VISIBLE);
        pref.setBack_value("");
        mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        // dialog = new Progress_dialog();

        LayoutInflater li = LayoutInflater.from(context);
        View vv = li.inflate(R.layout.progress_dialog_layout, null);
        dialog = new android.support.v7.app.AlertDialog.Builder(context, R.style.cart_dialog).setView(vv).show();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        GraduallyTextView txt_load;
        ImageView img_gif;

        txt_load = (GraduallyTextView) vv.findViewById(R.id.txt_load);
        img_gif = (ImageView) vv.findViewById(R.id.img_gif);

        Glide.with(context)
                .load(R.drawable.taxi_gif)
                .asGif()
                .placeholder(R.drawable.taxi_gif)
                .crossFade()
                .into(img_gif);
        txt_load.startLoading();
        dialog.show();

        rr_map = view.findViewById(R.id.rr_map);
        requestLOCAtion();

        myLocationButton = view.findViewById(R.id.myLocationButton);
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(locationn.getLatitude(), locationn.getLongitude()), 17);
                map.animateCamera(cameraUpdate, 800, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onCancel() {

                    }
                });

            }
        });
        getlastLocation();


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("Main Fragment");
    }

    private void getlastLocation() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if (location != null) {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            map = googleMap;

                            locationn = location;
                            latlngOne = new LatLng(location.getLatitude(), location.getLongitude());


                            if (markerCount == 1) {

                                Intent i = new Intent(getActivity(), GPS_Service.class);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                                    Context c = getActivity();
                                    c.startService(getServiceIntent(c));
                                } else {
                                    context.startService(i);
                                }


                                if (center == 1 || map.getCameraPosition().zoom == 17) {


                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlngOne, 17);
                                    map.animateCamera(cameraUpdate);
                                    map.setMapStyle(
                                            MapStyleOptions.loadRawResourceStyle(
                                                    getActivity(), R.raw.map_style));


                                    animateMarker(location, mk, location.getBearing());
                        /*     speed.setText("" + customer.speed);*/
/*
                                    Log.e("Speed", " " + customer.speed);
*/

                                    center = 0;


                                } else if (map.getCameraPosition().zoom < 17 || map.getCameraPosition().zoom > 17) {


                                    animateMarker(location, mk, location.getBearing());
                               /*     speed.setText("" + customer.speed);
                                    Log.e("Speed", " " + customer.speed);*/

                                }


                            } else if (markerCount == 0) {

                                rr_map.setVisibility(View.VISIBLE);
                                dialog.dismiss();

                                Intent i = new Intent(getActivity(), GPS_Service.class);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                                    Context c = getActivity();
                                    c.startService(getServiceIntent(c));
                                } else {
                                    context.startService(i);
                                }

                                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                map.setMyLocationEnabled(true);
                                map.getUiSettings().setCompassEnabled(false);
                                map.getUiSettings().setTiltGesturesEnabled(false);
                                map.setMaxZoomPreference(17);
                                map.getUiSettings().setMyLocationButtonEnabled(false);


//                                mk = map.addMarker(new MarkerOptions()
//                                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
//                                        .title("office").icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));

                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlngOne, 17);
                                map.animateCamera(cameraUpdate);
                                map.setMapStyle(
                                        MapStyleOptions.loadRawResourceStyle(
                                                getActivity(), R.raw.map_style));

                    /*map.setMapType(GoogleMap.MAP_TYPE_HYBRID);*/
                                markerCount = 1;
                            }

                        }
                    });

                } else {
                    NewLocation();
                }
            }
        });
    }

    public void NewLocation() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                if (location != null) {
                    locationManager.removeUpdates(locationListener);
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            map = googleMap;

                            locationn = location;
                            latlngOne = new LatLng(location.getLatitude(), location.getLongitude());


                            if (markerCount == 1) {

                                Intent i = new Intent(getActivity(), GPS_Service.class);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                                    Context c = getActivity();
                                    c.startService(getServiceIntent(c));
                                } else {
                                    context.startService(i);
                                }


                                if (center == 1 || map.getCameraPosition().zoom == 17) {


                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlngOne, 17);
                                    map.animateCamera(cameraUpdate);
                                    map.setMapStyle(
                                            MapStyleOptions.loadRawResourceStyle(
                                                    getActivity(), R.raw.map_style));


                                    animateMarker(location, mk, location.getBearing());
                        /*     speed.setText("" + customer.speed);*/
/*
                                    Log.e("Speed", " " + customer.speed);
*/

                                    center = 0;


                                } else if (map.getCameraPosition().zoom < 17 || map.getCameraPosition().zoom > 17) {


                                    animateMarker(location, mk, location.getBearing());
                               /*     speed.setText("" + customer.speed);
                                    Log.e("Speed", " " + customer.speed);*/

                                }


                            } else if (markerCount == 0) {

                                rr_map.setVisibility(View.VISIBLE);
                                dialog.dismiss();

                                Intent i = new Intent(getActivity(), GPS_Service.class);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                                    Context c = getActivity();
                                    c.startService(getServiceIntent(c));
                                } else {
                                    context.startService(i);
                                }

                                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                map.setMyLocationEnabled(true);
                                map.getUiSettings().setCompassEnabled(false);
                                map.getUiSettings().setTiltGesturesEnabled(false);
                                map.setMaxZoomPreference(17);
                                map.getUiSettings().setMyLocationButtonEnabled(false);


//                                mk = map.addMarker(new MarkerOptions()
//                                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
//                                        .title("office").icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));

                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlngOne, 17);
                                map.animateCamera(cameraUpdate);
                                map.setMapStyle(
                                        MapStyleOptions.loadRawResourceStyle(
                                                getActivity(), R.raw.map_style));

                    /*map.setMapType(GoogleMap.MAP_TYPE_HYBRID);*/
                                markerCount = 1;
                            }

                        }
                    });
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

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
// TODO: Consider calling
// ActivityCompat#requestPermissions
// here to request the missing permissions, and then overriding
// public void onRequestPermissionsResult(int requestCode, String[] permissions,
// int[] grantResults)
// to handle the case where the user grants the permission. See the documentation
// for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }

    private static Intent getServiceIntent(Context c) {
        return new Intent(c, GPS_Service.class);
    }


    public static void animateMarker(final Location destination, final Marker marker, final Float Bearing) {
        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());

            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(1000); // duration 1 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        marker.setRotation(computeRotation(v, startRotation, Bearing));
                        marker.setFlat(true);
                    } catch (Exception ex) {
                        // I don't care atm..
                    }
                }
            });

            valueAnimator.start();
        }
    }

    private interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolator {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }

    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }

    private void requestLOCAtion() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, STORAGE_PERMISSION_CODE_LOCATION);
    }
}
