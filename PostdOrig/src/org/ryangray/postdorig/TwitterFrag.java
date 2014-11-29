package org.ryangray.postdorig;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.LinearLayout;

public class TwitterFrag extends FragmentActivity {
	public static Object lock = new Object();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LinearLayout lview = new LinearLayout(this);
		setContentView(lview); 
	    @SuppressWarnings("unused")
		int mStackLevel = 0;
		mStackLevel++;
	    // DialogFragment.show() will take care of adding the fragment
	    // in a transaction.  We also want to remove any currently showing
	    // dialog, so make our own transaction and take care of that here.
	    final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	    Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);
	    // Create and show the dialog.
	    final DialogFragment newFragment = new TwitterFragment();	
	    
	    Log.i("RyanDebug", "Launching...");
	    
	    new FragAsync().execute(newFragment, ft);    
	    
	}
	
	private class FragAsync extends AsyncTask<Object, Integer, Integer> {
		@Override
		protected Integer doInBackground(Object... params) {
			Log.i("RyanDebug", "Starting Async");
			
			DialogFragment newFragment = (DialogFragment) params[0];
			FragmentTransaction ft = (FragmentTransaction) params[1];
			newFragment.show(ft, "dialog");
			synchronized (lock) {
		    	try {
					lock.wait();
					Log.i("RyanDebug", "Waiting");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Log.i("RyanDebug", "Continuing");
			finish();
			return null;
		}    	
    }
	
}


