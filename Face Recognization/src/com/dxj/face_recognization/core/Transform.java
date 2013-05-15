package com.dxj.face_recognization.core;

import android.util.Log;

public class Transform {
	final int ROWS=16;
	final int COLS=59;
	private final static String TAG="Transform";
	public static String Int2D2Str(int[] mat){
		Log.d(TAG, "Mat2str");
		long t=System.currentTimeMillis();
		StringBuffer ss=new StringBuffer(3000);
		int length=mat.length;
		for(int i=0;i<length;i++){
			ss.append(mat[i]);
			ss.append(",");
		}
		ss.deleteCharAt(ss.length()-1);
		t=System.currentTimeMillis()-t;
		Log.i(TAG, "Mat2str:"+t+"ms");
		return ss.toString();
	}
	public static int[] Str2Int2D(String str){
		String[] list1=str.split(",");
		int length=list1.length;
		int[] mat=new int[length];
		for(int i=0;i<length;i++){
			mat[i]= Integer.parseInt(list1[i]);
		}
		return mat;
	}
	public static String float2str(float[] weight){
		Log.d(TAG, "double2str");
		long t=System.currentTimeMillis();
		String s="";
		int length=weight.length;
		for(int i=0;i<length;i++){
			s+=weight[i];
			s+=",";
		}
		s=s.substring(0,s.length()-2);
		t=System.currentTimeMillis()-t;
		Log.i(TAG, "double2str:"+t+"ms");
		return s;
	}
	public static float[] str2float(String str){
		String[] list=str.split(",");
		int length=list.length;
		float[] result=new float[length];
		for(int i=0;i<length;i++){
			result[i]=Float.parseFloat(list[i]);
		}
		return result;
	}
}
