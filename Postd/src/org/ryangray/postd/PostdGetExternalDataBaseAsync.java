package org.ryangray.postd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class PostdGetExternalDataBaseAsync<Params> extends AsyncTask<Object, Integer, Activity>{
	
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
		
		activity = (Activity) object[0];	
		
		int sourceTotal = 0;
		for (PostdSource source : PostdMainActivity.allSources) {
			if (source.getChecked()) {
				sourceTotal++;
			}
		}
		String[] headlines = new String[sourceTotal];
    	int[] imgIds = new int[sourceTotal];
    	String[] urls = new String[sourceTotal];
    	String[] descriptions = new String[sourceTotal];
    	String[] pubDates = new String[sourceTotal];
    	
    	Connection con = null;
    	ResultSet result = null;
    	
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://ryangray.org:3306/news_db","postd","postd");
			PreparedStatement statement = con.prepareStatement("SELECT * FROM news_sources;");
			result = statement.executeQuery("SELECT * FROM news_sources;");
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			b.putString("exception", e.getMessage());
			e.printStackTrace();
			return activity;
		}

		HashMap<String, String> mapHeadlines = new HashMap<String, String>();
		HashMap<String, String> mapDescriptions = new HashMap<String, String>();
		HashMap<String, String> mapUrls = new HashMap<String, String>();
		HashMap<String, String> mapPubDates = new HashMap<String, String>();
		
		
		try {
			while (result.next()) {
				mapHeadlines.put(result.getString("id"), result.getString("headline"));
				mapDescriptions.put(result.getString("id"), result.getString("description"));
				mapUrls.put(result.getString("id"), result.getString("link"));
				mapPubDates.put(result.getString("id"), result.getString("pubdate"));
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		int i = 0;
		for (PostdSource source : PostdMainActivity.allSources) {
    		if (source.getChecked()) {
    			
				headlines[i] = mapHeadlines.get(source.getName());
				descriptions[i] = mapDescriptions.get(source.getName());
				urls[i] = mapUrls.get(source.getName());
				pubDates[i] = mapPubDates.get(source.getName());
				imgIds[i] = source.getImgId();
				i++;
    		}
		}
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		b.putStringArray("news_headlines", headlines);
		b.putIntArray("news_imgIds", imgIds);
		b.putStringArray("news_urls", urls);
		b.putStringArray("news_descriptions", descriptions);
		b.putStringArray("news_pubDates", pubDates);
		publishProgress(100);
		return activity;
	}
	
	@Override
	protected void onPostExecute(Activity activity) {
		Intent i = new Intent(activity, PostdNewsActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.putExtra("news_bundle", b);
		activity.startActivity(i);
		PostdMainActivity.activities++;
		if (activity instanceof PostdMainActivity || activity instanceof PostdNewsActivity) {
			activity.finish();
			PostdMainActivity.activities--;
		}
	}

}
