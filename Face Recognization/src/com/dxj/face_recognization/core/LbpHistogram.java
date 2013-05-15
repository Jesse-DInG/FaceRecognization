package com.dxj.face_recognization.core;


import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class LbpHistogram {
	private final String tag="LbpHistogram";
	private int _sum=-1;
	//求特点采样点数所需的码表	
	public int[] GetMapping(int samples){
		int length=1<<samples;//码表长度

		int[] mapping=new int[length];
		int newMax=samples*(samples-1)+3;	//59
		int index=0;
		int i=0,j=0;
		for(i=0;i<length;i++){
			int tp1=i>>(samples-1)&1;
		if(tp1>0)
			j=i<<1|1;//左移一位后，给第一位置
		else
			j=i<<1;//左移一位后，给第一位置
		int tp2=i^j;//异或操作
		int sum=0;
		for(int k=0;k<samples;k++)
			sum+=tp2>>k&1;
		if(sum<=2){
			mapping[i]=index;
			index+=1;
		}
		else
			mapping[i]=newMax-1;
		}	
		return mapping;
	}
	public int getsum(Mat mat,int count,int r){
		return (mat.rows()/count-2)*(mat.cols()/count-2);
	}
	private int[] Getlbp(Mat mat,int r,int[] mapping){
		int[] h=new int[59];
		for(int i=0;i<59;i++)
			h[i]=0;
		for(int i=r;i<mat.rows()-r;i++)
		{
			for(int j=r;j<mat.cols()-r;j++)
			{
				int lv=0;
				double value=mat.get(i, j)[0];
				lv+=(mat.get(i-r,j-r)[0]>value?1:0)<<0;
				lv+=(mat.get(i-r,j)[0]>value?1:0)<<1;
				lv+=(mat.get(i-r,j+r)[0]>value?1:0)<<2;
				lv+=(mat.get(i,j-r)[0]>value?1:0)<<3;
				lv+=(mat.get(i,j+r)[0]>value?1:0)<<4;
				lv+=(mat.get(i+r,j-r)[0]>value?1:0)<<5;
				lv+=(mat.get(i+r,j)[0]>value?1:0)<<6;
				lv+=(mat.get(i+r,j+r)[0]>value?1:0)<<7;
				h[mapping[lv]]++;
			}
		}
		return h;
	}
	public int[] GetlbpList(Mat mat,int count,int r,int[] mapping){
		int[] result=new int[count*count*59];
		int width=mat.cols()/count;
		int height=mat.rows()/count;
		for(int i=0;i<count;i++){
			for(int j=0;j<count;j++){
				Rect rect=new Rect(j*width, i*height, width, height);
				Mat submat=mat.submat(rect);
				int[] h=Getlbp(submat, 1, mapping);
				System.arraycopy(h, 0, result, (i*count+j)*59, 59);
			}
		}
		return result;
	}
	public int[] getmeanlbp(int[] lbp1,int[] lbp2){
		int length=lbp1.length;
		int[] result=new int[length];
		for(int i=0;i<length;i++){
			result[i]=(int)Math.round(Math.sqrt(lbp1[i]*lbp2[i]));
		}
		return result;
	}
	private float chitest(int[] arr1,int[] arr2,int length,int matlength)
	{
		float result=0.0f;
		for(int i=0;i<length;i++)
		{
			if(arr1[i]+arr2[i]==0)
				continue;
			result+=(arr1[i]-arr2[i])*(arr1[i]-arr2[i])/(arr1[i]+arr2[i]);
		}
		return 1.0f-result/matlength;
	}
	public float[] getchitest(int[] lbp1,int[] lbp2)
	{
		float[] result=new float[16];
		for(int i=0;i<16;i++)
		{
			int[] temp1=new int[59];
			int[] temp2=new int[59];
			for(int j=0;j<59;j++){
				temp1[j]=lbp1[i*59+j];
				temp2[j]=lbp2[i*59+j];
			}

			result[i]=chitest(temp1,temp2,16,getsum(lbp2));
		}
		return result;
	}
	public int getsum(int[] mat){
		if(_sum<0){
			int sum=0;
			for(int num:mat){
				sum+=num;
			}
			_sum=sum/16;
		}
		return _sum;
	}
	public float[] getweight(float[] plist){
		int count=plist.length;
		float[] weight=new float[count];
		int sum=0;
		for(int i=0;i<count;i++){
			int w=1;
			if(plist[i]>0.9)
				w=4;
			else if(plist[i]>0.8)
				w=2;
			else if(plist[i]<0.2)
				w=0;
			weight[i]=w;
			sum+=w;
		}
		for(int i=0;i<count;i++){
			weight[i]=weight[i]/sum;
		}
		return weight;
	}
	public float getSimilarity(int[] lbp1,int[] lbp2,int[] mapping,float[] weight){
		float p=0.0f;
		try{
			float[] plist=getchitest(lbp1, lbp2);
			int length=plist.length;

			for(int i=0;i<length;i++){
				p+=plist[i]*weight[i];
			}
		}
		catch (Exception e) {
			p=0.0f;
		}
		return p;
	}

}
