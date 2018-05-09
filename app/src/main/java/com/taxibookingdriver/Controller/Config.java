package com.taxibookingdriver.Controller;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.taxibookingdriver.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Admin on 12/5/2016.
 */

public class Config {


    public static String app_url = "https://enj4scog4b.execute-api.us-east-1.amazonaws.com/beta/";


    public static String headunm = "application/json";
    public static String KEY_VALUE = "wgNv4qZdy52i1M3bIjEcwap9lKl64MgQ8IcLZq0c";
    public static String driver = "Driver";


    public static String Message = "Server Error....Please try again...!!!";

    public static String checkmobile = app_url + "chekmobile";
    public static String Check_details = app_url + "authorizedetails";
    public static String register = app_url + "register-driver";
    public static String ResendOTP = app_url + "resendotp/";
    public static String login = app_url + "login";
    public static String logout = app_url + "logout";
    public static String Reset_pwd = app_url + "resetpassword";
    public static String Get_profile = app_url + "getprofile/";
    public static String Set_driver_status = app_url + "setdriverstatus";
    public static String Update_profile = app_url + "updateprofile";
    public static String Get_settings = app_url + "getsettings";
    public static String Add_fare = app_url + "addfare";
    public static String Get_Fare = app_url + "getfarelist/";
    public static String trip_into = app_url + "tripinfodriver";
    public static String driver_response = app_url + "drivertripresponse";
    public static String get_Confirm_data = app_url + "tripinforider";
    public static String start_trip = app_url + "starttrip";
    public static String End_trip = app_url + "endtrip";
    public static String trippaymentdetails = app_url + "trippaymentdetails";
    public static String addrating = app_url + "addrating";
    public static String viewratings = app_url + "viewratings/";
    public static String earning = app_url + "earning";
    public static String earningweekdetails = app_url + "earningweekdetails";
    public static String earningweekdaysdetails = app_url + "earningweekdaysdetails";
    public static String subscription = app_url + "subscription";
    public static String deleteaccount = app_url + "deleteaccount";
    public static String checkversion = app_url + "checkversion";
    public static String addchatmsg = app_url + "addchatmsg";

    // code to define map header
//    public static Map<String, String> getHeaderParam() {
//        Map<String, String> header = new HashMap<>();
//        header.put("Content-Type", Config.headunm);
//        return header;
//    }

    public static Map<String, String> getHeaderParam(Context context) {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", Config.headunm);
        header.put("x-api-key", Config.KEY_VALUE);
        return header;
    }

    public static boolean isValidEmailAddress(String emailAddress) {
        String emailRegEx;
        Pattern pattern;
        // Regex for a valid email address
        emailRegEx = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$";
        // Compare the regex with the email address
        pattern = Pattern.compile(emailRegEx);
        Matcher matcher = pattern.matcher(emailAddress);
        if (!matcher.find()) {
            return false;
        }
        return true;
    }

    public static String MobilePattern = "[0-9]{10}";

    public static class Utility {
        public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public static boolean checkPermission(final Context context) {
            int currentAPIVersion = Build.VERSION.SDK_INT;
            if (currentAPIVersion >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("External storage permission is necessary");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();
                    } else {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
    }

    public static void Select_Status(ArrayList<LinearLayout> ll, int pos, ArrayList<ImageView> Arr_Chk_cat) {
        for (int i = 0; i < ll.size(); i++) {
            Arr_Chk_cat.get(i).setImageResource((i == pos) ? R.drawable.radio_selected : R.drawable.radio_unselected);
        }

    }

    public void Change_Language(Context context, String lang) {
        //0 = English 1 = Arabic
        Locale locale = new Locale(lang.equals("1") ? "ar" : "en");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public boolean Check_GPS(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
