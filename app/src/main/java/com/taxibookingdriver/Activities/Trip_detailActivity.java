package com.taxibookingdriver.Activities;

import android.content.Context;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 2/9/2018.
 */

public class Trip_detailActivity extends AppCompatActivity {

    Pref_Master pref;
    Context context = this;
    RequestQueue queue;
    android.support.v7.app.AlertDialog dialog;
    TextView txt_from, txt_to, txt_coupan_code, txt_waiting_charge, txt_can_charge, txt_final_amt, txt_pay_by, txt_fare, txt_trip_number;
    String riderid = "";
    String tripid = "";
    String cncharge = "";
    String totalfare = "";
    String ptype = "";
    RelativeLayout rr_inproces;
    String pass = "";

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_trip_detail_second);
        pref = new Pref_Master(context);
        //dialog = new Progress_dialog();
        queue = Volley.newRequestQueue(context);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            pass = extras.getString("pass");
        }


        riderid = pref.getRiderid();
        tripid = pref.getTripid();


        txt_from = findViewById(R.id.txt_from);
        txt_to = findViewById(R.id.txt_to);
        txt_coupan_code = findViewById(R.id.txt_coupan_code);
        txt_waiting_charge = findViewById(R.id.txt_waiting_charge);
        txt_can_charge = findViewById(R.id.txt_can_charge);
        txt_final_amt = findViewById(R.id.txt_final_amt);
        txt_pay_by = findViewById(R.id.txt_pay_by);
        txt_fare = findViewById(R.id.txt_fare);
        txt_trip_number = findViewById(R.id.txt_trip_number);
        txt_trip_number.setText(tripid);
        Trip_detail();

        rr_inproces = findViewById(R.id.rr_inproces);

        MyApplication.getInstance().trackScreenView("Trip Detail Screen");

    }

    private void Trip_detail() {

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

        Log.e("parameter_payment_info", " " + new JSONObject(postParam));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.trippaymentdetails, new JSONObject(postParam),
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

                                txt_from.setText(object.getString("from"));
                                txt_to.setText(object.getString("to"));
                                txt_fare.setText(Constant.CURRENCY + object.getString("total"));
//                                if (object.getString("coupon").equals("")) {
//                                    txt_coupan_code.setText("0");
//                                } else {
//                                    txt_coupan_code.setText(object.getString("coupon"));
//                                }
                                txt_coupan_code.setText(Constant.CURRENCY + object.getString("cprate"));
                                txt_waiting_charge.setText(Constant.CURRENCY + object.getString("wcharge"));
                                txt_can_charge.setText(Constant.CURRENCY + object.getString("cancelcharge"));
                                cncharge = object.getString("cancelcharge");
                                totalfare = object.getString("totalfare");
                                txt_final_amt.setText(Constant.CURRENCY + object.getString("totalfare"));
                                txt_pay_by.setText(object.getString("ptype"));
                                ptype = object.getString("ptype");
                                if (object.getString("paymenstatus").equals("completed")) {
                                    if (pass.equals("1")) {
                                        DialogBox.setpayment(Trip_detailActivity.this, getString(R.string.trip_payment), pref.getRiderid());
                                    }
                                }


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

}
