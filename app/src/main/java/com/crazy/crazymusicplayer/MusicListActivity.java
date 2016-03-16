package com.crazy.crazymusicplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.crazy.crazymusicplayer.Utils.MusicBean;

import java.io.File;
import java.util.ArrayList;

/**
 *  Created by scxh on 2016/1/22.
 */
public class MusicListActivity extends Activity implements AdapterView.OnItemClickListener{

    private ListView mListView;
    private Handler mHandler = new Handler();
    private ArrayList<MusicBean> mMediaLists = new ArrayList<>();
    private MusicListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list_layout_sec);

        mListView = (ListView)findViewById(R.id.music_list_view_sec);
        mListView.setOnItemClickListener(this);

        adapter = new MusicListAdapter(this);
        mListView.setAdapter(adapter);

        asyncQueryMedia();

    }

    public void asyncQueryMedia() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mMediaLists.clear();
                queryMusic(Environment.getExternalStorageDirectory() + File.separator);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setListData(mMediaLists);
                    }
                });
            }
        }).start();
    }

    /**
     * 获取目录下的歌曲
     *
     * @param dirName
     */
    public void queryMusic(String dirName) {
        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Audio.Media.DATA + " like ?",
                new String[]{dirName + "%"},
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        if (cursor == null) return;

        // id title singer data time image
        MusicBean music;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // 如果不是音乐
            String isMusic = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC));
            if (isMusic != null && isMusic.equals("")) continue;

            String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));

            if (isRepeat(title, artist)) continue;

            music = new MusicBean();
            music.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
            music.setTitle(title);
            music.setArtist(artist);
            music.setMusicPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
            music.setLength(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
            music.setImage(getAlbumImage(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))));

            mMediaLists.add(music);
        }

        cursor.close();
    }


    /**
     * 根据音乐名称和艺术家来判断是否重复包含了
     *
     * @param title
     * @param artist
     * @return
     */
    private boolean isRepeat(String title, String artist) {
        for (MusicBean music : mMediaLists) {
            if (title.equals(music.getTitle()) && artist.equals(music.getArtist())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据歌曲id获取图片
     *
     * @param albumId
     * @return
     */
    private String getAlbumImage(int albumId) {
        String result = "";
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(
                    Uri.parse("content://media/external/audio/albums/"
                            + albumId), new String[]{"album_art"}, null,
                    null, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); ) {
                result = cursor.getString(0);
                break;
            }
        } catch (Exception e) {
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return null == result ? null : result;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MusicListAdapter adapter = (MusicListAdapter) parent.getAdapter();
        adapter.setPlayingPosition(position);

        Intent intent = new Intent(this,MainActivity.class);
        intent.putParcelableArrayListExtra("MUSIC_LIST", mMediaLists);
        intent.putExtra("CURRENT_POSITION", position);

        startActivity(intent);

    }


    private class MusicListAdapter extends BaseAdapter{

        private LayoutInflater inflater;

        private ArrayList<MusicBean> list = new ArrayList<>();
        private int mPlayingPosition;

        public MusicListAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void setPlayingPosition(int position) {
            mPlayingPosition = position;
            notifyDataSetChanged();
        }

        public void setListData(ArrayList<MusicBean> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder= null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.music_list_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.icon = (ImageView)convertView.findViewById(R.id.music_list_icon);
                viewHolder.title = (TextView)convertView.findViewById(R.id.tv_music_list_title);
                viewHolder.artist = (TextView)convertView.findViewById(R.id.tv_music_list_artist);
                viewHolder.mark = convertView.findViewById(R.id.music_list_selected);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            if (mPlayingPosition == position)
                viewHolder.mark.setVisibility(View.VISIBLE);
            else
                viewHolder.mark.setVisibility(View.INVISIBLE);

            MusicBean music = (MusicBean) getItem(position);

            Bitmap icon = BitmapFactory.decodeFile(music.getImage());
            viewHolder.icon.setImageBitmap(icon == null ?
                    BitmapFactory.decodeResource(
                            getResources(), R.drawable.skin_online_default_bg) : icon);
            viewHolder.title.setText(music.getTitle());
            viewHolder.artist.setText(music.getArtist());

            return convertView;
        }

        class ViewHolder {
            ImageView icon;
            TextView title, artist;
            View mark;
        }
    }
}
