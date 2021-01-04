package com.czt.mp3recorder.sample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

/**
 * @author taicheng
 * <p>
 * Created on 12/31/20.
 */
public class MainActivity extends Activity {
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getExternalCacheDir().mkdirs();
//        String in = getExternalCacheDir() + "/1.wav";
//        String out = getExternalCacheDir() + "/1.mp3";
//        MediaExtractor extractor = new MediaExtractor();
//        try {
//            extractor.setDataSource(in);
//            MediaFormat format = extractor.getTrackFormat(0);
//            int sampleRate = format.getInteger(KEY_SAMPLE_RATE);
//            int channel = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
//            LameUtil.wav2Mp3(in, out, sampleRate, channel, 32);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        startActivity(new Intent(this, RecorderActivity.class));
    }
}
