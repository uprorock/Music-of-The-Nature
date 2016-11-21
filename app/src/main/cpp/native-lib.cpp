#include <jni.h>
#include <string>

extern "C"
jstring
Java_com_example_prorock_musicofnature_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C+______";
    return env->NewStringUTF(hello.c_str());
}
