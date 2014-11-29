package org.ryangray.onkyo.server;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class OnkyoMain {

	public final static byte[] ISCP_HEADER = "ISCP".getBytes( );
	public final static byte[] ISCP_HEADER_SPACE = new byte[]{ 0x00000000, 0x00000000, 0x00000000 }; // Header size = 16
	public final static byte[] ISCP_HEADER_SIZE = new byte[]{ 0x00000010 }; // Header size = 16
	public final static byte[] ISCP_DATA_SPACE =  new byte[]{ 0x00000000, 0x00000000, 0x00000000 }; // Data size (command length) = 7 chars
	public static byte[] ISCP_DATA_SIZE; // Data size (command length) = 7 chars
	public final static byte[] ISCP_VERSION = new byte[]{ ( byte ) 0x00000001 } ; // Version 1.0.0.0
	public final static byte[] ISCP_RESERVED_SPACE = new byte[]{ 0x00000000, 0x00000000, 0x00000000 }; // Reserved three null
	public static byte[] ISCP_DATA_START = "!1".getBytes( );
	public static byte[] ISCP_COMMAND;
	public final static byte[] ISCP_END = new byte[]{ 0x0D }; // @CR
	public static byte[] ISCP_MESSAGE;
	public static String IP = "192.168.1.108";
	public static String PORT = "60128";
	public static Socket client;
	public static String command;
	public static String parameter;
	public static DataInputStream in;
	public static boolean debug = true;
	public static String[] finalResponses;
	
public static void main( String[] args ) throws Exception{
		
		String[] results = run( new String[] { "MVL", "0e" } );
		for ( String string: results ) {
			System.out.println( "Returned: " + string );
		}
		
	}
	
	public static String[] run( String[]... args ) throws Exception{
		
		try {
			finalResponses = new String[ args.length ];
			
			for ( int i = 0; i < args.length; i++ ) {
				String[] strings = args[i];
				System.out.println( "Executing: " + strings[0] + strings[1].toUpperCase( ) );
				finalResponses[i] = executeCommand( strings ) ;
			}
			
			return finalResponses;
		} catch ( Exception e ) {
			/**
			 * We don't want to allow the non-serializable SocketException to be returned to the client. Instead transfer the stack trace and throw Exception
			 */
			Exception e1 = new Exception( );
			e1.setStackTrace( e.getStackTrace( ) );
			throw e1;
		}
		
	}
    
	public static String executeCommand( String[] args ) throws Exception {
		
		getClientConnection( );
		in = new DataInputStream( client.getInputStream( ) );
		
		command = args[0].toUpperCase( );
		parameter = args[1].toUpperCase( );
		sendCommand( command + parameter );
		String receiverResponse = "";
		
//		System.out.println( "Response: " );
		byte[] responseBytes = new byte[ 32 ];
		int numBytesReceived = 0;
		int totBytesReceived = 0;
		int packetCounter = 0;
		long startTime = System.currentTimeMillis( );
		long timeout = startTime + ( 1000 * 5 );

		while ( System.currentTimeMillis( ) < timeout && receiverResponse.isEmpty( ) &&	( numBytesReceived = in.read( responseBytes ) ) > 0 ) {
			
			// Get entire response
			totBytesReceived = 0;
			StringBuilder msgBuffer = new StringBuilder( "" );
			while ( numBytesReceived > 0 ) {
				totBytesReceived += numBytesReceived;
				msgBuffer.append( new String( responseBytes ) );
				responseBytes = new byte[ 32 ];
				numBytesReceived = 0;
				if ( in.available( ) > 0 )
					numBytesReceived = in.read( responseBytes );
				System.out.print( " " + numBytesReceived );
			}

			if ( debug ) {
				convertStringToHex( msgBuffer.toString( ), debug );
			}

			// Look through response for our command to be returned. Either telling us it was successful, or answering a query (like current volume level)
			if ( msgBuffer.toString( ).contains( command ) ) {
				System.out.println( "Found string..." );
				receiverResponse = msgBuffer.toString( ).substring( msgBuffer.toString( ).indexOf( command ) );
				// Strip non digits (end bytes)
				receiverResponse = receiverResponse.replaceAll( "[^a-zA-Z0-9]", "" );
			}
		}
		
		in.close( );
		client.close( );
		
		if ( debug ) {
			System.out.print( "Receiver response: " + receiverResponse );
		}
		
		return receiverResponse;

	}

	private static void getClientConnection( ) throws NumberFormatException, UnknownHostException, IOException {
		client = new Socket( IP, Integer.parseInt( PORT ) );
	}

	private static void sendCommand( String command ) {

		ISCP_COMMAND = command.getBytes( );
		ISCP_DATA_SIZE = Integer.toHexString( command.getBytes( ).length ).getBytes( );
		try {
			System.out.println( "Connecting to " + IP + " on port " + Integer.parseInt( PORT ) );
			System.out.println( "Just connected to " + client.getRemoteSocketAddress( ) );
			OutputStream outToServer = client.getOutputStream( );
			DataOutputStream out = new DataOutputStream( outToServer );

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
			outputStream.write( ISCP_HEADER );
			outputStream.write( ISCP_HEADER_SPACE );
			outputStream.write( ISCP_HEADER_SIZE );
			outputStream.write( ISCP_DATA_SPACE );
			outputStream.write( ISCP_DATA_SIZE );
			outputStream.write( ISCP_VERSION );
			outputStream.write( ISCP_RESERVED_SPACE );
			outputStream.write( ISCP_DATA_START );
			outputStream.write( ISCP_COMMAND );
			outputStream.write( ISCP_END );
			ISCP_MESSAGE = outputStream.toByteArray( );

			if ( client.isConnected( ) && !client.isOutputShutdown( ) & !client.isOutputShutdown( ) ) {
				System.out.println( "Command: " + command );
				System.out.println( "Size of total message: " + ISCP_MESSAGE.length );
				System.out.println( "Message in hex: " );
				for ( byte b: ISCP_MESSAGE ) {
					System.out.print( b + " " );
				}
				System.out.println( "\nMessage in ASCII: " );
				for ( byte b: ISCP_MESSAGE ) {
					System.out.print( (char) b + " " );
				}
				out.write( ISCP_MESSAGE );
				out.flush( );
				System.out.println( "\n\n\n" );
			}

		} catch ( IOException e ) {
			e.printStackTrace( );
		}
	}

	public static String[] splitAndConvertToHex( String s, int interval ) {
		int arrayLength = ( int ) Math.ceil( ( ( s.length( ) / ( double ) interval ) ) );
		String[] result = new String[ arrayLength ];

		int j = 0;
		int lastIndex = result.length - 1;
		for ( int i = 0; i < lastIndex; i++ ) {
			result[ i ] = s.substring( j, j + interval );
			j += interval;
		}
		result[ lastIndex ] = s.substring( j );

		// Convert to hex values
		for ( int i = 0; i < result.length; i++ ) {
			result[ i ] = Integer.toHexString( Integer.parseInt( result[ i ] ) );
		}
		return result;
	}
	
	public static String convertStringToHex( String str, boolean dumpOut ) {
		char[] chars = str.toCharArray( );
		String out_put = "";

		if ( dumpOut )
			System.out.println( "    Ascii: " + str );
		if ( dumpOut )
			System.out.print( "    Hex: " );
		StringBuffer hex = new StringBuffer( );
		for ( int i = 0; i < chars.length; i++ ) {
			out_put = Integer.toHexString( ( int ) chars[ i ] );
			if ( out_put.length( ) == 1 )
				hex.append( "0" );
			hex.append( out_put );
			if ( dumpOut )
				System.out.print( "0x" + ( out_put.length( ) == 1 ? "0" : "" ) + out_put + " " );
		}
		if ( dumpOut )
			System.out.println( "" );

		return hex.toString( );
	}

}
