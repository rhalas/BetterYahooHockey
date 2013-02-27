package YahooObjects;

import java.util.List;

import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;

public class Roster implements Parcelable {
	
	public RosterStats stats;
	public int active_skaters = 0;
	public int active_goalies = 0;
	public List<Player> players;
	public Handler handler;
	public int msg_id;
	
	private String league_id;
	private String date;
		
	public class RosterStats{
		public SkaterStats skater_stats;
		public GoalieStats goalie_stats;
		
		public RosterStats(){
			this.skater_stats = new SkaterStats();
			this.goalie_stats = new GoalieStats();
		}
	}
	
	public Roster(String league_id, String date, Handler handler, int msg_id){
		this.league_id = league_id;
		this.date = date;
		this.stats = new RosterStats();
    	this.handler = handler;
    	this.msg_id = msg_id;
    	
    	this.PopulateRoster();
    	
	}
	
	public Roster(Parcel in){
		in.readList(this.players, null);
	}	
	
	public void writeToParcel(Parcel dest, int flags){
		dest.writeList(this.players);
	}
	
	public int describeContents(){
		return 0;
	}
	
	public void PopulateRoster(){
		RosterStatsQuery r = new RosterStatsQuery(this.league_id, this.date, this, this.handler, this.msg_id);
		r.execute(null, null, null);
		//UserQuery u = new UserQuery(null, this.handler, this.msg_id);
		//u.execute(null, null, null);
	}
	
	public RosterStats CompareRoster(Roster r){
		RosterStats rs = new RosterStats();

		rs.skater_stats.goals = r.stats.skater_stats.goals - this.stats.skater_stats.goals;
		rs.skater_stats.assists = r.stats.skater_stats.assists - this.stats.skater_stats.assists;
		rs.skater_stats.plus_minus = r.stats.skater_stats.plus_minus - this.stats.skater_stats.plus_minus;
		rs.skater_stats.power_play_points = r.stats.skater_stats.power_play_points - this.stats.skater_stats.power_play_points;
		rs.skater_stats.shots_on_goal = r.stats.skater_stats.shots_on_goal - this.stats.skater_stats.shots_on_goal;
		rs.skater_stats.hits = r.stats.skater_stats.hits - this.stats.skater_stats.hits;
		
		rs.goalie_stats.wins = r.stats.goalie_stats.wins - this.stats.goalie_stats.wins;
		rs.goalie_stats.gaa = r.stats.goalie_stats.gaa - this.stats.goalie_stats.gaa;
		rs.goalie_stats.saves = r.stats.goalie_stats.saves - this.stats.goalie_stats.saves;
		rs.goalie_stats.save_attempts = r.stats.goalie_stats.save_attempts - this.stats.goalie_stats.save_attempts;
		rs.goalie_stats.save_percentage = r.stats.goalie_stats.save_percentage - this.stats.goalie_stats.save_percentage;
		rs.goalie_stats.shutouts = r.stats.goalie_stats.shutouts - this.stats.goalie_stats.shutouts;
		
		return rs;
	}
	
	public String GenerateStatChangeText(RosterStats stats){
		String text = "";
		
		if(stats.skater_stats.goals != 0){
			text += "G: " + stats.skater_stats.goals;
		}
		if(stats.skater_stats.assists != 0){
			text += "A: " + stats.skater_stats.assists;
		}
		if(stats.skater_stats.plus_minus != 0){
			text += "+/-: " + stats.skater_stats.plus_minus;
		}
		if(stats.skater_stats.power_play_points != 0){
			text += "PPP: " + stats.skater_stats.power_play_points;
		}
		if(stats.skater_stats.shots_on_goal != 0){
			text += "S: " + stats.skater_stats.shots_on_goal;
		}
		if(stats.skater_stats.hits != 0){
			text += "H: " + stats.skater_stats.hits;
		}
		
		//Comment out goalie stats for now, find better way to display notification for them

		if(stats.goalie_stats.gaa != 0){
			text += "GAA:" + stats.goalie_stats.gaa;
		}
		if(stats.goalie_stats.saves != 0){
			text += "S:" + stats.goalie_stats.saves;
		}
		if(stats.goalie_stats.save_attempts != 0){
			text += "SA:" + stats.goalie_stats.save_attempts;
		}
		if(stats.goalie_stats.save_percentage != 0){
			text += "S%:" + stats.goalie_stats.save_percentage;
		}
		/*
		if(stats.goalie_stats.wins != 0){
			text += "W:" + stats.goalie_stats.wins;
		}
		if(stats.goalie_stats.shutouts != 0){
			text += "SO:" + stats.goalie_stats.shutouts;
		}*/
		
		return text;
	}
	
}
