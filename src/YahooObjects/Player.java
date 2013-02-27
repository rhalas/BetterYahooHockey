package YahooObjects;

import org.json.JSONArray;
import org.json.JSONObject;

public class Player{
	public String first_name;
	public String last_name;
	public String positions;
	public String set_position;
	public Stats stats;
	
	public Player(){
	}
	
	public Player(JSONArray jArray){
		try {			
			
			JSONArray player_info = jArray.getJSONArray(0);
			
			JSONObject player_name = player_info.getJSONObject(2);
			
			this.first_name = player_name.getJSONObject("name").getString("first");
			this.last_name = player_name.getJSONObject("name").getString("last");
			
			JSONArray position_info = jArray.getJSONObject(1).getJSONArray("selected_position");

			this.set_position = position_info.getJSONObject(1).getString("position");
			
			//Yahoo decided for some reason to change display_position based on the skaters status
			int display_position = 8;
			
			if(player_info.getJSONObject(3).has("status") == true){
				String s = player_info.getJSONObject(3).getString("status");
				if(s.compareTo("IR") == 0){
					display_position = 10;
				}
				else if(s.compareTo("DTD") == 0){
					display_position = 9;
				}
			}
			
			this.positions = player_info.getJSONObject(display_position).getString("display_position");
			
			if(this.positions.compareTo("G") == 0){
				this.stats = new GoalieStats(jArray.getJSONObject(3).getJSONObject("player_stats").getJSONArray("stats"));
			}
			else{
				this.stats = new SkaterStats(jArray.getJSONObject(3).getJSONObject("player_stats").getJSONArray("stats"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Player(String first_name, String last_name){
		this.first_name = first_name;
		this.last_name = last_name;
	}
}