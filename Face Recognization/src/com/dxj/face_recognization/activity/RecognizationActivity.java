package com.dxj.face_recognization.activity;

import java.text.DecimalFormat;

import webservice.DetectResult;

import com.dxj.face_recognization.R;
import com.dxj.face_recognization.model.FaceResult;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RecognizationActivity extends CustomedActivity{
	private TextView txt_result;
	public boolean flag;
	private RecognizationHandler handler;
	private Button btn_ok;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recognization);
		frview=(FrView)findViewById(R.id.frv_rec);

//		result_layout=(RelativeLayout)findViewById(R.id.result_layout);
		txt_result=(TextView)findViewById(R.id.txt_result);
		btn_ok=(Button)findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				RecognizationActivity.this.finish();
			}
		});
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		handler=new RecognizationHandler(this);
		frview.getHandler(handler);
		super.onResume();
		frview.setcameraIndex(camera_index);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		handler.sendEmptyMessage(0x0900);
		super.onPause();
	}
	public void getTestResult(DetectResult result){
//		if(result.probability>0){
		if(result==null)return;
			DecimalFormat df=new DecimalFormat("0.00");
		txt_result.setText(
				"检测结果:\n\t姓名:"+result.userName+"\n\t相似度:"	+df.format(result.p*100)+
				"%\n\t检测时间:"+result.costTime+"ms");
//			}
	}
	@Override
	protected void changeCamera() {
		// TODO Auto-generated method stub
		frview.setcameraIndex(camera_index);
	}
}
