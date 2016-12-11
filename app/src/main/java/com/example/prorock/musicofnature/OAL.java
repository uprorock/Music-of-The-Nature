package com.example.prorock.musicofnature;

import static java.lang.System.loadLibrary;

/**
 * Created by ProRock on 22.11.2016.
 */

public class OAL {
    static {
        loadLibrary("openal");

    }
    public native String stringFromJNI();
    public native int play(String backgroundSound, String sound1, String sound2);
}
