package com.taxibookingdriver.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taxibookingdriver.BuildConfig;
import com.taxibookingdriver.Controller.Config;
import com.taxibookingdriver.Controller.Constant;
import com.taxibookingdriver.Controller.DialogBox;
import com.taxibookingdriver.Controller.GPS_Service;
import com.taxibookingdriver.Controller.GraduallyTextView;
import com.taxibookingdriver.Controller.Pref_Master;
import com.taxibookingdriver.Controller.Progress_dialog;
import com.taxibookingdriver.MyApplication;
import com.taxibookingdriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SplashActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Pref_Master pref;
    Context context = this;
    int STORAGE_PERMISSION_CODE_LOCATION = 1243;
    LocationManager locationManager;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 55;
    Boolean GpsStatus;
    int location = 0;
    GoogleApiClient googleApiClient;
    int storage = 0;
    RequestQueue queue;
    String android_id = "";
    String version = "";
    //RotateLoading newtonCradleLoading;
    String abc = "";
    android.support.v7.app.AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        pref = new Pref_Master(context);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // dialog = new Progress_dialog();
        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        pref.setDevice_id(android_id);
        queue = Volley.newRequestQueue(this);
        Intent ii = new Intent(context, GPS_Service.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Context c = getActivity();
            context.startService(getServiceIntent(context));
        } else {
            startService(ii);
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                DialogBox.setNoData(context, getString(R.string.Location_disabled), runnable);
            }
            /*Toast.makeText(context, "GPS is disabled!", Toast.LENGTH_LONG).show();*/
            else {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Check_Version();
                    }
                }, 3000);
            }
        } else {
            checkAndRequestPermissions();
        }
        getsetting();
        MyApplication.getInstance().trackScreenView("Splash Screen");
        Log.e("DeviceID", pref.getDevice_id());

    }

    private void requestLOCAtion() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, STORAGE_PERMISSION_CODE_LOCATION);
    }


    private boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.e("DataRequest", "" + requestCode);
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {

            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++) {

                    if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.e("msg", "location granted");

                            final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

                            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                                DialogBox.setNoData(context, getString(R.string.Location_disabled), runnable);
                            }
            /*Toast.makeText(context, "GPS is disabled!", Toast.LENGTH_LONG).show();*/
                            else {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        Check_Version();
                                    }
                                }, 3000);

                            }
            /*Toast.makeText(context, "GPS is enabled!", Toast.LENGTH_LONG).show();*/

                        } else {
                            finish();
                            Log.e("msg", "location not granted");
                        }
                    }
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("Result Code", "" + requestCode);
        if (requestCode == 123) {
            final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                DialogBox.setNoData(context, getString(R.string.Location_disabled), runnable);
            }
            /*Toast.makeText(context, "GPS is disabled!", Toast.LENGTH_LONG).show();*/
            else {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Check_Version();

                    }
                }, 3000);

            }
        } else {
            finish();
        }
    }

    public void Intent() {
        if (location == 1) {
            if (googleApiClient == null) {
                googleApiClient = new GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API).addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this).build();
                googleApiClient.connect();
                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(30 * 1000);
                locationRequest.setFastestInterval(5 * 1000);
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);

                // **************************
                builder.setAlwaysShow(true); // this is the key ingredient
                // **************************

                PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                        .checkLocationSettings(googleApiClient, builder.build());
                result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                    @Override
                    public void onResult(LocationSettingsResult result) {
                        final Status status = result.getStatus();
                        final LocationSettingsStates state = result
                                .getLocationSettingsStates();
                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:
                                // All location settings are satisfied. The client can
                                // initialize location
                                // requests here.


                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied. But could be
                                // fixed by showing the user
                                // a dialog.
                                try {
                                    // Show the dialog by calling
                                    // startResolutionForResult(),
                                    // and check the result in onActivityResult().


                                    status.startResolutionForResult(SplashActivity.this, 1000);
                                } catch (IntentSender.SendIntentException e) {
                                    // Ignore the error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // Location settings are not satisfied. However, we have
                                // no way to fix the
                                // settings so we won't show the dialog.
                                break;
                        }
                    }
                });
            }


        } else {
            finish();
        }
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            Intent callGPSSettingIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(callGPSSettingIntent, 123);
        }
    };


    private void Check_Version() {

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

        if (pref.getUID().equals("")) {
            postParam.put("uid", "0");
        } else {
            postParam.put("uid", pref.getUID());
        }
        postParam.put("utype", "Driver");
        postParam.put("deviceid", android_id);


        Log.e("parameter", " " + new JSONObject(postParam));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.checkversion, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Reposnse", response.toString());

// {"status":200,"activity":"reset-success","message":"Your Password has been changed Successfully !","data":[{"uid":"1516340787249"}]}
                        String jsonData = response.toString();
//
                        try {
                            Log.e("Status", response.getString("status"));

                            if (response.getString("status").equals("200")) {

                                JSONArray jsonArray = new JSONArray(response.getString("data"));

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jobj1 = jsonArray.getJSONObject(i);
                                    version = jobj1.getString("version");
                                }

                                String versionCode = String.valueOf(BuildConfig.VERSION_NAME);
                                Log.e("pref_language", pref.getLanguage());
                                Log.e("pref_trip_id", pref.getTripid());
                                Log.e("user_id", pref.getUID());


                                if (versionCode.equals(version)) {

                                    if (pref.getTripid().equals("0")) {
                                        //newtonCradleLoading.stop();
                                        dialog.dismiss();
                                        if (pref.getUID().equals("")) {
                                            abc = (pref.getLanguage());
                                            new Config().Change_Language(context, pref.getLanguage().equals("ar") ? "1" : "0");
                                            pref.setLanguage(abc);
                                            Intent i = new Intent(getApplicationContext(), FirstScreen.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);

                                        } else {
                                            abc = (pref.getLanguage());
                                            new Config().Change_Language(context, pref.getLanguage().equals("ar") ? "1" : "0");
                                            pref.setLanguage(abc);
                                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);

                                        }

                                    } else {
                                        FirebaseDatabase.getInstance().getReference("cars").child("trip").child(pref.getTripid()).child("tripPage").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.getValue() == null) {
                                                    //newtonCradleLoading.stop();
                                                    dialog.dismiss();
                                                    if (pref.getUID().equals("")) {
                                                        abc = (pref.getLanguage());
                                                        new Config().Change_Language(context, pref.getLanguage().equals("ar") ? "1" : "0");
                                                        pref.setLanguage(abc);
                                                        Intent i = new Intent(getApplicationContext(), FirstScreen.class);
                                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(i);

                                                    } else {
                                                        abc = (pref.getLanguage());
                                                        new Config().Change_Language(context, pref.getLanguage().equals("ar") ? "1" : "0");
                                                        pref.setLanguage(abc);
                                                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(i);

                                                    }
                                                } else {
                                                    if (dataSnapshot.getValue().equals("0")) {
                                                        //  newtonCradleLoading.stop();
                                                        dialog.dismiss();
                                                        abc = (pref.getLanguage());
                                                        new Config().Change_Language(context, pref.getLanguage().equals("ar") ? "1" : "0");
                                                        pref.setLanguage(abc);
                                                        Intent i = new Intent(getApplicationContext(), NotificationActivity.class);
                                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(i);
                                                    } else if (dataSnapshot.getValue().equals("1")) {
                                                        // newtonCradleLoading.stop();
                                                        dialog.dismiss();
                                                        abc = (pref.getLanguage());
                                                        new Config().Change_Language(context, pref.getLanguage().equals("ar") ? "1" : "0");
                                                        pref.setLanguage(abc);
                                                        Intent i = new Intent(getApplicationContext(), ConfirmTripActivity_new.class);
                                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(i);
                                                    } else if (dataSnapshot.getValue().equals("2")) {
                                                        // newtonCradleLoading.stop();
                                                        dialog.dismiss();
                                                        abc = (pref.getLanguage());
                                                        new Config().Change_Language(context, pref.getLanguage().equals("ar") ? "1" : "0");
                                                        pref.setLanguage(abc);
                                                        Intent i = new Intent(getApplicationContext(), EndTripActivity.class);
                                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(i);
                                                    } else if (dataSnapshot.getValue().equals("3")) {
                                                        //  newtonCradleLoading.stop();
                                                        dialog.dismiss();
                                                        abc = (pref.getLanguage());
                                                        new Config().Change_Language(context, pref.getLanguage().equals("ar") ? "1" : "0");
                                                        pref.setLanguage(abc);
                                                        Intent i = new Intent(getApplicationContext(), Trip_detailActivity.class);
                                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(i);
                                                    } else if (dataSnapshot.getValue().equals("4")) {
                                                        // newtonCradleLoading.stop();
                                                        dialog.dismiss();
                                                        abc = (pref.getLanguage());
                                                        new Config().Change_Language(context, pref.getLanguage().equals("ar") ? "1" : "0");
                                                        pref.setLanguage(abc);
                                                        Intent i = new Intent(getApplicationContext(), Trip_detailActivity.class);
                                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        i.putExtra("pass", "1");
                                                        startActivity(i);
                                                    } else {
                                                        //  newtonCradleLoading.stop();
                                                        dialog.dismiss();
                                                        if (pref.getUID().equals("")) {
                                                            abc = (pref.getLanguage());
                                                            new Config().Change_Language(context, pref.getLanguage().equals("ar") ? "1" : "0");
                                                            pref.setLanguage(abc);
                                                            Intent i = new Intent(getApplicationContext(), FirstScreen.class);
                                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(i);

                                                        } else {
                                                            abc = (pref.getLanguage());
                                                            new Config().Change_Language(context, pref.getLanguage().equals("ar") ? "1" : "0");
                                                            pref.setLanguage(abc);
                                                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(i);

                                                        }

                                                    }
                                                }


                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                } else {
                                    DialogBox.setUpdateApp(context, getString(R.string.update_App));
                                }

                            } else if (response.getString("status").equals("400")) {
                                DialogBox.setLoginfromother(context, getString(R.string.Login_device_detected));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener()

        {


            @Override
            public void onErrorResponse(VolleyError error) {
                //  newtonCradleLoading.stop();
                dialog.dismiss();
                VolleyLog.e("Error", "Error: " + error.getMessage());

            }
        })

        {

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

    public void getsetting() {

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("settingtype", "all");

        Log.e("parameter", " " + new JSONObject(postParam));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.Get_settings, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Reposnse", response.toString());

// {"status":200,"activity":"reset-success","message":"Your Password has been changed Successfully !","data":[{"uid":"1516340787249"}]}
                        String jsonData = response.toString();
//

                        try {
                            JSONObject jobj = new JSONObject(jsonData);
                            if (jobj.getString("status").equals("200")) {

                                JSONArray array = jobj.getJSONArray("data");

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.getJSONObject(i);
                                    String sid = object.getString("sid");
                                    if (sid.equals("1")) {
                                        Constant.BUCKET = object.getString("sval");
                                    } else if (sid.equals("2")) {
                                        Constant.ACCESSKEY = object.getString("sval");
                                    } else if (sid.equals("3")) {
                                        Constant.SECRETKEY = object.getString("sval");
                                    } else if (sid.equals("4")) {
                                        Constant.POOL_ID = object.getString("sval");
                                    } else if (sid.equals("5")) {
                                        Constant.POOLARN = object.getString("sval");
                                    } else if (sid.equals("6")) {
                                        Constant.REGION = object.getString("sval");
                                    } else if (sid.equals("7")) {
                                        Constant.IMAGE_FOLDER = object.getString("sval");
                                    } else if (sid.equals("8")) {
                                        Constant.BUCKET_URL = object.getString("sval");
                                        pref.setBucket_url(object.getString("sval"));
                                    } else if (sid.equals("9")) {
                                        Constant.DEFAULT_FARE = object.getString("sval");
                                    } else if (sid.equals("10")) {
                                        Constant.RIDERCANCEL_RATE = object.getString("sval");
                                    } else if (sid.equals("11")) {
                                        Constant.WAITING_CHARGE = object.getString("sval");
                                    } else if (sid.equals("19")) {
                                        Constant.CURRENCY = object.getString("sval");
                                    } else if (sid.equals("20")) {
                                        Constant.MOBILE_LENGTH = object.getString("sval");
                                    } else if (sid.equals("21")) {
                                        Constant.four_Seater = object.getString("snm");
                                    } else if (sid.equals("22")) {
                                        Constant.six_Seater = object.getString("snm");
                                    } else if (sid.equals("23")) {
                                        Constant.MINI = object.getString("snm");

                                    }
                                }


                            } else {
                                Toast.makeText(context, jobj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (
                                JSONException e)

                        {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener()

        {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error", "Error: " + error.getMessage());

            }
        })

        {

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

    private static Intent getServiceIntent(Context c) {
        return new Intent(c, GPS_Service.class);
    }


}
