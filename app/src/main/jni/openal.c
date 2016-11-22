#include <jni.h>
#include <alBuffer.h>

JNIEXPORT jint JNICALL
Java_com_example_prorock_musicofnature_OAL_alGet(JNIEnv *env, jobject instance, jint what) {
    static ALenum LoadData(ALbuffer *ALBuf, ALuint freq, ALenum NewFormat, ALsizei frames, enum UserFmtChannels chans, enum UserFmtType type, const ALvoid *data, ALboolean storesrc);
    // TODO

}