package YahooObjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import Utils.ExecuteSignedGet;
import Utils.JSONParser;
import android.os.Handler;
import android.os.Parcelable;

public class RosterStatsQuery extends ExecuteSignedGet{
	public String team_key;
	public String date_string;
	public Roster r;
	public Handler handler;
	
	public RosterStatsQuery(String team_key, String date_string, Roster r, Handler handler, int msg_id){
		super(handler, msg_id);
		this.team_key = team_key;
		this.date_string = date_string;
		this.r = r;
	}
	
	public String GenerateURL(){
		String url = "http://fantasysports.yahooapis.com/fantasy/v2/team/";
		url += this.team_key;
		url += "/roster/players/stats";
		if(this.date_string.compareTo("") != 0){
			url += ";type=date;date=";
			url += this.date_string;
		}
		url += "?format=json";
		return url;
	}
	
	public Parcelable parse_json(JSONObject jObj){
		List<Player> players = new ArrayList<Player>();
		
		JSONArray array = JSONParser.GetJSONArray(jObj, Arrays.asList("fantasy_content", "team"));
		
		try {
			jObj = JSONParser.GetJSONObject(array.getJSONObject(1), Arrays.asList("roster", "0", "players"));
			
			JSONObject jObjTmp;
			
			for(int i = 0; i < 19; i++){
				jObjTmp = jObj.getJSONObject(Integer.toString(i));
				Player p = new Player(jObjTmp.getJSONArray("player"));
				players.add(p);
				
				if(p.set_position.compareTo("BN") != 0){
					if(p.set_position.compareTo("G") == 0){
						this.r.active_goalies++;
						GoalieStats gs = (GoalieStats)p.stats;
						this.r.stats.goalie_stats.wins += gs.wins;
						this.r.stats.goalie_stats.gaa = (this.r.stats.goalie_stats.gaa+gs.gaa)/this.r.active_goalies;
						this.r.stats.goalie_stats.saves += gs.saves;
						this.r.stats.goalie_stats.save_attempts += gs.save_attempts;
						this.r.stats.goalie_stats.save_percentage = (this.r.stats.goalie_stats.save_percentage+gs.save_percentage)/this.r.active_goalies;
						this.r.stats.goalie_stats.shutouts += gs.shutouts;
					}else{
						this.r.active_skaters++;
						SkaterStats ss = (SkaterStats)p.stats;
						this.r.stats.skater_stats.goals += ss.goals;
						this.r.stats.skater_stats.assists += ss.assists;
						this.r.stats.skater_stats.power_play_points += ss.power_play_points;
						this.r.stats.skater_stats.shots_on_goal += ss.shots_on_goal;
						this.r.stats.skater_stats.plus_minus += ss.plus_minus;
					}		
				}
			}
			
			this.r.players = players;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return (Parcelable)this.r;
	}
	
	public Roster onSuccess(String s, Roster r){
		return null;
	}
	
}