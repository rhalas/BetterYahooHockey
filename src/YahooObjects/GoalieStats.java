package YahooObjects;

import org.json.JSONArray;

public class GoalieStats extends Stats{
	public int wins;
	public float gaa;
	public int saves;
	public int save_attempts;
	public float save_percentage;
	public int shutouts;
	
	protected void populate_stat(Stat stat, float value){
		switch(stat){
			case GAA:
				this.gaa = value;
				break;
			case SAVE_PERCENTAGE:
				this.save_percentage = value;
				break;
		}
	}
	
  	protected void populate_stat(Stat stat, int value){
		switch(stat){
			case WINS:
				this.wins = value;
				break;
			case SAVES_MADE:
				this.saves = value;
				break;
			case SAVE_ATTEMPTS:
				this.save_attempts = value;
				break;
			case SHUTOUTS:
				this.shutouts = value;
				break;		
		}
  	}
  	
  	public GoalieStats(){
  		this.wins = 0;
  		this.gaa = 0.0f;
  		this.saves = 0;
  		this.save_attempts = 0;
  		this.save_percentage = 0.0f;
  		this.shutouts = 0;
  	}
  	
	public GoalieStats(JSONArray jArray){
		super(jArray);
	}
	
	public Stats CompareStats(Stats s){
		GoalieStats new_gs = (GoalieStats)s;
		GoalieStats gs = new GoalieStats();
		
		gs.wins = new_gs.wins - this.wins;
		gs.gaa = new_gs.gaa - this.gaa;
		gs.saves = new_gs.saves - this.saves;
		gs.save_attempts = new_gs.save_attempts - this.save_attempts;
		gs.save_percentage = new_gs.save_percentage - this.save_percentage;
		gs.shutouts = new_gs.shutouts - this.shutouts;
		
		return (Stats)gs;
	}
}
