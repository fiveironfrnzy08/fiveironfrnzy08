package org.ryangray.postd;

import twitter4j.TwitterException;
import android.os.AsyncTask;

public class TwitterTweet extends AsyncTask<String, Integer, String> {

	@Override
	protected String doInBackground(String... params) {
		try {
			PostdNewsActivity.twitter.updateStatus(params[0]);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
