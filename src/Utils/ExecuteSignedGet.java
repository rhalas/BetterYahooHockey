package Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;


public abstract class ExecuteSignedGet extends AsyncTask<String,Void,Void> {
	
	public String url;
	public String result;
	public Handler handler;
	public Parcelable p;
	public int msg_id;
	
	public abstract Parcelable parse_json(JSONObject jObj);
	
	public ExecuteSignedGet(Handler handler, int msg_id){
		this.handler = handler;
		this.msg_id = msg_id;
	}
	
	protected abstract String GenerateURL();
	
    @Override
    protected void onPreExecute() {
    }

    protected Void doInBackground(String... args){
		HttpResponse httpResponse;
		HttpEntity entity;
		String sUrl;
		JSONObject jObj;
		
		HttpGet request = null;
        HttpClient httpClient = new DefaultHttpClient(DataManager.mgr, DataManager.params);
        
		try {
			//Need to do this while token doesn't stay persistent in static class...
			DataManager.mConsumer.setTokenWithSecret(DataManager.token, DataManager.secret);
			sUrl = DataManager.mConsumer.sign(GenerateURL());
			request = new HttpGet(sUrl);
        	Log.d("HockeyLogCat", "Get Roster with address: " + GenerateURL());
        	Log.d("HockeyLogCat", "Get Roster with signed address: " + sUrl);
        	
			httpResponse = httpClient.execute(request);
			entity = httpResponse.getEntity();
			
			jObj = JSONParser.ConvertStringToJSON(EntityUtils.toString(entity));
			
			p = parse_json(jObj);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
			
        return null;
    }
    
    protected Void onProgressUpdate() {
    	return null;
    }

    @Override
    protected void onPostExecute(Void v) {
    	Message msg = new Message();
    	
    	Bundle b = new Bundle();
    	
    	b.putParcelable("http_request_return", (Parcelable)this.p);
    	
    	msg.what = msg_id;
    	msg.setData(b);
    	
    	this.handler.sendMessage(msg);
    }

}