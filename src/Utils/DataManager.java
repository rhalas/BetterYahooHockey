package Utils;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

public class DataManager {

	public static String YAHOO_CONSUMER_KEY = "dj0yJmk9ZHk1MGMwTmozYWxKJmQ9WVdrOWVEQmtkVGh6TjJrbWNHbzlNVEF6TWpjMU1EWTJNZy0tJnM9Y29uc3VtZXJzZWNyZXQmeD04ZQ--";
	public static String YAHOO_CONSUMER_SECRET = "10f35ce5a1453fad3f9ca425c4eb7c179f36f47b";
    public static OAuthConsumer mConsumer;
    public static OAuthProvider mProvider;
    public static String callbackUrl = "betterhockey://connect"; 
	public static ThreadSafeClientConnManager mgr;
    public static HttpParams params;
    public static HttpClient client;
    
    public static String token;
    public static String secret;
    
    public DataManager(){
    	DataManager.mConsumer = new CommonsHttpOAuthConsumer(YAHOO_CONSUMER_KEY, YAHOO_CONSUMER_SECRET);
    	
    	DataManager.params = new BasicHttpParams();
    	HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
    	ConnManagerParams.setMaxTotalConnections(params, 100);
    	ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(5));
    	
    	SchemeRegistry supportedSchemes = new SchemeRegistry();
    	supportedSchemes.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
    	supportedSchemes.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
    	
    	DataManager.mgr = new ThreadSafeClientConnManager(params, supportedSchemes);
    	
    	DataManager.client = new DefaultHttpClient(DataManager.mgr, DataManager.params);
    	
    	DataManager.mProvider = new CommonsHttpOAuthProvider(
        		"https://api.login.yahoo.com/oauth/v2/get_request_token", 
        		"https://api.login.yahoo.com/oauth/v2/get_token",
        		"https://api.login.yahoo.com/oauth/v2/request_auth", 
                DataManager.client);
        
    }
}
