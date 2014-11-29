package org.ryangray.postdorig;

import org.ryangray.postdorig.FBSessionEvents.AuthListener;

import android.util.Log;

public class FBAuthListener implements AuthListener
{

	public void onAuthSucceed()
		{
			Log.d("RyanDebug","You have logged in! ");
		}

	public void onAuthFail(String error)
		{
		}
}