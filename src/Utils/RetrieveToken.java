package Utils;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class RetrieveToken extends AsyncTask<String,Void,Void> {
	
	public DataManager dm;
	public Handler handler;
	public int msg_id;
	
	public RetrieveToken(DataManager dm, Handler handler, int msg_id){
		this.dm = dm;
		this.handler = handler;
		this.msg_id = msg_id;
	}
	
    protected Void doInBackground(String... args) {
        
    	String verifier = args[0];
    	
        try {
        	Log.d("HockeyLogCat", "Verifier: " + verifier);

        	Log.d("HockeyLogCat", "Consumer Token Before: " + DataManager.mConsumer.getToken());
        	Log.d("HockeyLogCat", "Consumer Secret Before: " + DataManager.mConsumer.getTokenSecret());
        	
        	// this will populate token and token_secret in consumer
        	
        	DataManager.mProvider.retrieveAccessToken(DataManager.mConsumer, verifier);
        	
        	DataManager.oauth_session_handle = DataManager.mProvider.getResponseParameters().getFirst("oauth_session_handle");

        	//For some reason the token isn't staying persistent (Java thing?), this gets around it for now
        	DataManager.token = DataManager.mConsumer.getToken();
        	DataManager.secret = DataManager.mConsumer.getTokenSecret();
        	
        	Log.d("HockeyLogCat", "Consumer Token After: " + DataManager.mConsumer.getToken());
        	Log.d("HockeyLogCat", "Consumer Secret After: " + DataManager.mConsumer.getTokenSecret());

        } catch (Exception e) {
        	Log.d("HockeyLogCat", "Exception: " + e.getMessage());
            }
           
            return null;
        }
        
        protected Void onProgressUpdate() {
        	return null;
        }

        @Override
        protected void onPostExecute(Void v) {
        	Message msg = new Message();
        	
        	msg.what = msg_id;
        	
        	this.handler.sendMessage(msg);
        }
    }
   