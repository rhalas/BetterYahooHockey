package Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class StringUtils {
	//There is definitely a better way to parse query parameters
	public static String GetQueryParameter(String param, String query_string){
		String value = "";
		
		int pos = query_string.indexOf(param);
		
		int start = query_string.indexOf("=", pos);
		int end = query_string.indexOf("&", pos);
		
		try {
			value = URLDecoder.decode(query_string.substring(start+1, end), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return value;
	}
}
