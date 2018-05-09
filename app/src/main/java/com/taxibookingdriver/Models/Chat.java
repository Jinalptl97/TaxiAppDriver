package com.taxibookingdriver.Models;

/**
 * Created by Admin on 2/2/2018.
 */

public class Chat {

    public String sender;
    public String receiver;
    public String message;
    public long timestamp;
    public String rpic;
    public String riderid;

    public String getRiderid() {
        return riderid;
    }

    public void setRiderid(String riderid) {
        this.riderid = riderid;
    }

    public String getRpic() {
        return rpic;
    }

    public void setRpic(String rpic) {
        this.rpic = rpic;
    }

    public Chat() {
    }

    public Chat(String sender, String receiver, String message, long timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timestamp = timestamp;
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
