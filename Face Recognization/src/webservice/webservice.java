package webservice;

import java.util.LinkedList;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.dxj.face_recognization.core.Transform;
import com.dxj.face_recognization.model.Faceinfo;

import android.util.Log;

public class webservice {
	private final String TAG="dxj"+webservice.class.getSimpleName();

	private static String url;
	private static String namespace;

	private static webservice _webservice;
	public static webservice getInstance(){
		if( _webservice==null)
			_webservice=new webservice();
		return _webservice;
	}
	public static void setUrl(String url) {
		webservice.url = url;
		Log.d("dxjwebservice", "url:"+url);
	}

	public static void setNamespace(String namespace) {
		webservice.namespace = namespace;
		Log.d("dxjwebservice", "namespace:"+namespace);
	}
	//
	//	public List<String> getCarIDs(){
	//
	//		String method="GetCarIDs";
	//
	//		List<String> list=new LinkedList<String>();
	//		SoapObject soapObject=new SoapObject(namespace,method);
	//		Object result=getResult(soapObject);
	//		if(result!=null){
	//			String ans=result.toString();
	//			if(!ans.startsWith("nodata")){
	//				String[] strlist=ans.split("[|]");
	//				for(String s:strlist)list.add(s);
	//			}
	//		}
	//		return list;
	//	}
	//
	//	public CarStatusBean GetCarStatus(String carid,String time){
	//
	//		String method="GetCarStatus";
	//		SoapObject soapObject=new SoapObject(namespace,method);
	//		soapObject.addProperty("carid",carid);
	//		soapObject.addProperty("time",time);
	//		Log.d(TAG, "carid:"+carid+"  time:"+time);
	//
	//		Object result=getResult(soapObject);
	//		if(result!=null){
	//			String ans=result.toString();
	//			if(!ans.startsWith("nodata")){
	//				return CarStatusBean.getCarStatusBean(carid, result.toString());
	//			}
	//		}
	//		return null;
	//	}
	//
	//	public CarStatusBean GetCarStatusNextOrPre(String carid, String time,int nextOrPrevious){
	//		String method="GetCarStatusNextOrPre";
	//
	//		SoapObject soapObject=new SoapObject(namespace,method);
	//		soapObject.addProperty("carid",carid);
	//		soapObject.addProperty("time",time);
	//		soapObject.addProperty("nextOrPrevious",nextOrPrevious);
	//		Log.d(TAG, "carid:"+carid+"  time:"+time+"  nextOrPrevious:"+nextOrPrevious);
	//		Object result=getResult(soapObject);
	//		if(result!=null){
	//			String ans=result.toString();
	//			if(!ans.startsWith("nodata")){
	//				return CarStatusBean.getCarStatusBean(carid, result.toString());
	//			}
	//		}
	//		return null;
	//	}
	//
	//	public CarStatusBean GetTopCarStatus(String carid){
	//
	//		String method="GetTopCarStatus";
	//		SoapObject soapObject=new SoapObject(namespace,method);
	//		soapObject.addProperty("carid",carid);
	//
	//		Object result=getResult(soapObject);
	//		if(result!=null){
	//			String ans=result.toString();
	//			if(!ans.startsWith("nodata")){
	//				Log.d(TAG,method+":"+ans);
	//				return CarStatusBean.getCarStatusBean(carid, result.toString());
	//			}
	//		}
	//		return null;
	//	}
	//
	//	public List<CarGPSBean> GetCarGPS(String carid,int top){
	//		String method="GetCarGPS";
	//		List<CarGPSBean> list=new LinkedList<CarGPSBean>();
	//		SoapObject soapObject=new SoapObject(namespace,method);
	//		soapObject.addProperty("carid",carid);
	//		soapObject.addProperty("top",top);
	//
	//
	//		Object result=getResult(soapObject);
	//		if(result!=null){
	//			String ans=result.toString();
	//			Log.d("zzzz", "carid:"+carid+" top:"+top);
	//			if(!ans.startsWith("nodata")){
	//				String[] strlist=ans.split("[|]");
	//				for(String s:strlist){
	//					CarGPSBean cb=new CarGPSBean();
	//					cb.setCarGPSBean(carid, s);
	//					list.add(cb);
	//				}
	//			}
	//
	//		}
	//		Log.d(TAG, "list:size="+list.size());
	//		return list;
	//	}
	public int saveUser(Faceinfo fi){		
		String method="saveUser";
		Log.d(TAG, method);
		SoapObject soapObject=new SoapObject(namespace,method);
		soapObject.addProperty("userName",fi.getName());
		soapObject.addProperty("lbp",Transform.Int2D2Str(fi.getLbp()));
		soapObject.addProperty("weights",Transform.float2str(fi.getWeight()));
		Object result=getResult(soapObject);

		if(result!=null){
			Log.d(TAG, "saveUser:"+result.toString());
			return Integer.parseInt(result.toString());
		}
		else
			return -1;
	}
	public DetectResult getUser(String carid){
		String method="getUser";
		SoapObject soapObject=new SoapObject(namespace,method);
		soapObject.addProperty("getUser",carid);

		SoapObject result=(SoapObject)getResult(soapObject);
		if(result!=null){
			DetectResult dr=new DetectResult();
			dr.userName=result.getProperty("userName").toString();
			dr.costTime=Long.parseLong(result.getProperty("costTime").toString());
			dr.p=Float.parseFloat(result.getProperty("p").toString());
			return dr;
		}
		return null;
	}

	private Object getResult(SoapObject soapObject){

		HttpTransportSE httpTransportSE=new HttpTransportSE(url);
		SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet=false;
		envelope.setOutputSoapObject(soapObject);
		try {
			httpTransportSE.call(namespace+soapObject.getName(),envelope);
			Object result=envelope.getResponse();
			if(result!=null){
				Log.d(TAG,soapObject.getName()+":"+result.toString());
				return result;
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.w(TAG, e);
		}
		return null;
	}
}
