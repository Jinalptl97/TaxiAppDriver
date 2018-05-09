package com.taxibookingdriver.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.taxibookingdriver.Controller.Config;
import com.taxibookingdriver.Controller.Constant;
import com.taxibookingdriver.Controller.Pref_Master;
import com.taxibookingdriver.Controller.SwipeDisableviewpager;
import com.taxibookingdriver.Font.TahomaBold;
import com.taxibookingdriver.Fragments.Bank_info;
import com.taxibookingdriver.Fragments.Car_info;
import com.taxibookingdriver.Fragments.Personal_info;
import com.taxibookingdriver.MyApplication;
import com.taxibookingdriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Admin on 1/16/2018.
 */

public class Information_tab extends AppCompatActivity {

    private TabLayout tabLayout;
    Pref_Master pref;
    public static SwipeDisableviewpager viewpager;
    private int[] tabIcons = {
            R.drawable.personal,
            R.drawable.car_icon,
            R.drawable.bank
    };


    Context context = this;
    TahomaBold textview;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.fragment_info);

        pref = new Pref_Master(context);
        viewpager = findViewById(R.id.viewpager);
        createViewPager(viewpager);
        viewpager.setPagingEnabled(false);
        queue = Volley.newRequestQueue(context);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewpager);
        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
        createTabIcons();
        getsetting();

        MyApplication.getInstance().trackScreenView("Information Tab Screen");


    }


    private void createTabIcons() {
        View headerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_tabbb, null, false);

        LinearLayout linearLayoutOne = (LinearLayout) headerView.findViewById(R.id.ll);
        LinearLayout linearLayout2 = (LinearLayout) headerView.findViewById(R.id.ll2);
        LinearLayout linearLayout3 = (LinearLayout) headerView.findViewById(R.id.ll3);

        tabLayout.getTabAt(0).setCustomView(linearLayoutOne);
        tabLayout.getTabAt(1).setCustomView(linearLayout2);
        tabLayout.getTabAt(2).setCustomView(linearLayout3);

    }

    private void createViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new Personal_info(), getResources().getString(R.string.Personal));
        adapter.addFrag(new Car_info(), getResources().getString(R.string.Car));
        adapter.addFrag(new Bank_info(), getResources().getString(R.string.Banks));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    public void getsetting() {

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("settingtype", "all");

        Log.e("parameter", " " + new JSONObject(postParam));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.Get_settings, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Reposnse", response.toString());

// {"status":200,"activity":"reset-success","message":"Your Password has been changed Successfully !","data":[{"uid":"1516340787249"}]}
                        String jsonData = response.toString();
//

                        try {
                            JSONObject jobj = new JSONObject(jsonData);
                            if (jobj.getString("status").equals("200")) {

                                JSONArray array = jobj.getJSONArray("data");

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.getJSONObject(i);
                                    String sid = object.getString("sid");
                                    if (sid.equals("1")) {
                                        Constant.BUCKET = object.getString("sval");
                                    } else if (sid.equals("2")) {
                                        Constant.ACCESSKEY = object.getString("sval");
                                    } else if (sid.equals("3")) {
                                        Constant.SECRETKEY = object.getString("sval");
                                    } else if (sid.equals("4")) {
                                        Constant.POOL_ID = object.getString("sval");
                                    } else if (sid.equals("5")) {
                                        Constant.POOLARN = object.getString("sval");
                                    } else if (sid.equals("6")) {
                                        Constant.REGION = object.getString("sval");
                                    } else if (sid.equals("7")) {
                                        Constant.IMAGE_FOLDER = object.getString("sval");
                                    } else if (sid.equals("8")) {
                                        Constant.BUCKET_URL = object.getString("sval");
                                        pref.setBucket_url(object.getString("sval"));
                                    } else if (sid.equals("9")) {
                                        Constant.DEFAULT_FARE = object.getString("sval");
                                    } else if (sid.equals("10")) {
                                        Constant.RIDERCANCEL_RATE = object.getString("sval");
                                    } else if (sid.equals("11")) {
                                        Constant.WAITING_CHARGE = object.getString("sval");
                                    } else if (sid.equals("19")) {
                                        Constant.CURRENCY = object.getString("sval");

                                    } else if (sid.equals("20")) {
                                        Constant.MOBILE_LENGTH = object.getString("sval");
                                    }else if (sid.equals("21")) {
                                        Constant.four_Seater = object.getString("snm");
                                    } else if (sid.equals("22")) {
                                        Constant.six_Seater = object.getString("snm");
                                    } else if (sid.equals("23")) {
                                        Constant.MINI = object.getString("snm");

                                    }
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

}
