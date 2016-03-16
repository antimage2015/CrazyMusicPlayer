package com.crazy.crazymusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

/**
 *  Created by antimage on 2016/1/24.
 */
public class WelcomeActivity extends Activity {

    private int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);

        init();
    }

    private void init(){
        final ImageView mImageView = (ImageView)findViewById(R.id.welcome_img);
        final int[] images = new int[]{R.drawable.welcome, R.drawable.guides1};

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (msg.what == 0x123) {
                    mImageView.setImageResource(images[pos++]);
                }
            }
        };


        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int curr = 1000;
            @Override
            public void run() {
                handler.sendEmptyMessage(0x123);
                curr += 1000;
                if (curr >= 3000) {
                    timer.cancel();
                    Intent intent = new Intent(WelcomeActivity.this, FirstViewActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },0, 1000);
    }

}
