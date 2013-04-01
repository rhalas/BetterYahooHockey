package com.example.betteryahoohockey;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import Utils.DataManager;
import Utils.RowSplit;
import YahooObjects.GoalieStats;
import YahooObjects.Roster;
import YahooObjects.SkaterStats;
import YahooObjects.User;
import YahooObjects.Roster.RosterStats;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MyServiceClass extends Service {
	private static final String PERFS_NAME = "BetterYahooHockeyPerfs";
        private Calendar currDate;
	public Roster current_roster;
	public User u;
	public DataManager dm;
	private Intent myIntent;
	private AlarmManager alarm;
	private PendingIntent pintent;
	private long mSeconds = 60;
	private int mCounter = 1;
	private boolean OnFirstBoot = true;

	private NotificationCompat.Builder mBuilder;
	private NotificationManager mNotificationManager;
	
    /** Keeps track of all current registered clients. */
    Messenger mClients = null;
    /** Holds last value set by a client. */
    int mValue = 0;
    
    /**
     * Command to the service to register a client, receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client where callbacks should be sent.
     */
    static final int MSG_REGISTER_CLIENT = 1;

    /**
     * Command to the service to unregister a client, ot stop receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client as previously given with MSG_REGISTER_CLIENT.
     */
    static final int MSG_UNREGISTER_CLIENT = 2;

    /**
     * Command to service to set a new value.  This can be sent to the
     * service to supply a new value, and will be sent by the service to
     * any registered clients with the new value.
     */
    static final int MSG_SET_VALUE = 3;
    
    static final int MSG_CHANGE_PERIOD = 4;
    
    static final int MSG_UPDATE_TABLE = 5;

    @Override
	public void onCreate() {
	  super.onCreate();
	  android.os.Debug.waitForDebugger();
	  Log.d("TestService","onCreate Called from service");
	}
	
	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.d("TestService","onStartCommand Called");
		if(OnFirstBoot)
		{
			try{
			SharedPreferences settings = getSharedPreferences(PERFS_NAME, 0);
			
			//Handler tmphandler = this.handler;
			
			dm = new DataManager();
			u = new User();
			
			//u.handler = handler;
	        
	        DataManager.secret = settings.getString("secret", "");
	        DataManager.token = settings.getString("token", "");
	        DataManager.oauth_session_handle = settings.getString("oauth_session_handle", "");
	        
	        //For now we'll just assume there's only one team key -- in the future add support for 
	        //multiple keys for different leagues
	        u.team_key = settings.getString("team_key", "");

	        /* Get the current date and store it */
	        currDate = Calendar.getInstance();

	        new Roster(u.team_key, "", handler, 0);
	        
			new Roster(u.team_key, "", handler, 1);
			
			this.mBuilder =	new NotificationCompat.Builder(this)
			    				.setSmallIcon(R.drawable.ic_launcher);
			
	    	this.mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	    	
			//sendRemoteTableUpdate();
			
			OnFirstBoot = false;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		myIntent = new Intent(this, MyServiceClass.class);
		pintent = PendingIntent.getService(this, 0, myIntent, 0);

		alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		// Start every 30 seconds
		Calendar cal = Calendar.getInstance();

		if(CheckForNewDay(cal) == false)
		{
			CheckForUpdatedStats();
		}

		alarm.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + (mSeconds*1000), pintent);
		
		mCounter++;
		
		if(mCounter % 60 == 0) {
			Toast.makeText(getApplicationContext(), "Service Running Iteration:" + mCounter/60, Toast.LENGTH_SHORT).show();
		}
		
		
	    return Service.START_STICKY;
	  }

	  @Override
	  public IBinder onBind(Intent intent) {
		  Log.d("TestService","onBind called from Service");
		  return mMessenger.getBinder();
	  }
	  
	    @Override
	    public void onDestroy() {
	      super.onDestroy();
	      Log.d("TestService","onDestroy called from service thread");
	      alarm.cancel(pintent);
	    }


	    /**
	     * Handler of incoming messages from clients.
	     */
	    class IncomingHandler extends Handler {
	        @Override
	        public void handleMessage(Message msg) {
	        	Log.d("TestService","handleMessage called from Service");
	        	if(msg == null)
	        	{
	        		Log.d("TestService","handleMessage called from Service, NULL PTR");
	        	}
	            switch (msg.what) {
	                case MSG_REGISTER_CLIENT:
	                    mClients = msg.replyTo;
	                    sendRemoteTableUpdate();
	                    break;
	                case MSG_UNREGISTER_CLIENT:
	                    mClients = null;
	                    break;
	                case MSG_SET_VALUE:
	                    mValue = msg.arg1;
	                        try {
	                            mClients.send(Message.obtain(null,
	                                    MSG_SET_VALUE, mValue, 0));
	                        } catch (RemoteException e) {
	                            // The client is dead.  Remove it from the list;
	                            // we are going through the list from back to front
	                            // so this is safe to do inside the loop.
	                            mClients = null;
	                        }
	                    break;
	                case MSG_CHANGE_PERIOD:
	                	Log.d("TestService","Change Period Request Received");
	                	Bundle msgData = msg.getData();
	                	mSeconds = msgData.getLong("period");
	                	Log.d("TestService","New Period: " + mSeconds);
	                	//mSeconds = msg.arg1;
	                	break;
	                	
	                default:
	                    super.handleMessage(msg);
	            }
	        }
	    }
	    
	    void sendRemoteTableUpdate() {
	    	try {
                mClients.send(Message.obtain(null,
                        MSG_UPDATE_TABLE, mValue, 0));
            } catch (RemoteException e) {
                // The client is dead.  Remove it from the list;
                // we are going through the list from back to front
                // so this is safe to do inside the loop.
                mClients = null;
            }
	    }

	    /**
	     * Target we publish for clients to send messages to IncomingHandler.
	     */
	    final Messenger mMessenger = new Messenger(new IncomingHandler());
	    
	    //Need to break this up before it gets ugly! Move into object specific handlers
	    public Handler handler = new Handler(){
	    	@Override
	    	public void handleMessage(Message msg){
	    		switch(msg.what){
	    			case 0:
    				Roster r = (Roster)msg.getData().getParcelable("http_request_return");
    				
    				current_roster = r;
    				break;
    				
	    			case 1:
	    				try{
	    				Roster new_roster = (Roster)msg.getData().getParcelable("http_request_return");
	    		    	RosterStats rs = current_roster.CompareRoster(new_roster);
	    		    	Push_Notification(current_roster.GenerateSkaterStatChangeText(rs), current_roster.GenerateGoalieStatChangeText(rs));
	    				}
	    				catch (Exception e) {
	    					e.printStackTrace();
	    					Log.d("TestService","Exception in handler: " + e.getMessage());
	    				}
	    				break;
	    			default:
	    				break;
	    		}
			}
	    };

	    public void Push_Notification(List<String> skater_changes, List<String> goalie_changes){
			Intent resultIntent = new Intent(this, MainActivity.class);
			
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			stackBuilder.addParentStack(MainActivity.class);
			stackBuilder.addNextIntent(resultIntent);
			
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			
			this.mBuilder.setContentIntent(resultPendingIntent);
			
			//sendRemoteTableUpdate();
		    
		    if(skater_changes.size() != 0){
				NotificationCompat.InboxStyle skater_inbox = new NotificationCompat.InboxStyle();
				skater_inbox.setBigContentTitle("New Skater Stat Changes");
		    	
		    	for(int i = 0; i < skater_changes.size(); i++){
		    		skater_inbox.addLine(skater_changes.get(i));
		    	}
		    	
		    	mBuilder.setStyle(skater_inbox);
		    	this.mNotificationManager.notify(0, this.mBuilder.build());
		    }
		    
		    if(goalie_changes.size() != 0){
				NotificationCompat.InboxStyle goalie_inbox = new NotificationCompat.InboxStyle();
				goalie_inbox.setBigContentTitle("New Goalie Stat Changes");
		    	
		    	for(int i = 0; i < goalie_changes.size(); i++){
		    		goalie_inbox.addLine(goalie_changes.get(i));
		    	}
		    	
		    	mBuilder.setStyle(goalie_inbox);
		    	this.mNotificationManager.notify(1, this.mBuilder.build());
		    }
	    }

	    private void CheckForUpdatedStats() {
	    	new Roster(u.team_key, "", handler, 1);
	    }
	    
	    private boolean CheckForNewDay(Calendar cal) {
	    	if(cal.get(Calendar.DAY_OF_WEEK) != currDate.get(Calendar.DAY_OF_WEEK))
	    	{
	    		/* New day */
	    		currDate = cal;
	    		/* Handle Date Change, Grab a new roster and use that as our baseline */
	    		new Roster(u.team_key, "", handler, 0);
	    		return true;
	    	}
	    	return false;
	    }

}
