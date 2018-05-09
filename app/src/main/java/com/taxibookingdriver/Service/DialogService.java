package com.taxibookingdriver.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class DialogService extends Service {
    Context mContext = this;
    private final IBinder mBinder = new LocalBinder();
    String msg;


    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent arg0) {
        return true;
    }

    public class LocalBinder extends Binder {
        public DialogService getService() {
            return DialogService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

    }

    public void createDialogIn(final long time) {
        Runnable myRun = new Runnable() {
            public void run() {
                try {
                    synchronized (this) {
                        wait(time);//WAIT 1 MINUTE THEN SEND NOTIFICATION
                        //THIS IS ALL JUST FOR TESTING
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.v("DialogService", "SENDING NOTIFICATION");



                SuperDialog.createDialog(SuperDialog.DIALOG_ERROR, mContext, msg);
            }
        };
        Thread T = new Thread(myRun);
        T.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null) {
            msg = intent.getStringExtra("msg");
        }


        return super.onStartCommand(intent, flags, startId);
    }
}
