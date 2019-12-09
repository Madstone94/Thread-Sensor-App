package com.example.fixed_assignment3;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicLong;

public class sensor_checker extends Service {
    private static final String TAG = "sensor checker";
    private final IBinder connection = new sensor_connection();
    private static SensorManager sensormanager;
    private static Sensor acceleration;
    private static acceleration listener;
    private background background;
    private boolean stop;
    public void onCreate(){
        super.onCreate();
        this.stop = false;
        sensormanager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        listener = new acceleration();
        if (sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            acceleration = sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        sensormanager.registerListener(listener,acceleration,sensormanager.SENSOR_DELAY_FASTEST);
        this.start();
    }
    public sensor_checker() {

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        sensormanager.unregisterListener(listener);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return connection;
    }

    public class sensor_connection extends Binder {
        sensor_checker getService() {
            if (sensor_checker.this == null) {Log.d("BINDER: ", "NULL BINDER");}
            return sensor_checker.this;
        }
    }

    public void start() {
        this.background = new background(15, listener);
        new Thread(background).start();
    }
    public void stop() {
        this.background.stop();
        this.stopSelf();
    }
    public void reset() {
        this.background.reset();
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        this.stopSelf();
    }
    public boolean diditmove() {
        AtomicLong current_time = new AtomicLong();
        current_time.set(System.currentTimeMillis());
        if (stop != true) {
            if ( (this.background.moved == true ) && (this.background.getmovetime() != 0) && (current_time.get() - this.background.getmovetime() > 15000) )
            {
                Log.d("TIME:", "MOVE TIME: " + (this.background.getmovetime()));
                Log.d("TIME:", "MOVE TIME: " + (current_time.get()));
                Log.d("DIDITMOVE: ", "PHONE MOVED");
                return true;
            }
            current_time.set(System.currentTimeMillis());
        }
        Log.d("DIDITMOVE: ", "PHONE STATIC");
        return false;
    }
}
