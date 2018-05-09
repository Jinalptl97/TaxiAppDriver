package com.taxibookingdriver.Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Pref_Master {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private String str_user_id = "uid";
    private String user_id = "";


    private String str_device_id = "did";
    private String device_id = "0";


    private String str_mobile = "mobile";
    private String mobile = "";

    private String str_fname = "fname";
    private String fname = "";


    private String str_lname = "lname";
    private String lname = "";

    private String str_email = "email";
    private String email = "";

    private String str_pwd = "pwd";
    private String pwd = "";

    private String str_id = "id";
    private String id = "";

    private String str_nric = "nric";
    private String nric = "";

    private String str_car_type = "car_type";
    private String car_type = "";

    private String str_car_model = "car_model";
    private String car_model = "";

    private String str_color = "color";
    private String color = "";

    private String str_reg_num = "reg_num";
    private String reg_num = "";


    private String str_front_image = "front_img";
    private String front_img = "";

    private String str_back_image = "back_img";
    private String back_img = "";


    private String str_back_image_var = "back_img_var";
    private String back_img_var = "";


    private String str_front_image_var = "front_img_var";
    private String front_img_var = "";


    private String str_status = "dr_status";
    private String dr_status = "";

    private String str_fare = "fare";
    private String fare = "";

    private String str_bucket_url = "bucket_url";
    private String bucket_url = "";

    private String str_back_value = "back_value";
    private String back_value = "";

    private String str_driverrating = "driverrating";
    private String driverrating = "";

    private String str_token = "token";
    private String token = "";

    private String str_autostart = "autostart";
    private int auto = 0;


    private String str_tripid = "tripid";
    private String tripid = "0";

    private String str_riderid = "riderid";
    private String riderid = "";

    private String str_from = "from";
    private String from = "";

    private String str_to = "to";
    private String to = "";

    private String str_startdt = "startdt";
    private String startdt = "";

    private String str_eta = "eta";
    private String eta = "";

    private String str_latitude = "latitude";
    private String latitude = "";

    private String str_longitude = "longitude";
    private String longitude = "";

    private String str_rtoken = "rtoken";
    private String rtoken = "";

    private String str_cus_name = "cus_name";
    private String cus_name = "";

    private String str_rider_pic = "rider_pic";
    private String rider_pic = "";

    private String str_language = "language";
    private String language = "";




    // 0 = English , 1 = Arabic , 3 = first
    Context context;

    public Pref_Master(Context context) {
        this.context = context;
        pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getUID() {
        return pref.getString(str_user_id, user_id);
    }

    public String getDevice_id() {
        return pref.getString(str_device_id, device_id);
    }

    public String getMobile() {
        return pref.getString(str_mobile, mobile);
    }

    public String getFname() {
        return pref.getString(str_fname, fname);
    }

    public String getLname() {
        return pref.getString(str_lname, lname);
    }

    public String getEmail() {
        return pref.getString(str_email, email);
    }

    public String getPwd() {
        return pref.getString(str_pwd, pwd);
    }

    public String getId() {
        return pref.getString(str_id, id);
    }

    public String getNric() {
        return pref.getString(str_nric, nric);
    }

    public int getAutostart() {
        return pref.getInt(str_autostart, auto);
    }

    public String getCar_type() {
        return pref.getString(str_car_type, car_type);

    }

    public String getCar_model() {
        return pref.getString(str_car_model, car_model);

    }

    public String getColor() {
        return pref.getString(str_color, color);

    }

    public String getreg_num() {
        return pref.getString(str_reg_num, reg_num);

    }

    public String getFront_img() {
        return pref.getString(str_front_image, front_img);

    }

    public String getBack_img() {
        return pref.getString(str_back_image, back_img);

    }

    public String getStr_back_image_var() {
        return pref.getString(str_back_image_var, back_img_var);

    }

    public String getStr_front_image_var() {
        return pref.getString(str_front_image_var, front_img_var);

    }

    public String getToken() {
        return pref.getString(str_token, token);
    }


    public String getDr_status() {
        return pref.getString(str_status, dr_status);
    }

    public String getFare() {
        return pref.getString(str_fare, fare);
    }

    public String getBucket_url() {
        return pref.getString(str_bucket_url, bucket_url);
    }

    public String getBack_value() {
        return pref.getString(str_back_value, back_value);
    }

    public String getDriverrating() {
        return pref.getString(str_driverrating, driverrating);
    }

    public String getTripid() {
        return pref.getString(str_tripid, tripid);
    }

    public String getRiderid() {
        return pref.getString(str_riderid, riderid);
    }

    public String getFrom() {
        return pref.getString(str_from, from);
    }

    public String getTo() {
        return pref.getString(str_to, to);
    }

    public String getLatitude() {
        return pref.getString(str_latitude, latitude);
    }

    public String getLongitude() {
        return pref.getString(str_longitude, longitude);
    }

    public String getStartdt() {
        return pref.getString(str_startdt, startdt);
    }

    public String getEta() {
        return pref.getString(str_eta, eta);
    }

    public String getRtoken() {
        return pref.getString(str_rtoken, rtoken);
    }

    public String getCus_name() {
        return pref.getString(str_cus_name, cus_name);
    }

    public String getRider_pic() {
        return pref.getString(str_rider_pic, rider_pic);
    }


    public String getLanguage() {
        return pref.getString(str_language, language);
    }


    public void setRiderid(String name) {
        editor = pref.edit();
        editor.putString(str_riderid, name);
        editor.apply();
    }

    public void setTripid(String name) {
        editor = pref.edit();
        editor.putString(str_tripid, name);
        editor.apply();
    }

    public void setFrom(String name) {
        editor = pref.edit();
        editor.putString(str_from, name);
        editor.apply();
    }

    public void setTo(String name) {
        editor = pref.edit();
        editor.putString(str_to, name);
        editor.apply();
    }

    public void setLatitude(String name) {
        editor = pref.edit();
        editor.putString(str_latitude, name);
        editor.apply();
    }

    public void setLongitude(String name) {
        editor = pref.edit();
        editor.putString(str_longitude, name);
        editor.apply();
    }

    public void setStartdt(String name) {
        editor = pref.edit();
        editor.putString(str_startdt, name);
        editor.apply();
    }

    public void setEta(String name) {
        editor = pref.edit();
        editor.putString(str_eta, name);
        editor.apply();
    }

    public void setStr_token(String name) {
        editor = pref.edit();
        editor.putString(str_rtoken, name);
        editor.apply();
    }

    public void setUID(String name) {
        editor = pref.edit();
        editor.putString(str_user_id, name);
        editor.apply();
    }

    public void setDevice_id(String name) {
        editor = pref.edit();
        editor.putString(str_device_id, name);
        editor.apply();
    }

    public void setMobile(String name) {
        editor = pref.edit();
        editor.putString(str_mobile, name);
        editor.apply();
    }

    public void setToken(String name) {
        editor = pref.edit();
        editor.putString(str_token, name);
        editor.apply();
    }

    public void setAuto(int name) {
        editor = pref.edit();
        editor.putInt(str_autostart, name);
        editor.apply();
    }

    public void setFname(String name) {
        editor = pref.edit();
        editor.putString(str_fname, name);
        editor.apply();
    }

    public void setLname(String name) {
        editor = pref.edit();
        editor.putString(str_lname, name);
        editor.apply();
    }

    public void setEmail(String name) {
        editor = pref.edit();
        editor.putString(str_email, name);
        editor.apply();
    }

    public void setPwd(String name) {
        editor = pref.edit();
        editor.putString(str_pwd, name);
        editor.apply();
    }

    public void setId(String name) {
        editor = pref.edit();
        editor.putString(str_id, name);
        editor.apply();
    }

    public void setNric(String name) {
        editor = pref.edit();
        editor.putString(str_nric, name);
        editor.apply();
    }

    public void setCar_model(String name) {
        editor = pref.edit();
        editor.putString(str_car_model, name);
        editor.apply();
    }

    public void setCar_type(String name) {
        editor = pref.edit();
        editor.putString(str_car_type, name);
        editor.apply();
    }

    public void setColor(String name) {
        editor = pref.edit();
        editor.putString(str_color, name);
        editor.apply();
    }

    public void setReg_num(String name) {
        editor = pref.edit();
        editor.putString(str_reg_num, name);
        editor.apply();
    }

    public void setFront_img(String name) {
        editor = pref.edit();
        editor.putString(str_front_image, name);
        editor.apply();
    }

    public void setBack_img(String name) {
        editor = pref.edit();
        editor.putString(str_back_image, name);
        editor.apply();
    }

    public void setBack_img_var(String name) {
        editor = pref.edit();
        editor.putString(str_back_image_var, name);
        editor.apply();
    }

    public void setFront_img_var(String name) {
        editor = pref.edit();
        editor.putString(str_front_image_var, name);
        editor.apply();
    }

    public void setDr_status(String name) {
        editor = pref.edit();
        editor.putString(str_status, name);
        editor.apply();
    }

    public void setFare(String name) {
        editor = pref.edit();
        editor.putString(str_fare, name);
        editor.apply();
    }

    public void setBucket_url(String name) {
        editor = pref.edit();
        editor.putString(str_bucket_url, name);
        editor.apply();
    }

    public void setBack_value(String name) {
        editor = pref.edit();
        editor.putString(str_back_value, name);
        editor.apply();
    }

    public void setDriverrating(String name) {
        editor = pref.edit();
        editor.putString(str_driverrating, name);
        editor.apply();
    }

    public void setCus_name(String name) {
        editor = pref.edit();
        editor.putString(str_cus_name, name);
        editor.apply();
    }

    public void setRider_pic(String name) {
        editor = pref.edit();
        editor.putString(str_rider_pic, name);
        editor.apply();
    }

    public void setLanguage(String name) {
        editor = pref.edit();
        editor.putString(str_language, name);
        editor.apply();
    }


    public void clear_pref() {
        pref.edit().clear().apply();
    }


}
