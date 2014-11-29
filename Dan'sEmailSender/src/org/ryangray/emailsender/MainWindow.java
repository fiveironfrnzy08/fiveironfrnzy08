package org.ryangray.emailsender;

import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

public class MainWindow {

	public static JFrame		frmDansEmailDistribution;
	public static JTextField	usernameEdit;
	public static JTextField	passwordEdit;
	public static File			toAddressesFile;
	public static File[]		attachments;
	public static JPanel		toAddressFileChosenPanel;
	public static JTextField		toAddressFileChosenEdit;
	public static JPanel		attachmentsChosenPanel;
	public static JTextPane		attachmentsFileListText;
	public static JPanel		greetingPanel;
	public static JTextField	subjectEdit;
	public static JTextField	greetingEdit;
	public static JTextPane	bodyEdit;

	public static boolean		debug			= true;
	public static boolean		personalize		= true;
	public static String		debugUsername	= "fiveironfrnzy08@gmail.com";
	public static String		debugPassword	= "rg135244";
	public static String		debugGreeting	= "Hello";
	public static String		debugSubject	= "Subject Line";
	public static String		debugBody		= "\tThis is a debugging body. \n\nThank you,\nRyan";
	public static File			debugTo			= new File( "C:\\Users\\rgray\\Desktop\\CSV Personalize.csv" );

	public static void main( String[] args ) {
		SwingUtilities.invokeLater( new Runnable( ) {
			public void run( ) {
				// Turn off metal's use of bold fonts
				try {
					UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName( ) );
				} catch ( Exception e ) {
					e.printStackTrace( );
				}
				createAndShowGUI( );
			}
		} );

	}

	private static void createAndShowGUI( ) {

		frmDansEmailDistribution = new JFrame( "FileChooser" );

		frmDansEmailDistribution.setTitle( "Dan's Email Distribution" );
		frmDansEmailDistribution.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frmDansEmailDistribution.getContentPane( ).setLayout( new BoxLayout( frmDansEmailDistribution.getContentPane( ), BoxLayout.Y_AXIS ) );

		frmDansEmailDistribution.getContentPane( ).add( getUsernamePanel( ) );
		frmDansEmailDistribution.getContentPane( ).add( getPasswordPanel( ) );
		frmDansEmailDistribution.getContentPane( ).add( getToAddressesPanel( ) );
		frmDansEmailDistribution.getContentPane( ).add( getToAddressFileChosenPanel( ) );
		frmDansEmailDistribution.getContentPane( ).add( getPersonalizePanel( ) );
		frmDansEmailDistribution.getContentPane( ).add( getSubjectPanel( ) );
		frmDansEmailDistribution.getContentPane( ).add( getGreetingPanel( ) );
		frmDansEmailDistribution.getContentPane( ).add( getBodyPanel( ) );
		frmDansEmailDistribution.getContentPane( ).add( getAttachmentsPanel( ) );
		frmDansEmailDistribution.getContentPane( ).add( getAttachmentsChosenPanel( ) );
		frmDansEmailDistribution.getContentPane( ).add( getSendPanel( ) );

		frmDansEmailDistribution.pack( );
		frmDansEmailDistribution.setVisible( true );

	}

	private static void setDefaultTabComponentProperties( Object object, String objectClass, String debugText ) {

		Component component = ( ( Component ) object );

		component.setFocusable( true );
		component.setFocusTraversalKeysEnabled( true );

		Set< AWTKeyStroke > forwardTab = new java.util.HashSet< AWTKeyStroke >( );
		forwardTab.add( KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_TAB, 0 ) );
		component.setFocusTraversalKeys( KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardTab );

		Set< AWTKeyStroke > backTab = new java.util.HashSet< AWTKeyStroke >( );
		backTab.add( KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_TAB, java.awt.event.InputEvent.SHIFT_MASK ) );
		component.setFocusTraversalKeys( KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backTab );

		if ( debug ) {
			try {
				( ( JEditorPane ) object ).setText( debugText );
			} catch ( Exception e ) {
				// TODO: handle exception
			}
		}

	}

	private static void setDefaultTextComponentProperties( JTextComponent textArea, String text ) {

		textArea.setEditable( false );
		textArea.setText( text );

	}

	private static List< String > parseToAddresses( File file ) throws IOException {

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		List< String > addressList = new ArrayList< String >( );
		FileWriter fw = new FileWriter( new File( file.getPath( ) + " Errored Addresses.csv" ) );

		String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Pattern pattern = Pattern.compile( emailPattern );
		Matcher matcher;

		try {

			br = new BufferedReader( new FileReader( file ) );

			while ( ( line = br.readLine( ) ) != null ) {

				String[] addresses = line.split( cvsSplitBy );
				String address = addresses[ 0 ];
				matcher = pattern.matcher( address.trim( ) );
				if ( matcher.matches( ) ) {
					addressList.add( line );
				} else {
					fw.write( address + "\n" );
				}

			}

		} catch ( FileNotFoundException e ) {
			e.printStackTrace( );
		} catch ( IOException e ) {
			e.printStackTrace( );
		} finally {
			if ( br != null || fw != null ) {
				try {
					br.close( );
					fw.close( );
				} catch ( IOException e ) {
					e.printStackTrace( );
				}
			}
		}

		System.out.println( "Done" );
		return addressList;

	}

	private static JPanel getUsernamePanel( ) {

		JPanel usernamePanel = new JPanel( );
		JTextPane usernameText = new JTextPane( );
		usernameEdit = new JTextField( );
		
//		usernamePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		setDefaultTextComponentProperties( usernameText, "Username/Email" );

		setDefaultTabComponentProperties( usernameEdit, usernameEdit.getClass( ).getSimpleName( ), debugUsername );
		usernameEdit.setPreferredSize( new Dimension( 200, 20 ) );

		usernamePanel.add( usernameText );
		usernamePanel.add( usernameEdit );
		return usernamePanel;

	}

	private static JPanel getPasswordPanel( ) {

		JPanel passwordPanel = new JPanel( );
		JTextPane passwordText = new JTextPane( );
		passwordEdit = new JTextField( );
		
//		passwordPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		setDefaultTextComponentProperties( passwordText, "Password" );

		setDefaultTabComponentProperties( passwordEdit, passwordEdit.getClass( ).getSimpleName( ), debugPassword );
		passwordEdit.setPreferredSize( new Dimension( 200, 20 ) );

		passwordPanel.add( passwordText );
		passwordPanel.add( passwordEdit );
		return passwordPanel;

	}

	private static JPanel getToAddressesPanel( ) {

		JPanel toAddressesPanel = new JPanel( );
		JTextPane toAddressesText = new JTextPane( );
		JButton toAddressButton = new JButton( );
		
//		toAddressesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		setDefaultTabComponentProperties( toAddressButton, toAddressButton.getClass( ).getSimpleName( ), null );
		toAddressButton.setName( "toAddressButton" );
		FileChooser toAddressFileChooser = new FileChooser( toAddressButton );

		setDefaultTextComponentProperties( toAddressesText, "To Addresses:" );

		toAddressesPanel.add( toAddressesText );
		toAddressesPanel.add( toAddressFileChooser );
		if ( debug ) {
			toAddressesFile = debugTo;
		}
		return toAddressesPanel;

	}

	private static JPanel getToAddressFileChosenPanel( ) {

		toAddressFileChosenPanel = new JPanel( );
		JTextPane toAddressFileChosenText = new JTextPane( );
		toAddressFileChosenEdit = new JTextField( );

		toAddressFileChosenPanel.setVisible( false );
//		toAddressFileChosenPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		toAddressFileChosenText.setText( "File Chosen:" );
		toAddressFileChosenText.setEditable( false );

		toAddressFileChosenEdit.setEditable( false );

		toAddressFileChosenPanel.add( toAddressFileChosenText );
		toAddressFileChosenPanel.add( toAddressFileChosenEdit );
		if ( debug ) {
			toAddressFileChosenEdit.setText( toAddressesFile.getName( ) );
			toAddressFileChosenPanel.setVisible( true );
		}
		return toAddressFileChosenPanel;

	}

	private static JPanel getPersonalizePanel( ) {

		JPanel personalizePanel = new JPanel( );
		JTextPane personalizeText = new JTextPane( );
		final JCheckBox personalizeCheckBox = new JCheckBox( "" );
		
//		personalizePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		setDefaultTextComponentProperties( personalizeText, "Personalized Emails (Spreadsheet must include names)" );

		setDefaultTabComponentProperties( personalizeCheckBox, personalizeCheckBox.getClass( ).getSimpleName( ), null );
		personalizeCheckBox.setSelected( true );
		personalizeCheckBox.addChangeListener( new ChangeListener( ) {

			@Override
			public void stateChanged( ChangeEvent change ) {
				if ( personalizeCheckBox.isSelected( ) ) {
					greetingPanel.setVisible( true );
					personalize = true;
				} else {
					greetingPanel.setVisible( false );
					personalize = false;
				}
				frmDansEmailDistribution.pack( );
			}
		} );

		personalizePanel.add( personalizeText );
		personalizePanel.add( personalizeCheckBox );
		return personalizePanel;

	}

	private static JPanel getSubjectPanel( ) {

		JPanel subjectPanel = new JPanel( );
		JTextPane subjectText = new JTextPane( );
		subjectEdit = new JTextField( );
		
//		subjectPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		subjectPanel.setMinimumSize( new Dimension( 300, 300 ) );
		subjectPanel.setLayout( new FlowLayout( FlowLayout.CENTER, 5, 5 ) );

		setDefaultTextComponentProperties( subjectText, "Subject" );

		setDefaultTabComponentProperties( subjectEdit, subjectEdit.getClass( ).getSimpleName( ), debugSubject );
		subjectEdit.setPreferredSize( new Dimension( 400, 40 ) );
		subjectEdit.setMinimumSize( new Dimension( 250, 250 ) );

		subjectPanel.add( subjectText );
		subjectPanel.add( subjectEdit );
		return subjectPanel;

	}

	private static JPanel getGreetingPanel( ) {

		greetingPanel = new JPanel( );
		JTextPane greetingText = new JTextPane( );
		greetingEdit = new JTextField( );
		
//		greetingPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		setDefaultTextComponentProperties( greetingText, "Greeting" );

		setDefaultTabComponentProperties( greetingEdit, greetingEdit.getClass( ).getSimpleName( ), debugGreeting );
		greetingEdit.setPreferredSize( new Dimension( 200, 20 ) );

		greetingPanel.add( greetingText );
		greetingPanel.add( greetingEdit );
		return greetingPanel;

	}

	private static JPanel getBodyPanel( ) {

		JPanel bodyPanel = new JPanel( );
		JTextPane bodyText = new JTextPane( );
		JTextPane bodyEdit = new JTextPane( );
		
//		bodyPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		bodyPanel.setMinimumSize( new Dimension( 300, 300 ) );
		bodyPanel.setLayout( new FlowLayout( FlowLayout.CENTER, 5, 5 ) );

		setDefaultTextComponentProperties( bodyText, "Body" );

		if ( debug ) {
			bodyEdit.setText( debugBody );
		}
		bodyEdit.setPreferredSize( new Dimension( 400, 300 ) );
		bodyEdit.setMinimumSize( new Dimension( 250, 250 ) );

		bodyPanel.add( bodyText );
		bodyPanel.add( bodyEdit );
		return bodyPanel;

	}

	private static JPanel getAttachmentsPanel( ) {

		JPanel attachmentsPanel = new JPanel( );
		JTextPane attachmentsText = new JTextPane( );
		JButton attachmentButton = new JButton( );
		FileChooser attachmentsButtonFileChooser = new FileChooser( attachmentButton );
		
//		attachmentsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		attachmentsPanel.setMinimumSize( new Dimension( 300, 300 ) );
		attachmentsPanel.setLayout( new FlowLayout( FlowLayout.CENTER, 5, 5 ) );

		setDefaultTextComponentProperties( attachmentsText, "Add Attachments" );

		attachmentButton.setName( "attachmentsButton" );

		attachmentsPanel.add( attachmentsText );
		attachmentsPanel.add( attachmentsButtonFileChooser );
		return attachmentsPanel;

	}

	private static JPanel getAttachmentsChosenPanel( ) {

		attachmentsChosenPanel = new JPanel( );
		JTextPane attachmentsChosenText = new JTextPane( );
		attachmentsFileListText = new JTextPane( );
		
//		attachmentsChosenPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		attachmentsChosenPanel.setVisible( false );
		attachmentsChosenPanel.setMinimumSize( new Dimension( 300, 300 ) );
		attachmentsChosenPanel.setLayout( new FlowLayout( FlowLayout.CENTER, 5, 5 ) );

		setDefaultTextComponentProperties( attachmentsChosenText, "Files Chosen:" );

		attachmentsFileListText.setEditable( false );
		attachmentsFileListText.setInheritsPopupMenu( true );

		attachmentsChosenPanel.add( attachmentsChosenText );
		attachmentsChosenPanel.add( attachmentsFileListText );
		return attachmentsChosenPanel;

	}

	private static JPanel getSendPanel( ) {

		JPanel sendPanel = new JPanel( );
		final JButton sendButton = new JButton( "Send Emails!" );
		
//		sendPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		sendPanel.setMinimumSize( new Dimension( 300, 300 ) );

		sendButton.addMouseListener( new MouseAdapter( ) {

			@Override
			public void mouseClicked( MouseEvent arg0 ) {
				List< String > toAddresses;
				try {

					sendButton.setEnabled( false );

					if ( usernameEdit.getText( ).isEmpty( ) || passwordEdit.getText( ).isEmpty( ) || toAddressesFile == null || subjectEdit.getText( ).isEmpty( ) || bodyEdit.getText( ).isEmpty( ) ) {

						JOptionPane.showMessageDialog( ( Component ) frmDansEmailDistribution, "Please make sure you have a valid username, password, recipient file, subject, and body!", "Error Sending Email", JOptionPane.INFORMATION_MESSAGE );
						sendButton.setEnabled( true );
						return;

					}

					if ( personalize ) {
						if ( greetingEdit.getText( ).isEmpty( ) ) {

							JOptionPane.showMessageDialog( ( Component ) frmDansEmailDistribution, "Please make sure you have a valid username, password, recipient file, subject, and body!", "Error Sending Email", JOptionPane.INFORMATION_MESSAGE );
							sendButton.setEnabled( true );
							return;

						}

					}

					toAddresses = parseToAddresses( toAddressesFile );

					System.out.println( toAddresses );
					List< Message > messages = SendEmail.buildEmail( usernameEdit.getText( ).trim( ), passwordEdit.getText( ).trim( ), toAddresses, subjectEdit.getText( ).trim( ), greetingEdit.getText( ).trim( ), bodyEdit.getText( ).trim( ), attachments );
					JOptionPane.showMessageDialog( ( Component ) frmDansEmailDistribution, "Email has " + toAddresses.size( ) + " recipients. Are you sure you would like to send this?", "Email Sending", JOptionPane.OK_CANCEL_OPTION );
					int action = JOptionPane.showOptionDialog( ( Component ) frmDansEmailDistribution, messages.get( 0 ).getContent( ).toString( ), "Email Preview - Is this OK?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null );
					if ( action != 0 ) {
						
						JOptionPane.showMessageDialog( ( Component ) frmDansEmailDistribution, "Email was aborted!", "Email Cancelled", JOptionPane.INFORMATION_MESSAGE );
					
					} else {
						
						SendEmail.sendEmail( messages );
						JOptionPane.showMessageDialog( ( Component ) frmDansEmailDistribution, "Email successfully sent!", "Email Sent", JOptionPane.INFORMATION_MESSAGE );

					}
					
				} catch ( Exception e ) {

					JOptionPane.showMessageDialog( ( Component ) frmDansEmailDistribution, "An error occurred, emails were NOT sent!\n\n" + e.getMessage( ), "Email NOT Sent", JOptionPane.ERROR_MESSAGE );
					e.printStackTrace( );

				} finally {

					sendButton.setEnabled( true );

				}

			}

		} );

		sendPanel.add( sendButton );
		return sendPanel;

	}

}
