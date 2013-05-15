package com.dxj.face_recognization.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.objdetect.CascadeClassifier;

import webservice.webservice;

import com.dxj.face_recognization.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FaceRecognizationActivity extends Activity {
	private final String TAG="FaceRecognizationActivity";

	public static CascadeClassifier face_cascade;
	public static CascadeClassifier Leye_cascade;
	public static CascadeClassifier Reye_cascade;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button btn_record=(Button)findViewById(R.id.btn_record);
		btn_record.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(FaceRecognizationActivity.this, RecordActivity.class);
				startActivity(intent);
			}
		});
		Button btn_recognize=(Button)findViewById(R.id.btn_recognize);
		btn_recognize.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(FaceRecognizationActivity.this, RecognizationActivity.class);
				startActivity(intent);
			}
		});
		loadCascade(this);
		webservice.setUrl(getResources().getString(R.string.url));
		webservice.setNamespace(getResources().getString(R.string.namespace));
	}

	private void loadCascade(Context context){
		//ÔØÈëÁ³²¿¼¶ÁªÆ÷
		if(face_cascade==null){
			try {
				File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
				File cascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt2.xml");
				if(!cascadeFile.exists()){
					Log.d(TAG, "copyface");
					InputStream is = context.getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
					FileOutputStream os = new FileOutputStream(cascadeFile);

					byte[] buffer = new byte[4096];
					int bytesRead;
					while ((bytesRead = is.read(buffer)) != -1) {
						os.write(buffer, 0, bytesRead);
					}
					is.close();
					os.close();
				}
				face_cascade=new CascadeClassifier(cascadeFile.getAbsolutePath());
				if (face_cascade.empty()) {
					Log.e(TAG, "Failed to load cascade classifier");
					face_cascade = null;
					cascadeFile.delete();
					cascadeDir.delete();
				} else
					Log.i(TAG, "Loaded cascade classifier from " + cascadeFile.getAbsolutePath());

			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
			}
		}
		//×óÑÛ
		if(Leye_cascade==null){
			try {

				File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
				File cascadeFile = new File(cascadeDir, "haarcascade_lefteye_2splits.xml");
				if(!cascadeFile.exists()){
					Log.d(TAG, "copylefteye");
					InputStream is = context.getResources().openRawResource(R.raw.haarcascade_lefteye_2splits);
					FileOutputStream os = new FileOutputStream(cascadeFile);

					byte[] buffer = new byte[4096];
					int bytesRead;
					while ((bytesRead = is.read(buffer)) != -1) {
						os.write(buffer, 0, bytesRead);
					}
					is.close();
					os.close();
				}
				Leye_cascade=new CascadeClassifier(cascadeFile.getAbsolutePath());
				if (Leye_cascade.empty()) {
					Log.e(TAG, "Failed to load cascade classifier");
					Leye_cascade = null;
					cascadeFile.delete();
					cascadeDir.delete();
				} else
					Log.i(TAG, "Loaded cascade classifier from " + cascadeFile.getAbsolutePath());



			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
			}
		}		
		//ÓÒÑÛ
		if(Reye_cascade==null){
			try {

				File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
				File cascadeFile = new File(cascadeDir, "haarcascade_righteye_2splits.xml");
				if(!cascadeFile.exists()){
					Log.d(TAG, "copyrighteye");
					InputStream is = context.getResources().openRawResource(R.raw.haarcascade_righteye_2splits);
					FileOutputStream os = new FileOutputStream(cascadeFile);

					byte[] buffer = new byte[4096];
					int bytesRead;
					while ((bytesRead = is.read(buffer)) != -1) {
						os.write(buffer, 0, bytesRead);
					}
					is.close();
					os.close();
				}
				Reye_cascade=new CascadeClassifier(cascadeFile.getAbsolutePath());
				if (Reye_cascade.empty()) {
					Log.e(TAG, "Failed to load cascade classifier");
					Reye_cascade = null;
					cascadeFile.delete();
					cascadeDir.delete();
				} else
					Log.i(TAG, "Loaded cascade classifier from " + cascadeFile.getAbsolutePath());



			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
			}
		}
	}
}