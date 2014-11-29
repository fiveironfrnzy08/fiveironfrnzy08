package org.ryangray.postdorig;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.openqa.selenium.Cookie;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.Authorization;
import twitter4j.auth.RequestToken;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
//import android.app.Fragment;
//import android.app.FragmentTransaction;

//@SuppressLint("NewApi")
public class NewsActivity extends ListActivity {
	
	public static final String PREFS_CHECK_BOOL = "all_check_bool";
	private ImageView loadingPngIV = null;
	private AnimationDrawable loadingPng = null;
	private final int visible = 0;
	private final int gone = 8;
	


	
	private Facebook facebook = null;
	@SuppressWarnings("unused")
	private AsyncFacebookRunner asyncRunner;
	
	public static final String FB_APP_ID = "444682848925661";
	public static final String TWITTER_ACCESS_TOKEN = "259032327-YxrkIlFhhw1VDFkuekSY0V9mGV1FXdM8gLSvbBhX";
	public static final String TWITTER_ACCESS_TOKEN_SECRET = "kAYww0kCaOTSlo060Qjwy6B4gzWiRE0HW4LZq9w1k";
	public static final String TWITTER_CONSUMER_KEY = "z8lIBCMWtHFjSiFkDuRsw";
	public static final String TWITTER_CONSUMER_SECRET = "8qvOhGu7gTqdPxF5vaQrEOXU5R1nAtF8njTPpK7gpRE"; // XXX Encode in your app
	public static final String TWITTER_CALLBACK_URL = "ryanthomasgray:///";
	public static String twitterArticleURL;
	public static SharedPreferences twitterPrefs;
	public static Twitter twitter;
	public static RequestToken twitterReqToken;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.news_activity);
	    
	    ListView list = (ListView) findViewById(android.R.id.list);
	    View footerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer,  null, false);
	    list.addFooterView(footerView);
	    
	    loadingPngIV = (ImageView) findViewById(R.id.loadingImageView);
        loadingPngIV.setBackgroundResource(R.drawable.loading_png);
        loadingPng = (AnimationDrawable) loadingPngIV.getBackground();
        loadingPngIV.setVisibility(gone);
        
//      TWITTER
// 		Create a new shared preference object to remember if the user has
// 		already given us permission
        twitterPrefs = getSharedPreferences("twitterPrefs", MODE_PRIVATE);
 		Log.i("RyanDebug", "Got Preferences");
// 		Load the twitter4j helper
 		twitter = new TwitterFactory().getInstance();
 		Log.i("RyanDebug", "Got Twitter4j");
// 		Tell twitter4j that we want to use it with our app
 		twitter.setOAuthConsumer(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET);
 		Log.i("RyanDebug", "Inflated Twitter4j");
	    
	    Bundle b = getIntent().getBundleExtra("news_bundle");
	    String[] headlines = b.getStringArray("news_headlines");
	    
	    setListAdapter(new MyAdapter(this,
	    		android.R.layout.simple_list_item_1, 
	    		R.id.headlineTextView,
	    		headlines));
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		facebook.authorizeCallback(requestCode, resultCode, data);
	}
	
	private class MyAdapter extends ArrayAdapter<String> {
		
		public MyAdapter(Context context, int resource, int textViewResourceId,
				String[] headlines) {
			super(context, resource, textViewResourceId, headlines);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = inflater.inflate(R.layout.list_item, parent, false);
			
			Bundle b = getIntent().getBundleExtra("news_bundle");
			String[] headlines = b.getStringArray("news_headlines");
			String[] descriptions = b.getStringArray("news_descriptions");
			String[] pubDates = b.getStringArray("news_pubDates");
			int[] imgIds = b.getIntArray("news_imgIds");
			String[] urls = b.getStringArray("news_urls");
			
//			Create view items
			ImageView icon = (ImageView) row.findViewById(R.id.headlineSourceImage);
			ImageView fshare = (ImageView) row.findViewById(R.id.fshare);
			ImageView tweet = (ImageView) row.findViewById(R.id.tweet);
			TextView headline = (TextView) row.findViewById(R.id.headlineTextView);
			TextView description = (TextView) row.findViewById(R.id.descriptionTextView);
			TextView pubDate = (TextView) row.findViewById(R.id.pubDateTextView);
			LinearLayout contentContainer = (LinearLayout) row.findViewById(R.id.contentContainer);
			LinearLayout itemContainer = (LinearLayout) row.findViewById(R.id.itemContainer);
			Log.i("ryanGray", headlines[position] + "\n" + String.valueOf(itemContainer) );
			Log.i("ryanGray", String.valueOf(R.id.itemContainer) );
			TextView headlineTextView = (TextView) row.findViewById(R.id.headlineTextView);
			
//			Set item values
			icon.setImageResource(imgIds[position]);
			headline.setText(headlines[position]);
			pubDate.setText(pubDates[position]);
			description.setText(descriptions[position]);
			if (description.getText().toString().trim().equals("")) {
				String defaultDescription = "<a href\"" + urls[position] + "\">View the whole story</a>";
				description.setText(Html.fromHtml(defaultDescription));
			} else {
				description.append("\n...GO TO FULL STORY");
			}
			
//			Set onClickListeners
			itemContainer.setOnClickListener(new OnItemClickListener(urls, position, this.getContext(), contentContainer, headlineTextView));
			description.setOnClickListener(new OnItemClickListener(urls, position, this.getContext(), contentContainer, headlineTextView));
			fshare.setOnClickListener(new OnItemClickListener(urls, position, this.getContext(), contentContainer, headlineTextView));
			tweet.setOnClickListener(new OnItemClickListener(urls, position, this.getContext(), contentContainer, headlineTextView));
			
			return row;
		}
		
		private class OnItemClickListener implements OnClickListener{       
		    private int mPosition;
		    private String[] mUrls;
		    private LinearLayout mContentContainer;
		    private TextView mHeadlineTextView;
		    OnItemClickListener(String[] urls, int position, Context context, LinearLayout contentContainer, TextView headlineTextView){
		        mPosition = position;
		        mUrls = urls;
		        mContentContainer = contentContainer;
		        mHeadlineTextView = headlineTextView;
		    }
		    public void onClick(View arg0) { 
		    	if (arg0.getId() == R.id.descriptionTextView) {
			        Intent myIntent = new Intent();
			        myIntent.setAction(Intent.ACTION_VIEW);
			        Uri uri = Uri.parse(mUrls[mPosition]);
			        myIntent.setData(uri);
			        startActivity(myIntent);
				} else if (arg0.getId() == R.id.itemContainer){
					Log.i("ryanGray", String.valueOf(R.id.itemContainer) );
					if (mContentContainer.getVisibility() == 8) {
						mContentContainer.setVisibility(0);
						mHeadlineTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.up_arrow));
					} else if (mContentContainer.getVisibility() == 0) {
						mContentContainer.setVisibility(8);
						mHeadlineTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.down_arrow));
					}
				} else if (arg0.getId() == R.id.fshare) {
					Log.i("RyanInfo", "FShare was clicked");
					String link = mUrls[mPosition]; 
					Bundle params = new Bundle();
					params.putString("link", link);
					facebook = new Facebook(FB_APP_ID);
					FBSessionStore.restore(facebook, NewsActivity.this);
					if (!facebook.isSessionValid()) {
						Log.d("RyanDebug","Session was invalid");
						facebook.authorize(NewsActivity.this, new DialogListener()
						{
							public void onComplete(Bundle values)
								{
									FBSessionStore.save(facebook, NewsActivity.this);
									String link = mUrls[mPosition]; 
									Bundle params = new Bundle();
									params.putString("link", link); 
									facebook.dialog(NewsActivity.this, "feed", params, new FBDialogListener());
								}

							public void onFacebookError(FacebookError error)
								{
								}

							public void onError(DialogError e)
								{
								}

							public void onCancel()
								{
								}
						});

						FBSessionEvents.addAuthListener(new FBAuthListener());
						asyncRunner = new AsyncFacebookRunner(facebook);
					} else {
						facebook.dialog(NewsActivity.this, "feed", params, new FBDialogListener());
					}
				} else if (arg0.getId() == R.id.tweet){
//					twitterArticleURL = TwitterURLShortener.shortenURL(mUrls[mPosition]);
					twitterArticleURL = null;
					TwitterURLShortenerAsync twitUrl = (TwitterURLShortenerAsync) new TwitterURLShortenerAsync().execute(mUrls[mPosition]);
					try {
						twitterArticleURL = twitUrl.get();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ExecutionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					if (!twitterPrefs.contains(TWITTER_ACCESS_TOKEN)) {
						try {
						      Authorization test = twitter.getAuthorization();
						      expireTwitterCookie();
						      test.toString();
						      AccessToken atoken = twitter.getOAuthAccessToken();
						      String token = atoken.getToken();
						      String secret = atoken.getTokenSecret();
						      Editor editor = NewsActivity.twitterPrefs.edit();
						      editor.putString(NewsActivity.TWITTER_ACCESS_TOKEN, token);
						      editor.putString(NewsActivity.TWITTER_ACCESS_TOKEN_SECRET, secret);
						      editor.commit();
					      } catch (Exception e) {
					    	  Log.i("RyanDebug", "Twitter is not authorized");
					    }
					}
					
					Log.i("RyanDebug", "Twitter authorization = " + twitter.getAuthorization().isEnabled());
					
					try {
						twitter.getAuthorization();
						Log.i("RyanDebug", "Repeat User");
						loginAuthorisedUser();
					} catch (Exception e) {
						Log.i("RyanDebug", "New User");
						loginNewUser();
					};				
					if (twitterPrefs.contains(TWITTER_ACCESS_TOKEN)) {
						Log.i("RyanDebug", "Authorized and launching tweet");
						startActivity(new Intent(NewsActivity.this, TwitterFrag.class));
					}
				}
		    }
			private void expireTwitterCookie() {
				CookieSyncManager.createInstance(NewsActivity.this);
				CookieManager cookieManager = CookieManager.getInstance();
				String sCookie = cookieManager.getCookie("twitter.com");
				Log.i("RyanDebug", "Cookie string = " + sCookie);
				String cName = sCookie.substring(0, sCookie.indexOf("="));
				String cValue = sCookie.substring(sCookie.indexOf("=") + 1, sCookie.length());
				Date date = new Date();
				Calendar cal = Calendar.getInstance();
				cal.set(2000, 1, 1);
				date = cal.getTime();
				Cookie cookie = new Cookie(cName, cValue, "twitter.com", null, date);
				Log.i("RyanDebug", "Cookie string = " + sCookie);
				cookieManager.setCookie("http://twitter.com", cookie.toString()); 
				twitter.setOAuthAccessToken(null);
				twitter.shutdown();
				Log.i("RyanDebug", "Twitter was authorized, but is no longer");
			}
			private void loginNewUser() {
				Log.i("RyanDebug", "Request App Authentication");
				Log.i("RyanDebug", "Starting Webview to login to twitter");
				Intent twitter_intent = new Intent(NewsActivity.this, TwitterWebView.class);
				
//				Context context = NewsActivity.this;
				startActivity(twitter_intent);
			}
			private void loginAuthorisedUser() {
				String token = twitterPrefs.getString(TWITTER_ACCESS_TOKEN, null);
				String secret = twitterPrefs.getString(TWITTER_ACCESS_TOKEN_SECRET, null);
				
				// Create the twitter access token from the credentials we got previously
				AccessToken at = new AccessToken(token, secret);

				twitter.setOAuthAccessToken(at);
			}
			
			
		}
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.news_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.refreshOption) {
			loadingPngIV.setVisibility(visible);
			loadingPng.start();
			GetHeadlines headline = new GetHeadlines();
			new GetFeedAsync<Object>().execute(NewsActivity.this, headline);
			MainActivity.activities++;
		}
		if(item.getItemId() == R.id.setPrefsOption) {
			startActivity(new Intent(NewsActivity.this, SelectSources.class));
			MainActivity.activities++;
		}
		if(item.getItemId() == R.id.exitOption) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		if (MainActivity.activities == 1) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else {
			super.onBackPressed();
		}
		
	    return;
	}
	
}
