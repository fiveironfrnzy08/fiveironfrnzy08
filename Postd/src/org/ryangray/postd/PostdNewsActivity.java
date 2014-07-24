package org.ryangray.postd;

import static org.ryangray.postd.GCMUtilities.DISPLAY_MESSAGE_ACTION;
import static org.ryangray.postd.GCMUtilities.NOTIFICATION_ID;
import static org.ryangray.postd.GCMUtilities.SENDER_ID;

import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.openqa.selenium.Cookie;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.Authorization;
import twitter4j.auth.RequestToken;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class PostdNewsActivity extends ListActivity {

	public static final String		PREFS_CHECK_BOOL			= "all_check_bool";
	private ImageView				loadingPngIV				= null;
	private AnimationDrawable		loadingPng					= null;
	private final int				visible						= 0;
	private final int				invisible					= 1;
	private final int				gone						= 8;

	public static final String		TWITTER_ACCESS_TOKEN		= "259032327-YxrkIlFhhw1VDFkuekSY0V9mGV1FXdM8gLSvbBhX";
	public static final String		TWITTER_ACCESS_TOKEN_SECRET	= "kAYww0kCaOTSlo060Qjwy6B4gzWiRE0HW4LZq9w1k";
	public static final String		TWITTER_CONSUMER_KEY		= "z8lIBCMWtHFjSiFkDuRsw";
	public static final String		TWITTER_CONSUMER_SECRET		= "8qvOhGu7gTqdPxF5vaQrEOXU5R1nAtF8njTPpK7gpRE";
	public static final String		TWITTER_CALLBACK_URL		= "ryanthomasgray:///";
	public static String			twitterArticleURL;
	public static SharedPreferences	twitterPrefs;
	public static Twitter			twitter;
	public static RequestToken		twitterReqToken;

	private Bundle					bundle;
	private String[]				headlines;
	private String[]				descriptions;
	private String[]				pubDates;
	private int[]					imgIds;
	private String[]				urls;

	private Button					articleUpdate;
	private Button					articleUpdatePlaceholder;
	private LinearLayout			articleUpdateLayout;

	private TextView				rate;
	private TextView				postdAppUpdater;

	private Context					unregContext;
	private String					TAG							= "PostdNewsActivity";
	PackageManager					packageManager;

	int								orientation;

	AsyncTask< Void, Void, Void >	mRegisterTask;

	public void onCreate( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );
		setContentView( R.layout.news_activity );

		// Check to see if an exception was packaged with the intent indicating a failure
		if ( getIntent( ).getBundleExtra( "news_bundle" ).containsKey( "exception" ) ) {

			String error = getIntent( ).getBundleExtra( "news_bundle" ).getString( "exception" );
			LayoutInflater inflater = getLayoutInflater( );

			View toastView = inflater.inflate( R.layout.error_toast, ( ViewGroup ) findViewById( R.id.error_toast_viewgroup ) );
			TextView text = ( TextView ) toastView.findViewById( R.id.text );
			text.setText( "Bummer man.\nNot to be confused with Bomber Man.\nPost'd can't reach the internet!\nIs your 3G/4G or WiFi turned on?\n\n" + "Error: " + error.substring( 0, error.lastIndexOf( "\n" ) ) );

			Toast toastError = new Toast( this );
			toastError.setGravity( Gravity.CENTER, 0, 0 );
			toastError.setDuration( Toast.LENGTH_LONG );
			toastError.setView( toastView );
			toastError.show( );

			finish( );

			Intent intent = new Intent( Intent.ACTION_MAIN );
			intent.addCategory( Intent.CATEGORY_HOME );
			startActivity( intent );

			return;

		}

		// Unregister/Reregister Google Cloud Messaging for article updates
		// Intent gcmIntent = new Intent(PostdNewsActivity.this, GCMMain.class);
		// gcmIntent.putExtra("articleUpdatesTV", R.id.articleUpdates);
		// startActivity(gcmIntent);

		unregContext = getApplicationContext( );
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice( this );
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest( this );

		SharedPreferences gcmShared = getSharedPreferences( "com.google.android.gcm", MODE_PRIVATE );
		Editor gcmEditor = gcmShared.edit( );

		gcmEditor.clear( );
		System.out.println( "Contains gcm shared preferences: " + gcmShared.contains( "com.google.android.gcm" ) );

		gcmEditor.commit( );
		System.out.println( "Contains gcm shared preferences: " + gcmShared.contains( "com.google.android.gcm" ) );

		if ( !GCMRegistrar.isRegistered( this ) ) {
			register( );
		}

		ListView list = ( ListView ) findViewById( android.R.id.list );
		View footerView = ( ( LayoutInflater ) this.getSystemService( Context.LAYOUT_INFLATER_SERVICE ) ).inflate( R.layout.news_activity_footer, null, false );
		rate = ( TextView ) footerView.findViewById( R.id.rate );
		rate.setOnClickListener( new OnClickListener( ) {

			public void onClick( View v ) {
				PostdRater.showRateDialog( PostdNewsActivity.this, null );
			}

		} );

		postdAppUpdater = ( TextView ) footerView.findViewById( R.id.updates );
		postdAppUpdater.setOnClickListener( new OnClickListener( ) {

			public void onClick( View v ) {

				Intent myIntent = new Intent( );
				myIntent.setAction( Intent.ACTION_VIEW );
				Uri uri = Uri.parse( "market://details?id=org.ryangray.postd" );
				myIntent.setData( uri );
				startActivity( myIntent );

			}

		} );

		list.addFooterView( footerView );

		articleUpdateLayout = ( LinearLayout ) findViewById( R.id.articleUpdatesLayout );
		articleUpdatePlaceholder = ( Button ) findViewById( R.id.articleUpdatesPlaceholder );
		articleUpdate = ( Button ) findViewById( R.id.articleUpdates );
		articleUpdate.setOnClickListener( new OnClickListener( ) {

			public void onClick( View v ) {

				loadingPngIV.setVisibility( visible );
				loadingPng.start( );
				new PostdGetExternalDataBaseAsync< Object >( ).execute( PostdNewsActivity.this );
				articleUpdateLayout.setVisibility( gone );
				articleUpdatePlaceholder.setVisibility( gone );

				// Remove notification
				String ns = Context.NOTIFICATION_SERVICE;
				NotificationManager nMgr = ( NotificationManager ) PostdNewsActivity.this.getSystemService( ns );
				nMgr.cancel( NOTIFICATION_ID );

			}

		} );

		setListAdapter( null );

		loadingPngIV = ( ImageView ) findViewById( R.id.loadingImageView );
		loadingPngIV.setBackgroundResource( R.drawable.loading_png );
		loadingPng = ( AnimationDrawable ) loadingPngIV.getBackground( );
		loadingPngIV.setVisibility( gone );

		// TWITTER
		twitterPrefs = getSharedPreferences( "twitterPrefs", MODE_PRIVATE );
		twitter = new TwitterFactory( ).getInstance( );
		twitter.setOAuthConsumer( TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET );

		bundle = getIntent( ).getBundleExtra( "news_bundle" );
		headlines = bundle.getStringArray( "news_headlines" );
		descriptions = bundle.getStringArray( "news_descriptions" );
		pubDates = bundle.getStringArray( "news_pubDates" );
		imgIds = bundle.getIntArray( "news_imgIds" );
		urls = bundle.getStringArray( "news_urls" );
		headlines = bundle.getStringArray( "news_headlines" );

		setListAdapter( new NewsAdapter( this, android.R.layout.simple_list_item_1, R.id.headlineTextView, headlines ) );

	}

	private void register( ) {

		registerReceiver( mHandleMessageReceiver, new IntentFilter( DISPLAY_MESSAGE_ACTION ) );
		final String regId = GCMRegistrar.getRegistrationId( this );

		if ( regId.equals( "" ) ) {

			// Automatically registers application on startup.
			GCMRegistrar.register( this, SENDER_ID );

		} else {

			// Device is already registered on GCM, check server.
			if ( GCMRegistrar.isRegisteredOnServer( this ) ) {
				// Skips registration.
				Log.v( TAG, getString( R.string.already_registered ) );

			} else {

				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;

				mRegisterTask = new AsyncTask< Void, Void, Void >( ) {

					@Override
					protected Void doInBackground( Void... params ) {

						boolean registered = GCMServerUtilities.register( context, regId );
						// At this point all attempts to register with the app
						// server failed, so we need to unregister the device
						// from GCM - the app will try to register again when
						// it is restarted. Note that GCM will send an
						// unregistered callback upon completion, but
						// GCMIntentService.onUnregistered() will ignore it.
						if ( !registered ) {
							GCMRegistrar.unregister( context );
						}

						return null;

					}

					@Override
					protected void onPostExecute( Void result ) {
						mRegisterTask = null;
					}

				};

				mRegisterTask.execute( null, null, null );

			}

		}

	}

	@Override
	protected void onDestroy( ) {

		if ( mRegisterTask != null ) {
			mRegisterTask.cancel( true );
		}

		try {
			unregisterReceiver( mHandleMessageReceiver );
		} catch ( IllegalArgumentException e ) {

			System.out.print( "No receiver registered" );
			e.printStackTrace( );

		}

		GCMRegistrar.onDestroy( unregContext );
		super.onDestroy( );

	}

	private final BroadcastReceiver	mHandleMessageReceiver	= new BroadcastReceiver( ) {

																@Override
																public void onReceive( Context context, Intent intent ) {

																	Log.v( TAG, "Message received in PostdNewsActivity" );
																	Set< String > keys = intent.getExtras( ).keySet( );
																	String newMessage = null;

																	for ( String string: keys ) {

																		newMessage = intent.getExtras( ).getString( string );

																		if ( intent.getExtras( ).getString( string ).equals( "New Articles Available!" ) ) {
																			updatesAvailable( newMessage );
																		}

																		Log.d( "GCMDebug", "Key: " + string + " --- Message: " + newMessage );

																	}

																}

																private void updatesAvailable( String newMessage ) {

																	articleUpdateLayout.setVisibility( visible );
																	articleUpdatePlaceholder.setVisibility( invisible );
																	articleUpdate.setText( newMessage );
																	articleUpdatePlaceholder.setText( newMessage );

																}

															};

	public void onActivityResult( int requestCode, int resultCode, Intent data ) {

		super.onActivityResult( requestCode, resultCode, data );
		Session.getActiveSession( ).onActivityResult( this, requestCode, resultCode, data );

	}

	private class NewsAdapter extends ArrayAdapter< String > {

		public NewsAdapter( Context context, int resource, int textViewResourceId, String[] headlines ) {
			super( context, resource, textViewResourceId, headlines );
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {

			LayoutInflater inflater = ( LayoutInflater ) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			View row = inflater.inflate( R.layout.list_item, parent, false );

			// Create view items
			ImageView icon = ( ImageView ) row.findViewById( R.id.headlineSourceImage );
			ImageView fshare = ( ImageView ) row.findViewById( R.id.fshare );
			ImageView tweet = ( ImageView ) row.findViewById( R.id.tweet );
			TextView headline = ( TextView ) row.findViewById( R.id.headlineTextView );
			TextView description = ( TextView ) row.findViewById( R.id.descriptionTextView );
			TextView pubDate = ( TextView ) row.findViewById( R.id.pubDateTextView );
			LinearLayout contentContainer = ( LinearLayout ) row.findViewById( R.id.contentContainer );
			LinearLayout itemContainer = ( LinearLayout ) row.findViewById( R.id.itemContainer );

			// Set item values
			icon.setImageResource( imgIds[ position ] );
			headline.setText( headlines[ position ] );
			pubDate.setText( pubDates[ position ] );
			description.setText( descriptions[ position ] );

			if ( description.getText( ).toString( ).trim( ).equals( "" ) ) {

				String defaultDescription = "<a href\"" + urls[ position ] + "\">View the whole story</a>";
				description.setText( Html.fromHtml( defaultDescription ) );

			} else {
				description.append( "\n...GO TO FULL STORY" );
			}

			// Set onClickListeners
			itemContainer.setOnClickListener( new OnItemClickListener( urls, position, this.getContext( ), contentContainer, headline ) );
			description.setOnClickListener( new OnItemClickListener( urls, position, this.getContext( ), contentContainer, headline ) );
			fshare.setOnClickListener( new OnItemClickListener( urls, position, this.getContext( ), contentContainer, headline ) );
			tweet.setOnClickListener( new OnItemClickListener( urls, position, this.getContext( ), contentContainer, headline ) );

			return row;
		}

		private class OnItemClickListener implements OnClickListener {

			private int				mPosition;
			private String[]		mUrls;
			private LinearLayout	mContentContainer;
			private TextView		mHeadlineTextView;

			OnItemClickListener( String[] urls, int position, Context context, LinearLayout contentContainer, TextView headlineTextView ) {

				mPosition = position;
				mUrls = urls;
				mContentContainer = contentContainer;
				mHeadlineTextView = headlineTextView;

			}

			public void onClick( View arg0 ) {

				if ( arg0.getId( ) == R.id.descriptionTextView ) {

					Intent webIntent = new Intent( );
					webIntent.setAction( Intent.ACTION_VIEW );
					Uri uri = Uri.parse( mUrls[ mPosition ] );
					webIntent.setData( uri );
					startActivity( webIntent );

				} else if ( arg0.getId( ) == R.id.itemContainer ) {

					if ( mContentContainer.getVisibility( ) == gone ) {

						mContentContainer.setVisibility( visible );
						mHeadlineTextView.setCompoundDrawablesWithIntrinsicBounds( null, null, null, getResources( ).getDrawable( R.drawable.up_arrow ) );

					} else if ( mContentContainer.getVisibility( ) == visible ) {

						mContentContainer.setVisibility( gone );
						mHeadlineTextView.setCompoundDrawablesWithIntrinsicBounds( null, null, null, getResources( ).getDrawable( R.drawable.down_arrow ) );

					}
				} else if ( arg0.getId( ) == R.id.fshare ) {

					final String link = mUrls[ mPosition ];
					Bundle params = new Bundle( );
					params.putString( "link", link );

					facebookLogin( );

					WebDialog feedDialog = ( new WebDialog.FeedDialogBuilder( PostdNewsActivity.this, Session.getActiveSession( ), params ) ).setOnCompleteListener( new OnCompleteListener( ) {

						public void onComplete( Bundle values, FacebookException error ) {

							if ( values.containsKey( "post_id" ) ) {

								Toast.makeText( getApplicationContext( ), "Way to spread the news!", Toast.LENGTH_LONG ).show( );
								Log.v( "Facebook", values.toString( ) );

							} else if ( error.getClass( ) == FacebookOperationCanceledException.class || values.size( ) == 0 ) {

								Log.v( "Facebook", "Post cancelled." );

							} else {

								Toast.makeText( getApplicationContext( ), "Bad luck... Something went wrong, your post didn't quite make it.", Toast.LENGTH_LONG ).show( );
								Log.e( "Facebook", error.toString( ) );

							}

						}

					} ).build( );

					feedDialog.show( );

				} else if ( arg0.getId( ) == R.id.tweet ) {

					twitterArticleURL = null;
					TwitterURLShortenerAsync twitUrl = ( TwitterURLShortenerAsync ) new TwitterURLShortenerAsync( ).execute( mUrls[ mPosition ] );

					try {
						twitterArticleURL = twitUrl.get( );
					} catch ( InterruptedException e1 ) {
						e1.printStackTrace( );
					} catch ( ExecutionException e1 ) {
						e1.printStackTrace( );
					}
					
					try {
						
						Twitter twitter = new TwitterFactory( ).getInstance( );

						twitter.setOAuthConsumer( TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET );
						AccessToken accessToken = new AccessToken( TWITTER_ACCESS_TOKEN, TWITTER_ACCESS_TOKEN_SECRET );
						twitter.setOAuthAccessToken( accessToken );

						twitter.updateStatus( "Post using Twitter4J Again" );

						System.out.println( "Successfully updated the status in Twitter." );
						
					} catch ( TwitterException te ) {
						te.printStackTrace( );
					}

//					if ( !twitterPrefs.contains( TWITTER_ACCESS_TOKEN ) ) {
//
//						try {
//
//							Authorization test = twitter.getAuthorization( );
//							expireTwitterCookie( );
//							test.toString( );
//							AccessToken atoken = twitter.getOAuthAccessToken( );
//							String token = atoken.getToken( );
//							String secret = atoken.getTokenSecret( );
//							Editor editor = PostdNewsActivity.twitterPrefs.edit( );
//							editor.putString( PostdNewsActivity.TWITTER_ACCESS_TOKEN, token );
//							editor.putString( PostdNewsActivity.TWITTER_ACCESS_TOKEN_SECRET, secret );
//							editor.commit( );
//
//						} catch ( Exception e ) {
//							e.printStackTrace( );
//						}
//
//					}
//
//					try {
//
//						twitter.getAuthorization( );
//						loginAuthorisedUser( );
//
//					} catch ( Exception e ) {
//						loginNewUser( );
//					}

//					if ( twitterPrefs.contains( TWITTER_ACCESS_TOKEN ) ) {
						startActivity( new Intent( PostdNewsActivity.this, TwitterFrag.class ) );
//					}

				}

			}

//			private void expireTwitterCookie( ) {
//
//				CookieSyncManager.createInstance( PostdNewsActivity.this );
//				CookieManager cookieManager = CookieManager.getInstance( );
//				String sCookie = cookieManager.getCookie( "twitter.com" );
//				String cName = sCookie.substring( 0, sCookie.indexOf( "=" ) );
//				String cValue = sCookie.substring( sCookie.indexOf( "=" ) + 1, sCookie.length( ) );
//				Calendar cal = Calendar.getInstance( );
//				cal.set( 2000, 1, 1 );
//				Cookie cookie = new Cookie( cName, cValue, "twitter.com", null, cal.getTime( ) );
//				cookieManager.setCookie( "http://twitter.com", cookie.toString( ) );
//				twitter.setOAuthAccessToken( null );
//				twitter.shutdown( );
//
//			}
//
//			private void loginNewUser( ) {
//
//				Intent twitter_intent = new Intent( PostdNewsActivity.this, TwitterWebView.class );
//				startActivity( twitter_intent );
//
//			}
//
//			private void loginAuthorisedUser( ) {
//
//				String token = twitterPrefs.getString( TWITTER_ACCESS_TOKEN, null );
//				String secret = twitterPrefs.getString( TWITTER_ACCESS_TOKEN_SECRET, null );
//
//				// Create the twitter access token from the credentials we got
//				// previously
//				AccessToken at = new AccessToken( token, secret );
//
//				twitter.setOAuthAccessToken( at );
//
//			}

		}
	}

	private void facebookLogin( ) {

		// start Facebook Login
		Session.openActiveSession( this, true, new Session.StatusCallback( ) {

			// callback when session changes state
			public void call( Session session, SessionState state, Exception exception ) {

				// if ( session.isOpened( ) ) {
				//
				// }

			}

		} );

	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		MenuInflater inflater = getMenuInflater( );
		inflater.inflate( R.menu.news_menu, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {

		if ( item.getItemId( ) == R.id.refreshOption ) {

			loadingPngIV.setVisibility( visible );
			loadingPng.start( );
			new PostdGetExternalDataBaseAsync< Object >( ).execute( PostdNewsActivity.this );
			PostdMainActivity.activities++;

		}

		if ( item.getItemId( ) == R.id.setPrefsOption ) {

			startActivity( new Intent( PostdNewsActivity.this, PostdSelectSources.class ) );
			PostdMainActivity.activities++;

		}

		if ( item.getItemId( ) == R.id.exitOption ) {

			Intent intent = new Intent( Intent.ACTION_MAIN );
			intent.addCategory( Intent.CATEGORY_HOME );
			intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
			startActivity( intent );

		}

		return super.onOptionsItemSelected( item );

	}

	@Override
	public void onBackPressed( ) {

		if ( PostdMainActivity.activities == 1 ) {

			Intent intent = new Intent( Intent.ACTION_MAIN );
			intent.addCategory( Intent.CATEGORY_HOME );
			startActivity( intent );

		} else {
			super.onBackPressed( );
		}

		return;

	}

}
