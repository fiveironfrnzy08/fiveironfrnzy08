package org.ryangray.postdbackend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Node;

public class UpdateDBMain {

	private static final String ap_name = "AP";
	private static final String abc_name = "ABC";
	private static final String bbc_name = "BBC";
	private static final String cbc_name = "CBC";
	private static final String cbs_name = "CBS";
	private static final String cnn_name = "CNN";
	private static final String fox_name = "Fox";
	private static final String google_name = "Google";
	private static final String nbc_name = "NBC";
	private static final String reuters_name = "Reuters";
	private static final String time_name = "Time";
	private static final String usatoday_name = "USAToday";
	private static final String wn_name = "WN";
	private static final String yahoo_name = "Yahoo";
	
	private static final String url_ap = "http://hosted.ap.org/lineups/TOPHEADS-rss_2.0.xml?SITE=RANDOM&amp;SECTION=HOME";
	private static final String url_abc = "http://feeds.abcnews.com/abcnews/topstories";
	private static final String url_bbc = "http://feeds.bbci.co.uk/news/rss.xml?edition=us";
	private static final String url_cbc = "http://rss.cbc.ca/lineup/topstories.xml";
	private static final String url_cbs = "http://feeds.cbsnews.com/CBSNewsMain";
	private static final String url_cnn = "http://rss.cnn.com/rss/cnn_topstories.rss";
	private static final String url_fox = "http://feeds.foxnews.com/foxnews/most-popular";
	private static final String url_google = "http://news.google.com/news?ned=us&topic=h&output=rss";	
	private static final String url_nbc = "http://rss.msnbc.msn.com/id/3032091/device/rss/rss.xml";
	private static final String url_reuters = "http://feeds.reuters.com/reuters/topNews";
	private static final String url_time = "http://feeds2.feedburner.com/time/topstories";
	private static final String url_usatoday = "http://rssfeeds.usatoday.com/usatoday-NewsTopStories";
	private static final String url_wn = "http://rss.wn.com/English/top-stories";
	private static final String url_yahoo = "http://news.yahoo.com/rss/";
	
	private static final String[] urls = new String[] {
		url_ap,
		url_abc,
		url_bbc,
		url_cbc,
		url_cbs,
		url_cnn,
		url_fox,
		url_google,
		url_nbc,
		url_reuters,
		url_time,
		url_usatoday,
		url_wn,
		url_yahoo
	};
	
	private static final String[] names = new String[] {
		ap_name,
		abc_name,
		bbc_name,
		cbc_name,
		cbs_name,
		cnn_name,
		fox_name,
		google_name,
		nbc_name,
		reuters_name,
		time_name,
		usatoday_name,
		wn_name,
		yahoo_name
	};
	
	public static void main(String[] args) throws Exception {
	
		UpdateDBGetHeadlines content = new UpdateDBGetHeadlines();
		
		Logger logger = Logger.getLogger("MyLog");  
        FileHandler fh;
        int resultCount = 0;
        
        try {  
        	String path = UpdateDBMain.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        	path = path.substring(0, path.lastIndexOf("/") + 1);
        	StringBuilder sb = new StringBuilder();
        	sb = sb.append(path);
        	sb = sb.append("SQLEntries");
        	path = sb.toString();
        	path = URLDecoder.decode(path, "UTF-8");
        	System.out.println(path);
            fh = new FileHandler(path, true);  
            
            logger.addHandler(fh);  
            fh.setFormatter(new Formatter() {
    			
    			@Override
    			public String format(LogRecord arg0) {
    				StringBuilder sb = new StringBuilder();
    				Date date = new Date();
    				sb.append(new Timestamp(date.getTime()))
    					.append(" ")
    					.append(arg0.getMessage())
    					.append(System.getProperty("line.separator"));
    				return sb.toString();
    			}
    		});
              
        } catch (SecurityException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
		
		for (int i = 0; i < names.length; i++) {
			
			Node node = content.getHeadline(urls[i]);
			Object[] contentReturn = UpdateDBGetFeedAsync.getFeed(node, names[i]);
			String headline = (String) contentReturn[0];
			String url = (String) contentReturn[1];
			String description = (String) contentReturn[2];
			String pubdate = (String) contentReturn[3];
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://ryangray.org:3306/news_db","root","ryan135244");
			PreparedStatement statement = con.prepareStatement("REPLACE INTO news_sources (id, headline, description, pubdate, link) VALUES (?, ?, ?, ?, ?)");
			statement.setString(1, names[i]);
			statement.setString(2, headline);
			statement.setString(3, description);
			statement.setString(4, pubdate);
			statement.setString(5, url);
			int result = statement.executeUpdate();
			System.out.println("  " + names[i] + " result is: " + result);
			logger.info("  " + names[i] + " result is: " + result);
			if (result > 1) {
				resultCount = resultCount + result;
			}
			
		}
		
		System.out.println("\n");
		logger.info(System.getProperty("line.separator"));
		
		if (resultCount > 0) {
			String url = "http://ryangray.org:8080/PostdS/sendAll";
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(url);
			HttpResponse response = client.execute(request);
			// Get the response
			BufferedReader rd = new BufferedReader
			  (new InputStreamReader(response.getEntity().getContent()));
			    
			String line = "";
			while ((line = rd.readLine()) != null) {
			  System.out.println(line);
			} 
		}
	}
}
