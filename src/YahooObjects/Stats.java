package YahooObjects;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class Stats{
	
	public Stats()
	{	
	}
	
	public enum Stat{
		GOALS(1),
		ASSISTS(2),
		PLUS_MINUS(4),
		POWER_PLAY_POINTS(8),
		SHOTS_ON_GOAL(14),
		WINS(19),
		GAA(23),
		SAVES_MADE(25),
		SAVE_ATTEMPTS(24),
		SAVE_PERCENTAGE(26),
		SHUTOUTS(27),
		HITS(31);
		
		public int stat_id;
		
		Stat(int stat_id){
			this.stat_id = stat_id;
		}
		
		public static Stat fromInt(int x){
			switch(x){
				case 1:
					return GOALS;
				case 2:
					return ASSISTS;
				case 4:
					return PLUS_MINUS;
				case 8:
					return POWER_PLAY_POINTS;
				case 14:
					return SHOTS_ON_GOAL;
				case 19:
					return WINS;
				case 23:
					return GAA;
				case 24:
					return SAVE_ATTEMPTS;
				case 25:
					return SAVES_MADE;
				case 26:
					return SAVE_PERCENTAGE;
				case 27:
					return SHUTOUTS;
				case 31:
					return HITS;
			}
			
			return null;
		}
	};
	
	protected abstract void populate_stat(Stat stat, int value);
	protected abstract void populate_stat(Stat stat, float value);
	
	public Stats(JSONArray jArray){
		int i_value = 0;
		float f_value = 0;
		int stat_id = 0;
		String val;
		Stat stat;
		JSONObject jObj;
		
		for(int i = 0; i < jArray.length(); i++){
			try {
				jObj = jArray.getJSONObject(i).getJSONObject("stat");
				
				stat_id = jObj.getInt("stat_id");
				val = jObj.getString("value");
				
				stat = Stat.fromInt(stat_id);
				
				if(val.compareTo("-") == 0){
					i_value = 0;
					f_value = 0;
				}
				else{
					//Better way to do this in OOP?
					if(stat == Stat.GAA || stat == Stat.SAVE_PERCENTAGE){
						f_value = Float.parseFloat(val);
						populate_stat(stat, f_value);
					}
					else{
						i_value = Integer.parseInt(val);
						populate_stat(stat, i_value);	
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
	}
}
