#include "lame-3.100/lame.h"
#include "com_czt_mp3recorder_util_LameUtil.h"
#include <stdio.h>
#include <jni.h>
#include <android/log.h>
#include <cstdlib>
#include <cstring>
#define LOG_TAG "lame_utils.cpp"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

static lame_global_flags *lame = NULL;

JNIEXPORT void JNICALL Java_com_czt_mp3recorder_util_LameUtil_init(
	JNIEnv *env, jclass cls, jint inSamplerate, jint inChannel, jint outSamplerate, jint outBitrate, jint quality) {
			if (lame != NULL) {
        		lame_close(lame);
        		lame = NULL;
        	}
        	lame = lame_init();
        	lame_set_in_samplerate(lame, inSamplerate);
        	lame_set_num_channels(lame, inChannel);//输入流的声道
        	lame_set_out_samplerate(lame, outSamplerate);
        	lame_set_brate(lame, outBitrate);
        	lame_set_quality(lame, quality);
        	lame_init_params(lame);
}


JNIEXPORT jint JNICALL Java_com_czt_mp3recorder_util_LameUtil_encode(
		JNIEnv *env, jclass cls, jshortArray buffer_l, jshortArray buffer_r,
		jint samples, jbyteArray mp3buf) {
	jshort* j_buffer_l = env->GetShortArrayElements(buffer_l, NULL);

	jshort* j_buffer_r = env->GetShortArrayElements(buffer_r, NULL);

	const jsize mp3buf_size = env->GetArrayLength( mp3buf);

	jbyte* j_mp3buf = env->GetByteArrayElements(mp3buf, NULL);

    unsigned char* j_mp3buf_cpp = reinterpret_cast<unsigned char*>(j_mp3buf);

	int result = lame_encode_buffer(lame, j_buffer_l, j_buffer_r,samples, j_mp3buf_cpp, mp3buf_size);

	env->ReleaseShortArrayElements( buffer_l, j_buffer_l, 0);
	env->ReleaseShortArrayElements( buffer_r, j_buffer_r, 0);
	env->ReleaseByteArrayElements( mp3buf, j_mp3buf, 0);

	return result;
}

JNIEXPORT jint JNICALL Java_com_czt_mp3recorder_util_LameUtil_flush(
		JNIEnv *env, jclass cls, jbyteArray mp3buf) {
	const jsize mp3buf_size = env->GetArrayLength(mp3buf);

	jbyte* j_mp3buf = env->GetByteArrayElements( mp3buf, NULL);

    unsigned char* j_mp3buf_cpp = reinterpret_cast<unsigned char*>(j_mp3buf);

	int result = lame_encode_flush(lame, j_mp3buf_cpp, mp3buf_size);

	env->ReleaseByteArrayElements(mp3buf, j_mp3buf, 0);

	return result;
}

/**
 * jstring to char
 */
char* Jstring2CStr(JNIEnv* env, jstring jstr) {
    char* rtn = NULL;
    jclass clsstring = env->FindClass( "java/lang/String"); //String
    jstring strencode = env->NewStringUTF("GB2312"); // 得到一个java字符串 "GB2312"
    jmethodID mid = env->GetMethodID( clsstring, "getBytes",
            "(Ljava/lang/String;)[B"); //[ String.getBytes("gb2312");
    jbyteArray barr = (jbyteArray)env->CallObjectMethod( jstr, mid,
            strencode); // String .getByte("GB2312");
    jsize alen = env->GetArrayLength( barr); // byte数组的长度
    jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char*) malloc(alen + 1); //"\0"
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0); //
    return rtn;
}


JNIEXPORT jboolean JNICALL Java_com_czt_mp3recorder_util_LameUtil_wav2Mp3
(JNIEnv * env, jobject obj, jstring inputPath, jstring outputPath, jint inSamplerate, jint inChannel, jint outBitrate) {
    //初始化lame
    lame_t lame =  lame_init();
    lame_set_in_samplerate(lame , inSamplerate);
    lame_set_out_samplerate(lame, inSamplerate);
    lame_set_num_channels(lame,inChannel);
    lame_set_mode(lame, MONO);
    lame_set_VBR(lame, vbr_default);
    lame_init_params(lame);

    // jsstring to char
    char* input =Jstring2CStr(env,inputPath) ;
    char* output=Jstring2CStr(env,outputPath);
    // 建立数据流
    FILE* inputFile = fopen(input,"rb");
    // 去除wav的head，to pcm
    fseek(inputFile, 4*1024, SEEK_CUR);
    FILE* outputFile = fopen(output,"wb+");
    short int buffer[8192*inChannel];
    unsigned char mp3_buffer[8192];
    int read ; int write;
    int total=0;
    do{
        read = fread(buffer,sizeof(short int)*inChannel, 8192,inputFile);
        total +=  read* sizeof(short int)*inChannel;
        if(read!=0){
            // pcm 编码mp3
            write = lame_encode_buffer(lame, buffer, NULL, read, mp3_buffer, 8192);
        }else{
            // 读完了
            write = lame_encode_flush(lame,mp3_buffer,8192);
        }
        // 写入mp3
        fwrite(mp3_buffer,1,write,outputFile);
    }while(read!=0);

    lame_close(lame);
    fclose(inputFile);
    fclose(outputFile);

    return JNI_TRUE;
}

JNIEXPORT void JNICALL Java_com_czt_mp3recorder_util_LameUtil_close
(JNIEnv *env, jclass cls) {
	lame_close(lame);
	lame = NULL;
}

inline int fromJByte(jbyte pixel) {
    // cast to unsigned char re-interprets values as 0-255
    // cast to int will make intermediate calculations safer
    return static_cast<int>(static_cast<unsigned char>(pixel));
}
