package com.taxibookingdriver.Models;

/**
 * Created by Admin on 1/29/2018.
 */

public class Model_fare {
    String fareid;
    String date;
    String time;
    String flag;
    String rate;

    public String getFareid() {
        return fareid;
    }

    public void setFareid(String fareid) {
        this.fareid = fareid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
