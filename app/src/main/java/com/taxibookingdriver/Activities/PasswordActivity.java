package com.taxibookingdriver.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.taxibookingdriver.Controller.Config;
import com.taxibookingdriver.Controller.GraduallyTextView;
import com.taxibookingdriver.Controller.Pref_Master;
import com.taxibookingdriver.Controller.Progress_dialog;
import com.taxibookingdriver.MyApplication;
import com.taxibookingdriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 1/18/2018.
 */

public class PasswordActivity extends AppCompatActivity {

    RelativeLayout rr_pwd;
    TextView rr_forgot;
    Context context = this;
    EditText et_pwd;
    RequestQueue queue;
    String android_id = "";
    String mobile = "";
    Pref_Master pref;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    Location location;
    GoogleApiClient mGoogleApiClient;
    private FirebaseDatabase mFirebaseInstance;
    android.support.v7.app.AlertDialog dialog;
    String value = "";
    public static String reg_id = "";
    LocationListener locationListener;
    LocationManager locationManager;
    TextInputLayout input_layout_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        pref = new Pref_Master(context);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // dialog = new Progress_dialog();
        input_layout_password = findViewById(R.id.input_layout_password);
        Bundle extras = getIntent().getExtras();
        mFirebaseInstance = FirebaseDatabase.getInstance();

        if (extras != null) {
            value = extras.getString("value");
        }


        reg_id = FirebaseInstanceId.getInstance().getToken();

        rr_pwd = findViewById(R.id.rr_pwd);
        rr_forgot = findViewById(R.id.rr_forgot);

        et_pwd = findViewById(R.id.et_pwd);
        queue = Volley.newRequestQueue(this);
        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        rr_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validate();
            }
        });

        rr_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, OTPActivity.class);
                i.putExtra("value", "2");
                startActivity(i);
            }
        });

        MyApplication.getInstance().trackScreenView("Password Screen");


    }

    private void Validate() {
        if (et_pwd.getText().toString().equals("")) {
            if (value.equals("1")) {
                input_layout_password.setError(getString(R.string.Enter_password));
            } else {
                input_layout_password.setError(getString(R.string.Enter_OLD_password));
            }
        } else if (et_pwd.getText().toString().trim().length() < 6) {
            input_layout_password.setError(getString(R.string.Enter_password_valid));
        } else {
            input_layout_password.setErrorEnabled(false);
            if (value.equals("1")) {
                Login();
            } else {
                // rr_forgot.setVisibility(View.GONE);
                OLD_pwd();
            }
        }
    }


    private void Login() {
        getLastLocation();
    }

    public void getLastLocation() {

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
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {

                    Map<String, String> postParam = new HashMap<String, String>();
                    postParam.put("mobile", pref.getMobile());
                    postParam.put("utype", Config.driver);
                    postParam.put("deviceid", android_id);
                    postParam.put("dtoken", reg_id);
                    postParam.put("password", et_pwd.getText().toString());
                    postParam.put("latlong", location.getLatitude() + "," + location.getLongitude());

                    Log.e("parameter", " " + new JSONObject(postParam));


                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Config.login, new JSONObject(postParam),
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    dialog.dismiss();
                                    Log.e("Reposnse", response.toString());
                                    String jsonData = response.toString();

                                    try {
                                        JSONObject jsonObj = new JSONObject(jsonData);
                                        String message = jsonObj.getString("message");

                                        if (jsonObj.getString("status").equals("200")) {
                                            JSONArray jsonArray = new JSONArray(jsonObj.getString("data"));

                                            JSONObject jobj = jsonArray.getJSONObject(0);
                                            String fname = jobj.getString("fnm");
                                            String lname = jobj.getString("lnm");
                                            pref.setUID(jobj.getString("uid"));

                                            Log.e("pref_UID", pref.getUID());

                                            mFirebaseInstance.getReference("cars").child("driverDetail").child(jobj.getString("uid")).child("driverstatus").setValue("0");

                                            Intent i = new Intent(context, MainActivity.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);


                                        } else {
                                            et_pwd.setText("");
                                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
                } else {
                    NewLocation();
                }
            }
        });

    }

    public void NewLocation() {
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

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    locationManager.removeUpdates(locationListener);

                    Map<String, String> postParam = new HashMap<String, String>();
                    postParam.put("mobile", pref.getMobile());
                    postParam.put("utype", Config.driver);
                    postParam.put("deviceid", android_id);
                    postParam.put("dtoken", reg_id);
                    postParam.put("password", et_pwd.getText().toString());
                    postParam.put("latlong", location.getLatitude() + "," + location.getLongitude());

                    Log.e("parameter", " " + new JSONObject(postParam));


                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Config.login, new JSONObject(postParam),
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    dialog.dismiss();
                                    Log.e("Reposnse", response.toString());
                                    String jsonData = response.toString();

                                    try {
                                        JSONObject jsonObj = new JSONObject(jsonData);
                                        String message = jsonObj.getString("message");

                                        if (jsonObj.getString("status").equals("200")) {
                                            JSONArray jsonArray = new JSONArray(jsonObj.getString("data"));

                                            JSONObject jobj = jsonArray.getJSONObject(0);
                                            String fname = jobj.getString("fnm");
                                            String lname = jobj.getString("lnm");
                                            pref.setUID(jobj.getString("uid"));

                                            Log.e("pref_UID", pref.getUID());

                                            mFirebaseInstance.getReference("cars").child("driverDetail").child(jobj.getString("uid")).child("driverstatus").setValue("0");

                                            Intent i = new Intent(context, MainActivity.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);


                                        } else {
                                            et_pwd.setText("");
                                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
                } else {
                    dialog.dismiss();
                    Toast.makeText(context, getString(R.string.Location_not_found), Toast.LENGTH_LONG).show();
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

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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


    private void OLD_pwd() {

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
        postParam.put("mobile", pref.getMobile());
        postParam.put("password", et_pwd.getText().toString());
        postParam.put("utype", Config.driver);
        postParam.put("reason", "change-password");
        postParam.put("otpcheck", "no");


        Log.e("parameter", " " + new JSONObject(postParam));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.Reset_pwd, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.e("Reposnse", response.toString());

// {"status":200,"activity":"reset-success","message":"Your Password has been changed Successfully !","data":[{"uid":"1516340787249"}]}
                        String jsonData = response.toString();
//
                        try {
                            JSONObject jobj = new JSONObject(jsonData);
                            if (jobj.getString("activity").equals("valid")) {
                                Intent i = new Intent(context, NewPasswordActivity.class);
                                startActivity(i);
                            } else {
                                et_pwd.setError(getString(R.string.Invalid_Old_Password));
                                //Toast.makeText(context, jobj.getString("message"), Toast.LENGTH_LONG).show();
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

    }


}
