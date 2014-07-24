package org.ryangray.postd;
import android.os.AsyncTask;

import com.rosaloves.bitlyj.Url;
import static com.rosaloves.bitlyj.Bitly.*;
public class TwitterURLShortenerAsync extends AsyncTask<String, Integer, String>{

	protected String doInBackground(String... params) {
		@SuppressWarnings("unused")
		String shortURL = null;
		Url shortUrl = as("fiveironfrnzy08", "R_8b8af6c64595ef69ccfad9bd8fe47423").call(shorten(params[0]));
		// url.getShortUrl() -> http://bit.ly/fB05
		return shortUrl.getShortUrl();
	}
	
}
