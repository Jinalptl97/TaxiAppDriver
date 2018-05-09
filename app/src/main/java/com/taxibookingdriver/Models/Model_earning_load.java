package com.taxibookingdriver.Models;

/**
 * Created by Admin on 2/12/2018.
 */

public class Model_earning_load {

    String tripday;
    String tripdate;
    String stype;
    String dtransid;
    String amt;
    String tripid;
    String triptime;
    String firstday;
    String lastday;
    public String type;
    String riderid;

    public String getRiderid() {
        return riderid;
    }

    public void setRiderid(String riderid) {
        this.riderid = riderid;
    }

    public Model_earning_load(String type) {
        this.type = type;
    }

    public String getFirstday() {
        return firstday;
    }

    public void setFirstday(String firstday) {
        this.firstday = firstday;
    }

    public String getLastday() {
        return lastday;
    }

    public void setLastday(String lastday) {
        this.lastday = lastday;
    }

    public String getTripid() {
        return tripid;
    }

    public void setTripid(String tripid) {
        this.tripid = tripid;
    }

    public String getTriptime() {
        return triptime;
    }

    public void setTriptime(String triptime) {
        this.triptime = triptime;
    }

    public String getTripday() {
        return tripday;
    }

    public void setTripday(String tripday) {
        this.tripday = tripday;
    }

    public String getTripdate() {
        return tripdate;
    }

    public void setTripdate(String tripdate) {
        this.tripdate = tripdate;
    }

    public String getStype() {
        return stype;
    }

    public void setStype(String stype) {
        this.stype = stype;
    }

    public String getDtransid() {
        return dtransid;
    }

    public void setDtransid(String dtransid) {
        this.dtransid = dtransid;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }
}
