package com.taxibookingdriver.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.taxibookingdriver.Activities.FirstScreen;
import com.taxibookingdriver.Amazon.Utils;
import com.taxibookingdriver.Controller.Config;
import com.taxibookingdriver.Controller.Constant;
import com.taxibookingdriver.Controller.DialogBox;
import com.taxibookingdriver.Controller.GraduallyTextView;
import com.taxibookingdriver.Controller.Pref_Master;
import com.taxibookingdriver.Controller.Progress_dialog;
import com.taxibookingdriver.MyApplication;
import com.taxibookingdriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 1/16/2018.
 */

public class Bank_info extends Fragment {

    Context context;
    RelativeLayout rr_bank;
    EditText et_bank_name, et_branch_name, et_account_name, et_ac_num;
    String android_id;
    Pref_Master pref;
    RequestQueue queue;
    GoogleApiClient mGoogleApiClient;
    Location location;
    private TransferUtility transferUtility;
    String getpostone = "";
    String userid = "";
    // RotateLoading newtonCradleLoading;
    private FusedLocationProviderClient mFusedLocationClient;
    LocationListener locationListener;
    LocationManager locationManager;
    android.support.v7.app.AlertDialog dialog;
    TextInputLayout input_bank_name, input_branch_name, input_ac_name, input_ac_number;


    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bank_info, container, false);
        context = getActivity();
        pref = new Pref_Master(context);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
       // dialog = new Progress_dialog();
        transferUtility = Utils.getTransferUtility(getActivity());

        android_id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        queue = Volley.newRequestQueue(context);


        rr_bank = view.findViewById(R.id.rr_bank);
        et_bank_name = view.findViewById(R.id.et_bank_name);
        et_branch_name = view.findViewById(R.id.et_branch_name);
        et_account_name = view.findViewById(R.id.et_account_name);
        et_ac_num = view.findViewById(R.id.et_ac_num);

        input_bank_name = view.findViewById(R.id.input_bank_name);
        input_branch_name = view.findViewById(R.id.input_branch_name);
        input_ac_name = view.findViewById(R.id.input_ac_name);
        input_ac_number = view.findViewById(R.id.input_ac_number);


        et_bank_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                input_bank_name.setErrorEnabled(false);

            }
        });

        et_branch_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                input_branch_name.setErrorEnabled(false);
            }
        });

        et_ac_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                input_ac_number.setErrorEnabled(false);
            }
        });

        et_account_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                input_ac_name.setErrorEnabled(false);

            }
        });

        rr_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (et_bank_name.getText().toString().equals("")) {
                    input_bank_name.setError(getString(R.string.Enter_bank_name));
                } else if (et_branch_name.getText().toString().equals("")) {
                    input_branch_name.setError(getString(R.string.Enter_branch_name));
                    input_bank_name.setErrorEnabled(false);
                } else if (et_account_name.getText().toString().equals("")) {
                    input_ac_name.setError(getString(R.string.Enter_account_name));
                    input_branch_name.setErrorEnabled(false);
                } else if (et_ac_num.getText().toString().equals("")) {
                    input_ac_number.setError(getString(R.string.Enter_account_number));
                    input_ac_name.setErrorEnabled(false);
                } else {

                    Check_details();
                }


            }
        });

        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            dialog.dismiss();
            DialogBox.setNoData(context, getString(R.string.Location_not_found), runnable);
        }
            /*Toast.makeText(context, "GPS is disabled!", Toast.LENGTH_LONG).show();*/
        else {

        }


        return view;
    }

    private void send_info() {
        getlastlocation();


    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            Intent callGPSSettingIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(callGPSSettingIntent, 123);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                dialog.dismiss();
                DialogBox.setNoData(context, getString(R.string.Location_disabled), runnable);
            }
            /*Toast.makeText(context, "GPS is disabled!", Toast.LENGTH_LONG).show();*/
            else {

                //getlastlocation();

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("Bank Info");
    }

    public void getlastlocation() {

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
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Map<String, String> postParam = new HashMap<String, String>();
                    postParam.put("mobile", pref.getMobile());
                    postParam.put("password", pref.getPwd());
                    postParam.put("fnm", pref.getFname());
                    postParam.put("lnm", pref.getLname());
                    postParam.put("email", pref.getEmail());
                    postParam.put("deviceid", android_id);
                    postParam.put("dtoken", FirebaseInstanceId.getInstance().getToken());
                    postParam.put("latlong", location.getLatitude() + "," + location.getLongitude());
                    postParam.put("idno", pref.getId());
                    postParam.put("frontpic", pref.getFront_img());
                    postParam.put("backpic", pref.getBack_img());
                    postParam.put("bankname", et_bank_name.getText().toString());
                    postParam.put("bankcode", et_branch_name.getText().toString());
                    postParam.put("accname", et_account_name.getText().toString());
                    postParam.put("accno", et_ac_num.getText().toString());
                    postParam.put("regno", pref.getreg_num());
                    postParam.put("carmodel", pref.getCar_model());
                    postParam.put("color", pref.getColor());
                    postParam.put("cartype", pref.getCar_type());

                    Log.e("parameter_bank_info", " " + new JSONObject(postParam));


                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                            Config.register, new JSONObject(postParam),
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.e("Reposnse", response.toString());


                                    //{"status":200,"activity":"register-success","message":"Registered Successfully !","data":[{"uid":"1516340787249"}]}

                                    String jsonData = response.toString();
//

                                    try {

                                        JSONObject jobj = new JSONObject(jsonData);

                                        if (jobj.getString("status").equals("200")) {

                                            if (jobj.getString("activity").equals("register-success")) {

                                                JSONArray array = jobj.getJSONArray("data");

                                                JSONObject object = array.getJSONObject(0);
                                                userid = object.getString("uid");
                                                Log.e("otp", userid);

                                                pref.setUID("");
                                                UploadImage(pref.getStr_front_image_var(), userid);


                                            } else {
                                                dialog.dismiss();
                                                DialogBox.setAction(context, jobj.getString("message"));
                                            }
                                        } else {
                                            dialog.dismiss();
                                            DialogBox.setAction(context, jobj.getString("message"));
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

                    jsonObjReq.setRetryPolicy(
                            new DefaultRetryPolicy(
                                    10000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                            )
                    );


// Adding request to request queue
                    queue.add(jsonObjReq);

                } else {
                    NewLocation();
                }
            }
        });

    }

    public void NewLocation() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    locationManager.removeUpdates(locationListener);
                    Map<String, String> postParam = new HashMap<String, String>();
                    postParam.put("mobile", pref.getMobile());
                    postParam.put("password", pref.getPwd());
                    postParam.put("fnm", pref.getFname());
                    postParam.put("lnm", pref.getLname());
                    postParam.put("email", pref.getEmail());
                    postParam.put("deviceid", android_id);
                    postParam.put("dtoken", FirebaseInstanceId.getInstance().getToken());
                    postParam.put("latlong", location.getLatitude() + "," + location.getLongitude());
                    postParam.put("idno", pref.getId());
                    postParam.put("frontpic", pref.getFront_img());
                    postParam.put("backpic", pref.getBack_img());
                    postParam.put("bankname", et_bank_name.getText().toString());
                    postParam.put("bankcode", et_branch_name.getText().toString());
                    postParam.put("accname", et_account_name.getText().toString());
                    postParam.put("accno", et_ac_num.getText().toString());
                    postParam.put("regno", pref.getreg_num());
                    postParam.put("carmodel", pref.getCar_model());
                    postParam.put("color", pref.getColor());
                    postParam.put("cartype", pref.getCar_type());

                    Log.e("parameter_bank_info", " " + new JSONObject(postParam));


                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                            Config.register, new JSONObject(postParam),
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.e("Reposnse", response.toString());


                                    //{"status":200,"activity":"register-success","message":"Registered Successfully !","data":[{"uid":"1516340787249"}]}

                                    String jsonData = response.toString();
//

                                    try {

                                        JSONObject jobj = new JSONObject(jsonData);

                                        if (jobj.getString("status").equals("200")) {

                                            if (jobj.getString("activity").equals("register-success")) {

                                                JSONArray array = jobj.getJSONArray("data");

                                                JSONObject object = array.getJSONObject(0);
                                                userid = object.getString("uid");
                                                Log.e("otp", userid);

                                                pref.setUID("");
                                                UploadImage(pref.getStr_front_image_var(), userid);


                                            } else {
                                                dialog.dismiss();
                                                Toast.makeText(context, jobj.getString("message"), Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            dialog.dismiss();
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

                    jsonObjReq.setRetryPolicy(
                            new DefaultRetryPolicy(
                                    10000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                            )
                    );


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

    private void Check_details() {

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
        postParam.put("tab", "3");
        postParam.put("accno", et_ac_num.getText().toString());

        Log.e("parameter_Car_info_info", " " + new JSONObject(postParam));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.Check_details, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //newtonCradleLoading.stop();
                        Log.e("Reposnse", response.toString());

                        //{"status":200,"activity":"register-success","message":"Registered Successfully !","data":[{"uid":"1516340787249"}]}

                        String jsonData = response.toString();
//

                        try {

                            JSONObject jobj = new JSONObject(jsonData);

                            if (jobj.getString("status").equals("200")) {

                                if (jobj.getString("activity").equals("allow-register")) {

                                    send_info();


                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(context, jobj.getString("message"), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                dialog.dismiss();
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


    public void UploadImage(String filePath, final String userid) {
        CannedAccessControlList cannedAccessControlList;
        cannedAccessControlList = CannedAccessControlList.PublicRead;

        if (filePath == null) {
            Toast.makeText(context, getString(R.string.Could_not_find_path), Toast.LENGTH_LONG).show();
            return;
        }
        File file = new File(filePath);


        getpostone = file.getName();
//        Toast.makeText(getApplicationContext(),file.getName(), Toast.LENGTH_LONG).show();
        final TransferObserver observer = transferUtility.upload(Constant.BUCKET + "/user-images/" + userid + "/License", file.getName(), file, new ObjectMetadata());

        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.COMPLETED.equals(observer.getState())) {
                    File file = new File(pref.getStr_front_image_var());
                    file.delete();
                    // Toast.makeText(getActivity(), "Upload 1 Completed", Toast.LENGTH_SHORT).show();

                    UploadImage2(pref.getStr_back_image_var(), userid);

                    // Toast.makeText(context, "File Upload Complete", Toast.LENGTH_SHORT).show();


                }

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }


            @Override
            public void onError(int id, Exception ex) {
                Toast.makeText(context, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void UploadImage2(String filePath, String userid) {
        CannedAccessControlList cannedAccessControlList;
        cannedAccessControlList = CannedAccessControlList.PublicRead;

        if (filePath == null) {
            Toast.makeText(context, getString(R.string.Could_not_find_path), Toast.LENGTH_LONG).show();
            return;
        }
        File file = new File(filePath);


        getpostone = file.getName();
//        Toast.makeText(getApplicationContext(),file.getName(), Toast.LENGTH_LONG).show();
        final TransferObserver observer = transferUtility.upload(Constant.BUCKET + "/user-images/" + userid + "/License", file.getName(), file, new ObjectMetadata());

        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.COMPLETED.equals(observer.getState())) {

                    dialog.dismiss();
                    // Toast.makeText(getActivity(), "Upload 2 Completed", Toast.LENGTH_LONG).show();
                    File filee = new File(pref.getStr_back_image_var());
                    filee.delete();

                    // Toast.makeText(context, "File Upload Complete", Toast.LENGTH_SHORT).show();

                    LayoutInflater li = LayoutInflater.from(context);
                    View v = li.inflate(R.layout.alert_popup, null);
                    final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context).setView(v).show();
                    alert.setCancelable(false);
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    TextView cancel = v.findViewById(R.id.con_ok);
                    TextView msglaert = v.findViewById(R.id.msglaert);
                    msglaert.setText(R.string.registration);

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.dismiss();
                            Intent i = new Intent(context, FirstScreen.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            pref.clear_pref();
                            pref.setBack_img("");
                            pref.setBack_img_var("");
                            pref.setFront_img("");
                            pref.setFront_img_var("");
                            pref.setUID("");
                            startActivity(i);
                        }
                    });


                }

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }


            @Override
            public void onError(int id, Exception ex) {
                Toast.makeText(context, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}
