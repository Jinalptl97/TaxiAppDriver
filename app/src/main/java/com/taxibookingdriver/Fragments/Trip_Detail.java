package com.taxibookingdriver.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.taxibookingdriver.Controller.Config;
import com.taxibookingdriver.Controller.Constant;
import com.taxibookingdriver.Controller.DirectionsJSONParser;
import com.taxibookingdriver.Controller.GraduallyTextView;
import com.taxibookingdriver.Controller.Pref_Master;
import com.taxibookingdriver.Controller.Progress_dialog;
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

import static com.taxibookingdriver.Fragments.Timewise_trip.riderid;
import static com.taxibookingdriver.Fragments.Timewise_trip.tripid;

/**
 * Created by Admin on 1/17/2018.
 */

public class Trip_Detail extends Fragment {

    SupportMapFragment mapFragment;
    RequestQueue queue;
    Pref_Master pref;
    Context context;
    CameraUpdate cu;
    TextView txt_head;
    FragmentManager manager;
    TextView txt_date, txt_time, txt_from, txt_to, txt_fare, txt_coupan, txt_cancel_charge, txt_earning,txt_trip_number;
    Marker marker1, marker2;
    String tolat = "";
    String tolang = "";
    Double dr_lat;
    Double dr_long;
    List<Marker> markersList = new ArrayList<Marker>();
    GoogleMap map;
    Location location;
    ArrayList<LatLng> points;
    String latitude = "";
    String longitude = "";
    Double rid_lat = 0.0;
    Double rid_longg = 0.0;
    LatLngBounds.Builder builder;
    LocationManager locationManager;
    GoogleApiClient mGoogleApiClient;
    final PolylineOptions polylineOptions = new PolylineOptions();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    android.support.v7.app.AlertDialog dialog;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trip_detail, container, false);
        context = getActivity();
        manager = getFragmentManager();
        pref = new Pref_Master(context);
        // dialog = new Progress_dialog();
        queue = Volley.newRequestQueue(context);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapp));

        txt_head = getActivity().findViewById(R.id.headertext);
        txt_head.setText(R.string.Trip_detail);

        txt_date = view.findViewById(R.id.txt_date);
        txt_time = view.findViewById(R.id.txt_time);
        txt_from = view.findViewById(R.id.txt_from);
        txt_to = view.findViewById(R.id.txt_to);
        txt_fare = view.findViewById(R.id.txt_fare);
        txt_coupan = view.findViewById(R.id.txt_coupan);
        txt_cancel_charge = view.findViewById(R.id.txt_cancel_charge);
        txt_trip_number = view.findViewById(R.id.txt_trip_number);
        txt_trip_number.setText(tripid);

        txt_earning = view.findViewById(R.id.txt_earning);
        onConnected();
        Get_info_driver();
        return view;


    }

    @Override
    public void onResume() {

        super.onResume();

        if (getView() == null) {
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_UP && i == KeyEvent.KEYCODE_BACK) {


                    FragmentManager fm = getFragmentManager();
                    fm.popBackStackImmediate();


                }

                return false;
            }
        });
        MyApplication.getInstance().trackScreenView("Trip Detail Fragment");

    }

    public void onConnected() {
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
            public void onSuccess(Location location) {
                if (location != null) {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            // dr_lat = location.getLatitude();
                            // dr_long = location.getLongitude();

                            // Log.e("dr_lat", ";" + dr_lat);
                            //Log.e("dr_long", ";" + dr_long);


                            map = googleMap;
                            //   enableMyLocation();
                            requestLocationPermission();


                            // CameraPosition cameraPosition = CameraPosition.builder().target(new LatLng(dr_lat, dr_long)).zoom(18).build();
                            // map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                        }
                    });
                }
            }
        });
    }

    private void Get_info_driver() {
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
                    postParam.put("driverid", pref.getUID());
                    postParam.put("riderid", riderid);
                    postParam.put("tripid", tripid);

                    Log.e("parameter_driver_data", " " + new JSONObject(postParam));


                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                            Config.trip_into, new JSONObject(postParam),
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    dialog.dismiss();
                                    Log.e("Reposnse", response.toString());

                                    //{"status":200,"activity":"register-success","message":"Registered Successfully !","data":[{"uid":"1516340787249"}]}

                                    String jsonData = response.toString();
//

                                    try {

                                        JSONObject jobj = new JSONObject(jsonData);

                                        if (jobj.getString("status").equals("200")) {

                                            JSONArray array = jobj.getJSONArray("data");

                                            JSONObject object = array.getJSONObject(0);

                                            txt_date.setText(object.getString("date"));
                                            txt_time.setText(object.getString("time"));
                                            txt_from.setText(object.getString("from"));
                                            txt_to.setText(object.getString("to"));
                                            txt_fare.setText(Constant.CURRENCY + object.getString("fare"));
//                                            if (object.getString("coupon").equals("")) {
//                                                txt_coupan.setText("0");
//                                            } else {
//                                                txt_coupan.setText(object.getString("coupon"));
//                                            }
                                            txt_coupan.setText(Constant.CURRENCY + object.getString("cprate"));
                                            txt_cancel_charge.setText(Constant.CURRENCY + object.getString("cnamt"));
                                            txt_earning.setText(Constant.CURRENCY + object.getString("total"));
                                            latitude = object.getString("flat");
                                            longitude = object.getString("flong");
                                            tolat = object.getString("tlat");
                                            tolang = object.getString("tlong");


                                            rid_lat = Double.valueOf(latitude);
                                            rid_longg = Double.valueOf(longitude);

                                            dr_lat = Double.valueOf(tolat);
                                            dr_long = Double.valueOf(tolang);

                                            Log.e("Latitude", ":" + rid_lat);
                                            Log.e("Longitude", ":" + rid_longg);


                                            mapFragment.getMapAsync(new OnMapReadyCallback() {
                                                @Override
                                                public void onMapReady(final GoogleMap googleMap) {

                                                    points = new ArrayList<>();
                                                    points.add(new LatLng(rid_lat, rid_longg));
                                                    points.add(new LatLng(dr_lat, dr_long));

                                                    marker1 = googleMap.addMarker(new MarkerOptions()
                                                            .position(new LatLng(rid_lat, rid_longg))
                                                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.rider_marker)));

                                                    marker2 = googleMap.addMarker(new MarkerOptions()
                                                            .position(new LatLng(dr_lat, dr_long))
                                                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.driver_marker)));

                                                    markersList.add(marker1);
                                                    markersList.add(marker2);


                                                    builder = new LatLngBounds.Builder();
                                                    for (Marker m : markersList) {
                                                        builder.include(m.getPosition());
                                                    }
                                                    int padding = 145;
                                                    LatLngBounds bounds = builder.build();
                                                    cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                                    googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                                                        @Override
                                                        public void onMapLoaded() {

                                                            dialog.dismiss();
                                                            googleMap.moveCamera(cu);
                                                        }
                                                    });


                                                    polylineOptions.addAll(points);
                                                    polylineOptions.width(3);
                                                    polylineOptions.color(Color.BLACK);
                                                    googleMap.addPolyline(polylineOptions);


                                                }
                                            });
                                            Log.e("lat_jin", ":" + location.getLatitude());
                                            Log.e("long_jin", ";" + location.getLongitude());
/*

                                String url = getMapsApiDirectionsUrl(new LatLng(rid_lat, rid_longg), new LatLng(dr_lat, dr_long));
                                ReadTask downloadTask = new ReadTask();
                                // Start downloading json data from Google Directions API
                                downloadTask.execute(url);
*/


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
            }
        });


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

        Log.e("URL", "" + url);

        return url;
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
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
                // Toast.makeText(getActivity().getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
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


                Log.e("Distancebetween", " " + distance);


                // Drawing polyline in the Google Map for the i-th route
                map.addPolyline(lineOptions);
             /*   map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location1.getLatitude(), location1.getLongitude())));
                map.animateCamera(CameraUpdateFactory.zoomTo(10));*/


            }
            markersList.add(marker1);
            markersList.add(marker2);


            map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            context, R.raw.map_style));

            builder = new LatLngBounds.Builder();
            for (Marker m : markersList) {
                builder.include(m.getPosition());
            }
            int padding = 145;
            LatLngBounds bounds = builder.build();
            cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    map.animateCamera(cu);

                }
            });

        }
    }

}
