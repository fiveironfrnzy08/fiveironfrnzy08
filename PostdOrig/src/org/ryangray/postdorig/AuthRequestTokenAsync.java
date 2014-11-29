package org.ryangray.postdorig;

import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;
import android.os.AsyncTask;

public class AuthRequestTokenAsync extends AsyncTask<String, Integer, RequestToken>{

	@Override
	protected RequestToken doInBackground(String... params) {
		RequestToken requestToken = null;
		try {
			requestToken = NewsActivity.twitter.getOAuthRequestToken(params[0]);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return requestToken;
	}

}
