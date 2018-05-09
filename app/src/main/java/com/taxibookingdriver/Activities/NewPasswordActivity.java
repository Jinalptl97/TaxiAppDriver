package com.taxibookingdriver.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
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
import com.taxibookingdriver.Controller.Config;
import com.taxibookingdriver.Controller.GraduallyTextView;
import com.taxibookingdriver.Controller.Pref_Master;
import com.taxibookingdriver.Controller.Progress_dialog;
import com.taxibookingdriver.MyApplication;
import com.taxibookingdriver.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewPasswordActivity extends AppCompatActivity {

    RelativeLayout rr_pwd;
    Context context = this;
    Pref_Master pref;
    EditText et_pwd;
    RequestQueue queue;
    TextInputLayout input_pwd;
    android.support.v7.app.AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        pref = new Pref_Master(context);
        // dialog = new Progress_dialog();
        queue = Volley.newRequestQueue(this);

        et_pwd = findViewById(R.id.et_pwd);
        rr_pwd = findViewById(R.id.rr_pwd);
        input_pwd = findViewById(R.id.input_pwd);

        rr_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_pwd.getText().toString().equals("")) {
                    input_pwd.setError(getString(R.string.Enter_new_password));
                } else if (et_pwd.getText().toString().trim().length() < 6) {
                    input_pwd.setError(getString(R.string.Enter_password_valid));
                } else {
                    input_pwd.setErrorEnabled(false);
                    NEW_PWD();
                }
            }
        });

        MyApplication.getInstance().trackScreenView("New Password Screen");

    }

    private void NEW_PWD() {


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
        postParam.put("otpcheck", "yes");


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
                            if (jobj.getString("activity").equals("reset-success")) {

                                Toast.makeText(context, getString(R.string.Password_success), Toast.LENGTH_LONG).show();
                                Intent i = new Intent(context, ProfileActivity.class);
                                pref.setPwd(et_pwd.getText().toString());
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
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
