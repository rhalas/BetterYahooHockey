package Utils;

import oauth.signpost.signature.HmacSha1MessageSigner;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;


public class AuthenticateUser extends AsyncTask<Void,Void,Void> {
	private Activity activity;
	
	public AuthenticateUser(Activity activity){
		this.activity = activity;
	}
	
    protected Void doInBackground(Void... args) {

        DataManager.mConsumer.setMessageSigner(new HmacSha1MessageSigner());
        DataManager.mProvider.setOAuth10a(true);  
        
        try {
            String aUrl = DataManager.mProvider.retrieveRequestToken(DataManager.mConsumer, DataManager.callbackUrl);
            //Toast.makeText(getApplicationContext(), aUrl,
              //      Toast.LENGTH_LONG).show();
            Log.d("HockeyLogCat", aUrl);

            String requestToken = DataManager.mConsumer.getToken();
            Log.d("HockeyLogCat", requestToken);
            //Toast.makeText(getApplicationContext(), requestToken,
              //      Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(aUrl)).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND);
            this.activity.startActivity(intent);
            
            Log.d("HockeyLogCat", "After startActivity call!");
            //mProvider

            //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(aUrl)));
        } catch (Exception ex) {
        	Log.d("HockeyLogCat", ex.getMessage());
        }

        return null;
    }
    
    protected Void onProgressUpdate() {
    	return null;
    }

    protected Void onPostExecute() {
        return null;
    }

}	

