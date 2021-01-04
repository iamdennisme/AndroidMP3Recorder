package com.czt.mp3recorder.sample;

import android.app.Activity;
import android.os.Bundle;

import com.czt.mp3recorder.Log;
import com.czt.mp3recorder.Mp3Recorder;
import com.czt.mp3recorder.Mp3RecorderUtil;
import java.io.File;


public class RecorderActivity extends Activity {

    private Mp3Recorder mRecorder;

    String path;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        path = new File(getFilesDir(), "test.mp3").getAbsolutePath();
        init();
    }

    private void init() {
        Mp3RecorderUtil.init(getApplicationContext(), true);
        mRecorder = new Mp3Recorder();
        mRecorder.setOutputFile(path)
                .setMaxDuration(30)//30s
                .setCallback(new Mp3Recorder.Callback() {
                    @Override
                    public void onRecording(double duration, double volume) {
                        String str = "";
                        str = String.format("duration:\n" + "%d分%d秒", (int) (duration / 1000 / 60), (int) (duration / 1000 % 60)) + "---" + duration + "\n"
                                + "分贝值:\n" + volume;
                    }

                    @Override
                    public void onStart() {
                        Log.d("RecorderActivity","开始了");

                    }

                    @Override
                    public void onPause() {
                        Log.d("RecorderActivity","暂停了");
                    }

                    @Override
                    public void onResume() {
                        Log.d("RecorderActivity","恢复了");
                    }

                    @Override
                    public void onStop(int action) {
                        Log.d("RecorderActivity","onStop");
                    }

                    @Override
                    public void onReset() {
                        Log.d("RecorderActivity","onReset");

                    }

                    @Override
                    public void onMaxDurationReached() {
                        Log.d("RecorderActivity","onMaxDurationReached");
                    }
                });
        mRecorder.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecorder.stop(Mp3Recorder.ACTION_STOP_ONLY);
    }
}
