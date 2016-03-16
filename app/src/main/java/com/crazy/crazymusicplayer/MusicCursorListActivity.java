package com.crazy.crazymusicplayer;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;


/**
 * MediaStore.Audio.Media.EXTERNAL_CONTENT_URI 对应字段
 * 歌曲ID：MediaStore.Audio.Media._ID 
 * 歌曲的名称：MediaStore.Audio.Media.TITLE
 * 歌曲的专辑名：MediaStore.Audio.Media.ALBUM 
 * 歌曲的歌手名：MediaStore.Audio.Media.ARTIST
 * 歌曲文件的路径：MediaStore.Audio.Media.DATA 
 * 歌曲的总播放时长：MediaStore.Audio.Media.DURATION
 * 歌曲文件的大小：MediaStore.Audio.Media.SIZE
 * 
 */

public class MusicCursorListActivity extends Activity {
	private ListView mListView;
	private FileCursorAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_music_list_layout);
		mListView = (ListView) findViewById(R.id.music_list_view);

		/**
		 * Android自身具有维护媒体库的功能
		 * 1.系统创建了一个SQLITE数据库存放所有音乐资源
		 * 2.MediaScaner类负责扫描系统文件，添加音乐资源到数据库 。
		 * 什么时间执行扫描操作：1.启动手机,2.插入拔出Sdcard时,3.接收到扫描广播时
		 */
		Cursor cursor = getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.TITLE_KEY);

		mAdapter = new FileCursorAdapter(this, cursor);
		mListView.setAdapter(mAdapter);
	}

	public class FileCursorAdapter extends CursorAdapter {
		private LayoutInflater layoutInflater;

		public FileCursorAdapter(Context context, Cursor c) {
			super(context, c, FLAG_AUTO_REQUERY);
			layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View view, Context arg1, Cursor cursor) {
			TextView titleTxt = (TextView) view.findViewById(R.id.music_title_txt);
			TextView articsTxt = (TextView) view.findViewById(R.id.music_artics_txt);

			String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
			titleTxt.setText(title);
			articsTxt.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
			return layoutInflater.inflate(R.layout.item_music_cursor_layout,null);
		}

	}

}
