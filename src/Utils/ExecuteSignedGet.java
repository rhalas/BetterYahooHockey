package Utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;

import Utils.StringUtils;

public abstract class ExecuteSignedGet extends AsyncTask<String,Void,Void> {
	
	public String url;
	public String result;
	public Handler handler;
	public Parcelable p;
	public int msg_id;
	public boolean err = false;
	
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
		URI err_uri;
		
		HttpGet request = null;
        HttpClient httpClient = new DefaultHttpClient(DataManager.mgr, DataManager.params);
        
		try {
			//Need to do this while token doesn't stay persistent in static class, look into why this is
			DataManager.mConsumer.setTokenWithSecret(DataManager.token, DataManager.secret);
			sUrl = DataManager.mConsumer.sign(GenerateURL());
			request = new HttpGet(sUrl);
        	
			httpResponse = httpClient.execute(request);
			
			entity = httpResponse.getEntity();
			
			jObj = JSONParser.ConvertStringToJSON(EntityUtils.toString(entity));
			
			err_uri = check_for_error(httpResponse, jObj);
			if(this.err){
				//See if we can recover from the error and try the request again
				request = new HttpGet(err_uri);
				httpResponse = httpClient.execute(request);
				entity = httpResponse.getEntity();
				String uri = EntityUtils.toString(entity);
				
				DataManager.token = StringUtils.GetQueryParameter("oauth_token", uri);
				DataManager.secret = StringUtils.GetQueryParameter("oauth_token_secret", uri);
				DataManager.oauth_session_handle = StringUtils.GetQueryParameter("oauth_session_handle", uri);
				
				DataManager.mConsumer.setTokenWithSecret(DataManager.token, DataManager.secret);
				
				sUrl = DataManager.mConsumer.sign(GenerateURL());
				request = new HttpGet(sUrl);
				httpResponse = httpClient.execute(request);
				entity = httpResponse.getEntity();
				jObj = JSONParser.ConvertStringToJSON(EntityUtils.toString(entity));
			}
			
			p = parse_json(jObj);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
			
        return null;
    }
    
    //Make function more generic to check for more then just expired tokens
    private URI check_for_error(HttpResponse httpResponse, JSONObject jObj){
		URI uri = null;
		String error = "";
		
		//If our token is expired we'll get a WWW-Authenticate header
		Header[] h = httpResponse.getHeaders("WWW-Authenticate");
		if(h.length != 0){
			error = h[0].toString();
		}
		
		if(error.contains("token_expired")){
			this.err = true;
	    	List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
	    	//Static nonce for now, look into dyanmic nonce generation
	    	params.add(new BasicNameValuePair("oauth_nonce", "ef3a091928d5491624c0ac23e697124422705091"));
	    	params.add(new BasicNameValuePair("oauth_consumer_key", DataManager.YAHOO_CONSUMER_KEY));
	    	params.add(new BasicNameValuePair("oauth_signature_method", "plaintext"));
	    	params.add(new BasicNameValuePair("oauth_signature", DataManager.YAHOO_CONSUMER_SECRET + "&" + DataManager.secret));
	    	params.add(new BasicNameValuePair("oauth_version", "1.0"));
	    	params.add(new BasicNameValuePair("oauth_token", DataManager.token));
	    	params.add(new BasicNameValuePair("oauth_timestamp", String.valueOf(System.currentTimeMillis()/1000)));
	    	params.add(new BasicNameValuePair("oauth_session_handle", DataManager.oauth_session_handle));
	    	
			try {
				uri = URIUtils.createURI("https", "api.login.yahoo.com", -1, "/oauth/v2/get_token", URLEncodedUtils.format(params, "UTF-8"), null);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		
		return uri;
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