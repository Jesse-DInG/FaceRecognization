package com.dxj.face_recognization.activity;


import org.opencv.core.Mat;

import webservice.webservice;

import com.dxj.face_recognization.R;
import com.dxj.face_recognization.core.LbpHistogram;
import com.dxj.face_recognization.model.Faceinfo;
import com.dxj.face_recognization.model.lbpMatrix;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;

public class RecordActivity extends CustomedActivity{
	private final String TAG=RecordActivity.class.getSimpleName();

	private RecordHandler handler;
	public boolean flag=true;
	private int mat_index;
	private Mat[] matlist;
	private LbpHistogram lbp;
	private int[] mapping;

	private Button btn_save;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.record_act);

		frview=(FrView)findViewById(R.id.frv_record);

		btn_save=(Button)findViewById(R.id.btn_save);
		btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				flag=true;
			}
		});

		lbp=new LbpHistogram();
		mapping=lbp.GetMapping(8);
		matlist=new Mat[2];
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		initdata();
		super.onResume();
		frview.setcameraIndex(camera_index);
		handler=new RecordHandler(this);
		frview.getHandler(handler);
	}
	private void initdata(){
		if(handler!=null)
			handler.removeMessages(0x0100);
		for(int i=0;i<2;i++){
			try{
				synchronized (matlist[i]) {
					if(matlist[i]!=null){
						matlist[i].release();
						matlist[i]=null;
					}
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		mat_index=0;
		flag=true;
		btn_save.setVisibility(View.INVISIBLE);
	}
	@Override
	protected void changeCamera() {
		// TODO Auto-generated method stub


		this.onResume();
	}
	public void getFaceMat(Mat mat){
		Log.d(TAG, "getFaceMat");

		if(mat_index<2){
			long t=System.currentTimeMillis();
			flag=false;
			matlist[mat_index]=mat;
			btn_save.setVisibility(View.VISIBLE);
			mat_index++;
			t=System.currentTimeMillis()-t;
			Log.d("testtime", "getFaceMat:"+t+"ms");
		}
		if(mat_index>1){

			flag=false;

			saveFace();
		}
	}
	private void saveFace(){
		final Builder builder=new Builder(this);

		builder.setTitle("保存设置");
		TableLayout settingsForm=(TableLayout)getLayoutInflater().inflate(R.layout.settings, null);
		builder.setView(settingsForm);
		final EditText edt_name=(EditText)settingsForm.findViewById(R.id.edt_name);

		builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				long t=System.currentTimeMillis();
				//				Log.d(TAG, "mat1:"+(new Transform()).Mat2Str(matlist[0]));
				//				Log.d(TAG, "mat1:"+(new Transform()).Mat2Str(matlist[1]));
				int[] lbp1=lbp.GetlbpList(matlist[0], 4, 1, mapping);
				long t1=System.currentTimeMillis();
				Log.d(TAG, "lbp1:"+(t1-t)+"ms");
				int[] lbp2=lbp.GetlbpList(matlist[1], 4, 1, mapping);
				long t2=System.currentTimeMillis();
				Log.d(TAG, "lbp2:"+(t2-t1)+"ms");
				int[] meanlbp=lbp.getmeanlbp(lbp1, lbp2);
				long t3=System.currentTimeMillis();
				Log.d(TAG, "meanlbp:"+(t3-t2)+"ms");
				float[] chiq = lbp.getchitest(lbp1, lbp2);
				long t4=System.currentTimeMillis();
				Log.d(TAG, "chi:"+(t4-t3)+"ms");
				float[] weight=lbp.getweight(chiq);
				long t5=System.currentTimeMillis();
				Log.d(TAG, "w:"+(t5-t4)+"ms");
				
				long t6=System.currentTimeMillis();
				Log.d(TAG, "test time:"+(t6-t)+"ms");
				String name=edt_name.getText().toString();
				Faceinfo fi=new Faceinfo();
				fi.setName(name);
				
				fi.setLbp(meanlbp);
				fi.setWeight(weight);
				Log.d(TAG, "beginsql");
				webservice ws=new webservice();
				ws.saveUser(fi);
//				Log.d("zzzz", ws.getUser("dxj"));
				t=System.currentTimeMillis()-t;
				Log.d(TAG, "save time:"+t+"ms");
				Log.d(TAG, "endsql");
				RecordActivity.this.finish();
				Log.d(TAG, "finish");
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				initdata();
				handler=new RecordHandler(RecordActivity.this);
			}
		});
		builder.create().show();
	}

}
