package org.ryangray.postd;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class PostdMainActivity extends Activity {

	public static int						activities			= 1;
	public static ArrayList< PostdSource >	allSources			= new ArrayList< PostdSource >( );
	public static final String				PREFS_CHECK_BOOL	= "all_check_bool";

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.main_activity );

		SharedPreferences sharedPrefs = getSharedPreferences( PREFS_CHECK_BOOL, 0 );
		sharedPrefs = getSharedPreferences( PREFS_CHECK_BOOL, 0 );
		allSources.clear( );
		PostdSource.createSources( sharedPrefs, PostdMainActivity.this );
		try {
			Thread.sleep( 5000 );
		} catch ( InterruptedException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace( );
		}
		// Add code to print out the key hash
	    try {
	        PackageInfo info = getPackageManager().getPackageInfo(
	                "org.ryangray.postd", 
	                PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	            MessageDigest md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
	            }
	    } catch (NameNotFoundException e) {

	    } catch (NoSuchAlgorithmException e) {

	    }
		new PostdGetExternalDataBaseAsync< Object >( ).execute( PostdMainActivity.this );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		MenuInflater inflater = getMenuInflater( );
		inflater.inflate( R.menu.main_activity_menu, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		if ( item.getItemId( ) == R.id.exitOption ) {
			Intent intent = new Intent( Intent.ACTION_MAIN );
			intent.addCategory( Intent.CATEGORY_HOME );
			intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
			startActivity( intent );
		}
		return super.onOptionsItemSelected( item );
	}

	protected void onStop( ) {
		super.onStop( );

		SharedPreferences sharedPrefs = getSharedPreferences( PREFS_CHECK_BOOL, 0 );
		sharedPrefs = getSharedPreferences( PREFS_CHECK_BOOL, 0 );
		SharedPreferences.Editor editor = sharedPrefs.edit( );
		for ( PostdSource source: allSources ) {
			editor.putBoolean( source.getName( ).toString( ), source.getChecked( ) );
		}
		editor.commit( );
	}
}