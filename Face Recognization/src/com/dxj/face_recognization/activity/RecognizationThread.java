package com.dxj.face_recognization.activity;

import org.opencv.core.Mat;

import android.os.Message;
import android.util.Log;

import com.dxj.face_recognization.core.LbpHistogram;
import com.dxj.face_recognization.model.FaceResult;
import com.dxj.face_recognization.model.Faceinfo;
import com.dxj.face_recognization.model.lbpMatrix;

public class RecognizationThread extends Thread{
	private final String TAG="RecognizationThread";

	private RecognizationHandler handler;
	protected Mat faceMat;
	private int[] mapping;

	private LbpHistogram lbp;
	public RecognizationThread(RecognizationHandler handler,Mat faceMat,int[] mapping){
		this.handler=handler;
		this.faceMat=faceMat;
		this.mapping=mapping;		
		lbp=new LbpHistogram();
	}	


	@Override
	public void run() {
		handler.threadflag=true;
		long t=System.currentTimeMillis();
		if(faceMat!=null){
			if(handler.facelist!=null){
				int size=handler.facelist.size();
				float p=0.0f;
				String name="";
				int[] facelbp=lbp.GetlbpList(faceMat, 4, 1, mapping);
				for(int i=0;i<size;i++){
					Faceinfo face=handler.facelist.get(i);
					float p1=lbp.getSimilarity(facelbp, face.getLbp(), mapping, face.getWeight());
					Log.d(TAG, "name:"+face.getName()+"  p="+p1);
					if(p1>p){
						p=p1;
						name=face.getName();
					}
				}
				FaceResult fr=new FaceResult();
				fr.name=name;
				fr.probability=p;
				fr.costTime=System.currentTimeMillis()-t;
				Message.obtain(handler, 0x0200, fr).sendToTarget();
			}	
			faceMat.release();
		}

		handler.threadflag=false;
	}
}
