package com.example.prorock.musicofnature;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

//TODO: Горизонтальная ориентация (сохранение состояния при повороте)

public class MainActivity extends AppCompatActivity {

    OAL asyncOAL;
    Button syncButton;
    Button playButton;
    TextView tv;
    String localFolderPath;
    static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    boolean musicPlaying = false;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("musicPlaying", musicPlaying);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        musicPlaying = savedInstanceState.getBoolean("musicPlaying");
        if (!musicPlaying)
            playButton.setText(R.string.button_play);
        else
            playButton.setText(R.string.button_stop);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.menu_sync:
                onSyncClicked();
                return true;
            case R.id.menu_about:
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        playButton = (Button)findViewById(R.id.button_play);
        tv = (TextView) findViewById(R.id.sample_text);
        localFolderPath = getLocalFolderPath() + File.separator + "NatureMusic";
    }

    public void onSyncClicked() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
            new SyncFiles(this, localFolderPath).execute();
        else
            Toast.makeText(getApplicationContext(), R.string.toast_permissions, Toast.LENGTH_SHORT).show();
    }

    public void onPlayButtonClicked(View v) {
        if (!musicPlaying) {
            String[] filePath = pickRandomSounds();
            if (filePath != null) {
                //int indicator = test.play(filePath2, filePath, null);
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    playButton.setText(R.string.button_stop);
                    musicPlaying = true;
                    asyncOAL = new OAL(filePath[0], filePath[1], filePath[2]);
                    asyncOAL.execute();
                } else
                    Toast.makeText(getApplicationContext(), R.string.toast_permissions, Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getApplicationContext(), R.string.toast_nofilesfound, Toast.LENGTH_SHORT).show();
        }
        else {
            new OAL(null,null,null).execute();
            playButton.setText(R.string.button_play);
            musicPlaying = false;
        }

    }


    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

    }

    private String getLocalFolderPath() {
        String sdState = android.os.Environment.getExternalStorageState(); //Получаем состояние SD карты (подключена она или нет)
        if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        else {
            return getApplicationContext().getFilesDir().toString();
        }
    }

    private String[] pickRandomSounds() {
        File background = new File(localFolderPath + File.separator + "Backgrounds");
        File sounds = new File(localFolderPath + File.separator + "Sounds");

        if (background.exists() && sounds.exists()) {
            String[] backgroundsFiles = background.list();
            String[] soundFiles = sounds.list();

            List<String> soundsList = pickNRandom(Arrays.asList(soundFiles), 2);
            List<String> backgroundsList = pickNRandom(Arrays.asList(backgroundsFiles), 1);

            String[] filePath = new String[3];
            filePath[0] = localFolderPath + File.separator + "Backgrounds" + File.separator + backgroundsList.get(0);
            filePath[1] = localFolderPath + File.separator + "Sounds" + File.separator + soundsList.get(0);
            filePath[2] = localFolderPath + File.separator + "Sounds" + File.separator + soundsList.get(1);

            return filePath;
        }
        else
            return null;
    }

    private List<String> pickNRandom(List<String> lst, int n) {
        List<String> copy = new LinkedList<String>(lst);
        Collections.shuffle(copy);
        return copy.subList(0, n);
    }
}
