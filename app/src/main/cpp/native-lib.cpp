#include <jni.h>
#include <string>
#include <android/log.h>

//
// Created by Ravindra on 10/2/2018.
//
#define LOG_TAG "Native-lib"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,__VA_ARGS__)

bool DEBUG = false;

std::string decrypt(std::string toEncrypt, int selection) {
    char key[5];
    if(selection == 1){
        key[0] = 'X';
        key[1] = 'C';
        key[2] = 'Q';
        key[3] = 'A';
        key[4] = 'S';
    }else if(selection == 2){
        key[0] = 'm';
        key[1] = 'v';
        key[2] = 'c';
        key[3] = 'k';
        key[4] = 'm';
    }else{
        key[0] = 'X';
    }
    std::string output = toEncrypt;
    for (int i = 0; i < toEncrypt.size(); i++)
        output[i] = toEncrypt[i] ^ key[i % (sizeof(key) / sizeof(char))];

    return output;
}

std::string getOne(int a){
    if(a==1){
        return "=viqaiq5";
    }else{
        return "TS[ZN[YT]";
    }

}

std::string getTwo(int a){
    if(a==1){
        return "{b$km!ht`kz";
    }else{
        return "0%[[)E%/+TA";
    }
}

std::string getThree(int a){
    if(a==1){
        return "zcwjo";
    }else{
        return "7W\\[[O";
    }
}

std::string getFour(int a){
    if(a==1){
        return "$7h&erj:";
    }else{
        return "XB[_X[";
    }
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_zopnote_android_merchant_util_Authenticator_getKey1(JNIEnv *env, jobject instance) {
    std::string returnValue = decrypt(getOne(1)+getFour(1)+getThree(1)+getTwo(1), 1);
    return env->NewStringUTF(returnValue.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_zopnote_android_merchant_util_CryptoUtil_getKey1(JNIEnv *env, jclass type) {
    std::string returnValue = decrypt(getFour(2)+ getTwo(2)+ getOne(2)+getThree(2), 2);
    return env->NewStringUTF(returnValue.c_str());
}