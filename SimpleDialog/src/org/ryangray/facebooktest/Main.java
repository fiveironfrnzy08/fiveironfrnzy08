package org.ryangray.facebooktest;

import org.ryangray.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.*;
import com.facebook.model.*;

public class Main extends Activity {

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.main );

		final Button button = ( Button ) findViewById( R.id.firstButton );
		button.setOnClickListener( new OnClickListener( ) {

			@Override
			public void onClick( View v ) {
				facebookLogin( );
			}

		} );

	}

	@Override
	public void onActivityResult( int requestCode, int resultCode, Intent data ) {
		super.onActivityResult( requestCode, resultCode, data );

		Session.getActiveSession( ).onActivityResult( this, requestCode, resultCode, data );

	}

	private void facebookLogin( ) {
		
		// start Facebook Login
		Session.openActiveSession( this, true, new Session.StatusCallback( ) {

			// callback when session changes state
			@Override
			public void call( Session session, SessionState state, Exception exception ) {

				if ( session.isOpened( ) ) {

					// make request to the /me API
					Request.newMeRequest( session, new Request.GraphUserCallback() {

					  // callback after Graph API response with user object
					  @Override
					  public void onCompleted(GraphUser user, Response response) {
						  
						  if ( user != null ) {
								Toast.makeText( getApplicationContext(), user.getName( ), Toast.LENGTH_LONG ).show( );
							}
						  
					  }
					  
					}).executeAsync( );
					
				}

			}

		} );
		
	}
}
