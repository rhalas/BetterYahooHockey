package YahooObjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Parcelable;
import Utils.ExecuteSignedGet;
import Utils.JSONParser;

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
			JSONArray user_array = JSONParser.GetJSONArray(jObj, Arrays.asList("fantasy_content", "users", "0", "user"));
			JSONArray game_array = JSONParser.GetJSONArray(user_array.getJSONObject(1), Arrays.asList("games", "0", "game"));
			JSONArray team_array = JSONParser.GetJSONArray(game_array.getJSONObject(1), Arrays.asList("teams", "0", "team"));
			
			this.u.team_key = team_array.getJSONArray(0).getJSONObject(0).getString("team_key");
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
}
