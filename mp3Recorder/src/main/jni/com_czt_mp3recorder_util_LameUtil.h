/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_czt_mp3recorder_util_LameUtil */

#ifndef _Included_com_czt_mp3recorder_util_LameUtil
#define _Included_com_czt_mp3recorder_util_LameUtil
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_czt_mp3recorder_util_LameUtil
 * Method:    init
 * Signature: (IIIII)V
 */
JNIEXPORT void JNICALL Java_com_czt_mp3recorder_util_LameUtil_init
  (JNIEnv *, jclass, jint, jint, jint, jint, jint);

/*
 * Class:     com_czt_mp3recorder_util_LameUtil
 * Method:    encode
 * Signature: ([S[SI[B)I
 */
JNIEXPORT jint JNICALL Java_com_czt_mp3recorder_util_LameUtil_encode
  (JNIEnv *, jclass, jshortArray, jshortArray, jint, jbyteArray);

/*
 * Class:     com_czt_mp3recorder_util_LameUtil
 * Method:    flush
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_com_czt_mp3recorder_util_LameUtil_flush
  (JNIEnv *, jclass, jbyteArray);

JNIEXPORT jboolean JNICALL Java_com_czt_mp3recorder_util_LameUtil_wav2Mp3
(JNIEnv * , jobject , jstring , jstring , jint , jint , jint);

/*
 * Class:     com_czt_mp3recorder_util_LameUtil
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_czt_mp3recorder_util_LameUtil_close
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
