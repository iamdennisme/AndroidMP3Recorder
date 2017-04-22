package com.czt.mp3recorder.sample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.czt.mp3recorder.AudioNoPermissionEvent;
import com.czt.mp3recorder.Mp3Recorder;
import com.czt.mp3recorder.Mp3RecorderUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

public class MainActivity extends Activity {

    @Bind(R.id.btn_start)
    Button btnStart;
    @Bind(R.id.btn_pause)
    Button btnPause;
    @Bind(R.id.btn_resume)
    Button btnResume;
    @Bind(R.id.btn_stop)
    Button btnStop;
    @Bind(R.id.btn_reset)
    Button btnReset;
    @Bind(R.id.tv_progress)
    TextView tvProgress;
    private Mp3Recorder mRecorder;

    String path;

    //private MediaPlayer player;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        //player = new MediaPlayer();
        //Button startButton = (Button) findViewById(R.id.StartButton);

        path = new File(Environment.getExternalStorageDirectory(), "test.mp3").getAbsolutePath();

        askPermission();

        //init();
		/*startButton.setOnClickListener(new OnClickListener() {
            @Override
			public void onClick(View v) {

					mRecorder.startRecording();

			}
		});*/
        //Button stopButton = (Button) findViewById(R.id.StopButton);
		/*stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mRecorder.stop(Mp3Recorder.ACTION_STOP_ONLY);
			}
		});*/
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AudioNoPermissionEvent event) {
        toast("没有权限,赶紧去设置吧");
    }

    private void askPermission() {
        new RxPermissions(this)
                .request(Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if(aBoolean){
                            init();
                        }else {
                            toast("权限被拒绝了");
                        }
                    }
                });
    }

    public void toast(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

    private void init() {
        Mp3RecorderUtil.init(getApplicationContext(), true);


        mRecorder = new Mp3Recorder();
        mRecorder.setOutputFile(path)
                .setMaxDuration(30)//30s
                .setCallback(new Mp3Recorder.Callback() {
                            @Override
                            public void onRecording(double duration) {
                                tvProgress.setText( String.format("%d分%d秒",(int)(duration/1000/60),(int)(duration/1000%60))+"---"+duration );
                            }

                            @Override
                            public void onStart() {
                                toast("开始了....");

                            }

                            @Override
                            public void onPause() {
                                toast("暂停了....");
                            }

                            @Override
                            public void onResume() {
                                toast("恢复....");
                            }

                            @Override
                            public void onStop(int action) {
                                toast("onStop....");
                                tvProgress.setText("onStop");
                            }

                            @Override
                            public void onReset() {
                                toast("onReset....");
                                tvProgress.setText("onReset");
                            }

                            @Override
                            public void onMaxDurationReached() {
                                toast("onMaxDurationReached....");
                                tvProgress.setText("onMaxDurationReached");
                            }
                        });

        //mRecorder.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecorder.stop(Mp3Recorder.ACTION_STOP_ONLY);
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.btn_start, R.id.btn_pause, R.id.btn_resume, R.id.btn_stop, R.id.btn_reset, R.id.btn_play})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                mRecorder.start();
                break;
            case R.id.btn_pause:
                mRecorder.pause();
                break;
            case R.id.btn_resume:
                mRecorder.resume();
                break;
            case R.id.btn_stop:
                mRecorder.stop(Mp3Recorder.ACTION_STOP_ONLY);
                break;
            case R.id.btn_reset:
                mRecorder.reset();
                break;
            case R.id.btn_play:
                Intent mIntent = new Intent();
                Uri uri = Uri.fromFile(new File(path));
                mIntent.setAction(android.content.Intent.ACTION_VIEW);
                mIntent.setDataAndType(uri , "audio/mp3");
                startActivity(mIntent);
                break;
        }
    }


}
