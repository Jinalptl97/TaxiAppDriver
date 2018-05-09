package com.taxibookingdriver.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
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

public class OTPActivity_Profile_change extends AppCompatActivity {
    RelativeLayout rr_next;
    Context context = this;
    EditText et_otp;
    String otp, fname, lname, mobile, email, pass, image;
    TextView resend_otp;
    RequestQueue queue;
    Pref_Master pref;
    String android_id;
    android.support.v7.app.AlertDialog dialog;
    TextInputLayout input_layout_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        pref = new Pref_Master(context);
        //dialog = new Progress_dialog();

        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        queue = Volley.newRequestQueue(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            otp = extras.getString("otp");
            fname = extras.getString("fname");
            lname = extras.getString("lname");
            mobile = extras.getString("mobile");
            email = extras.getString("email");
            pass = extras.getString("pass");
            image = extras.getString("image");
        }

        rr_next = findViewById(R.id.rr_next);
        et_otp = findViewById(R.id.et_otp);
        resend_otp = findViewById(R.id.resend_otp);
        input_layout_password = findViewById(R.id.input_layout_password);

        rr_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_otp.getText().toString().equals("")) {
                    input_layout_password.setError(getString(R.string.Enter_otp));
                } else {
                    if (otp.equals(et_otp.getText().toString())) {
                        input_layout_password.setErrorEnabled(false);
                        makeJsonObjReq();

                    } else {
                        input_layout_password.setError(getString(R.string.Check_otp));
                    }
                }


            }
        });


        resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resend_OTP();
            }
        });

        MyApplication.getInstance().trackScreenView("OTP Profile Change Screen");


    }

    public void Resend_OTP() {


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

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.ResendOTP + pref.getMobile() + Config.driver,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        String jsonData = response.toString();

                        try {
                            JSONObject jobj = new JSONObject(jsonData);
                            otp = jobj.getString("OTP");
                            Log.e("otp", otp);

                            Toast.makeText(context, getString(R.string.Otp_successfully), Toast.LENGTH_SHORT).show();

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

// Add the request to the RequestQueue.
        queue.add(stringRequest);


    }


    private void makeJsonObjReq() {

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
        postParam.put("uid", pref.getUID());
        postParam.put("utype", Config.driver);
        postParam.put("oldmobile", pref.getMobile());
        postParam.put("newmobile", mobile);
        postParam.put("oldemail", pref.getEmail());
        postParam.put("newemail", email);
        postParam.put("fname", fname);
        postParam.put("lname", lname);
        postParam.put("password", pass);
        postParam.put("upic", image);
        postParam.put("verifiedotp", "yes");

        Log.e("parameter", " " + new JSONObject(postParam));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.Update_profile, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.e("Reposnse", response.toString());
                        try {
                            Log.e("Status", response.getString("status"));

                            if (response.getString("status").equals("200")) {

                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();

                                if (response.getString("activity").equals("otp-sent")) {


                                } else if (response.getString("activity").equals("update-profile")) {

                                    JSONArray jsonArray = new JSONArray(response.getString("data"));

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jobj1 = jsonArray.getJSONObject(i);

                                        pref.setFname(jobj1.getString("fname"));
                                        pref.setLname(jobj1.getString("lname"));
                                        pref.setMobile(jobj1.getString("newmobile"));
                                        pref.setEmail(jobj1.getString("newemail"));
                                        pref.setBack_img(jobj1.getString("upic"));

                                        Intent ii = new Intent(context, ProfileActivity.class);
                                        startActivity(ii);


                                    }
                                }

                            } else if (response.getString("status").equals("400")) {

                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();

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
