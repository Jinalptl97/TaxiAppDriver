package com.taxibookingdriver.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.taxibookingdriver.Adapter.Datewise_adapter;
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

import static com.taxibookingdriver.Fragments.Earning.firstday;
import static com.taxibookingdriver.Fragments.Earning.lastday;
import static com.taxibookingdriver.Fragments.Earning.week_name;

/**
 * Created by Admin on 1/16/2018.
 */

public class Datewise_trip extends Fragment {
    RecyclerView rr_date_wise;
    Context context;
    ArrayList<Model_earning_load> array_trip = new ArrayList<>();
    Datewise_adapter timewiseTrip;
    TextView txt_head;
    RequestQueue queue;
    android.support.v7.app.AlertDialog dialog;
    Pref_Master pref;
    //    String firstday = "";
//    String lastday = "";
//    String week = "";
    FragmentManager manager;
    TextView txt_final, txt_card, txt_cash, txt_payment;
    String nextkey = "";
    public static String tripday;
    public static String tripdate;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_date_wise, container, false);
        context = getActivity();
        manager = getFragmentManager();
        pref = new Pref_Master(context);
        //dialog = new Progress_dialog();

        queue = Volley.newRequestQueue(context);

//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            firstday = bundle.getString("firstday");
//            lastday = bundle.getString("lastday");
//            week = bundle.getString("week");
//        }

        txt_head = getActivity().findViewById(R.id.headertext);
        txt_final = view.findViewById(R.id.txt_final);
        txt_card = view.findViewById(R.id.txt_card);
        txt_cash = view.findViewById(R.id.txt_cash);
        txt_payment = view.findViewById(R.id.txt_payment);

        txt_head.setText(week_name);
        rr_date_wise = view.findViewById(R.id.rr_date_wise);
        int numberOfColumns = 1;
        rr_date_wise.setLayoutManager(new GridLayoutManager(context, numberOfColumns));

        array_trip.clear();

        timewiseTrip = new Datewise_adapter(context, array_trip, manager);

        timewiseTrip.setLoadMoreListener(new Datewise_adapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                rr_date_wise.post(new Runnable() {
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

        rr_date_wise.setAdapter(timewiseTrip);
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

                    Fragment newFragment = new Earning();
                    txt_head.setText("Earning");
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.frame, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

// handle back button's click listener
                }

                return false;
            }
        });
        MyApplication.getInstance().trackScreenView("Datewise Trip");

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
        postParam.put("firstday", firstday);
        postParam.put("lastday", lastday);
        postParam.put("type", "");

        Log.e("week_details_info", " " + new JSONObject(postParam));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.earningweekdetails, new JSONObject(postParam),
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
                                    earning.setTripday(obj.getString("tripday"));
                                    earning.setTripdate(obj.getString("tripdate"));
                                    earning.setStype(obj.getString("stype"));
                                    earning.setDtransid(obj.getString("dtransid"));
                                    earning.setAmt(obj.getString("amt"));
                                    tripday = obj.getString("tripday");
                                    tripdate = obj.getString("tripdate");
                                    array_trip.add(earning);

                                }

                                JSONArray payment_array = jsonObject.getJSONArray("payment");

                                JSONObject jsonObj = payment_array.getJSONObject(0);
                                txt_card.setText(Constant.CURRENCY + jsonObj.getString("card"));
                                txt_cash.setText(Constant.CURRENCY + jsonObj.getString("cash"));
                                txt_payment.setText(Constant.CURRENCY + jsonObj.getString("total"));
                                txt_final.setText(Constant.CURRENCY + jsonObj.getString("total"));


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


        array_trip.add(new Model_earning_load("load"));
        timewiseTrip.notifyItemInserted(array_trip.size() - 1);


        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("driverid", pref.getUID());
        postParam.put("firstday", firstday);
        postParam.put("lastday", lastday);
        postParam.put("type", "");


        Log.e("week_details_info", " " + new JSONObject(postParam));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.earningweekdetails, new JSONObject(postParam),
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
                                    earning.setTripday(obj.getString("tripday"));
                                    earning.setTripdate(obj.getString("tripdate"));
                                    earning.setStype(obj.getString("stype"));
                                    earning.setDtransid(obj.getString("dtransid"));
                                    earning.setAmt(obj.getString("amt"));
                                    tripday = obj.getString("tripday");
                                    tripdate = obj.getString("tripdate");
                                    array_trip.add(earning);

                                }

                                JSONArray payment_array = jsonObject.getJSONArray("payment");

                                JSONObject jsonObj = payment_array.getJSONObject(0);
                                txt_card.setText(Constant.CURRENCY + jsonObj.getString("card"));
                                txt_cash.setText(Constant.CURRENCY + jsonObj.getString("cash"));
                                txt_payment.setText(Constant.CURRENCY + jsonObj.getString("balance"));
                                txt_final.setText(Constant.CURRENCY + jsonObj.getString("total"));


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
