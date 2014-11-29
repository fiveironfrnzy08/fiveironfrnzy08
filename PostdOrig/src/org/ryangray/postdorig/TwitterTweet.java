package org.ryangray.postdorig;

import twitter4j.TwitterException;
import android.os.AsyncTask;

public class TwitterTweet extends AsyncTask<String, Integer, String> {

	@Override
	protected String doInBackground(String... params) {
		try {
			NewsActivity.twitter.updateStatus(params[0]);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
