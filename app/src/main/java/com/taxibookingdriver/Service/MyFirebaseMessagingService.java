package com.taxibookingdriver.Service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.taxibookingdriver.Activities.Customer_ratingActivity;
import com.taxibookingdriver.Activities.FirstScreen;
import com.taxibookingdriver.Activities.MainActivity;
import com.taxibookingdriver.Activities.NotificationActivity;
import com.taxibookingdriver.Controller.Helper;
import com.taxibookingdriver.Controller.NotificationHelper;
import com.taxibookingdriver.Controller.Pref_Master;
import com.taxibookingdriver.R;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Random;


//Created by Dharvik on 6/25/2016.

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static String Noty_type;
    public static String message, ntype;
    private DialogService mBoundService;
    boolean mIsBound = false;
    Context context = this;
    public static String riderid = "";
    String detail = "";
    public static String driverid = "";
    public static String tripid = "";
    public static String uid = "";
    String CHANNEL_ID = "my_channel_01";
    GoogleApiClient mGoogleApiClient;
    Pref_Master pref_master;
    Notification.Builder builder2;
    NotificationHelper noti;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        remoteMessage.getCollapseKey();
        Log.e("get_data", ";" + remoteMessage.getData());

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        }
                    })
                    .addApi(LocationServices.API)
                    .build();
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }


        sendNotification(remoteMessage.getData());
    }

    //{body=You have one Trip Request !, ntype=r-request_trip, sound=default, title=JOY RIDE, details={"riderid":"1516450567908","driverid":"1517225402483","tripid":"t_1517473333438"}}
    private void sendNotification(Map<String, String> messageBody) {
        Log.i("Message Body", "" + messageBody);

        try {

            ntype = messageBody.get("ntype");
            message = messageBody.get("body");
            Log.e("Normal--->", message);

            detail = messageBody.get("details").toString();

            JSONObject object = new JSONObject(detail);
            riderid = object.getString("riderid");
            driverid = object.getString("driverid");
            tripid = object.getString("tripid");
            uid = object.getString("uid");


        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Helper.isAppIsInBackground(MyFirebaseMessagingService.this)) {
            Log.e("Off", "Off");

            GenerateNotification(messageBody.get("body"));

        } else {
            Log.e("On", "On");

            if (ntype.equals("r-payment-trip")) {
                GenerateNotification(messageBody.get("body"));
                doBindService();
            } else if (ntype.equals("a-status-reject")) {
                GenerateNotification(messageBody.get("body"));
            } else if (ntype.equals("a-status-suspend")) {
                GenerateNotification(messageBody.get("body"));
                doBindService();
            } else if (ntype.equals("a-status-delete")) {
                pref_master = new Pref_Master(context);
                if (driverid.equals(pref_master.getUID())) {
                    GenerateNotification(messageBody.get("body"));
                    doBindService();
                } else {
                    GenerateNotification(messageBody.get("body"));
                }
            } else if (ntype.equals("c-device-change")) {
                //GenerateNotification(messageBody.get("body"));
                pref_master = new Pref_Master(context);
                Log.e("pref_ID", pref_master.getUID());
                Log.e("driver_id", driverid);
                if (uid.equals(pref_master.getUID())) {
                    Log.e("IFF", "IFF");
                    doBindService();
                } else {
                    Log.e("ELSE", "ELSE");
                    GenerateNotification(messageBody.get("body"));
                }

            } else if (ntype.equals("a-status-active")) {
                GenerateNotification(messageBody.get("body"));
            } else if (ntype.equals("a-global-notification")) {
                GenerateNotification(messageBody.get("body"));
            } else if (ntype.equals("r-request_trip")) {
                GenerateNotification(messageBody.get("body"));
                doBindService();
            } else if (ntype.equals("r-cancel-trip")) {
                GenerateNotification(messageBody.get("body"));
                doBindService();
            }
        }


        Log.e("hellloo", "msg aayoo ");
    }


    private void GenerateNotification(String message) {
        final Random rand = new Random();
        int diceRoll = rand.nextInt(6) + 1;
        Intent noti_Intent = getNotificationInent();
        PendingIntent intent = PendingIntent.getActivity(this, diceRoll, noti_Intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            noti = new NotificationHelper(this);
            builder2 = null;
            builder2 = noti.getNotification2(message, intent);

            if (builder2 != null) {
                noti.notify(1200, builder2);
            }
        } else {
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon);

            NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.app_icon)
                    .setLargeIcon(Bitmap.createBitmap(icon))
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(intent)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setWhen(System.currentTimeMillis());

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, builder.build());

        }
    }

    public void noti_one(String message, PendingIntent intent) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(intent)
                .setChannelId(CHANNEL_ID)
                .setWhen(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.mipmap.app_icon);
        } else {
            builder.setSmallIcon(R.mipmap.app_icon);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }


    public void notification(String message, PendingIntent intent, String noti_url) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder nb = new NotificationCompat.Builder(this);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = getResources().getColor(R.color.Red);
            nb.setSmallIcon(R.mipmap.app_icon);
            nb.setColor(color);
        } else {
            nb.setSmallIcon(R.mipmap.app_icon);
        }
        nb.setContentTitle(getResources().getString(R.string.app_name));
        nb.setContentText(message);
        nb.setContentIntent(intent);
        nb.setSound(defaultSoundUri);
        nb.setAutoCancel(true);
        Bitmap bitmap_image = getBitmapFromURL(noti_url);

        bitmap_image = Bitmap.createScaledBitmap(bitmap_image, 500, 500, false);
        NotificationCompat.BigPictureStyle s = new NotificationCompat.BigPictureStyle().bigPicture(bitmap_image);
        s.setSummaryText(message);
        nb.setStyle(s);
        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(11221, nb.build());
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    Intent sendintents(Intent intent) {

        intent.putExtra("msg", message);
        return intent;
    }


    void doBindService() {
        if (!mIsBound) {
            bindService(sendintents(new Intent(this, DialogService.class)), mConnection, Context.BIND_AUTO_CREATE);
            mIsBound = true;
        }
    }


    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((DialogService.LocalBinder) service).getService();
            mBoundService.createDialogIn(1000);
            doUnbindService();
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
        }
    };

    void doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    public Intent getNotificationInent() {
        Intent noti_Intent = null;

        if (ntype.equals("r-request_trip")) {

            pref_master = new Pref_Master(context);
            pref_master.setRiderid(riderid);
            pref_master.setTripid(tripid);
            pref_master.setUID(driverid);
            noti_Intent = new Intent(this, NotificationActivity.class);

//            noti_Intent.putExtra("riderid", riderid);
//            noti_Intent.putExtra("driverid", driverid);
//            noti_Intent.putExtra("tripid", tripid);
            noti_Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        } else if (ntype.equals("a-status-active")) {

            pref_master = new Pref_Master(context);

            if (driverid.equals(pref_master.getUID())) {


                pref_master.clear_pref();
                pref_master.setAuto(1);
                pref_master.setUID("");
                pref_master.setFront_img_var("");
                pref_master.setFront_img("");
                pref_master.setBack_img_var("");
                pref_master.setBack_img("");
                noti_Intent = new Intent(this, FirstScreen.class);
                noti_Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);


            } else {

                noti_Intent = new Intent(this, Nullable.class);
                noti_Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            }

        } else if (ntype.equals("c-device-change")) {

            pref_master = new Pref_Master(context);
            pref_master.clear_pref();
            pref_master.setAuto(1);

            pref_master.setUID("");
            pref_master.setFront_img_var("");
            pref_master.setFront_img("");
            pref_master.setBack_img_var("");
            pref_master.setBack_img("");
            noti_Intent = new Intent(this, FirstScreen.class);
            noti_Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            // Logout(context);

        } else if (ntype.equals("a-global-notification")) {

            noti_Intent = new Intent(this, Nullable.class);
            noti_Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        } else if (ntype.equals("r-cancel-trip")) {

            pref_master = new Pref_Master(context);
            pref_master.setTripid("");
            pref_master.setDr_status("1");

            FirebaseDatabase.getInstance().getReference("cars").child("driverDetail").child(pref_master.getUID()).child("driverstatus").setValue("1");


            noti_Intent = new Intent(this, MainActivity.class);
            noti_Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        } else if (ntype.equals("a-status-reject")) {

            pref_master = new Pref_Master(context);

            if (driverid.equals(pref_master.getUID())) {


                pref_master.clear_pref();
                pref_master.setAuto(1);
                pref_master.setUID("");
                pref_master.setFront_img_var("");
                pref_master.setFront_img("");
                pref_master.setBack_img_var("");
                pref_master.setBack_img("");
                noti_Intent = new Intent(this, FirstScreen.class);
                noti_Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);


            } else {

                noti_Intent = new Intent(this, Nullable.class);
                noti_Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            }


        } else if (ntype.equals("a-status-delete")) {
            pref_master = new Pref_Master(context);

            if (driverid.equals(pref_master.getUID())) {


                pref_master.clear_pref();
                pref_master.setAuto(1);
                pref_master.setUID("");
                pref_master.setFront_img_var("");
                pref_master.setFront_img("");
                pref_master.setBack_img_var("");
                pref_master.setBack_img("");
                noti_Intent = new Intent(this, FirstScreen.class);
                noti_Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);


            } else {

                noti_Intent = new Intent(this, Nullable.class);
                noti_Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            }


        } else if (ntype.equals("r-payment-trip")) {
            noti_Intent = new Intent(this, Customer_ratingActivity.class);
            noti_Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        } else if (ntype.equals("a-status-suspend")) {

            pref_master = new Pref_Master(context);
            pref_master.clear_pref();
            pref_master.setAuto(1);
            pref_master.setUID("");
            pref_master.setFront_img_var("");
            pref_master.setFront_img("");
            pref_master.setBack_img_var("");
            pref_master.setBack_img("");
            noti_Intent = new Intent(this, FirstScreen.class);
            noti_Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }


        return noti_Intent;
    }


}
