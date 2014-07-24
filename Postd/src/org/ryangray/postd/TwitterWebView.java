package org.ryangray.postd;


import java.util.concurrent.ExecutionException;

import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class TwitterWebView extends Activity {
	
//	private String authURL;
	private WebView twitterSite;
//	private TwitterFragment twitterTweet;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitter_webview);
		Log.i("RyanDebug", "Loading TweetToTwitterActivity");
		twitterSite = (WebView) findViewById(R.id.twitterWebviewId);
		TwitterAuthRequestTokenAsync authAsync = (TwitterAuthRequestTokenAsync) new TwitterAuthRequestTokenAsync().execute(PostdNewsActivity.TWITTER_CALLBACK_URL);
		try {
			PostdNewsActivity.twitterReqToken = authAsync.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		twitterSite.loadUrl(PostdNewsActivity.twitterReqToken.getAuthenticationURL());
		setContentView(twitterSite);
		
	}
	
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.i("RyanDebug", "New Intent Arrived");
		dealWithTwitterResponse(intent);
	}
	private void dealWithTwitterResponse(Intent intent) {
		Uri uri = intent.getData();
		if (uri != null && uri.toString().startsWith(PostdNewsActivity.TWITTER_CALLBACK_URL) && !uri.getQuery().startsWith("denied")) { // If the user has just logged in
			Log.i("RyanDebug", "oauth verifier received");
			String oauthVerifier = uri.getQueryParameter("oauth_verifier");
			authoriseNewUser(oauthVerifier);
		} else if (uri != null && uri.getQuery().startsWith("denied")) {
			Log.i("RyanDebug", "user cancelled twitter");
			setContentView(R.layout.news_activity);
			finish();
		}
	}
	private void authoriseNewUser(String oauthVerifier) {
		TwitterAuthNewUserAsync authNewUser = (TwitterAuthNewUserAsync) new TwitterAuthNewUserAsync().execute(PostdNewsActivity.twitterReqToken, oauthVerifier);
		AccessToken at = null;
		try {
			at = authNewUser.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PostdNewsActivity.twitter.setOAuthAccessToken(at);
		saveAccessToken(at);
		// Set the content view back after we changed to a webview
		setContentView(R.layout.news_activity);
	}
	private void saveAccessToken(AccessToken at) {
		String token = at.getToken();
		String secret = at.getTokenSecret();
		Editor editor = PostdNewsActivity.twitterPrefs.edit();
		editor.putString(PostdNewsActivity.TWITTER_ACCESS_TOKEN, token);
		editor.putString(PostdNewsActivity.TWITTER_ACCESS_TOKEN_SECRET, secret);
		editor.commit();
		finish();
	}
}