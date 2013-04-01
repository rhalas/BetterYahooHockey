package YahooObjects;

import java.util.ArrayList;
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
		public List<Player> skaters;
		public List<Player> goalies;
		public SkaterStats skater_stats;
		public GoalieStats goalie_stats;
		
		public RosterStats(){
			this.skaters = new ArrayList<Player>();
			this.goalies = new ArrayList<Player>();
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
	}
	
	public RosterStats CompareRoster(Roster r){
 		RosterStats rs = new RosterStats();
 		
		for(int i = 0; i < r.players.size(); i++){
			Player p_current = this.players.get(i);
			Player p_new = r.players.get(i);
			
			if(p_current.positions.compareTo("G") == 0){
				Player g = new Player();
				GoalieStats gs = (GoalieStats)p_current.stats.CompareStats(p_new.stats);
				g.first_name = p_current.first_name;
				g.last_name = p_current.last_name;
				g.stats = gs;
				
				rs.goalies.add(g);
			}
			else{
				Player s = new Player();
				SkaterStats ss = (SkaterStats)p_current.stats.CompareStats(p_new.stats);
				s.first_name = p_current.first_name;
				s.last_name = p_current.last_name;
				s.stats = ss;
				
				
				rs.skaters.add(s);
			}
		}
		
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
	
	public List<String> GenerateSkaterStatChangeText(RosterStats stats){
		List<String> change_strings = new ArrayList<String>();
		
		String total_change = "";
		
		if(stats.skater_stats.goals != 0){
			total_change += "G:" + stats.skater_stats.goals;
		}
		if(stats.skater_stats.assists != 0){
			total_change += " A:" + stats.skater_stats.assists;
		}
		if(stats.skater_stats.plus_minus != 0){
			total_change += " +/-: " + stats.skater_stats.plus_minus;
		}
		if(stats.skater_stats.power_play_points != 0){
			total_change += " PPP: " + stats.skater_stats.power_play_points;
		}
		if(stats.skater_stats.shots_on_goal != 0){
			total_change += " S:" + stats.skater_stats.shots_on_goal;
		}
		if(stats.skater_stats.hits != 0){
			total_change += " H:" + stats.skater_stats.hits;
		}
		
		change_strings.add(total_change);
		
		for(int i = 0; i < stats.skaters.size(); i++){
			Boolean appendToList = false;
			Player p = stats.skaters.get(i);
			
			String s = p.first_name + " " + p.last_name + " ";
			
			SkaterStats ss = (SkaterStats)p.stats;
			
			if(ss.goals != 0){
				s += "G: " + ss.goals + " ";
				appendToList = true;
			}
			if(ss.assists != 0){
				s += "A: " + ss.assists + " ";
				appendToList = true;
			}
			if(ss.plus_minus != 0){
				s += "+/-: " + ss.plus_minus + " ";
				appendToList = true;
			}
			if(ss.power_play_points != 0){
				s += "PPP: " + ss.power_play_points + " ";
				appendToList = true;
			}
			if(ss.shots_on_goal != 0){
				s += "SOG: " + ss.shots_on_goal + " ";
				appendToList = true;
			}
			if(ss.hits != 0){
				s += "H: " + ss.hits + " ";
				appendToList = true;
			}
			
			if(appendToList){
				change_strings.add(s);
			}
		}
		
		return change_strings;
	}
	
	public List<String> GenerateGoalieStatChangeText(RosterStats stats){
		List<String> change_strings = new ArrayList<String>();
		
		String total_change = "";
		
		if(stats.goalie_stats.gaa != 0){
			total_change += "GAA:" + stats.goalie_stats.gaa;
		}
		if(stats.goalie_stats.saves != 0){
			total_change += " S:" + stats.goalie_stats.saves;
		}
		if(stats.goalie_stats.save_attempts != 0){
			total_change += " SA:" + stats.goalie_stats.save_attempts;
		}
		if(stats.goalie_stats.save_percentage != 0){
			total_change += " S%:" + stats.goalie_stats.save_percentage;
		}
		if(stats.goalie_stats.wins != 0){
			total_change += " W:" + stats.goalie_stats.wins;
		}
		if(stats.goalie_stats.shutouts != 0){
			total_change += " SO:" + stats.goalie_stats.shutouts;
		}
		
		change_strings.add(total_change);
		
		for(int i = 0; i < stats.goalies.size(); i++){
			Boolean appendToList = false;
			Player p = stats.goalies.get(i);
			
			String s = p.first_name + " " + p.last_name + " ";
			
			GoalieStats gs = (GoalieStats)p.stats;
			
			if(gs.gaa != 0){
				s += "GAA: " + gs.gaa + " ";
				appendToList = true;
			}
			if(gs.saves != 0){
				s += "S: " + gs.saves + " ";
				appendToList = true;
			}
			if(gs.save_attempts != 0){
				s += "SA: " + gs.save_attempts + " ";
				appendToList = true;
			}
			if(gs.save_percentage != 0){
				s += "S%: " + gs.save_percentage + " ";
				appendToList = true;
			}
			if(gs.wins != 0){
				s += "W: " + gs.wins + " ";
				appendToList = true;
			}
			if(gs.shutouts != 0){
				s += "SO: " + gs.shutouts + " ";
				appendToList = true;
			}
			
			if(appendToList){
				change_strings.add(s);
			}
		}
		
		return change_strings;
	}
	
}