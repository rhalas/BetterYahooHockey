package YahooObjects;

import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{

	public Handler handler;
	public int msg_id;
	
	//Lot more fields we can get, team_key is good enough for now though
	public String team_key;
	
	public User(Handler handler, int msg_id){
    	this.handler = handler;
    	this.msg_id = msg_id;
    	
    	this.PopulateUser();
	}
	
	public void PopulateUser(){
		RosterStatsQuery r = new RosterStatsQuery(null, null, null, this.handler, this.msg_id);
		r.execute(null, null, null);
	}
	
	public User(Parcel in){
		in.readString();
	}	
	
	public void writeToParcel(Parcel dest, int flags){
		dest.writeString(this.team_key);
	}
	
	public int describeContents(){
		return 0;
	}
}
