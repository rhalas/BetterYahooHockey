package YahooObjects;

import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{

	public Handler handler;
	public int msg_id;
	
	//Lot more fields we can get, team_key is good enough for now though
	public String team_key;
	
	public User(){
		
	}
	
	public User(Handler handler, int msg_id){
    	this.handler = handler;
    	this.msg_id = msg_id;
    	
    	this.PopulateUser();
	}
	
	public void PopulateUser(){
		UserQuery u = new UserQuery(this, this.handler, this.msg_id);
		u.execute(null, null, null);
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
