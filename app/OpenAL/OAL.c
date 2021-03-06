//
// Created by User7 on 22.11.2016.
//

#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <AL/al.h>
#include <AL/alc.h>

ALuint sourceBackground, sourceSound1, sourceSound2;
ALuint bufferBackground, bufferSound1, bufferSound2;


typedef struct {
    char  riff[4];//'RIFF'
    unsigned int riffSize;
    char  wave[4];//'WAVE'
    char  fmt[4];//'fmt '
    unsigned int fmtSize;
    unsigned short format;
    unsigned short channels;
    unsigned int samplesPerSec;
    unsigned int bytesPerSec;
    unsigned short blockAlign;
    unsigned short bitsPerSample;
    char  data[4];//'data'
    unsigned int dataSize;
}BasicWAVEHeader;

//WARNING: This Doesn't Check To See If These Pointers Are Valid
char* readWAV(char* filename,BasicWAVEHeader* header){
    char* buffer = 0;

    FILE* file = fopen(filename,"rd");

    if (!file) {
        return 0;
    }

    if (fread(header,sizeof(BasicWAVEHeader),1,file)){
        if (!(//these things *must* be valid with this basic header
                memcmp("RIFF",header->riff,4) ||
                memcmp("WAVE",header->wave,4) ||
                memcmp("fmt ",header->fmt,4)  ||
                memcmp("data",header->data,4)
        )){

            buffer = (char*)malloc(header->dataSize);
            if (buffer){
                if (fread(buffer,header->dataSize,1,file)){
                    fclose(file);
                    return buffer;
                }
                free(buffer);
            }
        }
    }
    fclose(file);
    return 0;
}

ALuint createBufferFromWave(char* data,BasicWAVEHeader header){

    ALuint buffer = 0;
    ALuint format = 0;
    switch (header.bitsPerSample){
        case 8:
            format = (header.channels == 1) ? AL_FORMAT_MONO8 : AL_FORMAT_STEREO8;
            break;
        case 16:
            format = (header.channels == 1) ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16;
            break;
        default:
            return 0;
    }

    alGenBuffers(1,&buffer);
    alBufferData(buffer,format,data,header.dataSize,header.samplesPerSec);
    return buffer;
}

int createBufferForWav(JNIEnv *env, ALuint *soundBuffer, jstring soundPath) {
    const char* fnameptr = (*env)->GetStringUTFChars(env, soundPath, NULL);
    BasicWAVEHeader header;
    char* data = readWAV(fnameptr,&header);
    if (data){
        //Now We've Got A Wave In Memory, Time To Turn It Into A Usable Buffer
        *soundBuffer = createBufferFromWave(data,header);
        if (!*soundBuffer){
            free(data);
            return -1;
        }

    } else {
        return -1;
    }
}

JNIEXPORT jint JNICALL
Java_com_example_prorock_musicofnature_OAL_play(JNIEnv *env, jobject instance, jstring backgroundSound,
                                                jstring sound1, jstring sound2) {

    // Global Variables
    ALCdevice* device = 0;
    ALCcontext* context = 0;
    const ALint context_attribs[] = { ALC_FREQUENCY, 22050, 0 };

    // Initialization
    device = alcOpenDevice(0);
    context = alcCreateContext(device, context_attribs);
    alcMakeContextCurrent(context);

    // Create audio bufferBackground

    createBufferForWav(env,&bufferBackground,backgroundSound);
    createBufferForWav(env,&bufferSound1,sound1);
    createBufferForWav(env,&bufferSound2,sound2);

    // Create sourceBackground from bufferBackground and play it
    sourceBackground = 0;
    alGenSources(1, &sourceBackground );
    alSourcei(sourceBackground, AL_LOOPING, 1);
    alSourcei(sourceBackground, AL_BUFFER, bufferBackground);

    sourceSound1 = 0;
    alGenSources(1, &sourceSound1 );
    alSourcei(sourceSound1, AL_LOOPING, 1);
    alSourcei(sourceSound1, AL_BUFFER, bufferSound1);

    sourceSound2 = 0;
    alGenSources(1, &sourceSound2 );
    alSourcei(sourceSound2, AL_LOOPING, 1);
    alSourcei(sourceSound2, AL_BUFFER, bufferSound2);

    // Play sourceBackground
    alSourcePlay(sourceBackground);
    alSourcePlay(sourceSound1);
    alSourcePlay(sourceSound2);

    return 0;
}

JNIEXPORT jint JNICALL
Java_com_example_prorock_musicofnature_OAL_stop(JNIEnv *env, jobject instance) {

    alSourcei(sourceBackground, AL_LOOPING, 0);
    alSourcei(sourceSound1, AL_LOOPING, 0);
    alSourcei(sourceSound2, AL_LOOPING, 0);

    alSourceStop(sourceBackground);
    alSourceStop(sourceSound1);
    alSourceStop(sourceSound2);

    // Release sourceBackground
    alDeleteSources(1, &sourceBackground);
    alDeleteSources(1, &sourceSound1);
    alDeleteSources(1, &sourceSound2);

    // Release audio bufferBackground
    alDeleteBuffers(1, &bufferBackground);
    alDeleteBuffers(1, &bufferSound1);
    alDeleteBuffers(1, &bufferSound2);

    return 0;
}
