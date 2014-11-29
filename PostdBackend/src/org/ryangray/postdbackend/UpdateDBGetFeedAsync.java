package org.ryangray.postdbackend;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jsoup.Jsoup;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;


public class UpdateDBGetFeedAsync {

	public static Object[] getFeed(Node node, String name) {
		
		String headline = null;
    	String url = null;
    	String description = null;
    	String pubDate = null;
		
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		
		try {
			String data = null;
			
//					Get title
			node = (Node) xpath.evaluate("title", node, XPathConstants.NODE);
			data = getData(node, name, data);
			data = Jsoup.parse(data).text();
			data = Jsoup.parse(data).text();
//			data = data.replaceAll( "&([^;]+(?!(?:\\w|;)))", "&amp;$1" );
			headline =  data;
			
//					Get link
			node = (Node) xpath.evaluate("../link", node, XPathConstants.NODE);
			data = getData(node, name, data);
//			data = data.replaceAll( "&([^;]+(?!(?:\\w|;)))", "&amp;$1" );
			url =  data;
			
//					Get description
			node = (Node) xpath.evaluate("../description", node, XPathConstants.NODE);
			data = getData(node, name, data);
			if (name.equals("Google")) {
				data = Jsoup.parse(data).text();
				int indexa = data.indexOf(">");
				int indexb = data.indexOf("...");
				if (indexa != -1 && indexb != -1) {
					data = data.substring(indexa + 1, indexb + 3);
				}
			}
			if (data != null) {
//						Twice to resolve all HTML escape sequences, for example "&amp;amp;#160;" in the FoxNews Feed
				data = Jsoup.parse(data).text();
				data = Jsoup.parse(data).text();
//				data = Html.fromHtml("<p>" + data + "</p>").toString().trim();
			}
//			data = data.replaceAll( "&([^;]+(?!(?:\\w|;)))", "&amp;$1" );
			description =  data;
			
//					Get pubDate
			node = (Node) xpath.evaluate("../pubDate", node, XPathConstants.NODE);
			data = getData(node, name, data);
			SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
			Date date;
			try {
				date = formatter.parse(data);
			} catch (ParseException e) {
//				This exception occurs expectedly with World News. The time format is HH:mm instead of HH:mm:ss
				formatter.applyPattern("EEE, dd MMM yyyy HH:mm zzz");
				date = formatter.parse(data);
//				e.printStackTrace();
			}
			TimeZone tz = TimeZone.getDefault();
			formatter.setTimeZone(tz);
			formatter.applyPattern("K:mma EEE, dd MMM yyyy");
			data = formatter.format(date);
//			data = data.replaceAll( "&([^;]+(?!(?:\\w|;)))", "&amp;$1" );
			pubDate =  data;
			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Object[] content = new Object[] {
				headline,
				url,
				description,
				pubDate
		};
		return content; 

    	
//		return activity;
	}

	private static String getData(Node node, String name, String data)
			throws DOMException {
		node = node.getFirstChild();
		StringBuilder sb = new StringBuilder();
		String nodeName = node.getNodeName().toString();
		int asteriskCount = 1;
		while (node.getNodeName().equals(nodeName)) {
			if (node.getNodeValue().toString().trim().equals("'") && asteriskCount % 2 == 1) {
				if (sb.substring(sb.length() - 1 ).equals(" ")) {
					sb.append(" ");
					asteriskCount++;
//					Log.d("RyanDebug", "First asterisk was parsed");
				} 
			}
			sb.append(node.getNodeValue().toString().trim());
			if (node.getNodeValue().toString().trim().equals("'") && asteriskCount % 2 == 0) {
				asteriskCount++;
				sb.append(" ");
//				Log.d("RyanDebug", "Second asterisk was parsed");
			}
			if (node.getNodeValue().toString().trim().equals("&")) {
				if (!name.equals("Google")) {
					sb.append("amp;");
				}
			}
//			if (node.getNodeValue().toString().trim().equals("'")) {
//				sb.append("amp;");
//			}
			if (node.getNextSibling() != null) {
				node = node.getNextSibling();
//				Log.d("RyanDebug", "A split text node was found in the " + source.getName() + " " + target);
			} else {
				nodeName = "";
			}
		}
		if (name.equals("Google")) {
			int index = sb.indexOf("url=");
			if (index != -1) {
				data = sb.substring(index + 4);
				return data; 
			}
		}
		data = sb.toString();
		return data;
	}

}
