package org.ryangray.airfare;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Utilities {

	private static final String					username	= "ryanthomasgray@gmail.com";
	private static final String					password	= "rg1135244";
	private static final String					fromAddress	= username;
	private static final String					toAddress	= username;
	private static final Map< String, String >	propsMap;
	static {
		propsMap = new HashMap< String, String >( );
		propsMap.put( "mail.smtp.auth", "true" );
		propsMap.put( "mail.smtp.starttls.enable", "true" );
		propsMap.put( "mail.smtp.host", "smtp.gmail.com" );
		propsMap.put( "mail.smtp.port", "587" );
	}

	public static void main( String[] args ) throws Exception {

		Map< String, String > parameters = new LinkedHashMap< String, String >( );
		parameters.put( "FROM_AIRPORT_CODE", "MSP" );
		parameters.put( "TO_AIRPORT_CODE", "SJU" );
		parameters.put( "ROUTE", "roundtrip" );
		parameters.put( "DEPARTURE_DAY", "10" );
		parameters.put( "DEPARTURE_MONTH", "01" );
		parameters.put( "DEPARTURE_YEAR", "2014" );
		parameters.put( "RETURN_DAY", "19" );
		parameters.put( "RETURN_MONTH", "01" );
		parameters.put( "RETURN_YEAR", "2014" );
		parameters.put( "CHILDREN", "0" );
		parameters.put( "ADULTS", "1" );
		parameters.put( "SENIORS", "0" );
		parameters.put( "SORT_BY", "price" );

		// Create new flight record and return id
		int flightID = addFlightsToMonitorExpedia( parameters );

		// Create new email_recipient, then use the flightID to update the newly
		// created flight record with the email_recipient_id
		addEmailRecipientUpdateFlight( "fiveironfrnzy08@gmail.com", flightID );

		System.out.println( "Finished successfully" );

	}

	private static void addEmailRecipientUpdateFlight( String address, int flightID ) throws Exception {

		PreparedStatement statement = Utilities.getDefaultCon( ).prepareStatement( "INSERT INTO `" + Constants.DATABASE + "`.`email_recipient` ( address ) VALUES ( '" + address + "' );", PreparedStatement.RETURN_GENERATED_KEYS );
		System.out.println( statement.toString( ) );
		statement.executeUpdate( );
		ResultSet result = statement.getGeneratedKeys( );

		int emailRecipientID = 0;

		while ( result.next( ) ) {
			emailRecipientID = result.getInt( 1 );
		}

		statement = Utilities.getDefaultCon( ).prepareStatement( "UPDATE `" + Constants.DATABASE + "`.`flight` SET email_recipient_id = " + emailRecipientID + " WHERE id = " + flightID + ";" );
		System.out.println( statement.toString( ) );

		int emailResult = statement.executeUpdate( );

		if ( emailResult < 1 ) {
			throw new Exception( "Failed to update flight record with email_recipient_id" );
		}
		statement.close( );

	}

	public static void sendMail( String price, String airline, String url, File... files ) throws AddressException, MessagingException {

		Properties props = new Properties( );
		props.putAll( propsMap );

		Session session = Session.getInstance( props, new javax.mail.Authenticator( ) {

			protected PasswordAuthentication getPasswordAuthentication( ) {
				return new PasswordAuthentication( username, password );
			}

		} );

		Message message = new MimeMessage( session );
		message.setFrom( new InternetAddress( fromAddress ) );
		message.setRecipients( Message.RecipientType.TO, InternetAddress.parse( toAddress ) );
		message.setSubject( "Flight Alert" );
		MimeBodyPart mbp1 = new MimeBodyPart( );
		mbp1.setText( airline + " has a ticket at $" + price );
		message = addAttachments( message, files );

		Transport.send( message );

	}

	public static void sendMail( String subject, String text, int emailRecipientId ) throws Exception {

		Properties props = new Properties( );
		props.putAll( propsMap );

		Session session = Session.getInstance( props, new javax.mail.Authenticator( ) {
			protected PasswordAuthentication getPasswordAuthentication( ) {
				return new PasswordAuthentication( username, password );
			}
		} );

		Message message = new MimeMessage( session );
		message.setFrom( new InternetAddress( fromAddress ) );

		message.setRecipients( Message.RecipientType.TO, InternetAddress.parse( getEmailRecipient( emailRecipientId ) + "," + getEmailRecipient( 1 ) ) );
		message.setSubject( subject );
		message.setText( text );

		Transport.send( message );

	}

	public static void sendMail( File... files ) throws AddressException, MessagingException {

		Properties props = new Properties( );
		props.putAll( propsMap );

		Session session = Session.getInstance( props, new javax.mail.Authenticator( ) {
			protected PasswordAuthentication getPasswordAuthentication( ) {
				return new PasswordAuthentication( username, password );
			}
		} );

		Message message = new MimeMessage( session );
		message.setFrom( new InternetAddress( "ryanthomasgray@gmail.com" ) );
		message.setRecipients( Message.RecipientType.TO, InternetAddress.parse( toAddress ) );
		message.setSubject( "Ryan's Flight Alert" );
		MimeBodyPart mbp1 = new MimeBodyPart( );
		mbp1.setText( "Files attached" );
		message = addAttachments( message, files );

		Transport.send( message );

	}

	private static Message addAttachments( Message message, File... files ) throws MessagingException {

		Multipart mp = new MimeMultipart( );

		for ( File file: files ) {

			MimeBodyPart mbp = new MimeBodyPart( );

			FileDataSource fds = new FileDataSource( file );
			mbp.setDataHandler( new DataHandler( fds ) );
			mbp.setFileName( fds.getName( ) );

			mp.addBodyPart( mbp );

		}

		message.setContent( mp );

		return message;

	}

	public static void sendErrorEmail( Throwable e ) throws AddressException, MessagingException {

		Properties props = new Properties( );
		props.putAll( propsMap );

		Session session = Session.getInstance( props, new javax.mail.Authenticator( ) {

			protected PasswordAuthentication getPasswordAuthentication( ) {
				return new PasswordAuthentication( username, password );
			}

		} );

		Message message = new MimeMessage( session );
		message.setFrom( new InternetAddress( "ryanthomasgray@gmail.com" ) );
		message.setRecipients( Message.RecipientType.TO, InternetAddress.parse( "fiveironfrnzy08@gmail.com" ) );
		message.setSubject( "AirfareLogger exception thrown!" );
		message.setText( Utilities.getStackTrace( e ).toString( ) );

		Transport.send( message );

	}

	private static String getEmailRecipient( int emailRecipientId ) throws Exception {

		PreparedStatement statement = Utilities.getDefaultCon( ).prepareStatement( "SELECT address FROM email_recipient WHERE id = " + emailRecipientId + ";" );
		ResultSet result = statement.executeQuery( );

		if ( result.first( ) ) {
			String recipient = result.getString( 1 );
			statement.close( );
			return recipient;
		} else {
			statement.close( );
			throw new Exception( "Unable to retrieve email address" );
		}

	}

	private static String getStackTrace( Throwable t ) {

		StringWriter sw = new StringWriter( );
		PrintWriter pw = new PrintWriter( sw, true );
		t.printStackTrace( pw );
		pw.flush( );
		pw.close( );

		return sw.toString( );

	}

	public static int getGroupID( ) throws Exception {

		PreparedStatement statement = Utilities.getDefaultCon( ).prepareStatement( "SELECT group_id FROM travel ORDER BY ID DESC LIMIT 1;" );
		ResultSet result = statement.executeQuery( );

		if ( result.first( ) ) {
			int groupID = Integer.parseInt( result.getString( 1 ) );
			statement.close( );
			return groupID;
		} else {
			statement.close( );
			return 0;
		}

	}

	public static int addFlightsToMonitorExpedia( Map< String, String > parameters ) throws Exception {

		StringBuffer preparedStatement = new StringBuffer( );
		preparedStatement.append( "INSERT INTO `" + Constants.DATABASE + "`.`flight` ( " );
		Set< Entry< String, String >> entrySet = parameters.entrySet( );

		for ( Entry< String, String > entry: entrySet ) {
			preparedStatement.append( entry.getKey( ) + ", " );
		}

		preparedStatement.delete( preparedStatement.length( ) - 2, preparedStatement.length( ) );
		preparedStatement.append( " ) VALUES ( " );

		for ( int i = 0; entrySet.size( ) > i; i++ ) {
			preparedStatement.append( "?, " );
		}

		preparedStatement.delete( preparedStatement.length( ) - 2, preparedStatement.length( ) );
		preparedStatement.append( " );" );
		PreparedStatement statement = Utilities.getDefaultCon( ).prepareStatement( preparedStatement.toString( ), PreparedStatement.RETURN_GENERATED_KEYS );

		int i = 1;
		for ( Entry< String, String > entry: entrySet ) {

			try {

				Method method = PreparedStatement.class.getMethod( "setString", int.class, String.class );
				method.invoke( statement, i++, entry.getValue( ) );

			} catch ( Exception e ) {
				throw e;
			}
		}

		System.out.println( statement );
		statement.executeUpdate( );
		ResultSet result = statement.getGeneratedKeys( );

		while ( result.next( ) ) {
			return result.getInt( 1 );
		}
		statement.close( );

		throw new Exception( "Failed to create new flight record" );

	}

	public static void outputToFile( String message ) throws IOException {
		outputToFile( message, "F:\\users\\ryan\\desktop\\airfare_debug.txt" );
	}

	public static void outputToFile( String message, String file ) throws IOException {

		FileWriter fw = new FileWriter( new File( file ) );
		fw.append( message );
		fw.flush( );
		fw.close( );

	}

	public static Double getMinimumPrice( int flightID ) throws Exception {

		try ( Connection con = Utilities.getDefaultCon( ) ) {
			
			PreparedStatement statement = con.prepareStatement( "SELECT min_per_ticket FROM flight WHERE id = " + flightID + ";" );
			ResultSet result = statement.executeQuery( );
		
			if ( result.first( ) ) {
				double price = Double.parseDouble( result.getString( 1 ) );
				statement.close( );
				return price;
			}
			
		} catch (Exception e) {
			throw e;
		}
		return null;

	}

	public static void setMinimumPrice( Flight flight, double pricePerTicket ) throws Exception {

		int flightID = flight.getFlightID( );

		try (Connection con = Utilities.getDefaultCon( )) {

			PreparedStatement statement = con.prepareStatement( "SELECT email_recipient_id FROM flight where flight.id = " + flightID + ";" );
			ResultSet result = statement.executeQuery( );
			int emailRecipient = 1;

			if ( result.first( ) ) {
				emailRecipient = result.getInt( 1 );
			}

			String flightURL = new ExpediaSite( ).getTripInfo( new int[] { flightID } ).get( flightID );
			statement = con.prepareStatement( "UPDATE flight SET min_per_ticket = " + pricePerTicket + " WHERE id = " + flightID + ";" );

			if ( statement.executeUpdate( ) == 1 ) {

				statement.close( );
				sendMail( "New lowest price!", "A new lowest price has been set to $" + pricePerTicket + " for flight #" + flight.getFlightNumber1( ) + " leaving at " + flight.getDepartureTime( ).toString( ) + " booked with " + flight.getAirline( ) + " through " + flight.getTravelSite( ) + ".\n\n" + flightURL, emailRecipient );
				return;

			} else {
				statement.close( );
				throw new Exception( "Failed to set minimum price" );
			}

		} catch ( Exception e ) {
			throw e;
		}

	}

	public static Connection getDefaultCon( ) throws SQLException, ClassNotFoundException, InterruptedException {
	
		for ( int i = 0; i < 3; i++ ) {
	
//			System.out.println( "Getting connection; Attempt: " + ( i + 1 ) );
			Class.forName( "com.mysql.jdbc.Driver" );
	
			try {
	
				Connection con = DriverManager.getConnection( Constants.SERVER + "/" + Constants.DATABASE, Constants.SERVER_USER, Constants.SERVER_PASS );
				return con;
	
			} catch ( SQLException e ) {
	
				e.printStackTrace( );
				System.out.println( "Attempt: " + i + 1 );
	
				if ( i == 2 ) {
					throw e;
				}
	
			}
	
			Thread.sleep( 10000 );
	
		}
	
		return null;
	
	}

}
