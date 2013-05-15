package com.dxj.face_recognization.activity;

import org.opencv.core.Mat;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public final class RecordHandler extends Handler {
	private final String TAG=RecordHandler.class.getSimpleName();


	private RecordActivity activity;
	RecordHandler(RecordActivity activity){
		this.activity=activity;
	}
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 0x0100:
			Log.d(TAG, "send face");
			long t=System.currentTimeMillis();
			if(activity!=null&&activity.flag)
				activity.getFaceMat((Mat)msg.obj);
			t=System.currentTimeMillis()-t;
			Log.d("sendmsg", "handler:"+t+"ms");
			break;
		default:
			break;
		}
	}


}
