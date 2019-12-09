package com.example.fixed_assignment3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fixed_assignment3.sensor_checker.sensor_connection;

public class MainActivity extends AppCompatActivity {
    private sensor_checker connection;
    private boolean bound;
    private static boolean paused;
    private static TextView monitor;
    private static Button reset;
    private static Button exit;
    PowerManager.WakeLock wakeLock;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.paused = false;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        final PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"mytag:");
        wakeLock.acquire();
        setContentView(R.layout.activity_main);
        monitor = (TextView) findViewById(R.id.monitor);
        monitor.setText("Hasn’t budged an inch");
        reset = (Button) findViewById(R.id.reset_button);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connection != null && bound == true) {
                    connection.stop();
                }
                if (bound = true) {
                    unbindService(accelerometer_connection);
                    bound = false;
                    connection.reset();
                }
                bindService();
                monitor.setText("Hasn’t budged an inch");
            }
        });
        exit = (Button) findViewById(R.id.exit_button);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // kills thread
                if (connection != null && bound == true) {
                    connection.stop();
                }
                if (bound = true) {
                    unbindService(accelerometer_connection);
                    bound = false;
                }
                // kills wakelock
                wakeLock.release();
                System.exit(0);
            }
        });
        bindService();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (connection != null && bound == true) {
            Log.d("RESUME: ", "CONNECTED AND BOUND");
            Intent service = new Intent(MainActivity.this,sensor_checker.class);
            bindService(service,accelerometer_connection, Context.BIND_AUTO_CREATE);
            if (connection.diditmove() == true && paused == true) {
                paused = false;
                monitor.setText("the phone has moved!");
                Log.d("textview: ", "CHANGED BY RESUME");
            } else {
                monitor.setText("Hasn’t budged an inch");
            }
        } else {
            Log.d("CONNECTION: ", "NULL");
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        this.paused = true;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        wakeLock.release();
    }
    private void start_service() {
        Intent service = new Intent(MainActivity.this,sensor_checker.class);
        startService(service);
        bindService();
        Log.d("BINDER: ", "BEGINNING BINDING!");
    }
    private void bindService () {
        Intent service = new Intent(MainActivity.this,sensor_checker.class);
        bindService(service,accelerometer_connection, Context.BIND_AUTO_CREATE);
        Log.d("BINDER: ", "BOUND SERVICE");
    }
    public static void update() {
        if (paused != true) {
            monitor.setText("The phone was moved!");
        } else {
            monitor.setText("Hasn’t budged an inch");
        }
    }
    public static boolean getstatus() {
        return paused;
    }
    private ServiceConnection accelerometer_connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            sensor_connection binder = (sensor_connection) service;
            connection = binder.getService();
            if (connection == null) {Log.d("CONNECTION: ", "NULL CREATED");} else {Log.d("CONNECTION: ", "NOT NULL");}
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };
}
