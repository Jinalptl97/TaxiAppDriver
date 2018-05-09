package com.taxibookingdriver.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.taxibookingdriver.Adapter.Timewise_adapter;
import com.taxibookingdriver.Controller.Config;
import com.taxibookingdriver.Controller.Constant;
import com.taxibookingdriver.Controller.GraduallyTextView;
import com.taxibookingdriver.Controller.Pref_Master;
import com.taxibookingdriver.Controller.Progress_dialog;
import com.taxibookingdriver.Models.Model_earning_load;
import com.taxibookingdriver.MyApplication;
import com.taxibookingdriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.taxibookingdriver.Fragments.Datewise_trip.tripdate;
import static com.taxibookingdriver.Fragments.Datewise_trip.tripday;

/**
 * Created by Admin on 1/16/2018.
 */

public class Timewise_trip extends Fragment {

    RecyclerView rr_time_wise;
    Context context;
    ArrayList<Model_earning_load> array_trip = new ArrayList<>();
    Timewise_adapter timewiseTrip;
    FragmentManager manager;
    Pref_Master pref;
    TextView txt_head;
    RequestQueue queue;
    android.support.v7.app.AlertDialog dialog;
    TextView txt_total_trip, txt_payment, txt_cash, txt_card, txt_final;
    String nextkey = "";
    public static String tripid = "";
    public static String riderid = "";


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_wise, container, false);
        context = getActivity();
        manager = getFragmentManager();
        pref = new Pref_Master(context);
        // dialog = new Progress_dialog();
        queue = Volley.newRequestQueue(context);

        Bundle bundle = getArguments();
        if (bundle != null) {
            tripdate = bundle.getString("tripdate");
            tripday = bundle.getString("tripday");

        }


        txt_head = getActivity().findViewById(R.id.headertext);
        txt_head.setText(tripday);

        txt_total_trip = view.findViewById(R.id.txt_total_trip);
        txt_payment = view.findViewById(R.id.txt_payment);
        txt_cash = view.findViewById(R.id.txt_cash);
        txt_card = view.findViewById(R.id.txt_card);
        txt_final = view.findViewById(R.id.txt_final);


        rr_time_wise = view.findViewById(R.id.rr_time_wise);
        int numberOfColumns = 1;
        rr_time_wise.setLayoutManager(new GridLayoutManager(context, numberOfColumns));

        array_trip.clear();

        timewiseTrip = new Timewise_adapter(context, array_trip, manager);

        timewiseTrip.setLoadMoreListener(new Timewise_adapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                rr_time_wise.post(new Runnable() {
                    @Override
                    public void run() {
                        if (nextkey.equals("")) {

                        } else {
                            earning_weekdetails_Reload();
                        }


                    }
                });
            }
        });
        rr_time_wise.setAdapter(timewiseTrip);
        earning_weekdetails();
        return view;
    }


    @Override
    public void onResume() {

        super.onResume();

        if (getView() == null) {
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_UP && i == KeyEvent.KEYCODE_BACK) {

                    FragmentManager fm = getFragmentManager();
                    fm.popBackStackImmediate();


/*
                    Fragment newFragment = new Datewise_trip();
//                    Bundle data = new Bundle();
//                    data.putString("firstday", firstday);
//                    data.putString("lastday", lastday);
//                    data.putString("week", week_name);
                    FragmentTransaction transaction = manager.beginTransaction();
                    // newFragment.setArguments(data);
                    transaction.replace(R.id.frame, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();*/
// handle back button's click listener
                }

                return false;
            }
        });
        MyApplication.getInstance().trackScreenView("Timewise Trip");

    }

    private void earning_weekdetails() {


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
        postParam.put("tripdate", tripdate);

        Log.e("week_details_info", " " + new JSONObject(postParam));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.earningweekdaysdetails, new JSONObject(postParam),
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

                                JSONObject object = jobj.getJSONObject("data");

                                JSONObject jsonObject = object.getJSONObject("trip");
                                nextkey = object.getString("nextkey");


                                JSONArray array = jsonObject.getJSONArray("days");

                                for (int i = 0; i < array.length(); i++) {
                                    Model_earning_load earning = new Model_earning_load("post");
                                    JSONObject obj = array.getJSONObject(i);
                                    earning.setTripid(obj.getString("tripid"));
                                    earning.setRiderid(obj.getString("riderid"));
                                    earning.setTripday(obj.getString("tripday"));
                                    earning.setTriptime(obj.getString("time"));
                                    earning.setAmt(obj.getString("amt"));
                                 /*   tripid = obj.getString("tripid");
                                    riderid = obj.getString("riderid");*/
                                    array_trip.add(earning);

                                }

                                JSONArray payment_array = jsonObject.getJSONArray("payment");

                                JSONObject jsonObj = payment_array.getJSONObject(0);
                                txt_card.setText(Constant.CURRENCY + jsonObj.getString("card"));
                                txt_cash.setText(Constant.CURRENCY + jsonObj.getString("cash"));
                                txt_payment.setText(Constant.CURRENCY + jsonObj.getString("total"));
                                txt_final.setText(Constant.CURRENCY + jsonObj.getString("total"));
                                txt_total_trip.setText(jsonObj.getString("trip"));


                            } else if (response.getString("status").equals("400")) {
                                Toast.makeText(context, jobj.getString("message"), Toast.LENGTH_LONG).show();
                                timewiseTrip.setMoreDataAvailable(false);
                                timewiseTrip.notifyDataChanged();
                            }
                            timewiseTrip.notifyDataSetChanged();

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

    private void earning_weekdetails_Reload() {

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
        postParam.put("tripdate", tripdate);

        Log.e("week_details_info", " " + new JSONObject(postParam));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.earningweekdaysdetails, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.e("Reposnse", response.toString());
                        array_trip.remove(array_trip.size() - 1);
                        String jsonData = response.toString();
//

                        try {

                            JSONObject jobj = new JSONObject(jsonData);

                            if (jobj.getString("status").equals("200")) {

                                JSONObject object = jobj.getJSONObject("data");

                                JSONObject jsonObject = object.getJSONObject("trip");
                                nextkey = object.getString("nextkey");


                                JSONArray array = jsonObject.getJSONArray("days");

                                for (int i = 0; i < array.length(); i++) {
                                    Model_earning_load earning = new Model_earning_load("post");
                                    JSONObject obj = array.getJSONObject(i);
                                    earning.setTripid(obj.getString("tripid"));
                                    earning.setRiderid(obj.getString("riderid"));
                                    earning.setTripday(obj.getString("tripday"));
                                    earning.setTriptime(obj.getString("time"));
                                    earning.setAmt(obj.getString("amt"));
                                /*   tripid = obj.getString("tripid");
                                    riderid = obj.getString("riderid");*/
                                    array_trip.add(earning);

                                }

                                JSONArray payment_array = jsonObject.getJSONArray("payment");

                                JSONObject jsonObj = payment_array.getJSONObject(0);
                                txt_card.setText(Constant.CURRENCY + jsonObj.getString("card"));
                                txt_cash.setText(Constant.CURRENCY + jsonObj.getString("cash"));
                                txt_payment.setText(Constant.CURRENCY + jsonObj.getString("total"));
                                txt_final.setText(Constant.CURRENCY + jsonObj.getString("total"));
                                txt_total_trip.setText(jsonObj.getString("trip"));


                            } else if (response.getString("status").equals("400")) {
                                Toast.makeText(context, jobj.getString("message"), Toast.LENGTH_LONG).show();
                                timewiseTrip.setMoreDataAvailable(false);
                                timewiseTrip.notifyDataChanged();
                            }
                            timewiseTrip.notifyDataSetChanged();

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
