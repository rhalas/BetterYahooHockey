package com.example.betteryahoohockey;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import Utils.AuthenticateUser;
import Utils.DataManager;
import Utils.RetrieveToken;
import Utils.RowSplit;
import YahooObjects.GoalieStats;
import YahooObjects.Roster;
import YahooObjects.Roster.RosterStats;
import YahooObjects.SkaterStats;
import YahooObjects.User;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import android.net.Uri;
import android.os.Message;
import android.util.Log;


import java.util.Calendar;

import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;


import android.content.ServiceConnection;
import android.graphics.Color;
import android.util.Log;


import android.widget.Toast;

public class MainActivity extends Activity {
	public DataManager dm;
	public Roster current_roster;
	public User u;
	
	public boolean isLoggedIn = false;
	
	private Context context;
	private Intent myIntent;
	
	private static final String PERFS_NAME = "BetterYahooHockeyPerfs";
	
	/** Messenger for communicating with service. */
	Messenger mService = null;
	/** Flag indicating whether we have called bind on the service. */
	boolean mIsBound;
	/** Some text view we are using to show state information. */
	//TextView mCallbackText;
	private long nSeconds = 1;
	
	public void startServiceFunc() {
		Log.d("TestService","startServiceFunc Called");
		
		try{
			myIntent = new Intent(this, MyServiceClass.class);
			startService(myIntent);
		}
		catch(Exception e)
		{
			Log.d("TestService","startService Failed: " + e.toString());
			
		}
	 }
	
	public void killServiceFunc(View view) {
		Log.d("TestService","killServiceFunc Called");
		doUnbindService();
		stopService(myIntent);
	 }
	
	public void activityBindService(View view) {
		Log.d("TestService","activityBindService Called");
		doBindService();
	 }
	
	public void sendMsgA(View view) {
		Log.d("TestService","sendMsgA Called");
		
		Message msg = Message.obtain(null, MyServiceClass.MSG_CHANGE_PERIOD);
		Bundle msgBundle = new Bundle();
		msgBundle.putLong("period", nSeconds);
		msg.setData(msgBundle);
		Log.d("TestService","Setting New Period From App: " + nSeconds);
    	
		try
		{
			mService.send(msg);
		}
		catch (Exception e)
		{
			Log.d("TestService","Exception in sending: " + e.toString());
		}
		
		nSeconds++;
		if(nSeconds == 6) 
		{
			nSeconds = 1;
		}
	 }

	/**
	 * Handler of incoming messages from service.
	 */
	class IncomingHandler extends Handler {
	    @Override
	    public void handleMessage(Message msg) {
	    	Log.d("TestService","handleMessage called from App");
			    switch (msg.what) {
			    case MyServiceClass.MSG_SET_VALUE:
	                //mCallbackText.setText("Received from service: " + msg.arg1);
	                break;
			    case MyServiceClass.MSG_UPDATE_TABLE:
			    	Log.d("TestService","handleMessage Updating Roster Table");
			    	new Roster(u.team_key, "", handler, 0);
	                break;
	            default:
	            	Log.d("TestService","handleMessage called from App, Unhandled Case: " + msg.what);
	                super.handleMessage(msg);
	        }
	    }
	}

	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger mMessenger = new Messenger(new IncomingHandler());

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className,
	            IBinder service) {
	    	Log.d("TestService","onServiceConnected called from App");
			// This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  We are communicating with our
	        // service through an IDL interface, so get a client-side
	        // representation of that from the raw service object.
	        mService = new Messenger(service);

	        // We want to monitor the service for as long as we are
	        // connected to it.
	        try {
	            Message msg = Message.obtain(null,
	            		MyServiceClass.MSG_REGISTER_CLIENT);
	            msg.replyTo = mMessenger;
	            mService.send(msg);

	            // Give it some value as an example.
	            msg = Message.obtain(null,
	            		MyServiceClass.MSG_SET_VALUE, this.hashCode(), 0);
	            mService.send(msg);
	        } catch (RemoteException e) {
	            // In this case the service has crashed before we could even
	            // do anything with it; we can count on soon being
	            // disconnected (and then reconnected if it can be restarted)
	            // so there is no need to do anything here.
	        }

	        // As part of the sample, tell the user what happened.
	        Toast.makeText(context, "onServiceConnected",
	                Toast.LENGTH_SHORT).show();
	    }

	    public void onServiceDisconnected(ComponentName className) {
	    	Log.d("TestService","onServiceDisconnected called from App");
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        mService = null;

	        // As part of the sample, tell the user what happened.
	        Toast.makeText(context, "onServiceDisconnected",
	                Toast.LENGTH_SHORT).show();
	    }
	};

	void doBindService() {
	    // Establish a connection with the service.  We use an explicit
	    // class name because there is no reason to be able to let other
	    // applications replace our component.
		Log.d("TestService","doBindService called from App");
		bindService(new Intent(context, 
	            MyServiceClass.class), mConnection, Context.BIND_AUTO_CREATE);
	    mIsBound = true;
	}

	void doUnbindService() {
		Log.d("TestService","doUnbindService called from App");
		if (mIsBound) {
	        // If we have received the service, and hence registered with
	        // it, then now is the time to unregister.
	        if (mService != null) {
	            try {
	                Message msg = Message.obtain(null,
	                		MyServiceClass.MSG_UNREGISTER_CLIENT);
	                msg.replyTo = mMessenger;
	                mService.send(msg);
	            } catch (RemoteException e) {
	                // There is nothing special we need to do if the service
	                // has crashed.
	            }
	        }

	        // Detach our existing connection.
	        unbindService(mConnection);
	        mIsBound = false;
	    }
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        
        SharedPreferences settings = getSharedPreferences(PERFS_NAME, 0);

        dm = new DataManager();
        u = new User();
        
        DataManager.secret = settings.getString("secret", "");
        DataManager.token = settings.getString("token", "");
        
        //For now we'll just assume there's only one team key -- in the future add support for 
        //multiple keys for different leagues
        u.team_key = settings.getString("team_key", "");
        
        if(DataManager.secret.compareTo("") != 0 && DataManager.token.compareTo("") != 0){
        	Toast.makeText(context, "Logged in", Toast.LENGTH_SHORT).show();
        	isLoggedIn = true;
        }
     }
    
    @Override
    protected void onStop(){
    	super.onStop();
    	
    	saveSettings();
    }
     
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("HockeyLogCat", "onNewIntent called");
        Uri uri = intent.getData();
        if (uri != null && uri.toString().startsWith(DataManager.callbackUrl)) {
            String verifier = uri.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);
            new RetrieveToken(dm, handler, 2).execute(verifier, null, null);
            isLoggedIn = true;
        }
        saveSettings();
        startServiceFunc();		
		doBindService();
    }
    
    public void config_yahoo(View view){
    	Log.d("HockeyLogCat", "config_yahoo called");
    	new AuthenticateUser(this).execute(null, null, null);
    }

    //Need to break this up before it gets ugly! Move into object specific handlers
    public Handler handler = new Handler(){
    	@Override
    	public void handleMessage(Message msg){
    		switch(msg.what){
    			case 0:
    				Roster r = (Roster)msg.getData().getParcelable("http_request_return");
    				
    				current_roster = r;
    				
    				TableLayout tl = (TableLayout)findViewById(R.id.info_table_layout);

    				tl.removeAllViewsInLayout();
    				
    				String row_text = "";

					Context context = getApplicationContext();
					
					TableRow tr;
					TextView tv;
					
					LinkedList pList = new LinkedList();
					LinkedList gList = new LinkedList();
					
					row_text = " ";
					row_text += "," + "G" + "," + "A" + "," + "+/-" + "," + "PPP" + "," + "SOG" + "," + "H";
					
					pList.addLast(row_text);
					
					row_text = " ";
					row_text += "," + "W" + "," + "GAA" + "," + "SV" + "," + "SA" + "," + "SV%" + "," + "SO";
					gList.addLast(row_text);
					
					
    				for(int i = 0; i < r.players.size(); i++){
    					try {
    						row_text = "";
    						row_text += r.players.get(i).first_name + " " + r.players.get(i).last_name;
    						
    						if(r.players.get(i).positions.compareTo("G") == 0 ){
    							GoalieStats gs = (GoalieStats)r.players.get(i).stats;
    							row_text += "," + gs.wins + "," + gs.gaa + "," + gs.saves + "," + gs.save_attempts + "," + gs.save_percentage + "," + gs.shutouts;
    							gList.addLast(row_text);
    						}
    						else{
    							SkaterStats ss = (SkaterStats)r.players.get(i).stats;
    							row_text += "," + ss.goals + "," + ss.assists + "," + ss.plus_minus + "," + ss.power_play_points + "," + ss.shots_on_goal + "," + ss.hits;
    							pList.addLast(row_text);
    						}
    						
    					} catch (Exception e) {
    						e.printStackTrace();
    					}
    				}
    				
    				row_text = "Total";
					row_text += "," + r.stats.skater_stats.goals + "," + r.stats.skater_stats.assists + "," + r.stats.skater_stats.plus_minus + "," + r.stats.skater_stats.power_play_points + "," + r.stats.skater_stats.shots_on_goal + "," + r.stats.skater_stats.hits;
					pList.addLast(row_text);
					
					row_text = "Total";
					row_text += "," + r.stats.goalie_stats.wins + "," + r.stats.goalie_stats.gaa + "," + r.stats.goalie_stats.saves + "," + r.stats.goalie_stats.save_attempts + "," + r.stats.goalie_stats.save_percentage + "," + r.stats.goalie_stats.shutouts;
					gList.addLast(row_text);
					
					Iterator i = pList.iterator();
					while(i.hasNext())
					{
						RowSplit.splitText((String)i.next(), context, tl);
					}
					
					i = gList.iterator();
					while(i.hasNext())
					{
						RowSplit.splitText((String)i.next(), context, tl);
					}
					
					break;
    			case 1:
    				new Roster(u.team_key, "", handler, 0);
    				break;
    			case 2:
    				u = new User(handler, 3);
    				break;
    			default:
    				break;
    		}
		}
    };
    
    public void config_google(View view){
    	if(isLoggedIn){
    		startServiceFunc();		
    		doBindService();
    	}
    	else{
	        Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show();
    	}
    	
    }
    
    private void saveSettings()
    {
    	SharedPreferences settings = getSharedPreferences(PERFS_NAME, 0);
    	SharedPreferences.Editor editor = settings.edit();
    	editor.putString("secret", DataManager.secret);
    	editor.putString("token", DataManager.token);
    	editor.putString("team_key", u.team_key);
    	editor.commit();
    }

    
    public void test_click(View view){
	}
}
