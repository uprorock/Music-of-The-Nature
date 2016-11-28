package com.example.prorock.musicofnature;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import android.media.MediaPlayer;

public class MainActivity extends AppCompatActivity {

    OAL test;
    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("openal");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);


            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
*/
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

/*
        String filePath = Environment.getExternalStorageDirectory().toString() + "/EarthQuake.wav";
        MediaPlayer mp = MediaPlayer.create(this, Uri.parse(filePath));
        mp.start();
*/
/*
        String path = Environment.getExternalStorageDirectory().toString()+"/Pictures";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
        }
*/
        String filePath = Environment.getExternalStorageDirectory().toString() + "/EarthQuake.wav";
        //int indicator = test.play(filePath);
        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(test.stringFromJNI());
    }
}
