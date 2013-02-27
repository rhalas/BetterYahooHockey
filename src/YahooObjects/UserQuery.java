package YahooObjects;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Parcelable;
import Utils.ExecuteSignedGet;

public class UserQuery extends ExecuteSignedGet{
	
	public User u;
	
	public UserQuery(User u, Handler handler, int msg_id){
		super(handler, msg_id);
		this.u = u;
	}
	
	public String GenerateURL(){
		return "http://fantasysports.yahooapis.com/fantasy/v2/users;use_login=1/games/teams?format=json";
	}
	
	public Parcelable parse_json(JSONObject jObj){
		try{
		
		}catch(Exception e){
			
		}
		
		return null;
	}
}
