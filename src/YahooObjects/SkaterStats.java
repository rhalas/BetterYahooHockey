package YahooObjects;

import org.json.JSONArray;

public class SkaterStats extends Stats{

	public int goals;
  	public int assists;
  	public int plus_minus;
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
			case POWER_PLAY_POINTS:
				this.power_play_points = value;
				break;
			case SHOTS_ON_GOAL:
				this.shots_on_goal = value;
				break;
			case HITS:
				this.hits = value;
				break;
		}
  	}
  	
  	public SkaterStats(){
  		this.goals = 0;
  		this.assists = 0;
  	  	this.plus_minus = 0;
  	  	this.power_play_points = 0;
  	  	this.shots_on_goal = 0;
  	  	this.hits = 0;
  	}
  	
	public SkaterStats(JSONArray jArray){
		super(jArray);
	}

}
