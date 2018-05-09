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
import com.taxibookingdriver.Activities.MainActivity;
import com.taxibookingdriver.Adapter.Subscription_adapter;
import com.taxibookingdriver.Controller.Config;
import com.taxibookingdriver.Controller.Constant;
import com.taxibookingdriver.Controller.GraduallyTextView;
import com.taxibookingdriver.Controller.Pref_Master;
import com.taxibookingdriver.Controller.Progress_dialog;
import com.taxibookingdriver.Models.Model_earning;
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

public class Subscription extends Fragment {

    RecyclerView rr_subscription_list;
    Context context;
    ArrayList<Model_earning> array_subscription = new ArrayList<>();
    Subscription_adapter subscription;
    TextView txt_total_due;
    RequestQueue queue;
    android.support.v7.app.AlertDialog dialog;

    Pref_Master pref;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subscription, container, false);
        context = getActivity();
        pref = new Pref_Master(context);
        //  dialog = new Progress_dialog();
        queue = Volley.newRequestQueue(context);

        rr_subscription_list = view.findViewById(R.id.rr_subscription_list);
        int numberOfColumns = 1;
        rr_subscription_list.setLayoutManager(new GridLayoutManager(context, numberOfColumns));

        subscription = new Subscription_adapter(context, array_subscription);
        rr_subscription_list.setAdapter(subscription);

        txt_total_due = view.findViewById(R.id.txt_total_due);

        get_subscription();
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

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

//                    FragmentManager fm = getFragmentManager();
//                    FragmentTransaction ft = fm.beginTransaction();
//                    ft.remove(Subscription.this);
//                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
//                    ft.commit();
// handle back button's click listener
                }

                return false;
            }
        });
        MyApplication.getInstance().trackScreenView("Subscription");

    }


    private void get_subscription() {

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

        Log.e("subscription_info", " " + new JSONObject(postParam));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.subscription, new JSONObject(postParam),
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

                                JSONObject jsonObject = object.getJSONObject("payment");

                                // txt_sub_cycle.setText("Subscription Cycle : " + jsonObject.getString("subcycle"));
                                txt_total_due.setText(Constant.CURRENCY + jsonObject.getString("subpending"));
                                /*txt_total_due.setText("$" + jsonObject.getString("balance"));*/


                                JSONArray array = object.getJSONArray("subscription");


                                for (int i = 0; i < array.length(); i++) {
                                    Model_earning earning = new Model_earning();
                                    JSONObject obj = array.getJSONObject(i);
                                    earning.setFirstday(obj.getString("firstDay"));
                                    earning.setLastday(obj.getString("lastDay"));
                                    earning.setAmt(obj.getString("amt"));
                                    array_subscription.add(earning);

                                }
                                subscription.notifyDataSetChanged();


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
