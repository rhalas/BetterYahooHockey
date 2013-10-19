package YahooObjects;

import org.json.JSONArray;

public class SkaterStats extends Stats{

	public int goals;
  	public int assists;
  	public int plus_minus;
  	public int penalty_minutes;
  	public int power_play_points;
  	public int shots_on_goal;
  	public int hits;
	
  	protected void populate_stat(Stat stat, float value){}
  	
  	protected void populate_stat(Stat stat, int value){
		switch(stat){
			case GOALS:
				this.goals = value;
				break;
			case ASSISTS:
				this.assists = value;
				break;
			case PLUS_MINUS:
				this.plus_minus = value;
				break;
			case PENALTY_MINUTES:
				this.penalty_minutes = value;
				break;
			case POWER_PLAY_POINTS:
				this.power_play_points = value;
				break;
			case SHOTS_ON_GOAL:
				this.shots_on_goal = value;
				break;
		}
  	}
  	
  	public SkaterStats(){
  		this.goals = 0;
  		this.assists = 0;
  	  	this.plus_minus = 0;
  	  	this.penalty_minutes = 0;
  	  	this.power_play_points = 0;
  	  	this.shots_on_goal = 0;
  	}
  	
	public SkaterStats(JSONArray jArray){
		super(jArray);
	}
	
	public Stats CompareStats(Stats s){
		SkaterStats new_ss = (SkaterStats)s;
		SkaterStats ss = new SkaterStats();
		
		ss.goals = new_ss.goals - this.goals;
		ss.assists = new_ss.assists - this.assists;
		ss.plus_minus = new_ss.plus_minus - this.plus_minus;
		ss.power_play_points = new_ss.power_play_points - this.power_play_points;
		ss.shots_on_goal = new_ss.shots_on_goal - this.shots_on_goal;
		ss.penalty_minutes = new_ss.penalty_minutes - this.penalty_minutes;
		
		return (Stats)ss;
	}

}
