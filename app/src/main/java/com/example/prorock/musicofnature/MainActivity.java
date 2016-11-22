package com.example.prorock.musicofnature;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    OAL test;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("openal");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        test = new OAL();
        String extStore = Environment.getExternalStorageDirectory().getAbsolutePath();

        String sdpath,sd1path,usbdiskpath,sd0path;
        sdpath = sd1path = usbdiskpath = sd0path = null;

        if(new File("/storage/extSdCard/").exists())
        {
            sdpath="/storage/extSdCard/";
            Log.i("Sd Cardext Path",sdpath);
        }
        if(new File("/storage/sdcard1/").exists()) // exists
        {
            sd1path="/storage/sdcard1/";
            Log.i("Sd Card1 Path",sd1path);
        }
        if(new File("/storage/usbcard1/").exists())
        {
            usbdiskpath="/storage/usbcard1/";
            Log.i("USB Path",usbdiskpath);
        }
        if(new File("/storage/sdcard0/").exists()) // exists
        {
            sd0path="/storage/sdcard0/";
            Log.i("Sd Card0 Path",sd0path);
        }

        int indicator = test.play("/mnt/sdcard1/EarthQuake.wav");
        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(test.stringFromJNI());
    }
}
