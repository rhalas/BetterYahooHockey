package com.example.betteryahoohockey;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MyServiceClass extends Service {

	private Intent myIntent;
	private AlarmManager alarm;
	private PendingIntent pintent;
	private long mSeconds = 5;
	private int mCounter = 1;
	
	
    /** Keeps track of all current registered clients. */
    ArrayList<Messenger> mClients = new ArrayList<Messenger>();
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
    
    
    @Override
	public void onCreate() {
	  super.onCreate();
	  Log.d("TestService","onCreate Called from service");
	}
	
	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("TestService","onStartCommand Called");
		
		myIntent = new Intent(this, MyServiceClass.class);
		pintent = PendingIntent.getService(this, 0, myIntent, 0);

		alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		// Start every 30 seconds
		Calendar cal = Calendar.getInstance();
		
		
		alarm.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + (mSeconds*1000), pintent);
		
		Toast.makeText(getApplicationContext(), "Service Running Iteration:" + mCounter, Toast.LENGTH_SHORT).show();
		
		mCounter++;
		
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
	            switch (msg.what) {
	                case MSG_REGISTER_CLIENT:
	                    mClients.add(msg.replyTo);
	                    break;
	                case MSG_UNREGISTER_CLIENT:
	                    mClients.remove(msg.replyTo);
	                    break;
	                case MSG_SET_VALUE:
	                    mValue = msg.arg1;
	                    for (int i=mClients.size()-1; i>=0; i--) {
	                        try {
	                            mClients.get(i).send(Message.obtain(null,
	                                    MSG_SET_VALUE, mValue, 0));
	                        } catch (RemoteException e) {
	                            // The client is dead.  Remove it from the list;
	                            // we are going through the list from back to front
	                            // so this is safe to do inside the loop.
	                            mClients.remove(i);
	                        }
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

	    /**
	     * Target we publish for clients to send messages to IncomingHandler.
	     */
	    final Messenger mMessenger = new Messenger(new IncomingHandler());

}
