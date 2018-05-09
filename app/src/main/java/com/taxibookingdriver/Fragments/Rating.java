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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.taxibookingdriver.Activities.MainActivity;
import com.taxibookingdriver.Adapter.Rating_adapter;
import com.taxibookingdriver.Controller.Config;
import com.taxibookingdriver.Controller.GraduallyTextView;
import com.taxibookingdriver.Controller.Pref_Master;
import com.taxibookingdriver.Controller.Progress_dialog;
import com.taxibookingdriver.Models.Model_rating;
import com.taxibookingdriver.MyApplication;
import com.taxibookingdriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by Admin on 1/16/2018.
 */

public class Rating extends Fragment {

    RecyclerView rr_rating;
    Context context;
    ArrayList<Model_rating> array_rating = new ArrayList<>();
    Rating_adapter rating_adapter;
    TextView txt_total_rating, five_rating, four_rating, three_rating, two_rating, one_rating;
    android.support.v7.app.AlertDialog dialog;
    RequestQueue queue;
    Pref_Master pref;
    MaterialRatingBar rating_dis;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rating, container, false);
        context = getActivity();
        pref = new Pref_Master(context);
        //  dialog = new Progress_dialog();
        queue = Volley.newRequestQueue(context);

        txt_total_rating = view.findViewById(R.id.txt_total_rating);
        five_rating = view.findViewById(R.id.five_rating);
        four_rating = view.findViewById(R.id.four_rating);
        three_rating = view.findViewById(R.id.three_rating);
        two_rating = view.findViewById(R.id.two_rating);
        one_rating = view.findViewById(R.id.one_rating);
        rating_dis = view.findViewById(R.id.rating_dis);

        rr_rating = view.findViewById(R.id.rr_rating);
        int numberOfColumns = 1;
        rr_rating.setLayoutManager(new GridLayoutManager(context, numberOfColumns));

        rating_adapter = new Rating_adapter(context, array_rating);
        rr_rating.setAdapter(rating_adapter);
        Get_rating();
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
        MyApplication.getInstance().trackScreenView("Rating");

    }

    private void Get_rating() {
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

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.viewratings + pref.getUID(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        String jsonData = response.toString();
                        Log.e("response_rating", response);

                        try {
                            JSONObject jobj = new JSONObject(jsonData);
                            if (jobj.getString("status").equals("200")) {


                                JSONArray array = jobj.getJSONArray("data");

                                JSONObject object = array.getJSONObject(0);
                                txt_total_rating.setText(object.getString("avg"));
                                rating_dis.setRating(Float.parseFloat(object.getString("avg")));
                                five_rating.setText(object.getString("rate5") + "%");
                                four_rating.setText(object.getString("rate4") + "%");
                                three_rating.setText(object.getString("rate3") + "%");
                                two_rating.setText(object.getString("rate2") + "%");
                                one_rating.setText(object.getString("rate1") + "%");

                                JSONArray array1 = object.getJSONArray("comment");

                                for (int i = 0; i < array1.length(); i++) {
                                    Model_rating rating = new Model_rating();
                                    JSONObject obj = array1.getJSONObject(i);
                                    rating.setRating_msg(obj.getString("msg"));
                                    rating.setRating_date(obj.getString("msgon"));
                                    rating.setRating(obj.getString("star"));
                                    array_rating.add(rating);
                                }
                                if (array_rating.size() == 0) {
                                    rr_rating.setVisibility(View.GONE);
                                } else {
                                    rr_rating.setVisibility(View.VISIBLE);
                                }
                                rating_adapter.notifyDataSetChanged();

                            } else {
                                txt_total_rating.setText("0.00");
                                Toast.makeText(context, "No Rating Found", Toast.LENGTH_LONG).show();
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
