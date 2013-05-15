package com.dxj.face_recognization.activity;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public abstract class CustomedActivity extends Activity{
	protected SharedPreferences preferences;
	protected SharedPreferences.Editor editor;
	protected int camera_index;
	protected FrView frview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		preferences=getSharedPreferences("Face Recognization", MODE_PRIVATE);
		editor=preferences.edit();
		camera_index=preferences.getInt("camera_index", 1);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0,0,Menu.NONE, "ÇÐ»»ÉãÏñÍ·");
		menu.add(0, 1, Menu.NONE, "ÍË³ö");
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case 0:
			//ÇÐ»»ÉãÏñÍ·
			camera_index=1-camera_index;
			editor.putInt("camera_index", camera_index);
			editor.commit();
			changeCamera();
			break;
		case 1:
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	protected abstract void changeCamera();
}
