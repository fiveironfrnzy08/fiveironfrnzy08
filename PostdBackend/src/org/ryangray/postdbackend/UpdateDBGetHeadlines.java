package org.ryangray.postdbackend;

import java.net.URL;
import java.net.URLConnection;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class UpdateDBGetHeadlines {
	
	public final String tag = "RSSReader";
	private Node feed = null;

    public Node getHeadline(String url) {
        
        feed = getFeed(url);
        return feed;
    }
    
    private Node getFeed(String urlToRssFeed)
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
            return null;
        }
    }
}
