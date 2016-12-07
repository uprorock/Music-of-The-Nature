package com.example.prorock.musicofnature;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.renderscript.Byte2;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

//TODO: Спросить разрешения при первом запуске и проверять в каждом запуске
//TODO: Сделать внешний конф файл со структурой файловой системы
//TODO: Синхронизация должна осуществляться при первом запуске

public class MainActivity extends AppCompatActivity {

    OAL test;
    Button syncButton;
    Button playButton;
    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    int MY_PERMISSIONS_REQUEST_INTERNET = 0;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("openal");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        syncButton = (Button)findViewById(R.id.button_sync);
        playButton = (Button)findViewById(R.id.button_play);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED
                ) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    MY_PERMISSIONS_REQUEST_INTERNET);

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

        }

/*
        test = new OAL();
        String extStore = Environment.getExternalStorageDirectory().getAbsolutePath();

        String filePath = Environment.getExternalStorageDirectory().toString() + "/NatureMusic/Sounds/bird.wav";
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

        String filePath = Environment.getExternalStorageDirectory().toString() + "/EarthQuake.wav";
        int indicator = test.play(filePath);
        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
         tv.setText(test.stringFromJNI());
*/
    }

    public void onSyncButtonClicked(View v) {
        new SyncFiles(this).execute();
    }

    public void onPlayButtonClicked(View v) {
        /*
        String filePath = Environment.getExternalStorageDirectory().toString() + "/NatureMusic/Sounds/bird.wav";
        MediaPlayer mp = MediaPlayer.create(this, Uri.parse(filePath));
        mp.start();
        */
        test = new OAL();
        String filePath = Environment.getExternalStorageDirectory().toString() + "/NatureMusic/Sounds/bird_chirp.wav";
        int indicator = test.play(filePath);
    }


    private void requestPermissions() {

    }

    private void pickRandomSounds() {

    }
}
