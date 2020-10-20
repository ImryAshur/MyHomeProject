package com.example.project;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;


public class MyService extends Service {

    public static final String BROADCAST = "com.example.project.NEW_LOCATION_DETECTED";

    public static final String ACTION_START_SERVICE = "ACTION_START_SERVICE";
    public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";

    private boolean isServiceRunningRightNow;
    private MyEventCallback myEventCallback = new MyEventCallback() {
        @Override
        public void createEvent(MyEvent myEvent) {
            if (myEvent != null){
                sendEvent(myEvent);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        String action = intent.getAction();
        if (action == null) {
            return Service.START_STICKY;
        }

        if (action.equals(ACTION_START_SERVICE)) {

            if (!isServiceRunningRightNow) {
                isServiceRunningRightNow = true;

            }

        } else if (action.equals(ACTION_STOP_SERVICE)) {
            stopSelf();
        }


        return Service.START_STICKY;
    }


    public void sendEvent(MyEvent myEvent) {
        String mEvent = new Gson().toJson(myEvent);
        Intent intent = new Intent(BROADCAST);
        intent.putExtra("EXTRA_LOCATION", mEvent);
        LocalBroadcastManager.getInstance(MyService.this).sendBroadcast(intent);
        // sendBroadcast(intent); = global send broadcast
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
