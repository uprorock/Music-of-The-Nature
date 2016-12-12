package com.example.prorock.musicofnature;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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


//TODO: Прогресс диалог на время синхронизации?
//TODO: Локализация
//TODO: Добавить активити с информацией о приложении и авторе
//TODO: Горизонтальная ориентация

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        syncButton = (Button)findViewById(R.id.button_sync);
        playButton = (Button)findViewById(R.id.button_play);
        tv = (TextView) findViewById(R.id.sample_text);
        localFolderPath = getLocalFolderPath() + File.separator + "NatureMusic";
    }

    public void onSyncButtonClicked(View v) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
            new SyncFiles(this, localFolderPath).execute();
        else
            Toast.makeText(getApplicationContext(), "You need to give permissions, restart app!", Toast.LENGTH_SHORT).show();
    }

    public void onStopButtonClicked(View v) {
        //asyncOAL.cancel(true);
        new OAL(null,null,null).execute();
        tv.setText("CANCELED");
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
                    playButton.setText("STOP");
                    musicPlaying = true;
                    asyncOAL = new OAL(filePath[0], filePath[1], filePath[2]);
                    asyncOAL.execute();
                } else
                    Toast.makeText(getApplicationContext(), "You need to give permissions, restart app!", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getApplicationContext(), "There is no sound files, synchronize!", Toast.LENGTH_SHORT).show();
        }
        else {
            new OAL(null,null,null).execute();
            playButton.setText("PLAY");
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
