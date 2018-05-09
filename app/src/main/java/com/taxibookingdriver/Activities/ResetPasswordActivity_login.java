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
import com.google.android.gms.tasks.OnSuccessListener;
import com.taxibookingdriver.Controller.Config;
import com.taxibookingdriver.Controller.GraduallyTextView;
import com.taxibookingdriver.Controller.Pref_Master;
import com.taxibookingdriver.Controller.Progress_dialog;
import com.taxibookingdriver.MyApplication;
import com.taxibookingdriver.R;
import com.taxibookingdriver.Service.MyFirebaseInstanceIDService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 1/18/2018.
 */

public class ResetPasswordActivity_login extends AppCompatActivity {

    RelativeLayout rr_reset;
    Context context = this;
    EditText et_pwd, et_conf_pwd;
    RequestQueue queue;
    Pref_Master pref;
    String android_id;
    GoogleApiClient mGoogleApiClient;
    android.support.v7.app.AlertDialog dialog;
    private FusedLocationProviderClient mFusedLocationClient;
    LocationListener locationListener;
    LocationManager locationManager;
    TextInputLayout input_pwd, input_conf_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        pref = new Pref_Master(context);
        //dialog = new Progress_dialog();

        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        queue = Volley.newRequestQueue(this);
        rr_reset = findViewById(R.id.rr_reset);
        et_pwd = findViewById(R.id.et_pwd);
        et_conf_pwd = findViewById(R.id.et_conf_pwd);
        input_pwd = findViewById(R.id.input_pwd);
        input_conf_pwd = findViewById(R.id.input_conf_pwd);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


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


        rr_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validate();
            }
        });


        MyApplication.getInstance().trackScreenView("Reset Password Login Screen");


    }

    private void Validate() {
        if (et_pwd.getText().toString().equals("")) {
            input_pwd.setError(getString(R.string.Enter_password));
        } else if (et_pwd.getText().toString().trim().length() < 6) {
            input_pwd.setError(getString(R.string.Enter_password_valid));
        } else if (et_conf_pwd.getText().toString().equals("")) {
            input_conf_pwd.setError(getString(R.string.Enter_confirm_password));
            input_pwd.setErrorEnabled(false);
        } else if (!et_pwd.getText().toString().equals(et_conf_pwd.getText().toString())) {
            input_conf_pwd.setError(getString(R.string.Password_does_not_match));
            input_pwd.setErrorEnabled(false);
        } else {
            input_conf_pwd.setErrorEnabled(false);
            Reset_pwd();
        }
    }

    private void Reset_pwd() {
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
                    // Logic to handle location object

                    Map<String, String> postParam = new HashMap<String, String>();
                    postParam.put("mobile", pref.getMobile());
                    postParam.put("password", et_conf_pwd.getText().toString());
                    postParam.put("utype", Config.driver);
                    postParam.put("reason", "device-password");
                    postParam.put("deviceid", android_id);
                    postParam.put("dtoken", MyFirebaseInstanceIDService.rtoken);
                    postParam.put("latlong", location.getLatitude() + "," + location.getLongitude());
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

                                            String otp = jobj.getString("OTP");
                                            Log.e("otp", otp);

                                            Toast.makeText(context, getString(R.string.Otp_successfully), Toast.LENGTH_LONG).show();
                                            Intent i = new Intent(context, OTPActivity_login.class);
                                            i.putExtra("otp", otp);
                                            i.putExtra("password", et_conf_pwd.getText().toString());
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);
                                        } else if (jobj.getString("activity").equals("invalid")) {
                                            Toast.makeText(context, getString(R.string.Mobile_no_already_exist), Toast.LENGTH_LONG).show();
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
                    // Logic to handle location object

                    Map<String, String> postParam = new HashMap<String, String>();
                    postParam.put("mobile", pref.getMobile());
                    postParam.put("password", et_conf_pwd.getText().toString());
                    postParam.put("utype", Config.driver);
                    postParam.put("reason", "device-password");
                    postParam.put("deviceid", android_id);
                    postParam.put("dtoken", MyFirebaseInstanceIDService.rtoken);
                    postParam.put("latlong", location.getLatitude() + "," + location.getLongitude());
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

                                            String otp = jobj.getString("OTP");
                                            Log.e("otp", otp);

                                            Toast.makeText(context, getString(R.string.Otp_successfully), Toast.LENGTH_LONG).show();
                                            Intent i = new Intent(context, OTPActivity_login.class);
                                            i.putExtra("otp", otp);
                                            i.putExtra("password", et_conf_pwd.getText().toString());
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);
                                        } else {
                                            Toast.makeText(context, getString(R.string.Mobile_no_already_exist), Toast.LENGTH_LONG).show();
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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


}
