package com.crazy.crazymusicplayer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.crazy.crazymusicplayer.Utils.MusicBean;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *   由 MusicListActivity 启动 MainActivity
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton bt_play, bt_pre, bt_next;
    private SeekBar seekBar;
    private MyHandler mHandler = new MyHandler(this);
    private SimpleDateFormat format = new SimpleDateFormat("mm:ss");
    private TextView currentTimeTxt, totalTimeTxt;
    private MusicService.CallBack callBack;
    private boolean mFlag = true;
    private ArrayList<MusicBean> musicBeanList = new ArrayList<>();
    private int mProgress;

    private static class MyHandler extends Handler {
        // 弱引用
        private WeakReference<MainActivity> reference;

        public MyHandler(MainActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = reference.get();
            if (activity != null) {

                int currentTime = activity.callBack.callCurrentTime();
                int totalTime = activity.callBack.callTotalDate();
                activity.seekBar.setMax(totalTime);
                activity.seekBar.setProgress(currentTime);

                String current = activity.format .format(new Date(currentTime));
                String total = activity.format.format(new Date(totalTime));

                activity.currentTimeTxt.setText(current);
                activity.totalTimeTxt.setText(total);
            }
        }
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            callBack = (MusicService.MyBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            callBack = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        getMusInfoAndStService();

        seekTime();
        forSeekBar();
    }

    private void initData(){
        seekBar = (SeekBar)findViewById(R.id.seek_bar);
        bt_play = (ImageButton)findViewById(R.id.bt_play);
        bt_pre = (ImageButton)findViewById(R.id.bt_pre);
        bt_next = (ImageButton)findViewById(R.id.bt_next);

        currentTimeTxt = (TextView)findViewById(R.id.current_time_txt);
        totalTimeTxt = (TextView)findViewById(R.id.total_time_txt);

        bt_play.setOnClickListener(this);
        bt_pre.setOnClickListener(this);
        bt_next.setOnClickListener(this);
    }

    private void getMusInfoAndStService(){
        /** 接收音乐列表资源 */
        musicBeanList = getIntent().getParcelableArrayListExtra("MUSIC_LIST");
        int currentPosition = getIntent().getIntExtra("CURRENT_POSITION", 0);

        /** 构造启动音乐播放服务的Intent，设置音乐资源 */
        Intent intent = new Intent(this, MusicService.class);
        intent.putParcelableArrayListExtra("MUSIC_LIST", musicBeanList);
        intent.putExtra("CURRENT_POSITION", currentPosition);

        startService(intent);
        bindService(intent, conn, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private class MyMainActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    private void seekTime(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mFlag) {
                    if (callBack != null) {

                        mHandler.sendMessage(Message.obtain());

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();

    }

    private void forSeekBar(){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (callBack != null)
                    mProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (callBack != null) {
                    callBack.iSeekTo(mProgress);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 播放或者暂停
            case R.id.bt_play:
                playerMusicByIBinder();
                break;
            case R.id.bt_pre:
                callBack.isPlayPre();
                break;
            case R.id.bt_next:
                callBack.isPlayNext();
                break;

        }
    }

    /**
     * 播放音乐通过Binder接口实现
     */
    public void playerMusicByIBinder() {
        boolean playerState = callBack.isPlayerMusic();
        if (playerState) {
            bt_play.setImageResource(R.drawable.btn_playback_pause);
        } else {
            bt_play.setImageResource(R.drawable.btn_playback_play);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (conn != null || callBack != null) {
            unbindService(conn);
            callBack = null;
        }
        Intent intent = new Intent(this, MusicService.class);
        stopService(intent);
        mFlag = false;
    }
}
