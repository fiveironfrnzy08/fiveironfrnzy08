package org.ryangray.bter;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

public class BTERQuery {

	private static String SECRET = "YOUR_SECRET_HERE";
	private static String KEY = "YOUR_KEY_HERE";
	private static boolean	DEBUG	= true;

	public static void main( String[] args ) throws Exception {

		/*
		 * Try it out! doRequest needs the request type ( post or get ), the API URL, and arguments if necessary (otherwise uses the stub method, as shown here)
		 */

		BTERQuery request = new BTERQuery( );

		/*
		 * Example 1 -- GET method, no arguments
		 */
		JSONObject response1 = request.doRequest( "bter", "get", "http://data.bter.com/api/1/ticker/doge_btc" );
		System.out.println( response1 );

		/*
		 * Example 2 -- POST method, no arguments
		 */
		JSONObject response2 = request.doRequest( "bter", "post", "https://bter.com/api/1/private/getfunds" );
		System.out.println( response2 );

		/*
		 * Example 2 -- POST method, no arguments
		 */
		Map< String, String > arguments = new HashMap<>( );
		arguments.put( "order_id", "123456" );
		JSONObject response3 = request.doRequest( "bter", "post", "https://bter.com/api/1/private/getorder", arguments );
		System.out.println( response3 );

	}

	public JSONObject doRequest( String api, String requestType, String url ) throws Exception {
		return doRequest( api, requestType, url, new HashMap< String, String >( ) );
	}

	public JSONObject doRequest( String api, String requestType, String url, Map< String, String > arguments ) throws Exception {

		List< NameValuePair > urlParameters = new ArrayList< NameValuePair >( );

		Mac mac = null;
		SecretKeySpec key = null;

		String postData = "";

		for ( Iterator< Entry< String, String >> argumentIterator = arguments.entrySet( ).iterator( ); argumentIterator.hasNext( ); ) {

			Map.Entry< String, String > argument = argumentIterator.next( );

			urlParameters.add( new BasicNameValuePair( argument.getKey( ).toString( ), argument.getValue( ).toString( ) ) );

			if ( postData.length( ) > 0 ) {
				postData += "&";
			}

			postData += argument.getKey( ) + "=" + argument.getValue( );

		}

		// Create a new secret key
		try {
			key = new SecretKeySpec( SECRET.getBytes( "UTF-8" ), "HmacSHA512" );
		} catch ( UnsupportedEncodingException uee ) {
			System.err.println( "Unsupported encoding exception: " + uee.toString( ) );
		}

		// Create a new mac
		try {
			mac = Mac.getInstance( "HmacSHA512" );
		} catch ( NoSuchAlgorithmException nsae ) {
			System.err.println( "No such algorithm exception: " + nsae.toString( ) );
		}

		// Init mac with key.
		try {
			mac.init( key );
		} catch ( InvalidKeyException ike ) {
			System.err.println( "Invalid key exception: " + ike.toString( ) );
		}

		// add header
		Header[] headers = new Header[ 2 ];
		headers[ 0 ] = new BasicHeader( "Key", KEY );
		headers[ 1 ] = new BasicHeader( "Sign", Hex.encodeHexString( mac.doFinal( postData.getBytes( "UTF-8" ) ) ) );

		// Now do the actual request

		HttpClient client = HttpClientBuilder.create( ).build( );
		HttpPost post = null;
		HttpGet get = null;
		HttpResponse response = null;

		if ( requestType == "post" ) {
			post = new HttpPost( url );
			post.setEntity( new UrlEncodedFormEntity( urlParameters ) );
			post.setHeaders( headers );
			response = client.execute( post );
		} else if ( requestType == "get" ) {
			get = new HttpGet( url );
			get.setHeaders( headers );
			response = client.execute( get );
		}

		BufferedReader rd = new BufferedReader( new InputStreamReader( response.getEntity( ).getContent( ) ) );

		StringBuffer buffer = new StringBuffer( );
		String line = "";
		while ( ( line = rd.readLine( ) ) != null ) {
			buffer.append( line );
		}

		JSONObject json = new JSONObject( buffer.toString( ) );

		if ( DEBUG ) {

			System.out.println( "\nSending '" + requestType + "' request to URL : " + url );
			if ( post != null ) {
				System.out.println( "Post parameters : " + post.getEntity( ) );
			}
			System.out.println( "Response Code : " + response.getStatusLine( ).getStatusCode( ) );
			System.out.println( "JSON Returned : " + json.toString( ) );

		}

		return json;

	}
}