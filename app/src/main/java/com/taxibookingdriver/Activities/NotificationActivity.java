package com.taxibookingdriver.Activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.taxibookingdriver.Controller.Config;
import com.taxibookingdriver.Controller.Constant;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Admin on 1/17/2018.
 */

public class NotificationActivity extends AppCompatActivity {

    Context context = this;
    Pref_Master pref;
    String riderid = "";
    String driverid = "";
    String tripid = "";
    CircleImageView img_profile;
    RelativeLayout rr_accept, rr_reject;
    TextView txt_from, txt_to;
    RequestQueue queue;
    String regno, ctype;
    ImageView img_back;
    FirebaseDatabase mFirebaseInstance;
    TextView txt_cus_name;
    String from, to;
    android.support.v7.app.AlertDialog dialog;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        pref = new Pref_Master(context);
        // dialog = new Progress_dialog();
        queue = Volley.newRequestQueue(context);
        mFirebaseInstance = FirebaseDatabase.getInstance();


        riderid = pref.getRiderid();
        driverid = pref.getUID();
        tripid = pref.getTripid();


        Log.e("rider_id", riderid);
        Log.e("driver_id", driverid);
        Log.e("trip_id", tripid);

        img_profile = findViewById(R.id.img_profile);
        txt_cus_name = findViewById(R.id.txt_cus_name);
        rr_accept = findViewById(R.id.rr_accept);
        rr_reject = findViewById(R.id.rr_reject);
        txt_from = findViewById(R.id.txt_from);
        txt_to = findViewById(R.id.txt_to);
        img_back = findViewById(R.id.img_back);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getsetting();
        Get_notification_data();


        rr_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(context);
                View vv = li.inflate(R.layout.new_noti_popup, null);
                final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context).setView(vv).show();
                alert.setCancelable(true);
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView txt_no = vv.findViewById(R.id.txt_no);
                TextView txt_yes = vv.findViewById(R.id.txt_yes);
                TextView msglaert = vv.findViewById(R.id.msglaert);
                ImageView img_ride = vv.findViewById(R.id.img_ride);
                TextView txt_ride = vv.findViewById(R.id.txt_ride);
                msglaert.setText(R.string.accept_noti);
                txt_ride.setText(R.string.accept_ride);
                img_ride.setImageResource(R.drawable.popup_success);

                txt_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                    }
                });


                txt_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();

                        Accept_noti();
                    }
                });
            }
        });

        rr_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(context);
                View vv = li.inflate(R.layout.new_noti_popup, null);
                final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context).setView(vv).show();
                alert.setCancelable(true);
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView txt_no = vv.findViewById(R.id.txt_no);
                TextView txt_yes = vv.findViewById(R.id.txt_yes);
                TextView msglaert = vv.findViewById(R.id.msglaert);
                ImageView img_ride = vv.findViewById(R.id.img_ride);
                TextView txt_ride = vv.findViewById(R.id.txt_ride);
                msglaert.setText(R.string.reject_noti);
                txt_ride.setText(R.string.Reject_ride);
                img_ride.setImageResource(R.drawable.popup_close);

                txt_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                    }
                });

                txt_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                        Reject_noti();
                    }
                });
            }
        });

        MyApplication.getInstance().trackScreenView("Notification Screen");
    }
//{"status":400,"activity":"trip-invalid","message":"Invalid Trip !","data":[{"tripid":"tripid","riderid":"riderid"}]}

    private void Accept_noti() {

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
        postParam.put("tripid", tripid);
        postParam.put("regno", regno);
        postParam.put("ctype", ctype);
        postParam.put("driverid", driverid);
        postParam.put("riderid", riderid);
        postParam.put("action", "1");

        Log.e("parameter_accepted", " " + new JSONObject(postParam));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.driver_response, new JSONObject(postParam),
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
                                String trip_id = object.getString("tripid");
                                String riderid = object.getString("riderid");

                                pref.setTripid(trip_id);
                                pref.setRiderid(riderid);
                                pref.setFrom(from);
                                pref.setTo(to);

                                pref.setDr_status("2");
                                FirebaseDatabase.getInstance().getReference("cars").child("driverDetail").child(pref.getUID()).child("driverstatus").setValue(pref.getDr_status());
                                FirebaseDatabase.getInstance().getReference("cars").child("trip").child(pref.getTripid()).child("tripPage").setValue("1");
                                Intent i = new Intent(NotificationActivity.this, ConfirmTripActivity_new.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

//                                i.putExtra("tripid", trip_id);
//                                i.putExtra("riderid", riderid);
//                                i.putExtra("from", from);
//                                i.putExtra("to", to);
                                startActivity(i);


                            } else if (jobj.getString("status").equals("400")) {
                                if (jobj.getString("activity").equals("trip-invalid")) {
                                    LayoutInflater li = LayoutInflater.from(context);
                                    View vv = li.inflate(R.layout.alert_notification_popup, null);
                                    final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog).setView(vv).show();
                                    alert.setCancelable(false);
                                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    TextView cancel = vv.findViewById(R.id.con_ok);
                                    TextView msglaert = vv.findViewById(R.id.msglaert);
                                    msglaert.setText(jobj.getString("message"));

                                    cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {


                                            alert.dismiss();
                                            NotificationManager nManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
                                            nManager.cancelAll();


                                            Intent i = new Intent(context, MainActivity.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            context.startActivity(i);


                                        }
                                    });

                                }
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

    private void Reject_noti() {

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
        postParam.put("tripid", tripid);
        postParam.put("driverid", driverid);
        postParam.put("riderid", riderid);
        postParam.put("action", "2");
        postParam.put("ctype", ctype);

        Log.e("parameter_rejected", " " + new JSONObject(postParam));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.driver_response, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.e("ReposnseRejected", response.toString());

                        //{"status":200,"activity":"register-success","message":"Registered Successfully !","data":[{"uid":"1516340787249"}]}

                        String jsonData = response.toString();
//

                        try {

                            JSONObject jobj = new JSONObject(jsonData);

                            if (jobj.getString("status").equals("200")) {

                                mFirebaseInstance.getReference("cars").child("trip").child(tripid).child("rejecteddriver").child(pref.getUID()).setValue(pref.getUID());
                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("cars").child("trip").child(pref.getTripid());
                                reference.removeValue();
                                pref.setTripid("0");

                                Intent i = new Intent(NotificationActivity.this, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);

                            } else if (jobj.getString("status").equals("400")) {
                                if (jobj.getString("activity").equals("trip-invalid")) {
                                    LayoutInflater li = LayoutInflater.from(context);
                                    View vv = li.inflate(R.layout.alert_notification_popup, null);
                                    final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog).setView(vv).show();
                                    alert.setCancelable(false);
                                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    TextView cancel = vv.findViewById(R.id.con_ok);
                                    TextView msglaert = vv.findViewById(R.id.msglaert);
                                    msglaert.setText(jobj.getString("message"));

                                    cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {


                                            alert.dismiss();
                                            NotificationManager nManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
                                            nManager.cancelAll();


                                            Intent i = new Intent(context, MainActivity.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            context.startActivity(i);


                                        }
                                    });

                                }
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

    private void Get_notification_data() {

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
        postParam.put("driverid", driverid);
        postParam.put("riderid", riderid);
        postParam.put("tripid", tripid);

        Log.e("parameter_noti_data", " " + new JSONObject(postParam));


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

                                txt_cus_name.setText(object.getString("rnm"));
                                txt_from.setText("From" + " " + ":" + " " + object.getString("from"));
                                txt_to.setText("To" + " " + ":" + " " + object.getString("to"));
                                tripid = object.getString("tripid");
                                regno = object.getString("regno");
                                ctype = object.getString("ctype");
                                driverid = object.getString("driverid");
                                riderid = object.getString("riderid");
                                from = object.getString("from");
                                to = object.getString("to");

                                Log.e("Rider_pic", pref.getBucket_url() + pref.getUID() + "/" + object.getString("rpic"));

                                if (object.getString("rpic").equals("")) {
                                    img_profile.setImageResource(R.drawable.personal);

                                } else {

                                    Picasso.with(context)
                                            .load(pref.getBucket_url() + object.getString("riderid") + "/" + object.getString("rpic")) //extract as User instance method
                                            .placeholder(R.drawable.personal)
                                            .into(img_profile);
                                }
                                FirebaseDatabase.getInstance().getReference("cars").child("trip").child(pref.getTripid()).child("tripPage").setValue("0");


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
