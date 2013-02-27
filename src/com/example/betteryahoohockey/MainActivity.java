package com.example.betteryahoohockey;

import java.util.Timer;
import java.util.TimerTask;

import Utils.AuthenticateUser;
import Utils.DataManager;
import Utils.RetrieveToken;
import YahooObjects.GoalieStats;
import YahooObjects.Roster;
import YahooObjects.Roster.RosterStats;
import YahooObjects.SkaterStats;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import android.util.Log;


import android.widget.Toast;

public class MainActivity extends Activity {
	public DataManager dm;
	public Roster current_roster;

	private Context context;
	private Intent myIntent;
	
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
	            default:
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
        
		startServiceFunc();
		
		doBindService();
		
        dm = new DataManager();
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
        }
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
					
    				for(int i = 0; i < r.players.size(); i++){
    					try {
    						row_text = "";
    						row_text += r.players.get(i).first_name + " " + r.players.get(i).last_name;
    						
    						if(r.players.get(i).positions.compareTo("G") == 0 ){
    							GoalieStats gs = (GoalieStats)r.players.get(i).stats;
    							row_text += " " + gs.wins + " " + gs.gaa + " " + gs.saves + " " + gs.save_attempts + " " + gs.save_percentage + " " + gs.shutouts;
    						}
    						else{
    							SkaterStats ss = (SkaterStats)r.players.get(i).stats;
    							row_text += " " + ss.goals + " " + ss.assists + " " + ss.plus_minus + " " + ss.power_play_points + " " + ss.shots_on_goal + " " + ss.hits;
    						}
    						
    						tr = new TableRow(context);
    						tv = new TextView(context);
    						tr.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    						tv.setText(row_text);
    						tr.addView(tv);
    						tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    						
    						
    					} catch (Exception e) {
    						e.printStackTrace();
    					}
    				}
    				

					tr = new TableRow(context);
					tv = new TextView(context);
					row_text = "";
					row_text += " " + r.stats.skater_stats.goals + " " + r.stats.skater_stats.assists + " " + r.stats.skater_stats.plus_minus + " " + r.stats.skater_stats.power_play_points + " " + r.stats.skater_stats.shots_on_goal + " " + r.stats.skater_stats.hits;
					tr.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					tv.setText(row_text);
					tr.addView(tv);
					tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					
					break;
    			case 1:
    				Roster new_roster = (Roster)msg.getData().getParcelable("roster");
    		    	RosterStats rs = current_roster.CompareRoster(new_roster);
    		    	Push_Notification(current_roster.GenerateStatChangeText(rs));
    			case 2:
    				//new User(handler, 2);
    				break;
    			default:
    				break;
    		}
		}
    };
    
    public void config_google(View view){
    	new Roster("303.l.69307.t.5", "2013-02-26", handler, 0);
    	
        Timer update_stats_timer = new Timer();
        update_stats_timer.schedule(new TimerTask() {
           @Override
           public void run() {CheckForUpdatedStats();}
        }, 60000, 60000);
        
    }

    private void CheckForUpdatedStats() {
    	new Roster("303.l.69307.t.5", "2013-02-26", handler, 1);
    }
    
    public void Push_Notification(String text){
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE); 
		
	    NotificationCompat.Builder builder =  
	            new NotificationCompat.Builder(this)
	    		.setSmallIcon(R.drawable.ic_launcher)
	            .setContentTitle("New Stat Changes")  
	            .setContentText(text)
	            .setContentIntent(pendingIntent);
	    
	    Notification notification= builder.build();
	    notificationManager.notify(0, notification);
    }
    
    public void test_click(View view){
	}
}
