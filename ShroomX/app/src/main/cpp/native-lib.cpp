#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_ch_usi_inf_mc_shroomx_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
