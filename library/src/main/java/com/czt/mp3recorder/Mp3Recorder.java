package com.czt.mp3recorder;

import java.io.File;

/**
 * Created by hss01248 on 12/29/2015.
 */
public class Mp3Recorder {
    private int mMaxDuration;// 最长录音时间，单位：毫秒
    private String outputFilePath;
    private AudioRecorder audioRecorder = null;
    private int state = State.UNINITIALIZED;
    //private Handler mHandler;
    //private OnMaxDurationReached onMaxDurationReachedListener;
    public static final   int ACTION_RESET = 1;
    public static final    int ACTION_STOP_AND_NEXT =2;
    public static final    int ACTION_STOP_ONLY =3;
    Callback mStateListener;

    public class State {
        public static final int UNINITIALIZED = -1;
        public static final int INITIALIZED = 0;
        public static final int PREPARED = 1;
        public static final int RECORDING = 2;
        public static final int PAUSED = 3;
        public static final int STOPPED = 4;
    }

    /*private Runnable r = new Runnable() {

        @Override
        public void run() {
            if(state != State.STOPPED){
                onMaxDurationReachedListener.onMaxDurationReached();
            }
        }
    };*/

    /**
     * TODO 要考虑使用单例模式
     */
    public Mp3Recorder(){
        //mHandler = new Handler(this);
    }

    public int getmMaxDuration() {
        return mMaxDuration;
    }


    /**
     *
     * @param maxDurationInSecond 单位为秒
     */
    public Mp3Recorder setMaxDuration(int maxDurationInSecond) {
        this.mMaxDuration = maxDurationInSecond*1000;
        return this;
    }

    public Mp3Recorder setOutputFile(String path){
        this.outputFilePath = path;
        return this;
    }
    public Mp3Recorder setCallback(Callback listener){
        this.mStateListener = listener;
        return this;
    }



    public void start(){
        audioRecorder = new AudioRecorder(new File(outputFilePath),this);
        audioRecorder.setCallback(mStateListener);
        audioRecorder.setMaxDuration(mMaxDuration);
        audioRecorder.start();
        state = State.PREPARED;
        audioRecorder.startRecording();

    }


    /**
     * 只供AudioRecorder调用
     */
     void onstart(){
        if(state == State.PREPARED){
            state = State.RECORDING;
            if (mStateListener != null){
                mStateListener.onStart();
            }
        }
    }

    public void pause(){
        if (audioRecorder != null){
            audioRecorder.pauseRecord();
            state = State.PAUSED;
            if (mStateListener != null){
                mStateListener.onPause();
            }
        }

    }

    public void resume(){
        if (audioRecorder != null){
            audioRecorder.resumeRecord();
            state = State.RECORDING;
            if (mStateListener != null){
                mStateListener.onResume();
            }
        }

    }

    public void stop(int action){
        if (audioRecorder != null){
            audioRecorder.stopRecord();

            state = State.STOPPED;
            if (mStateListener != null){
                mStateListener.onStop(action);
            }
        }

    }


    public int getRecorderState(){
        return state;
    }

    public void reset(){
        if(null == audioRecorder){
           // start();
            return;
        }
        if(null != audioRecorder && state != State.STOPPED){
            stop(ACTION_RESET);
        }
        audioRecorder = null;
       // start();

        if (mStateListener != null){
            mStateListener.onReset();
        }
    }




    /*@Override
    public boolean handleMessage(Message msg) {
        return false;
    }*/




    public interface Callback {
        void onStart();
        void onPause();
        void onResume();
        void onStop(int action);
        void onReset();

        void onRecording(double duration);
        void onMaxDurationReached();
    }

}
