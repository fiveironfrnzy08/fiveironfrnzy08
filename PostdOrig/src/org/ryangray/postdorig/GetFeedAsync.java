package org.ryangray.postdorig;

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

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class GetFeedAsync<Params> extends AsyncTask<Object, Integer, Activity>{
	
	public static final String PREFS_CHECK_BOOL = "all_check_bool";
	private Activity activity;
	@SuppressWarnings("unused")
	private double progress;
	@SuppressWarnings("unused")
	private int sourceTotal;
	@SuppressWarnings("unused")
	private int progressCount;
	private Bundle b = new Bundle();
	

	@Override
	protected Activity doInBackground(Object... object) {
		publishProgress(0);
		
		GetHeadlines headline = (GetHeadlines) object[1];
		activity = (Activity) object[0];	
		
		double progress = 0;
		int sourceTotal = 0;
		int progressCount = 0;
		
		for (Source source : MainActivity.allSources) {
			if (source.getChecked()) {
				sourceTotal++;
			}
		}
		
		Node node;
		String[] headlines = new String[sourceTotal];
    	int[] imgIds = new int[sourceTotal];
    	String[] urls = new String[sourceTotal];
    	String[] descriptions = new String[sourceTotal];
    	String[] pubDates = new String[sourceTotal];
    	int sourcesIndexCount = -1;
		
    	for (Source source : MainActivity.allSources) {
    		if (source.getChecked()) {
    			sourcesIndexCount++;
    			XPathFactory factory = XPathFactory.newInstance();
    			XPath xpath = factory.newXPath();
    			node = null;
    			
    			try {
    				String data = null;
    				
					node = headline.getHeadline(source.getUrl(), activity);
					
					progressCount++;
    				progress = ((double) progressCount / ((double) sourceTotal * 2))*100;
					publishProgress((int)progress);
					
//					Get title
					node = (Node) xpath.evaluate("title", node, XPathConstants.NODE);
					data = getData(headlines, node, source, data, "title");
					data = Jsoup.parse(data).text();
					data = Jsoup.parse(data).text();
					headlines[sourcesIndexCount] =  data;
					
//					Get link
					node = (Node) xpath.evaluate("../link", node, XPathConstants.NODE);
					data = getData(headlines, node, source, data, "link");
					urls[sourcesIndexCount] =  data;
					
//					Get description
					node = (Node) xpath.evaluate("../description", node, XPathConstants.NODE);
					data = getData(headlines, node, source, data, "description");
					if (source.getName().equals("Google")) {
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
						data = Html.fromHtml("<p>" + data + "</p>").toString().trim();
					}
					descriptions[sourcesIndexCount] =  data;
					
//					Get pubDate
					node = (Node) xpath.evaluate("../pubDate", node, XPathConstants.NODE);
					data = getData(headlines, node, source, data, "pubDate");
					SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
					Date date;
					try {
						date = formatter.parse(data);
					} catch (ParseException e) {
						Log.d("RyanDegug", "Trying another SimpleDateFormat pattern...");
						formatter.applyPattern("EEE, dd MMM yyyy HH:mm zzz");
						date = formatter.parse(data);
						e.printStackTrace();
					}
					TimeZone tz = TimeZone.getDefault();
					formatter.setTimeZone(tz);
					formatter.applyPattern("K:mma'\n'EEE, dd MMM yyyy");
					data = formatter.format(date);
					pubDates[sourcesIndexCount] =  data;
					
				} catch (XPathExpressionException e) {
					e.printStackTrace();
				} catch (DOMException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
    			imgIds[sourcesIndexCount] = source.getImgId();
				
				progressCount++;
				progress = ((double) progressCount / ((double) sourceTotal * 2))*100;
				publishProgress((int)progress);
			}
		}
    	
		b.putStringArray("news_headlines", headlines);
		b.putIntArray("news_imgIds", imgIds);
		b.putStringArray("news_urls", urls);
		b.putStringArray("news_descriptions", descriptions);
		b.putStringArray("news_pubDates", pubDates);
		publishProgress(100);
		return activity;
	}

	private String getData(String[] headlines, Node node, Source source, String data, String target)
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
					Log.d("RyanDebug", "First asterisk was parsed");
				} 
			}
			sb.append(node.getNodeValue().toString().trim());
			if (node.getNodeValue().toString().trim().equals("'") && asteriskCount % 2 == 0) {
				asteriskCount++;
				sb.append(" ");
				Log.d("RyanDebug", "Second asterisk was parsed");
			}
			if (node.getNodeValue().toString().trim().equals("&")) {
				if (!source.getName().equals("Google")) {
					sb.append("amp;");
				}
			}
//			if (node.getNodeValue().toString().trim().equals("'")) {
//				sb.append("amp;");
//			}
			if (node.getNextSibling() != null) {
				node = node.getNextSibling();
				Log.d("RyanDebug", "A split text node was found in the " + source.getName() + " " + target);
			} else {
				nodeName = "";
			}
		}
		if (source.getName().equals("Google")) {
			int index = sb.indexOf("url=");
			if (index != -1) {
				data = sb.substring(index + 4);
				return data; 
			}
		}
		data = sb.toString();
		return data;
	}
	
	@Override
	protected void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate(progress);
		TextView tv = (TextView) activity.findViewById(R.id.progressTextView);
		String sProgress = String.valueOf(progress[0]);
		tv.setText(sProgress);
		Log.d("RyanDebug", "Set progress " + progress[0]);
		
    }
	
	@Override
	protected void onPostExecute(Activity activity) {
		ImageView progressIV = (ImageView) activity.findViewById(R.id.loadingImageView);
		TextView progressTV = (TextView) activity.findViewById(R.id.progressTextView);
		progressIV.setVisibility(8);
		progressTV.setVisibility(8);
		Intent i = new Intent(activity, NewsActivity.class);
//		Activity activity = (Activity) activity;
		i.putExtra("news_bundle", b);
		activity.startActivity(i);
		MainActivity.activities++;
		if (activity instanceof MainActivity || activity instanceof NewsActivity) {
			activity.finish();
			MainActivity.activities--;
		}
	}

}
