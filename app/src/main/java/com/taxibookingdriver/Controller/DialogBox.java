package com.taxibookingdriver.Controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;
import com.taxibookingdriver.Activities.Customer_ratingActivity;
import com.taxibookingdriver.Activities.FirstScreen;
import com.taxibookingdriver.Activities.MainActivity;
import com.taxibookingdriver.Activities.NotificationActivity;
import com.taxibookingdriver.R;

public class DialogBox extends Activity {

    public static void dialog_connection(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("No Internet Available.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();
    }


    public static void progress_dialog(final Context context) {
        LayoutInflater li = LayoutInflater.from(context);
        View vv = li.inflate(R.layout.progress_dialog_layout, null);
        final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context, R.style.cart_dialog).setView(vv).show();
        alert.setCancelable(false);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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

        alert.show();

    }

    public static void setpayment(final Context context, String message, final String riderid) {
        LayoutInflater li = LayoutInflater.from(context);
        View vv = li.inflate(R.layout.alert_notification_popup, null);
        final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog).setView(vv).show();
        alert.setCancelable(false);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView cancel = vv.findViewById(R.id.con_ok);
        TextView msglaert = vv.findViewById(R.id.msglaert);
        msglaert.setText(message);
        final Pref_Master pref = new Pref_Master(context);
        final FirebaseDatabase mFirebaseInstance;
        mFirebaseInstance = FirebaseDatabase.getInstance();


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();

                NotificationManager nManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
                nManager.cancelAll();

                mFirebaseInstance.getReference("cars").child("driverDetail").child(pref.getUID()).child("driverstatus").setValue("1");

                Intent i = new Intent(context, Customer_ratingActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("riderid", riderid);
                context.startActivity(i);
            }
        });
    }

    public static void setNoData(final Context context, String s, final Runnable runnable) {

        LayoutInflater li = LayoutInflater.from(context);
        View vv = li.inflate(R.layout.alert_notification_popup, null);
        final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog).setView(vv).show();
        alert.setCancelable(false);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView cancel = vv.findViewById(R.id.con_ok);
        TextView msglaert = vv.findViewById(R.id.msglaert);
        msglaert.setText(s);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runnable.run();
                alert.dismiss();
                NotificationManager nManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
                nManager.cancelAll();


            }
        });

    }

    public static void setAction(final Context context, String message) {
        LayoutInflater li = LayoutInflater.from(context);
        View vv = li.inflate(R.layout.alert_notification_popup, null);
        final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog).setView(vv).show();
        alert.setCancelable(false);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView cancel = vv.findViewById(R.id.con_ok);
        TextView msglaert = vv.findViewById(R.id.msglaert);
        msglaert.setText(message);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                alert.dismiss();
                NotificationManager nManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
                nManager.cancelAll();

                Pref_Master pref = new Pref_Master(context);
                Intent i = new Intent(context, FirstScreen.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                pref.clear_pref();
                pref.setAuto(1);
                pref.setUID("");
                pref.setFront_img_var("");
                pref.setFront_img("");
                pref.setBack_img_var("");
                pref.setBack_img("");
                context.startActivity(i);


            }
        });
    }

    public static void setAutoStart(final Context context, String message, final Runnable runnable) {
        LayoutInflater li = LayoutInflater.from(context);
        View vv = li.inflate(R.layout.alert_notification_popup, null);
        final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog).setView(vv).show();
        alert.setCancelable(false);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView cancel = vv.findViewById(R.id.con_ok);
        TextView msglaert = vv.findViewById(R.id.msglaert);
        final Pref_Master pref_master;
        pref_master = new Pref_Master(context);
        msglaert.setText(message);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pref_master.setAuto(1);
                alert.dismiss();
                runnable.run();


            }
        });
    }

    public static void setDriverDeleted(final Context context, String message, final String driverid) {
        LayoutInflater li = LayoutInflater.from(context);
        View vv = li.inflate(R.layout.alert_delete_popup, null);
        final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog).setView(vv).show();
        alert.setCancelable(false);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView cancel = vv.findViewById(R.id.con_ok);
        final Pref_Master pref_master = new Pref_Master(context);
        TextView msglaert = vv.findViewById(R.id.msglaert);
        msglaert.setText(message);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (driverid.equals(pref_master.getUID())) {

                    alert.dismiss();
                    NotificationManager nManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
                    nManager.cancelAll();
                    Log.e("Internal", "Internal");

                    Pref_Master pref = new Pref_Master(context);
                    Intent i = new Intent(context, FirstScreen.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    pref.clear_pref();
                    pref.setAuto(1);
                    pref.setUID("");
                    pref.setFront_img_var("");
                    pref.setFront_img("");
                    pref.setBack_img_var("");
                    pref.setBack_img("");
                    context.startActivity(i);


                }


            }
        });
    }

    public static void setActionNothing(final Context context, String message) {
        LayoutInflater li = LayoutInflater.from(context);
        View vv = li.inflate(R.layout.alert_notification_popup, null);
        final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog).setView(vv).show();
        alert.setCancelable(false);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView cancel = vv.findViewById(R.id.con_ok);
        TextView msglaert = vv.findViewById(R.id.msglaert);
        msglaert.setText(message);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                NotificationManager nManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
                nManager.cancelAll();


            }
        });
    }

    public static void setReceiveTrip(final Context context, String message, final String driverid, final String riderid, final String tripid) {
        LayoutInflater li = LayoutInflater.from(context);
        View vv = li.inflate(R.layout.alert_notification_popup, null);
        final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog).setView(vv).show();
        alert.setCancelable(false);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView cancel = vv.findViewById(R.id.con_ok);
        TextView msglaert = vv.findViewById(R.id.msglaert);
        msglaert.setText(message);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                NotificationManager nManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
                nManager.cancelAll();


                Pref_Master pref = new Pref_Master(context);
                pref.setRiderid(riderid);
                pref.setTripid(tripid);
                pref.setUID(driverid);
                Intent i = new Intent(context, NotificationActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                i.putExtra("riderid", riderid);
//                i.putExtra("driverid", driverid);
//                i.putExtra("tripid", tripid);
                context.startActivity(i);


            }
        });
    }

    public static void setDriverRejected(final Context context, String message) {
        LayoutInflater li = LayoutInflater.from(context);
        View vv = li.inflate(R.layout.alert_notification_popup, null);
        final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog).setView(vv).show();
        alert.setCancelable(false);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView cancel = vv.findViewById(R.id.con_ok);
        TextView msglaert = vv.findViewById(R.id.msglaert);
        msglaert.setText(message);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Pref_Master pref = new Pref_Master(context);
                alert.dismiss();

                NotificationManager nManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
                nManager.cancelAll();


            }
        });
    }

    public static void setLoginfromother(final Context context, String message) {
        LayoutInflater li = LayoutInflater.from(context);
        View vv = li.inflate(R.layout.alert_notification_popup, null);
        final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog).setView(vv).show();
        alert.setCancelable(false);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView cancel = vv.findViewById(R.id.con_ok);
        TextView msglaert = vv.findViewById(R.id.msglaert);
        msglaert.setText(message);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Pref_Master pref = new Pref_Master(context);
                alert.dismiss();

                NotificationManager nManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
                nManager.cancelAll();

                Intent i = new Intent(context, FirstScreen.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                pref.clear_pref();
                pref.setAuto(1);
                pref.setUID("");
                pref.setFront_img_var("");
                pref.setFront_img("");
                pref.setBack_img_var("");
                pref.setBack_img("");
                context.startActivity(i);


            }
        });
    }


    public static void setCancelTrip(final Context context, String message) {
        LayoutInflater li = LayoutInflater.from(context);
        View vv = li.inflate(R.layout.alert_notification_popup, null);
        final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog).setView(vv).show();
        alert.setCancelable(false);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView cancel = vv.findViewById(R.id.con_ok);
        TextView msglaert = vv.findViewById(R.id.msglaert);
        msglaert.setText(message);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Pref_Master pref = new Pref_Master(context);

                alert.dismiss();
                NotificationManager nManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
                nManager.cancelAll();

                FirebaseDatabase.getInstance().getReference("cars").child("driverDetail").child(pref.getUID()).child("driverstatus").setValue("1");

                pref.setDr_status("1");
                Intent i = new Intent(context, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);


            }
        });
    }

    public static void setUpdateApp(final Context context, String message) {
        LayoutInflater li = LayoutInflater.from(context);
        View vv = li.inflate(R.layout.alert_notification_popup, null);
        final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog).setView(vv).show();
        alert.setCancelable(false);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView cancel = vv.findViewById(R.id.con_ok);
        TextView msglaert = vv.findViewById(R.id.msglaert);
        msglaert.setText(message);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Pref_Master pref = new Pref_Master(context);
                alert.dismiss();

                NotificationManager nManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
                nManager.cancelAll();

                String urll = "https://play.google.com/store/apps/details?id=com.joydriver";
                Intent sendIntent = new Intent();
                sendIntent.setType("text/plain");
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, urll);
                context.startActivity(sendIntent);


            }
        });

    }

    public static void setAddFare(final Context context, String message, final Runnable runnable) {
        LayoutInflater li = LayoutInflater.from(context);
        View vv = li.inflate(R.layout.alert_notification_popup, null);
        final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog).setView(vv).show();
        alert.setCancelable(false);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView cancel = vv.findViewById(R.id.con_ok);
        TextView msglaert = vv.findViewById(R.id.msglaert);
        msglaert.setText(message);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Pref_Master pref = new Pref_Master(context);

                alert.dismiss();
                NotificationManager nManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
                nManager.cancelAll();

                runnable.run();


            }
        });
    }
}
