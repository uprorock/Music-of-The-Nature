package com.example.prorock.musicofnature;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import android.media.MediaPlayer;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    OAL test;
    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("openal");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        }

        test = new OAL();
        String extStore = Environment.getExternalStorageDirectory().getAbsolutePath();
/*
        String filePath = Environment.getExternalStorageDirectory().toString() + "/EarthQuake.wav";
        MediaPlayer mp = MediaPlayer.create(this, Uri.parse(filePath));
        mp.start();


        File[] externalStorage = getExternalFilesDirs(null);

        String path = externalStorage[1].getPath().toString();
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
        int indicator = test.play(filePath);
        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
         tv.setText(test.stringFromJNI());
    }
}
