package com.crazy.crazymusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;


public class FirstViewActivity extends Activity implements View.OnClickListener{

    RelativeLayout  load, mv, local_songs, list_songs, my_like, lasted_songs, my_radio, new_list;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstview_act_layout_item);

        init();

    }

    private void init(){
        load = (RelativeLayout)findViewById(R.id.load);
        mv = (RelativeLayout)findViewById(R.id.mv);
        local_songs = (RelativeLayout)findViewById(R.id.local_songs);
        list_songs = (RelativeLayout)findViewById(R.id.list_songs);
        my_like = (RelativeLayout)findViewById(R.id.my_like);
        lasted_songs = (RelativeLayout)findViewById(R.id.lasted_songs);
        my_radio = (RelativeLayout)findViewById(R.id.my_radio);
        new_list = (RelativeLayout)findViewById(R.id.new_list);

        load.setOnClickListener(this);
        mv.setOnClickListener(this);
        local_songs.setOnClickListener(this);
        list_songs.setOnClickListener(this);
        my_like.setOnClickListener(this);
        lasted_songs.setOnClickListener(this);
        my_radio.setOnClickListener(this);
        new_list.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.load:
                break;
            case R.id.mv:
                break;
            case R.id.local_songs:
                intent = new Intent(this, MusicListActivity.class);
                startActivity(intent);
                break;
            case R.id.list_songs:
                break;
            case R.id.my_like:
                break;
            case R.id.lasted_songs:
                break;
            case R.id.my_radio:
                break;
            case R.id.new_list:
                break;

        }
    }
}
