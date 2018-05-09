package com.taxibookingdriver.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
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
import com.taxibookingdriver.Controller.Constant;
import com.taxibookingdriver.Controller.GraduallyTextView;
import com.taxibookingdriver.Controller.Pref_Master;
import com.taxibookingdriver.Controller.Progress_dialog;
import com.taxibookingdriver.Models.Model_user;
import com.taxibookingdriver.MyApplication;
import com.taxibookingdriver.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstScreen extends AppCompatActivity {

    RelativeLayout next;
    Context context = this;
    EditText edit_mobile;
    ArrayList<Model_user> array_user = new ArrayList<>();
    String android_id;
    Pref_Master pref;
    RequestQueue queue;
    TextInputLayout input_layout_password;
    //Progress_dialog dialog;
    android.support.v7.app.AlertDialog dialog;
    int maxLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);
        pref = new Pref_Master(context);
        // dialog = new Progress_dialog();

        next = (RelativeLayout) findViewById(R.id.next);
        edit_mobile = (EditText) findViewById(R.id.edit_mobile);
        maxLength = Integer.parseInt(Constant.MOBILE_LENGTH);
        edit_mobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        input_layout_password = (TextInputLayout) findViewById(R.id.input_layout_password);

        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        queue = Volley.newRequestQueue(this);

        Log.e("android_id", android_id);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validate();
            }
        });

        MyApplication.getInstance().trackScreenView("First Screen");
    }

    private void Validate() {
        if (edit_mobile.getText().toString().equals("")) {
            input_layout_password.setError(getResources().getString(R.string.Enter_mobile));
        } else if (edit_mobile.getText().toString().length() < maxLength) {
            input_layout_password.setError(getResources().getString(R.string.Mobile_number));
        } else {
            input_layout_password.setErrorEnabled(false);
            Check_mobile();
        }
    }


    private void Check_mobile() {

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


        // dialog.show(this.getSupportFragmentManager(), "");
        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("mobile", edit_mobile.getText().toString());
        postParam.put("utype", Config.driver);
        postParam.put("deviceid", android_id);

        Log.e("parameter", " " + new JSONObject(postParam));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.checkmobile, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.e("Reposnse", response.toString());

                        //{"status":200,"activity":"allow-login","message":"Exist"}

                        //{"status":200,"activity":"allow-new","message":"success","OTP":OTP}

                        String jsonData = response.toString();


                        try {
                            JSONObject jobj = new JSONObject(jsonData);
                            if (jobj.getString("activity").equals("allow-new")) {
                                String otp = jobj.getString("OTP");
                                Log.e("otp", otp);

                                Toast.makeText(context, getResources().getString(R.string.Otp_successfully), Toast.LENGTH_LONG).show();
                                Intent i = new Intent(context, OTPActivity.class);
                                pref.setMobile(edit_mobile.getText().toString());
                                i.putExtra("otp", otp);
                                i.putExtra("value", "1");
                                startActivity(i);
                            } else if (jobj.getString("activity").equals("allow-login")) {

                                Intent i = new Intent(context, PasswordActivity.class);
                                i.putExtra("value", "1");
                                pref.setMobile(edit_mobile.getText().toString());
                                startActivity(i);

                            } else if (jobj.getString("activity").equals("device-exist")) {

                                Toast.makeText(context, jobj.getString("message"), Toast.LENGTH_LONG).show();

                                Intent i = new Intent(context, ResetPasswordActivity_login.class);
                                pref.setMobile(edit_mobile.getText().toString());
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
                // newtonCradleLoading.stop();
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


    Runnable runnable2 = new Runnable() {
        @Override
        public void run() {

            try {
                Intent intent = new Intent();
                String manufacturer = android.os.Build.MANUFACTURER;
                if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
                } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                } else if ("huawei".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                }

                List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (list.size() > 0) {
                    startActivity(intent);
                }
            } catch (Exception e) {

            }
        }
    };


}
