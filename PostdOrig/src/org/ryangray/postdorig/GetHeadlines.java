package org.ryangray.postdorig;

import java.net.URL;
import java.net.URLConnection;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

@TargetApi(9)
public class GetHeadlines extends Activity {
	
	public final String tag = "RSSReader";
	private Node feed = null;

    public Node getHeadline(String url, Context context) {
        
        feed = getFeed(url, context);
        return feed;
    }
    
    private Node getFeed(String urlToRssFeed, Context context)
    {
        try
        {
            URL url = new URL(urlToRssFeed);
             
            XPath xpath = XPathFactory.newInstance().newXPath();
             
            URLConnection con = url.openConnection();
            con.getContentLength();
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);
            InputSource is = new InputSource(con.getInputStream());
            
            Node node = null;
			node = (Node) xpath.evaluate("/rss/channel/item[1]", is, XPathConstants.NODE);
            
            return node;
        }
            catch (Exception ee)
        {
            Log.d("RyanDebug", ee.getMessage());
            return null;
        }
    }
}
