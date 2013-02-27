package Utils;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONParser {

	public static JSONObject ConvertStringToJSON(String text){
		JSONObject jObj = null;
		
		try{
			jObj = new JSONObject(text);
		} catch(Exception e){
			e.printStackTrace();
		}
		
		return jObj;
	}
	
	public static JSONObject GetJSONObject(JSONObject jObj, List<String> nodes){
		for(int i = 0; i < nodes.size(); i++){
			try{
				jObj = jObj.getJSONObject(nodes.get(i));
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return jObj;
	}	
	
	public static JSONArray GetJSONArray(JSONObject jObj, List<String> nodes){
		JSONArray jArray = null;
		
		for(int i = 0; i < nodes.size() - 1; i++){
			try{
				jObj = jObj.getJSONObject(nodes.get(i));
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		
		try {
			jArray = jObj.getJSONArray(nodes.get(nodes.size() - 1));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return jArray;
	}
}
