package com.taxibookingdriver.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.taxibookingdriver.Controller.GraduallyTextView;
import com.taxibookingdriver.Controller.Pref_Master;
import com.taxibookingdriver.Controller.Progress_dialog;
import com.taxibookingdriver.Models.Cartype;
import com.taxibookingdriver.MyApplication;
import com.taxibookingdriver.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.taxibookingdriver.Activities.Information_tab.viewpager;

/**
 * Created by Admin on 1/16/2018.
 */

public class Car_info extends Fragment {

    Context context;
    TextView txt_head;
    Spinner car_type;
    RelativeLayout rr_car;
    EditText et_car_model, et_color, et_reg_num;
    Pref_Master pref;
    String value = "";
    ArrayList<String> array_car = new ArrayList<>();
    android.support.v7.app.AlertDialog dialog;
    RequestQueue queue;
    TextInputLayout input_model, input_color, input_register;


    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_car_info, container, false);
        context = getActivity();
        pref = new Pref_Master(context);

        //dialog = new Progress_dialog();


        queue = Volley.newRequestQueue(context);


        txt_head = getActivity().findViewById(R.id.headertext);
        et_car_model = view.findViewById(R.id.et_car_model);
        et_color = view.findViewById(R.id.et_color);
        et_reg_num = view.findViewById(R.id.et_reg_num);
        rr_car = view.findViewById(R.id.rr_car);

        input_model = view.findViewById(R.id.input_model);
        input_color = view.findViewById(R.id.input_color);
        input_register = view.findViewById(R.id.input_register);


        car_type = view.findViewById(R.id.car_type);
        //   Cartype.arrayList(array_car);
        array_car.add(getResources().getString(R.string.Select_Car_Type));
        array_car.add(getResources().getString(R.string.Sedan));
        array_car.add(getResources().getString(R.string.SUV));
        array_car.add(getResources().getString(R.string.Mini));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_layout, array_car);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        car_type.setAdapter(adapter);

        car_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("item", ":" + position);
                if (position == 1) {
                    pref.setCar_type(Constant.four_Seater);
                } else if (position == 2) {
                    pref.setCar_type(Constant.six_Seater);
                } else if (position == 3) {
                    pref.setCar_type(Constant.MINI);
                }
                // pref.setCar_type((String) parent.getItemAtPosition(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rr_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validate();

            }
        });

        et_car_model.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                input_model.setErrorEnabled(false);

            }
        });

        et_color.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                input_color.setErrorEnabled(false);

            }
        });

        et_reg_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                input_register.setErrorEnabled(false);

            }
        });

        return view;
    }

    private void Validate() {
        if (car_type.getSelectedItem().toString().equals(getString(R.string.Select_Car_Type))) {
            TextView errorText = (TextView) car_type.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText(getString(R.string.Please_select_car_type));
        } else if (et_car_model.getText().toString().equals("")) {
            input_model.setError(getString(R.string.Enter_car_model));
        } else if (et_color.getText().toString().equals("")) {
            input_color.setError(getString(R.string.Enter_car_color));
            input_model.setErrorEnabled(false);
        } else if (et_reg_num.getText().toString().equals("")) {
            input_register.setError(getString(R.string.Enter_Resgistration_number));
            input_color.setErrorEnabled(false);
        } else {
            input_register.setErrorEnabled(false);
            Check_details();


        }

    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("Car Info");
    }

    private void Check_details() {


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
        postParam.put("tab", "2");
        postParam.put("regno", et_reg_num.getText().toString());

        Log.e("parameter_Car_info_info", " " + new JSONObject(postParam));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.Check_details, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.e("Reposnse", response.toString());

                        //{"status":200,"activity":"register-success","message":"Registered Successfully !","data":[{"uid":"1516340787249"}]}

                        String jsonData = response.toString();
//

                        try {

                            JSONObject jobj = new JSONObject(jsonData);

                            if (jobj.getString("status").equals("200")) {

                                if (jobj.getString("activity").equals("allow-tab3")) {

                                    viewpager.setCurrentItem(2);
                                    txt_head = getActivity().findViewById(R.id.headertext);
                                    txt_head.setText(getResources().getString(R.string.Banks));
                                    pref.setCar_model(et_car_model.getText().toString());
                                    pref.setColor(et_color.getText().toString());
                                    pref.setReg_num(et_reg_num.getText().toString());


                                } else {
                                    Toast.makeText(context, jobj.getString("message"), Toast.LENGTH_LONG).show();
                                }
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
