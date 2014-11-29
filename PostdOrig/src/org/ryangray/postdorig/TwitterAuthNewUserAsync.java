package org.ryangray.postdorig;

import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.os.AsyncTask;

public class TwitterAuthNewUserAsync extends AsyncTask<Object, Integer, AccessToken>{

	@Override
	protected AccessToken doInBackground(Object... params) {
		RequestToken twitterReqtoken = (RequestToken) params[0];
		String oauthVerifier = (String) params[1];
		AccessToken at = null;
		try {
			at = NewsActivity.twitter.getOAuthAccessToken(twitterReqtoken, oauthVerifier);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return at;
	}

}
