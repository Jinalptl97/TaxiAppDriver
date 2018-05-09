package com.taxibookingdriver.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;
import com.taxibookingdriver.Activities.MainActivity;
import com.taxibookingdriver.Adapter.Fare_Setting_adapter;
import com.taxibookingdriver.Controller.Config;
import com.taxibookingdriver.Controller.Constant;
import com.taxibookingdriver.Controller.GraduallyTextView;
import com.taxibookingdriver.Controller.Pref_Master;
import com.taxibookingdriver.Controller.Progress_dialog;
import com.taxibookingdriver.Models.Model_fare;
import com.taxibookingdriver.MyApplication;
import com.taxibookingdriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 1/16/2018.
 */

public class Fare_Setting extends Fragment {

    RecyclerView rr_fare_setting_list;
    Context context;
    ArrayList<Model_fare> array_fare_setting = new ArrayList<>();
    Fare_Setting_adapter fare_setting;
    EditText et_per_rate;
    RelativeLayout rr_fare_submit;
    RequestQueue queue;
    int default_fare, value;
    Pref_Master pref;
    RelativeLayout rr_last_rate;
    private FirebaseDatabase mFirebaseInstance;
    android.support.v7.app.AlertDialog dialog;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fare_setting, container, false);
        context = getActivity();
        pref = new Pref_Master(context);
        // dialog = new Progress_dialog();
        queue = Volley.newRequestQueue(context);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        rr_fare_setting_list = view.findViewById(R.id.rr_fare_setting_list);
        rr_fare_submit = view.findViewById(R.id.rr_fare_submit);
        rr_last_rate = view.findViewById(R.id.rr_last_rate);

        et_per_rate = view.findViewById(R.id.et_per_rate);

        rr_fare_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_per_rate.getText().toString().equals("")) {
                    Toast.makeText(context, getString(R.string.Enter_rate), Toast.LENGTH_SHORT).show();
                } else if (et_per_rate.getText().toString().equals("0")) {
                    Toast.makeText(context, getString(R.string.Enter_valid_rate), Toast.LENGTH_SHORT).show();
                } else {

                    int value = Math.round(Float.parseFloat(et_per_rate.getText().toString()));
                    default_fare = Integer.parseInt(Constant.DEFAULT_FARE);
                    Log.e("value", ":" + value);
                    Log.e("Default_value", ":" + default_fare);
                    if (value > default_fare) {
                        Toast.makeText(context, getString(R.string.You_can_not_add_fare) + " " + default_fare, Toast.LENGTH_LONG).show();
                    } else {

                        Add_fare();
                    }
                }
            }
        });
        int numberOfColumns = 1;
        rr_fare_setting_list.setLayoutManager(new GridLayoutManager(context, numberOfColumns));

        fare_setting = new Fare_Setting_adapter(context, array_fare_setting);
        rr_fare_setting_list.setAdapter(fare_setting);


        // getsetting();
        Get_farelist();
        return view;
    }

    @Override
    public void onResume() {

        super.onResume();
        Log.e("back_fare", "back_fare");

        if (getView() == null) {
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_UP && i == KeyEvent.KEYCODE_BACK) {

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

//                    FragmentManager fm = getFragmentManager();
//                    FragmentTransaction ft = fm.beginTransaction();
//                    ft.remove(Fare_Setting.this);
//                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
//                    ft.commit();
// handle back button's click listener
                }

                return false;
            }
        });
        MyApplication.getInstance().trackScreenView("Fare Setting");

    }

    public void Add_fare() {

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
        postParam.put("rate", et_per_rate.getText().toString());
        // newtonCradleLoading.start();
        Log.e("parameter", " " + new JSONObject(postParam));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.Add_fare, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Reposnse", response.toString());
                        //  newtonCradleLoading.stop();
                        dialog.dismiss();
// {"status":200,"activity":"reset-success","message":"Your Password has been changed Successfully !","data":[{"uid":"1516340787249"}]}
                        String jsonData = response.toString();
//

                        try {
                            JSONObject jobj = new JSONObject(jsonData);
                            if (jobj.getString("status").equals("200")) {

                                pref.setFare(et_per_rate.getText().toString());
                                mFirebaseInstance.getReference("cars").child("driverDetail").child(pref.getUID()).child("Fare").setValue(et_per_rate.getText().toString());
                                array_fare_setting.clear();
                                Get_farelist();
                                et_per_rate.setText("");

                            } else {
                                et_per_rate.setText("");
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


    }

    private void Get_farelist() {
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

        Log.e("userid", pref.getUID());

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.Get_Fare + pref.getUID(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        String jsonData = response.toString();
                        Log.e("ResponseFarelist", response.toString());

                        try {
                            JSONObject jobj = new JSONObject(jsonData);
                            if (jobj.getString("status").equals("200")) {


                                JSONArray array = jobj.getJSONArray("data");

                                for (int i = 0; i < array.length(); i++) {
                                    Model_fare fare = new Model_fare();
                                    JSONObject object = array.getJSONObject(i);
                                    fare.setFareid(object.getString("fareid"));
                                    fare.setRate(object.getString("rate"));
                                    fare.setDate(object.getString("con"));
                                    fare.setTime(object.getString("ctime"));
                                    fare.setFlag(object.getString("flag"));
                                    array_fare_setting.add(fare);
                                }
                                if (array_fare_setting.size() == 0) {
                                    rr_last_rate.setVisibility(View.GONE);
                                } else {
                                    rr_last_rate.setVisibility(View.VISIBLE);
                                }
                                fare_setting.notifyDataSetChanged();

                            } else if (jobj.getString("status").equals("400")) {

                                String activity = jobj.getString("activity");
                                if (activity.equals("invalid")) {
                                    Toast.makeText(context, getString(R.string.Add_fare), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context, jobj.getString("message"), Toast.LENGTH_LONG).show();
                                }


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

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}

