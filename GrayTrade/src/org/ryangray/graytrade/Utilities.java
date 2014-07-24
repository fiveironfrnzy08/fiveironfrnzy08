package org.ryangray.graytrade;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Utilities {

	private static final String					username	= "ryanthomasgray@gmail.com";
	private static final String					password	= "rg1135244";
	private static final Map< String, String >	propsMap;

	static {

		propsMap = new HashMap< String, String >( );
		propsMap.put( "mail.smtp.auth", "true" );
		propsMap.put( "mail.smtp.starttls.enable", "true" );
		propsMap.put( "mail.smtp.host", "smtp.gmail.com" );
		propsMap.put( "mail.smtp.port", "587" );

	}

	public static void main( String[] args ) throws Exception {

	}

	public static Connection getDefaultCon( ) throws SQLException {

		Connection con = DriverManager.getConnection( Constants.SERVER + "/" + Constants.DATABASE, Constants.SERVER_USER, Constants.SERVER_PASS );
		return con;

	}

	public static void sendErrorEmail( Throwable exception ) throws Exception {

		Properties props = new Properties( );
		props.putAll( propsMap );

		Session session = Session.getInstance( props, new javax.mail.Authenticator( ) {
			protected PasswordAuthentication getPasswordAuthentication( ) {
				return new PasswordAuthentication( username, password );
			}
		} );

		Message message = new MimeMessage( session );
		message.setFrom( new InternetAddress( "ryanthomasgray@gmail.com" ) );
		message.setRecipients( Message.RecipientType.TO, InternetAddress.parse( getEmailRecipient( "error" ) ) );
		message.setSubject( Constants.PROGRAM_NAME + " exception thrown!" );
		message.setText( Utilities.getStackTrace( exception ).toString( ) );

		try {

			Transport.send( message );

		} catch ( Exception e ) {

			session = Session.getInstance( props, new javax.mail.Authenticator( ) {
				protected PasswordAuthentication getPasswordAuthentication( ) {
					return new PasswordAuthentication( "fiveironfrnzy08@gmail.com", "rg135244" );
				}
			} );

			sendErrorEmail( e );

		}

	}

	private static String getEmailRecipient( String type ) throws Exception {

		try ( Connection con = Utilities.getDefaultCon( ) ) {

			PreparedStatement statement = con.prepareStatement( "SELECT address FROM email WHERE type = '" + type + "';" );
			ResultSet result = statement.executeQuery( );

			if ( result.first( ) ) {
				return result.getString( 1 );
			} else {
				throw new Exception( "Unable to retrieve email address" );
			}

		} catch ( Exception e ) {
			e.printStackTrace( );
		}
		return null;

	}

	private static String getStackTrace( Throwable t ) {

		StringWriter sw = new StringWriter( );
		PrintWriter pw = new PrintWriter( sw, true );
		t.printStackTrace( pw );
		pw.flush( );
		pw.close( );

		return sw.toString( );

	}

	public static void outputToFile( String message ) throws IOException {
		outputToFile( message, "F:\\users\\ryan\\desktop\\airfare_debug.txt" );
	}

	public static void outputToFile( String message, String file ) throws IOException {

		FileWriter fw = new FileWriter( new File( file ) );
		fw.append( message );
		fw.close( );

	}

}
