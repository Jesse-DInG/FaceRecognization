package com.dxj.face_recognization.activity;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import com.dxj.face_recognization.core.Prehandler;
import com.dxj.face_recognization.model.Face;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class FrView extends SurfaceView implements SurfaceHolder.Callback{
	private final String TAG="FrView";
	public  final int CAMERA_WIDTH=240;
	public final int CAMERA_HEIGHT=320;
	//级联器
	//	private CascadeClassifier face_cascade;
	//	private CascadeClassifier Leye_cascade;
	//	private CascadeClassifier Reye_cascade;

	private Camera camera;
	private int cameraIndex;
	private Mat mYuv;
	private boolean isCreated=false;

	private SurfaceHolder mHolder;
	private Prehandler ph;
	private Handler handler;
	private FpsMeter mFps;

	public FrView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mHolder=this.getHolder();
		mHolder.addCallback(this);
		mFps = new FpsMeter();
		//		loadCascade(context);
		ph=new Prehandler();
		//		ph.loadCacade(face_cascade, Leye_cascade, Reye_cascade);
		Log.d(TAG,"FrView");
	}
	public void getHandler(Handler handler){
		this.handler=handler;
	}

	public void setcameraIndex(int camera_index){
		this.cameraIndex=camera_index;
		destroyCamera();

		openCamera(camera_index);
		initCamera();
	}


	private Camera.PreviewCallback previewcallback=new Camera.PreviewCallback() {
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			// TODO Auto-generated method stub

			long t=System.currentTimeMillis();

			mYuv.put(0, 0, data);
			Mat mRgba = new Mat();
			Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);

			Bitmap bmp = Bitmap.createBitmap(CAMERA_HEIGHT, CAMERA_WIDTH, Bitmap.Config.ARGB_8888);

			if (Utils.matToBitmap(mRgba, bmp)){
				Matrix m=new Matrix();
				if(cameraIndex==0)
					m.setRotate(90);
				else{
					m.setRotate(-90);
					m.postScale(-1, 1);
				}
				Bitmap mBit=Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
				Mat mbit=Utils.bitmapToMat(mBit);
				Mat mGray=new Mat();
				long t2=System.currentTimeMillis();
				Log.d(TAG, "catch time:"+(t2-t));
				Imgproc.cvtColor(mbit, mGray, Imgproc.COLOR_RGBA2GRAY);
				Face face=ph.detectFace_and_eyes(mGray, 1.0f);
				long t3=System.currentTimeMillis();
				Log.d(TAG, "detectFace_and_eyes time:"+(t3-t2));
				m.reset();
				m.postScale(2, 2);
				Bitmap bmshow=Bitmap.createBitmap(mBit, 0, 0, mBit.getWidth(), mBit.getHeight(), m, true);
				Canvas canvas=mHolder.lockCanvas();
				canvas.drawBitmap(bmshow, 0, 0, null);


				if(face!=null){
					Paint paint=new Paint();
					paint.setStrokeWidth(3);
					paint.setStyle(Style.STROKE);
					paint.setColor(Color.GREEN);
					canvas.drawRect(getRect(face.face_area), paint);

					paint.setColor(Color.CYAN);
					canvas.drawRect(getRect(face.left_eye), paint);
					canvas.drawRect(getRect(face.right_eye), paint); 

					if(face.enable){
						
						Mat faceimg=ph.makefaceimg(mGray, face);
						if(faceimg!=null){
							Message.obtain(handler, 0x0100, faceimg).sendToTarget();
						}
						Log.d(TAG, "prehandle time:"+(System.currentTimeMillis()-t3));
					}
				}	      
				mFps.measure();
				mFps.draw(canvas, (canvas.getWidth() - bmp.getWidth()) / 2, 0);

				mHolder.unlockCanvasAndPost(canvas);
				mGray.release();
				mbit.release();
				mBit.recycle();
			}
			mRgba.release();
			bmp.recycle();

			t=System.currentTimeMillis()-t;
			Log.d(TAG, "time:"+t+"ms");
		}
	};
	private Rect getRect(org.opencv.core.Rect r){
		return new Rect(r.x*2,r.y*2,(r.x+r.width)*2,(r.y+r.height)*2);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		//		initCamera();
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		//		openCamera(cameraIndex);
		isCreated=true;
		Log.d(TAG,"OPEN");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {

		destroyCamera();
		Log.d(TAG,"destroy");
	}
	private void openCamera(int index){
		if(camera!=null)
			camera.release();
		if(cameraIndex==1){		//前置摄像头
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
					CameraInfo info = new CameraInfo();
					Camera.getCameraInfo(i, info);
					if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
						camera = Camera.open(i);
					}
				}
			}
		}
		else{
			camera=Camera.open();
		}
		Log.d(TAG,"OPEN");
	}	
	private void initCamera(){
		try{
			Log.d(TAG,"surfaceChanged");
			if(camera!=null){
				Log.d(TAG,"Parameters");
				Camera.Parameters p=camera.getParameters();
				p.setPreviewSize(CAMERA_HEIGHT, CAMERA_WIDTH);
				camera.setParameters(p);
				camera.setPreviewCallback(previewcallback);
				camera.startPreview();
				//				Size s=camera.getParameters().getPreviewSize();
				//				Log.d(TAG,"width:"+s.width+"height:"+s.height);
				mYuv = new Mat(CAMERA_WIDTH + CAMERA_WIDTH / 2, CAMERA_HEIGHT, CvType.CV_8UC1);
				mFps.init();
			}
		}catch (Exception e) {
			Log.w(TAG, e);
		}
	}
	private void destroyCamera(){
		if(camera!=null){
			camera.setPreviewCallback(null);
			if(isCreated){
				Bitmap bm=Bitmap.createBitmap(480, 640, Bitmap.Config.ARGB_8888);
				for(int i=0;i<480;i++){
					for(int j=0;j<640;j++){
						bm.setPixel(i, j, Color.BLACK);
					}
				}
				Canvas canvas=mHolder.lockCanvas();
				canvas.drawBitmap(bm, 0, 0, null);
				mHolder.unlockCanvasAndPost(canvas);
			}
			camera.stopPreview();

			camera.release();
			camera=null;
		}

		synchronized (this) {
			if(mYuv!=null){
				mYuv.release();
				mYuv=null;		
			}
		}

		Log.d(TAG,"destroy");
	}
}
