package org.ryangray.twittertest;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class MainActivity extends ActionBarActivity {
	
	public static final String		TWITTER_ACCESS_TOKEN		= "903316099-bGK8Lnlk0ZuPjj1UzajXRSzmC6ts1zGZ0ur0DUqp";
	public static final String		TWITTER_ACCESS_TOKEN_SECRET	= "sVhDBKlGTuGEb5BpjZ29pAzPfjSWoVPBym3NfelToBP1U";
	public static final String		TWITTER_CONSUMER_KEY		= "9z7pfZTFXJAxRJtDYAu6XjRSp";
	public static final String		TWITTER_CONSUMER_SECRET		= "pnGtEjRRQbVHrhUV3ultnwkcIJEztnYY4F0fAz7iLHjPHEnnD3";
	public static final String		TWITTER_CALLBACK_URL		= "ryanthomasgray:///";

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		
		Need to wrap in AsyncTask
		Twitter twitter = new TwitterFactory( ).getInstance( );

		twitter.setOAuthConsumer( TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET );
		AccessToken accessToken = new AccessToken( TWITTER_ACCESS_TOKEN, TWITTER_ACCESS_TOKEN_SECRET );
		twitter.setOAuthAccessToken( accessToken );

//			twitter.updateStatus( "Post using Twitter4J Again" );

		try {
			System.out.println( "Successfully updated the status in Twitter. " + twitter.getScreenName( ) );
		} catch ( IllegalStateException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( TwitterException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		if ( savedInstanceState == null ) {
//			getSupportFragmentManager( ).beginTransaction( ).add( R.id.container, new PlaceholderFragment( ) ).commit( );
//		}
	}

//	@Override
//	public boolean onCreateOptionsMenu( Menu menu ) {
//
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater( ).inflate( R.menu.main, menu );
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected( MenuItem item ) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId( );
//		if ( id == R.id.action_settings ) {
//			return true;
//		}
//		return super.onOptionsItemSelected( item );
//	}
//
//	/**
//	 * A placeholder fragment containing a simple view.
//	 */
//	public static class PlaceholderFragment extends Fragment {
//
//		public PlaceholderFragment( ) {
//		}
//
//		@Override
//		public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
//			View rootView = inflater.inflate( R.layout.fragment_main, container, false );
//			return rootView;
//		}
//	}

}
