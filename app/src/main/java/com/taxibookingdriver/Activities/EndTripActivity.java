package com.taxibookingdriver.Activities;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taxibookingdriver.Controller.Config;
import com.taxibookingdriver.Controller.Constant;
import com.taxibookingdriver.Controller.DirectionsJSONParser;
import com.taxibookingdriver.Controller.GraduallyTextView;
import com.taxibookingdriver.Controller.Pref_Master;
import com.taxibookingdriver.Controller.Progress_dialog;
import com.taxibookingdriver.Models.Chat;
import com.taxibookingdriver.MyApplication;
import com.taxibookingdriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EndTripActivity extends AppCompatActivity {

    RelativeLayout rr_end_trip;
    Context context = this;
    Pref_Master pref;
    RequestQueue queue;
    String tripid, startdt, eta, riderid, from, to, latitude, longitude, rtoken;
    SupportMapFragment mapFragment;
    GoogleApiClient mGoogleApiClient;
    GoogleMap map;
    ArrayList<LatLng> markerPoints;
    LocationManager locationManager;
    Double lati, lng;
    Boolean drawline = false;
    int startcalculate = 0;
    Location newlocation, oldlocation;
    float olddistance;
    private ArrayList<LatLng> points;
    Polyline line;
    Marker marker1, marker2;
    String real_distance = "";
    String value;
    private FusedLocationProviderClient mFusedLocationClient;
    Marker mk = null;
    android.support.v7.app.AlertDialog alert;
    ArrayList<Chat> chatArrayList = new ArrayList<>();
    JSONArray jsonArray;
    JSONObject object;
    android.support.v7.app.AlertDialog dialog;


    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_trip);
        pref = new Pref_Master(context);
        // dialog = new Progress_dialog();
        queue = Volley.newRequestQueue(context);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        tripid = pref.getTripid();
        startdt = pref.getStartdt();
        eta = pref.getEta();
        riderid = pref.getRiderid();
        from = pref.getFrom();
        to = pref.getTo();
        latitude = pref.getLatitude();
        longitude = pref.getLongitude();

        Log.e("pref_latitude", pref.getLatitude());
        Log.e("pref_longitude", pref.getLongitude());
        rtoken = pref.getToken();

        points = new ArrayList<LatLng>();
        MapsInitializer.initialize(getApplicationContext());
        markerPoints = new ArrayList<LatLng>();

        MapsInitializer.initialize(getApplicationContext());

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapp);
        getMessage();

        rr_end_trip = findViewById(R.id.rr_end_trip);
        rr_end_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                End_trip();
            }
        });

        onConnected();

        MyApplication.getInstance().trackScreenView("End Trip Screen");
    }


    private void End_trip() {


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


        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("riderid", riderid);
        postParam.put("driverid", pref.getUID());
        postParam.put("tripid", tripid);
        postParam.put("fromlocation", from);
        postParam.put("tolocation", to);
        postParam.put("flat", latitude);
        postParam.put("flong", longitude);
        postParam.put("startdt", startdt);
        postParam.put("eta", eta);
        postParam.put("wamt", Constant.WAITING_CHARGE);
        postParam.put("dtoken", rtoken);
        postParam.put("km", value);


        Log.e("parameter_end_trip_info", " " + new JSONObject(postParam));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.End_trip, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.e("Reposnse", response.toString());

                        String jsonData = response.toString();
//

                        try {

                            JSONObject jobj = new JSONObject(jsonData);

                            if (jobj.getString("status").equals("200")) {

                                JSONArray array = jobj.getJSONArray("data");

                                JSONObject object = array.getJSONObject(0);

                                FirebaseDatabase.getInstance().getReference("cars").child("trip").child(pref.getTripid()).child("tripPage").setValue("3");

                                add_chat_nsg();

                                //MainActivity mainActivity = new MainActivity();
                                //mainActivity.status_change("1");
                                //pref.setDr_status("1");


                            } else {
                                Toast.makeText(context, jobj.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                VolleyLog.e("Error", "Error: " + error.getMessage());

            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Config.getHeaderParam(context);
            }


        };

        jsonObjReq.setTag("POST");
// Adding request to request queue
        queue.add(jsonObjReq);

// Cancelling request
/* if (queue!= null) {
queue.cancelAll(TAG);
} */

    }

    public void add_chat_nsg() throws JSONException {

        // newtonCradleLoading.start();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tripid", tripid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject2 = new JSONObject();
        try {
            jsonObject2.put("riderid", pref.getRiderid());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONObject jsonObject3 = new JSONObject();
        try {
            jsonObject3.put("driverid", pref.getUID());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Map<String, JSONObject> postParam = new HashMap<String, JSONObject>();
        postParam.put("tripid", jsonObject);
        postParam.put("riderid", jsonObject2);
        postParam.put("driverid", jsonObject3);

        jsonArray = new JSONArray();


        for (int i = 0; i < chatArrayList.size(); i++) {
            object = new JSONObject();
            Log.e("MessageCount", "" + chatArrayList.size());
            Log.e("MessageData", "" + chatArrayList.get(i).getMessage());

            object.put("m", chatArrayList.get(i).getMessage());
            object.put("s", chatArrayList.get(i).getSender());
            object.put("r", chatArrayList.get(i).getReceiver());
            object.put("t", chatArrayList.get(i).getTimestamp());
            jsonArray.put(object);
        }
        JSONObject msg = new JSONObject();
        try {
            msg.put("messages", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        postParam.put("messagearray", msg);

        Log.e("parameter_add_msg", " " + new JSONObject(postParam));

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.addchatmsg, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Reposnse", response.toString());
                        try {
                            Log.e("Status", response.getString("status"));

                            if (response.getString("status").equals("200")) {

                                //Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                                Intent i = new Intent(EndTripActivity.this, Trip_detailActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);

                            } else {
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error", "Error: " + error.getMessage());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Config.getHeaderParam(context);
            }
        };
        jsonObjReq.setTag("POST");
        queue.add(jsonObjReq);

    }


    private void getMessage() {
        FirebaseDatabase.getInstance().getReference("cars").child("trip").child(tripid).child("msg").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot chilSnapshot : dataSnapshot.getChildren()) {

                    Chat chat = new Chat();
                    chat.setMessage(String.valueOf(chilSnapshot.child("message").getValue()));
                    chat.setSender(String.valueOf(chilSnapshot.child("sender").getValue()));
                    chat.setReceiver(String.valueOf(chilSnapshot.child("receiver").getValue()));
                    chat.setTimestamp((Long) chilSnapshot.child("timestamp").getValue());
                    chat.setRiderid(riderid);
                    chatArrayList.add(chat);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void onConnected() {

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
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if (location != null) {

                    lati = location.getLatitude();
                    lng = location.getLongitude();
                    drawline = true;

                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            map = googleMap;

                            marker1 = googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)))
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.rider_marker)));

                            marker2 = googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.driver_marker)));


                        }
                    });
                    Log.e("latitude", latitude);
                    Log.e("Longitude", longitude);
                    Log.e("location_lati", ":" + location.getLatitude());
                    Log.e("location_longi", ":" + location.getLongitude());

                    String url = getMapsApiDirectionsUrl(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)), new LatLng(location.getLatitude(), location.getLongitude()));
                    ReadTask downloadTask = new ReadTask();
                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);

                    LocationListener locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            if (drawline) {
                                if (startcalculate == 1) {
                                    newlocation = new Location("");
                                    newlocation.setLatitude(location.getLatitude());
                                    newlocation.setLongitude(location.getLongitude());


                                    float distance = olddistance + oldlocation.distanceTo(newlocation);
                                    olddistance = distance;
                                    real_distance = String.valueOf(olddistance / 1000);


                                    value = real_distance.substring(0, 4);

                                    //value = (int) Math.round(my_distance * 100) / (double) 100;
                                    //Log.e("value", ":" + value);

                                    location = location;
                                    LatLng latlngOne = new LatLng(location.getLatitude(), location.getLongitude());


                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlngOne, 18);
                                    map.animateCamera(cameraUpdate);
                                    map.setBuildingsEnabled(true);
                                    map.setMapStyle(
                                            MapStyleOptions.loadRawResourceStyle(
                                                    context, R.raw.map_style));

                                    animateMarker(location, mk, location.getBearing());
                                }
                                location = location;
                                oldlocation = new Location("");
                                oldlocation.setLatitude(location.getLatitude());
                                oldlocation.setLongitude(location.getLongitude());


                                if (startcalculate == 0) {
                                    Location pickuplocation = new Location("");
                                    pickuplocation.setLatitude(lati);
                                    pickuplocation.setLongitude(lng);


                                    float pickupdistance = pickuplocation.distanceTo(oldlocation);

                                    olddistance = pickupdistance;


                                    location = location;

                                    LatLng latlngOne = new LatLng(location.getLatitude(), location.getLongitude());

          /*  String url = getMapsApiDirectionsUrl(latlngOne, latlngTwo);
           ReadTask downloadTask = new ReadTask();
            // Start downloading json data from Google Directions API
            downloadTask.execute(url);*/
/*
            mk = map.addMarker(new MarkerOptions()
                    .position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                    .flat(true)
                    .anchor(0.5f, 0.5f)
                    .title("office").icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));*/

                                    mk = map.addMarker(new MarkerOptions()
                                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.fourseater)));

     /*       map.addMarker(new MarkerOptions().position(new LatLng(23.6133995, 72.4082554)).title("home"));*/
                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlngOne, 18);
                                    map.animateCamera(cameraUpdate);
                                    map.setMapStyle(
                                            MapStyleOptions.loadRawResourceStyle(
                                                    context, R.raw.map_style));

                                }
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();

                                LatLng latLng = new LatLng(latitude, longitude); //you already have this
                                points.add(latLng); //added
                                redrawLine();//added
                                startcalculate = 1;


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
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, locationListener);

                }
            }
        });


    }

    public static void animateMarker(final Location destination, final Marker marker, final float bearing) {
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
                        marker.setFlat(true);
                        marker.setRotation(computeRotation(v, startRotation, bearing));

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


    private void redrawLine() {

        // map.clear();  //clears all Markers and Polylines

        PolylineOptions options = new PolylineOptions().width(3).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < points.size(); i++) {
            LatLng point = points.get(i);
            options.add(point);
        }
        //add Marker in current position


        //map.addMarker(new MarkerOptions().position(new LatLng(lati, lng)));


        line = map.addPolyline(options); //add Polyline
    }


    private String getMapsApiDirectionsUrl(LatLng origin, LatLng dest) {
// Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

// Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


// Sensor enabled
        String sensor = "sensor=false";

// Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

// Output format
        String output = "json";

// Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private class ReadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            // TODO Auto-generated method stub
            String data = "";
            try {
                MapHttpConnection http = new MapHttpConnection();
                data = http.readUr(url[0]);


            } catch (Exception e) {
                // TODO: handle exception
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }

    }

    public class MapHttpConnection {
        public String readUr(String mapsApiDirectionsUrl) throws IOException {
            String data = "";
            InputStream istream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(mapsApiDirectionsUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                istream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(istream));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                br.close();


            } catch (Exception e) {
                Log.d("Exception reading url", e.toString());
            } finally {
                istream.close();
                urlConnection.disconnect();
            }
            return data;

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";


            if (result.size() < 1) {


                map.clear();

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
                onConnected();
                // Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            } else {

                if (result.size() > 1) {

                    dialog.dismiss();
                }

                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        if (j == 0) {    // Get distance from the list

                            distance = (String) point.get("distance");
                            // pref_master.setDistance(distance);
                            continue;
                        } else if (j == 1) { // Get duration from the list
                            duration = (String) point.get("duration");


                            // pref_master.setTripeta(separated[0]);
                            continue;
                        }

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(5);
                    lineOptions.color(Color.BLACK);

                }


                // Drawing polyline in the Google Map for the i-th route
                map.addPolyline(lineOptions);
             /*   map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location1.getLatitude(), location1.getLongitude())));
                map.animateCamera(CameraUpdateFactory.zoomTo(10));*/


            }

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            builder.include(marker1.getPosition());
            builder.include(marker2.getPosition());

            LatLngBounds bounds = builder.build();

            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.15);

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

            map.animateCamera(cu);


        }
    }

}
