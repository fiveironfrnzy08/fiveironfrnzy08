package org.ryangray.emailsender;

import java.awt.Component;

import javax.swing.JOptionPane;

public class AsyncDialog implements Runnable {
	
	private Component component;
	private String message;
	private String title;
	private Integer dialogType;
	
	public AsyncDialog( Component c, String m, String t, Integer d ) {
		
		component = c;
		message = m;
		title = t;
		dialogType = d;
		
	}

	@Override
	public void run( ) {
		
		JOptionPane.showMessageDialog( component, message, title, dialogType );
		
	}
	
}
