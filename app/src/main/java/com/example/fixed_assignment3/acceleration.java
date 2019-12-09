package com.example.fixed_assignment3;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.concurrent.atomic.AtomicLong;

// SENSOR LISTENER
class acceleration implements SensorEventListener {
    private float startx;
    private float starty;
    private float endx;
    private float endy;
    boolean reseted;
    // vars
    SensorManager sensorManager;
    public acceleration () {
        this.reseted = true;
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (reseted == true) {
            this.startx = event.values[0];
            Log.d("ADebugTag", "start x: " + Float.toString(startx));
            this.starty = event.values[1];
            Log.d("ADebugTag", "start y: " + Float.toString(starty));
            this.reseted = false;
        } else {
            this.endx = event.values[0];
            //Log.d("ADebugTag", " new x: " + Float.toString(endx));
            this.endy = event.values[1];
            //Log.d("ADebugTag", " new y: " + Float.toString(endy));
        }
    }
    public boolean getchange() {
        if ( (startx != endx) || (starty != endy) ) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}