package org.ryangray.emailsender;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;

public class SendEmail {

	public static void sendEmail( List< Message > messages ) throws MessagingException {

		for ( Message message: messages ) {
			
			Transport.send( message );

		}
		System.out.println( "Done" );

	}

	public static List< Message > buildEmail( final String username, final String password, List< String > to, String subject, String greeting, String body, File[] attachments ) throws MessagingException {
		Message message;

		Properties props = new Properties( );
		props.put( "mail.smtp.auth", "true" );
		props.put( "mail.smtp.starttls.enable", "true" );
		props.put( "mail.smtp.host", "smtp.gmail.com" );
		props.put( "mail.smtp.port", "587" );

		Session session = Session.getInstance( props, new javax.mail.Authenticator( ) {
			protected PasswordAuthentication getPasswordAuthentication( ) {
				return new PasswordAuthentication( username, password );
			}
		} );
		
		List< Message > messages = new ArrayList< Message >( );

		for ( String toAddress: to ) {

			message = new MimeMessage( session );
			message.setFrom( new InternetAddress( username ) );
			if ( MainWindow.personalize ) {
				message.setRecipients( Message.RecipientType.TO, InternetAddress.parse( StringUtils.split( toAddress, "," )[0] ) );
			} else {
				message.setRecipients( Message.RecipientType.TO, InternetAddress.parse( toAddress ) );
			}
			message.setSubject( subject );
			message.setText( getBody( toAddress, greeting, body ) );

			if ( attachments != null ) {
				Multipart multipart = new MimeMultipart( );

				for ( File file: attachments ) {
					MimeBodyPart messageBodyPart = new MimeBodyPart( );
					messageBodyPart = new MimeBodyPart( );
					String fileName = file.getName( );
					DataSource source = new FileDataSource( file );
					messageBodyPart.setDataHandler( new DataHandler( source ) );
					messageBodyPart.setFileName( fileName );
					multipart.addBodyPart( messageBodyPart );
				}

				message.setContent( multipart );
			}
			messages.add( message );
		}
		return messages;
	}

	private static String getBody( String toAddress, String greeting, String body ) {

		if ( MainWindow.personalize ) {
			StringBuilder sb = new StringBuilder( );
			String[] emailFirstLast = StringUtils.split( toAddress, "," );
			sb.append( greeting + " " + emailFirstLast[1] + ",\n\n" );
			sb.append( body );
			return sb.toString( );
		} else {
			return body;
		}
	}
}