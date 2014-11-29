package org.ryangray.onkyo.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String[][] command, AsyncCallback< String[] > callback)
			throws IllegalArgumentException;
}
