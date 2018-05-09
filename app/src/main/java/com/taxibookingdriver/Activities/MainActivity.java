package com.taxibookingdriver.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.taxibookingdriver.Controller.Config;
import com.taxibookingdriver.Controller.Constant;
import com.taxibookingdriver.Controller.DialogBox;
import com.taxibookingdriver.Controller.GPS_Service;
import com.taxibookingdriver.Controller.GraduallyTextView;
import com.taxibookingdriver.Controller.Pref_Master;
import com.taxibookingdriver.Controller.Progress_dialog;
import com.taxibookingdriver.Fragments.Earning;
import com.taxibookingdriver.Fragments.Fare_Setting;
import com.taxibookingdriver.Fragments.Legal;
import com.taxibookingdriver.Fragments.MainFragment;
import com.taxibookingdriver.Fragments.Rating;
import com.taxibookingdriver.Fragments.Subscription;
import com.taxibookingdriver.MyApplication;
import com.taxibookingdriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    RecyclerView list;
    Context context = this;
    U_NavigationAdapter adapter;
    ImageView notify_bar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    TextView headertext;
    FrameLayout frame;
    public MainFragment mainFragment;
    TextView txt_head;
    ArrayList<String> array_menu = new ArrayList<>();
    private FirebaseDatabase mFirebaseInstance;
    private static final int REQUEST_LOCATION = 1;
    DrawerLayout drawer;
    GoogleApiClient mGoogleApiClient;
    ArrayList<Integer> array_icon = new ArrayList<>();
    ImageView profileimage;
    Bundle extras;
    SwitchCompat switch1;
    Pref_Master pref;
    int autostart = 0;
    RequestQueue queue;
    TextView drivername;
    Location location;
    boolean doubleBackToExitPressedOnce = false;
    TextView onlinetext, offlinetext;
    String fare = "";
    private FusedLocationProviderClient mFusedLocationClient;
    RelativeLayout header;
    android.support.v7.app.AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        pref = new Pref_Master(context);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        showPermiosssionn();
        // dialog = new Progress_dialog();
        queue = Volley.newRequestQueue(this);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        extras = getIntent().getExtras();
        frame = findViewById(R.id.frame);
        Constant.DEVICEID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Constant.DEVICETOKEN = FirebaseInstanceId.getInstance().getToken();
        getsetting();
        profileimage = findViewById(R.id.profileimage);
        drivername = findViewById(R.id.drivername);
        get_profile();
        list = findViewById(R.id.list);
        headertext = findViewById(R.id.headertext);
        header = findViewById(R.id.header);
        switch1 = findViewById(R.id.switch1);
        onlinetext = findViewById(R.id.onlinetext);
        offlinetext = findViewById(R.id.offlinetext);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                setfragment("", new MainFragment());

                get_profile();
            }
        });


        if (pref.getDr_status().equals("1")) {
            switch1.setChecked(true);
            switch1.setThumbDrawable(getResources().getDrawable(R.drawable.only_online_round));
            offlinetext.setTextColor(Color.parseColor("#ffffff"));
            onlinetext.setTextColor(Color.parseColor("#0cfa0c"));


        } else {
            switch1.setChecked(false);
            switch1.setThumbDrawable(getResources().getDrawable(R.drawable.only_offline_round));
            offlinetext.setTextColor(Color.parseColor("#eb2629"));
            onlinetext.setTextColor(Color.parseColor("#ffffff"));
        }

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Driver_status("1", buttonView);
                    switch1.setThumbDrawable(getResources().getDrawable(R.drawable.only_online_round));
                } else {
                    Driver_status("2", buttonView);
                    switch1.setThumbDrawable(getResources().getDrawable(R.drawable.only_offline_round));
                }
            }
        });


        notify_bar = findViewById(R.id.notify_bar);


        notify_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });


        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(i);


//                setfragment("My Profile", new Profile());
//                mDrawerLayout.closeDrawers();
//                switch1.setVisibility(View.GONE);
            }
        });
        mDrawerLayout = findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            // mDrawerToggle = new ActionBarDrawerToggle(ServiceProvider_home.this, mDrawerLayout, R.string.openDrawer, R.string.openDrawer) {
            public void onDrawerClosed(View view) {

                super.onDrawerClosed(view);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        if (pref.getUID().equals("")) {
            notify_bar.setVisibility(View.GONE);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        } else {
            notify_bar.setVisibility(View.VISIBLE);
        }


        adapter = new U_NavigationAdapter();
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(context));
        list.setHasFixedSize(true);

        final int[] size = {array_menu.size()};
        String string = String.valueOf(size[0]);
        boolean arrraaay = Boolean.valueOf(string);

        list.setNestedScrollingEnabled(arrraaay);


        if (pref.getAutostart() == 1) {

        } else {
            try {
                Intent intent1 = new Intent();
                String manufacturer = Build.MANUFACTURER;
                if ("xiaomi".equalsIgnoreCase(manufacturer)) {

                    DialogBox.setAutoStart(context, getString(R.string.auto), runnable2);

                } else if ("oppo".equalsIgnoreCase(manufacturer)) {

                    DialogBox.setAutoStart(context, getString(R.string.auto), runnable2);

                } else if ("vivo".equalsIgnoreCase(manufacturer)) {

                    DialogBox.setAutoStart(context, getString(R.string.auto), runnable2);

                } else if ("huawei".equalsIgnoreCase(manufacturer)) {

                    DialogBox.setAutoStart(context, getString(R.string.auto), runnable2);
                }

                List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent1, PackageManager.MATCH_DEFAULT_ONLY);
                if (list.size() > 0) {
                    startActivity(intent1);
                }
            } catch (Exception e) {

            }

        }

        MyApplication.getInstance().trackScreenView("Main Screen");
        // setfragment("Personal", new Information_tab());


    }

    public void status_change(String value) {
//        String name = pref.getUID();
//        Log.e("name", name);
//        mFirebaseInstance = FirebaseDatabase.getInstance();
//
//        mFirebaseInstance.getReference("cars").child("driverDetail").child(name).child("value").setValue(value);


    }

    @Override
    public void onBackPressed() {
        if (!pref.getBack_value().equals("1")) {
            if (!pref.getUID().equals("")) {
                setfragment("", new MainFragment());
                switch1.setVisibility(View.VISIBLE);
                onlinetext.setVisibility(View.VISIBLE);
                offlinetext.setVisibility(View.VISIBLE);
                headertext.setVisibility(View.GONE);
//        finish();

                if (doubleBackToExitPressedOnce) {
               /*     Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);*/
                    finish();
                    return;
                }


                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, getResources().getString(R.string.Press_again_to_exit), Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                System.exit(0);
            }
        }

    }

    public void showPermiosssionn() {

        if (Build.VERSION.SDK_INT >= 19) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.i("Hello", "Wasssss up");
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_SMS)) {
                    Log.e("YESS", "yes");
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                } else {
                    Log.e("NO", "no");
                    Log.i("Coming in this block", "Wasssss up");
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                }
            }
        }
    }


    public void setfragment(String title, Fragment fragment) {
        headertext.setText(title);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    public class U_NavigationAdapter extends RecyclerView.Adapter<U_NavigationHolder> {


        public U_NavigationAdapter() {

            array_icon.add(R.drawable.dashboard);
            array_icon.add(R.drawable.earning);
            // array_icon.add(R.drawable.subscription);
            array_icon.add(R.drawable.rating1);
            array_icon.add(R.drawable.share_app);
            //  array_icon.add(R.drawable.legal);
            array_icon.add(R.drawable.setting);
            array_icon.add(R.drawable.logout);


            array_menu.add(getString(R.string.Dashboard));
            array_menu.add(getString(R.string.Earning));
            // array_menu.add(getString(R.string.Subscription));
            array_menu.add(getString(R.string.Rating));
            array_menu.add(getString(R.string.Share_App));
            //  array_menu.add(getString(R.string.Legal));
            array_menu.add(getString(R.string.Settings));
            array_menu.add(getString(R.string.Logout));

        }

        @Override
        public U_NavigationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new U_NavigationHolder(LayoutInflater.from(context).inflate(R.layout.u_navigation_listrow, null));
        }

        @Override
        public void onBindViewHolder(final U_NavigationHolder holder, final int position) {
            holder.image_row.setImageResource(array_icon.get(position));
            holder.u_navigation_row_name.setText(array_menu.get(position));

            holder.ll_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (position) {
                        case 0:
                            pref.setBack_value("");
                            setfragment(getString(R.string.Offline), new MainFragment());
                            switch1.setVisibility(View.VISIBLE);
                            onlinetext.setVisibility(View.VISIBLE);
                            offlinetext.setVisibility(View.VISIBLE);
                            headertext.setVisibility(View.GONE);
                            mDrawerLayout.closeDrawers();
                            break;

                        case 1:
                            pref.setBack_value("");
                            setfragment(getString(R.string.Earning), new Earning());
                            switch1.setVisibility(View.GONE);
                            onlinetext.setVisibility(View.GONE);
                            offlinetext.setVisibility(View.GONE);
                            headertext.setVisibility(View.VISIBLE);
                            mDrawerLayout.closeDrawers();
                            break;
//                        case 2:
//                            pref.setBack_value("");
//                            setfragment(getString(R.string.Subscription), new Subscription());
//                            switch1.setVisibility(View.GONE);
//                            onlinetext.setVisibility(View.GONE);
//                            offlinetext.setVisibility(View.GONE);
//                            headertext.setVisibility(View.VISIBLE);
//                            mDrawerLayout.closeDrawers();
//                            break;
//                        case 3:
//                            pref.setBack_value("");
//                            setfragment(getString(R.string.Fare_Setting), new Fare_Setting());
//                            switch1.setVisibility(View.GONE);
//                            onlinetext.setVisibility(View.GONE);
//                            offlinetext.setVisibility(View.GONE);
//                            headertext.setVisibility(View.VISIBLE);
//                            mDrawerLayout.closeDrawers();
//                            break;
                        case 2:
                            pref.setBack_value("");
                            setfragment(getString(R.string.Rating), new Rating());
                            switch1.setVisibility(View.GONE);
                            onlinetext.setVisibility(View.GONE);
                            offlinetext.setVisibility(View.GONE);
                            headertext.setVisibility(View.VISIBLE);
                            mDrawerLayout.closeDrawers();
                            break;
                        case 3:
                            String urll = "https://play.google.com/store/apps/details?id=com.taxibookingdriver";
                            Intent sendIntent = new Intent();
                            sendIntent.setType("text/plain");
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, urll);
                            startActivity(sendIntent);
                            mDrawerLayout.closeDrawers();

                            break;

//                        case 5:
//                            pref.setBack_value("");
//                            setfragment(getString(R.string.Legal), new Legal());
//                            switch1.setVisibility(View.GONE);
//                            onlinetext.setVisibility(View.GONE);
//                            offlinetext.setVisibility(View.GONE);
//                            headertext.setVisibility(View.VISIBLE);
//                            mDrawerLayout.closeDrawers();
//                            break;
                        case 4:
                            LayoutInflater li = LayoutInflater.from(context);
                            View v = li.inflate(R.layout.dialog_language, null);
                            final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context).setView(v).show();
                            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            alert.setCancelable(true);

                            final ArrayList<LinearLayout> Arr_ll_cat = new ArrayList<>();
                            final ArrayList<ImageView> Arr_Chk_cat = new ArrayList<>();

                            Arr_ll_cat.add(0, (LinearLayout) v.findViewById(R.id.ll_l_English));
                            Arr_ll_cat.add(1, (LinearLayout) v.findViewById(R.id.ll_l_Arabic));

                            Arr_Chk_cat.add(0, (ImageView) v.findViewById(R.id.chk_l_english));
                            Arr_Chk_cat.add(1, (ImageView) v.findViewById(R.id.chk_l_arabic));

                            TextView okbtn = v.findViewById(R.id.okbtn);
                            TextView cancel = v.findViewById(R.id.cancel);

                            final String[] pos_lang = {"0"};
                            if (pref.getLanguage().equals("ar")) {
                                Config.Select_Status(Arr_ll_cat, 1, Arr_Chk_cat);
                                pos_lang[0] = "1";
                            } else {
                                Config.Select_Status(Arr_ll_cat, 0, Arr_Chk_cat);
                                pos_lang[0] = "0";
                            }

                            Arr_ll_cat.get(0).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Config.Select_Status(Arr_ll_cat, 0, Arr_Chk_cat);
                                    pos_lang[0] = "0";
                                }
                            });

                            Arr_ll_cat.get(1).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Config.Select_Status(Arr_ll_cat, 1, Arr_Chk_cat);
                                    pos_lang[0] = "1";
                                }
                            });

                            okbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String languageToLoad = "en";
                                    if (pos_lang[0].equals("1")) {
                                        languageToLoad = "ar";
                                        pref.setLanguage("ar");

                                    } else {
                                        languageToLoad = "en";
                                        pref.setLanguage("en");
                                    }
                                    Locale locale = new Locale(languageToLoad);
                                    Locale.setDefault(locale);
                                    Configuration config = new Configuration();
                                    config.locale = locale;
                                    context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
                                    alert.dismiss();
                                    Intent refresh = new Intent(context, MainActivity.class);
                                    refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(refresh);
                                    ((Activity) context).finish();
                                }
                            });
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alert.dismiss();
                                }
                            });
                            mDrawerLayout.closeDrawers();
                            break;


                        case 5:
                            pref.setBack_value("");
                            Logout();
                            break;


                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return array_menu.size();
        }
    }

    public class U_NavigationHolder extends RecyclerView.ViewHolder {

        TextView u_navigation_row_name;
        LinearLayout ll_click;
        View v;
        ImageView image_row;

        public U_NavigationHolder(View itemView) {
            super(itemView);
            this.v = itemView;
            u_navigation_row_name = (TextView) itemView.findViewById(R.id.u_navigation_row_name);
            ll_click = (LinearLayout) itemView.findViewById(R.id.ll_click);
            image_row = itemView.findViewById(R.id.imageicons);
        }
    }

    public void Logout() {


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
        postParam.put("uid", pref.getUID());
        postParam.put("utype", Config.driver);

        Log.e("parameter", " " + new JSONObject(postParam));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.logout, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e("Reposnse", response.toString());
                        String jsonData = response.toString();
                        dialog.dismiss();
                        try {
                            JSONObject jobj = new JSONObject(jsonData);
                            if (jobj.getString("activity").equals("logout-success")) {


                                mFirebaseInstance.getReference("cars").child("driverDetail").child(pref.getUID()).child("driverstatus").setValue("0");


                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("cars").child(pref.getCar_type()).child("driverAvailable");
                                reference.child(pref.getUID()).removeValue();


                                Intent i = new Intent(context, FirstScreen.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                // pref.clear_pref();
                                pref.setCar_type("");
                                pref.setAuto(1);
                                pref.setUID("");
                                pref.setFront_img_var("");
                                pref.setFront_img("");
                                pref.setBack_img_var("");
                                pref.setBack_img("");
                                startActivity(i);
                                finish();


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

    }

    public void get_profile() {
        // newtonCradleLoading.start();
        Log.e("userid", pref.getUID());

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.Get_profile + pref.getUID() + "/" + Config.driver,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // newtonCradleLoading.stop();
                        String jsonData = response.toString();

                        Log.e("ResponseGetProfile", response.toString());

                        try {
                            JSONObject jobj = new JSONObject(jsonData);
                            if (jobj.getString("status").equals("200")) {

                                JSONArray array = jobj.getJSONArray("data");

                                JSONObject object = array.getJSONObject(0);

                                if (!object.getString("upic").equals("")) {
                                    Glide.with(context)
                                            .load(Constant.BUCKET_URL + pref.getUID() + "/" + "Profile" + "/" + object.getString("upic")) //extract as User instance method
                                            .placeholder(R.drawable.personal)
                                            .into(profileimage);

                                }
                                drivername.setText(object.getString("fname") + " " + object.getString("lname"));


                                pref.setBack_img(object.getString("upic"));
                                pref.setFname(object.getString("fname"));
                                pref.setLname(object.getString("lname"));
                                pref.setMobile(object.getString("mobile"));
                                pref.setEmail(object.getString("email"));
                                pref.setReg_num(object.getString("regno"));
                                pref.setColor(object.getString("color"));
                                pref.setCar_type(object.getString("ctype"));
                                pref.setCar_model(object.getString("model"));
                                pref.setPwd(object.getString("password"));
                                pref.setDriverrating(object.getString("avg"));
                                pref.setFare(object.getString("dfare"));
                                mFirebaseInstance.getReference("cars").child("driverDetail").child(pref.getUID()).child("driverrating").setValue(object.getString("avg"));
                                mFirebaseInstance.getReference("cars").child("driverDetail").child(pref.getUID()).child("Fare").setValue(object.getString("dfare"));


                            } else {
                                //  Toast.makeText(context, jobj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //newtonCradleLoading.stop();
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


    private void Driver_status(final String status, final CompoundButton compoundButton) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {

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
                    postParam.put("uid", pref.getUID());
                    postParam.put("action", status);
                    postParam.put("latlong", location.getLatitude() + "," + location.getLongitude());

                    Log.e("parameter", " " + new JSONObject(postParam));


                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                            Config.Set_driver_status, new JSONObject(postParam),
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.e("Reposnse", response.toString());
                                    String jsonData = response.toString();

                                    try {
                                        JSONObject jobj = new JSONObject(jsonData);
                                        if (jobj.getString("status").equals("200")) {
                                            Log.e("status", status);
                                            if (status.equals("1")) {

                                                Log.e("CurrentFare", pref.getFare());

                                                if (pref.getFare().equals("0.00")) {
                                                    compoundButton.setChecked(false);
                                                    DialogBox.setAddFare(context, "Please Enter Fare", runnable);
                                                } else {
                                                    pref.setDr_status("1");
                                                    Log.e("Pref_value_if", pref.getDr_status());
                                                    status_change("1");

                                                    Intent ii = new Intent(context, GPS_Service.class);
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                                                        // Context c = getActivity();
                                                        context.startService(getServiceIntent(context));
                                                    } else {
                                                        startService(ii);
                                                    }
//                                                    offlinetext.setTextColor(Color.parseColor("#000000"));
//                                                    onlinetext.setTextColor(Color.parseColor("#0cfa0c"));
                                                    FirebaseDatabase.getInstance().getReference("cars").child("driverDetail").child(pref.getUID()).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.child("driverstatus").getValue().equals("1")) {
                                                                dialog.dismiss();
                                                                offlinetext.setTextColor(Color.parseColor("#ffffff"));
                                                                onlinetext.setTextColor(Color.parseColor("#0cfa0c"));
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                }


                                            } else {
                                                dialog.dismiss();
                                                pref.setDr_status("0");
                                                Log.e("Pref_value_else", pref.getDr_status());
                                                status_change("0");
                                                mFirebaseInstance.getReference("cars").child("driverDetail").child(pref.getUID()).child("driverstatus").setValue("0");
                                                offlinetext.setTextColor(Color.parseColor("#eb2629"));
                                                onlinetext.setTextColor(Color.parseColor("#ffffff"));
                                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("cars").child(pref.getCar_type()).child("driverAvailable");
                                                reference.child(pref.getUID()).removeValue();
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

                    jsonObjReq.setTag("POST");
// Adding request to request queue
                    queue.add(jsonObjReq);
                }
            }
        });


// Cancelling request
/* if (queue!= null) {
queue.cancelAll(TAG);
} */

    }

    private static Intent getServiceIntent(Context c) {
        return new Intent(c, GPS_Service.class);
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
                                    } else if (sid.equals("21")) {
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

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            pref.setBack_value("");
            setfragment(getString(R.string.Fare_Setting), new Fare_Setting());
            switch1.setVisibility(View.GONE);
            onlinetext.setVisibility(View.GONE);
            offlinetext.setVisibility(View.GONE);
            headertext.setVisibility(View.VISIBLE);
            mDrawerLayout.closeDrawers();
        }
    };

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
