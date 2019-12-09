package com.example.fixed_assignment3;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.MainThread;

import java.util.concurrent.atomic.AtomicLong;

// BACK GROUND THREAD
class background implements Runnable {
    private int seconds;
    private boolean stop;
    boolean moved;
    private AtomicLong movetime;
    private acceleration accelerometer;
    public background (int seconds, acceleration accelerometer) {
        this.seconds = seconds;
        this.stop = false;
        this.accelerometer = accelerometer;
        this.movetime = new AtomicLong();
        this.moved = false;
    }
    @Override
    public void run() {
        for (int x = 0; x < seconds; x++) {
            try {
                if (stop != true) {
                    Thread.sleep(1000);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d("background: ", "background done waiting");
        while (stop != true) {
            try {
                if (stop != true) {
                    if ( accelerometer.getchange() == true ) {
                        moved = true;
                        this.movetime.set(System.currentTimeMillis());
                        if (MainActivity.getstatus() == false) {
                            MainActivity.update();
                        }
                        Log.d("background: ", "values changed");
                        this.stop();
                        break;
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void stop() {
        this.stop = true;
    }
    public void reset() {
        this.stop = false;
    }
    public Long getmovetime() {
        Log.d("background: ", "movetime gotten!");
        Log.d("background:", "movetime: " + (movetime.get()));
        return this.movetime.get();
    }
}