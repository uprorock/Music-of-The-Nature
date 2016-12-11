package com.example.prorock.musicofnature;

import android.os.AsyncTask;

import static java.lang.System.loadLibrary;

/**
 * Created by ProRock on 22.11.2016.
 */

public class OAL extends AsyncTask<Void,Void,Void> {
    static {
        loadLibrary("openal");

    }

    String backgroundSound = null, sound1 = null, sound2 = null;
    public native String stringFromJNI();
    public native int play(String backgroundSound, String sound1, String sound2);
    public native int stop();

    OAL(String bS, String s1, String s2) {
        backgroundSound = bS;
        sound1 = s1;
        sound2 = s2;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        stop();
    }

    @Override
    protected Void doInBackground(Void... params) {
        // 3 null means STOP playing
        if (backgroundSound == null && sound1 == null && sound2 == null)
            stop();
        else
            play(backgroundSound, sound1, sound2);
        return null;
    }
}

