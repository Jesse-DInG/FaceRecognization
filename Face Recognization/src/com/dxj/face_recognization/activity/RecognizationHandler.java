package com.dxj.face_recognization.activity;

import java.util.List;

import org.opencv.core.Mat;

import webservice.DetectResult;
import webservice.webservice;

import com.dxj.face_recognization.core.LbpHistogram;
import com.dxj.face_recognization.model.FaceResult;
import com.dxj.face_recognization.model.Faceinfo;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public final class RecognizationHandler extends Handler implements Runnable{
	private final String TAG=RecognizationHandler.class.getSimpleName();

	private RecognizationActivity activity;
	protected boolean threadflag;
	protected List<Faceinfo> facelist;

	private LbpHistogram lbp;
	private int[] mapping;

	RecognizationHandler(RecognizationActivity activity){
		this.activity=activity;
		lbp=new LbpHistogram();
		mapping=lbp.GetMapping(8);
//		(new Thread(this)).start();
		threadflag=false;
	}
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 0x0100:
//			Log.d(TAG, "send face");	
			Mat mat=(Mat)msg.obj;
			if(threadflag)
				return;
//			(new RecognizationThread(this,mat,mapping)).start();
			(new Thread(this)).start();
			break;
		case 0x0200:
//			Log.d(TAG, "getanswer");
			if(activity!=null)
				activity.getTestResult((DetectResult)msg.obj);
			break;
		case 0x0900:
			Log.d(TAG, "thread stop");
			threadflag=false;
			break;
		default:
			break;
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Log.d(TAG, "run");
		webservice ws=new webservice();
		activity.getTestResult(ws.getUser("dxj"));
//		SQLcontrol sc=new SQLcontrol(activity);
//		facelist=sc.getDataList();
//		sc.close();
	}
}
