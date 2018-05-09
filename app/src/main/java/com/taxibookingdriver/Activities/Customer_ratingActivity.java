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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by Admin on 2/9/2018.
 */

public class Customer_ratingActivity extends AppCompatActivity {

    Pref_Master pref;
    Context context = this;
    RequestQueue queue;
    MaterialRatingBar ratingbar;
    EditText et_rating;
    RelativeLayout rr_rating_submit;
    String riderid = "";
    RelativeLayout rr_comment;
    String ratingg = "";
    float ratings = 0;
    android.support.v7.app.AlertDialog alert;
    TextInputLayout input_comment;
    android.support.v7.app.AlertDialog dialog;


    @Override
    public void onBackPressed() {

    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_customer_rating);
        pref = new Pref_Master(context);
        //dialog = new Progress_dialog();
        queue = Volley.newRequestQueue(context);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            riderid = extras.getString("riderid");
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        pref.setDr_status("1");
        FirebaseDatabase.getInstance().getReference("cars").child("driverDetail").child(pref.getUID()).child("driverstatus").setValue(pref.getDr_status());


        ratingbar = findViewById(R.id.ratingbar);
        input_comment = findViewById(R.id.input_comment);
        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingg = String.valueOf(ratingbar.getRating());
                ratings = ratingbar.getRating();
                Log.e("rating", ratingg);
                ratings = rating;
//                if (rating < 3.0) {
//                    rr_comment.setVisibility(View.VISIBLE);
//                } else {
////                    InputMethodManager inputMethodManager = (InputMethodManager)
////                            getSystemService(Context.INPUT_METHOD_SERVICE);
////                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//                    rr_comment.setVisibility(View.GONE);
//                }
            }
        });
        et_rating = findViewById(R.id.et_rating);

        rr_rating_submit = findViewById(R.id.rr_rating_submit);
        rr_comment = findViewById(R.id.rr_comment);

        rr_rating_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  if (ratings < 3) {

                if (et_rating.getText().toString().trim().length() == 0) {
                    input_comment.setError(getString(R.string.Enter_Comment));
                } else {


                    add_rating();
                }


            }
        });

        MyApplication.getInstance().trackScreenView("Customer Rating Screen");
    }

    private void add_rating() {


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


        int rating = Math.round(ratingbar.getRating());
        Log.e("Ratingsss", "" + rating);


        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("utype", Config.driver);
        postParam.put("driverid", pref.getUID());
        postParam.put("riderid", pref.getRiderid());
        postParam.put("comment", et_rating.getText().toString());
        postParam.put("star", String.valueOf(rating));

        Log.e("parameter_payment_info", " " + new JSONObject(postParam));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.addrating, new JSONObject(postParam),
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


                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("cars").child("trip").child(pref.getTripid());
                                reference.removeValue();
                                pref.setTripid("0");
                                Toast.makeText(context, getResources().getString(R.string.Rating_added), Toast.LENGTH_LONG).show();
                                Intent i = new Intent(Customer_ratingActivity.this, MainActivity.class);
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
