package com.dxj.face_recognization.core;

import java.util.LinkedList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

//import android.util.Log;

import com.dxj.face_recognization.activity.FaceRecognizationActivity;
import com.dxj.face_recognization.model.Face;

public class Prehandler {
	public Face detectFace_and_eyes(Mat img, float scale){
		Face face=new Face();
		face.scale=scale;
		if (FaceRecognizationActivity.face_cascade!=null) {

			int faceSize = Math.round(img.cols()*0.6f);
			List<Rect> faces = new LinkedList<Rect>();

			FaceRecognizationActivity.face_cascade.detectMultiScale(img, faces, 1.1, 2, 0 
					, new Size(faceSize, faceSize));
			if(faces.size()>0){
				Rect r=faces.get(0);
				face.face_area=new Rect(Math.round(r.x*face.scale),Math.round(r.y*face.scale),Math.round(r.width*face.scale),Math.round(r.height*face.scale));
				Rect left_eye=detect_eye(img, new Rect(r.x+r.width/2,r.y,r.width/2,r.height/2), FaceRecognizationActivity.Leye_cascade, "left_eye");
				if(left_eye!=null)
					face.left_eye=new Rect(Math.round((r.x+r.width/2+left_eye.x)*face.scale),Math.round((r.y+left_eye.y)*face.scale),Math.round(left_eye.width*face.scale),Math.round(left_eye.height*face.scale));

				Rect right_eye=detect_eye(img, new Rect(r.x,r.y,r.width/2,r.height/2), FaceRecognizationActivity.Reye_cascade, "right_eye");
				if(right_eye!=null)
					face.right_eye=new Rect(Math.round((r.x+right_eye.x)*face.scale),Math.round((r.y+right_eye.y)*face.scale),Math.round(right_eye.width*face.scale),Math.round(right_eye.height*face.scale));
				if(left_eye!=null&&right_eye!=null){
					if(Math.abs(face.left_eye.width-face.right_eye.width)<Math.max(face.left_eye.width,face.right_eye.width)/10)
						face.enable=true;
				}
			}
		}
		return face;
	}
	private Rect detect_eye(Mat img,Rect eyeRect,CascadeClassifier cascade,String str)
	{
		Rect rect=null;
		Mat eyeMat=img.submat(eyeRect);

		List<Rect> eyes=new LinkedList<Rect>();

		if( cascade!=null )
		{
			cascade.detectMultiScale( eyeMat, eyes, 
					1.1, 2, 0/*CV_HAAR_DO_CANNY_PRUNING*/,
					new Size(eyeRect.width/3, eyeRect.width/6) );
			if(eyes.size()>0)
			{
				rect= eyes.get(0);
			}
		}
		eyeMat.release();
		return rect;
	}
	public Mat makefaceimg(Mat img,Face face){
		long t=System.currentTimeMillis();
		Point lp=new Point(Math.round((face.left_eye.x+face.left_eye.width/2)/face.scale),Math.round((face.left_eye.y+face.left_eye.height/2)/face.scale));
		Point rp=new Point(Math.round((face.right_eye.x+face.right_eye.width/2)/face.scale),Math.round((face.right_eye.y+face.right_eye.height/2)/face.scale));
		double factor=80.0/Math.sqrt((double)Math.pow((float)(lp.x-rp.x),2)+Math.pow((float)(lp.y-rp.y),2));
		double angle=Math.atan((double)(rp.y-lp.y)/(rp.x-lp.x));
		if(Math.abs(angle)<Math.PI/18)
		{
			// 将旋转中心移至图像中间
			Point center=new Point(Math.round((face.face_area.x+face.face_area.width*0.5f)/face.scale),Math.round((face.face_area.y+face.face_area.height*0.5f)/face.scale));

			Core.circle(img, center, 2, new Scalar(0, 255, 0, 255),2);
			Core.circle(img, lp, 2, new Scalar(0, 255, 0, 255),2);
			Core.circle(img, rp, 2, new Scalar(0, 255, 0, 255),2);
			Mat dst=new Mat();
			Size msize=new Size(center.x*2, center.y*2);
			Mat rot_matrix=Imgproc.getRotationMatrix2D(center, angle/Math.PI*180, factor);
			Imgproc.warpAffine(img, dst, rot_matrix, msize);
			Mat dst2=new Mat(160,160,CvType.CV_8UC1);
			try{
				if(center.x<80){
					int rstart=(int)Math.round(80-center.y);
					int cstart=(int)Math.round(80-center.x);
					dst.copyTo(dst2.submat(rstart, rstart+dst.rows(), cstart, cstart+dst.cols()));
				}
				else{
					int rstart=(int)Math.round(center.y-80);
					int cstart=(int)Math.round(center.x-80);
					dst.submat(rstart, rstart+160, cstart, cstart+160).copyTo(dst2);
				}
			}catch (Exception e) {
				return null;
			}

			dst.release();
			t=System.currentTimeMillis()-t;
			Mat dst3=new Mat(160,160,CvType.CV_8UC1);
			Imgproc.medianBlur(dst2, dst3, 3);
			return dst3;
		}
		return null;
	}
}
