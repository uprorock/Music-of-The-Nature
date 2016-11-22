package com.example.prorock.musicofnature;

import static java.lang.System.loadLibrary;

/**
 * Created by ProRock on 22.11.2016.
 */

public class OAL {
    static {
        loadLibrary("openal");
    }
    public native int alGet(int what);

    int Java_com_uprorock_openal_alGet(int what) {
        return alGet(what);
    }

}
