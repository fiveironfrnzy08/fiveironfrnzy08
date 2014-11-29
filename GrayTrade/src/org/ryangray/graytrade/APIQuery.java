package org.ryangray.graytrade;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

public class APIQuery {

	private static long		NONCE;
	private static String	SECRET;
	private static String	KEY;
	private static boolean	DEBUG	= true;

	public static void main( String[] args ) throws Exception {

		Map< String, String > tradeArgs = new LinkedHashMap<>( );

		tradeArgs = new LinkedHashMap<>( );
		tradeArgs.put( "symbol", "LTCBTC" );
		JSONObject response = APIQuery.doRequest( "hitbtc", "get", "http://demo-api.hitbtc.com", "/api/1/trading/orders/active", tradeArgs );

		tradeArgs = new LinkedHashMap<>( );
		tradeArgs.put( "by", "ts" );
		tradeArgs.put( "start_index", "0" );
		tradeArgs.put( "max_results", "100" );
		response = APIQuery.doRequest( "hitbtc", "get", "http://demo-api.hitbtc.com", "/api/1/trading/trades", tradeArgs );

		tradeArgs = new LinkedHashMap<>( );
		// tradeArgs.put( "clientOrderId", String.valueOf( new Date( ).getTime( ) ) );
		tradeArgs.put( "clientOrderId", "12345678" );
		tradeArgs.put( "symbol", "LTCBTC" );
		tradeArgs.put( "side", "sell" );
		tradeArgs.put( "price", "0.0124" );
		tradeArgs.put( "quantity", "100" );
		tradeArgs.put( "type", "limit" );
		tradeArgs.put( "timeInForce", "GTC" );
		JSONObject responseGetInfo = APIQuery.doRequest( "hitbtc", "post", "http://demo-api.hitbtc.com", "/api/1/trading/new_order", tradeArgs );
		/*
		 * Example 1 -- GET method, no arguments
		 */
		 JSONObject response1 = APIQuery.doRequest( "bter", "get", "http://data.bter.com", "/api/1/ticker/doge_btc" );
		 System.out.println( response1 );
		
		 /*
		 * Example 2 -- POST method, no arguments
		 */
		 JSONObject response2 = APIQuery.doRequest( "bter", "post", "https://bter.com", "/api/1/private/getfunds" );
		 System.out.println( response2 );
		
		 /*
		 * Example 2 -- POST method, no arguments
		 */
		 Map< String, String > arguments = new HashMap<>( );
		 arguments.put( "order_id", "123456" );
		 JSONObject response3 = APIQuery.doRequest( "bter", "post", "https://bter.com", "/api/1/private/getorder", arguments );
		 System.out.println( response3 );

	}

	public static JSONObject doRequest( String api, String requestType, String url ) throws Exception {
		return doRequest( api, requestType, url, "", new HashMap< String, String >( ) );
	}

	public static JSONObject doRequest( String api, String requestType, String url, String method ) throws Exception {
		return doRequest( api, requestType, url, method, new HashMap< String, String >( ) );
	}

	public static JSONObject doRequest( String api, String requestType, String url, Map< String, String > arguments ) throws Exception {
		return doRequest( api, requestType, url, "", arguments );
	}

	public static JSONObject doRequest( String api, String requestType, String url, String method, Map< String, String > arguments ) throws Exception {

		String headerKeyKey = "Key";
		String headerSignatureKey = "Sign";
		URI uri = new URL( url ).toURI( ).normalize( );

		switch ( api ) {

		case "btce":
			SECRET = "c1e1dc9066f9712926959f0e3c941b72d7a0548eeed698b920d95b090b7444c7";
			KEY = "GYAPLTTV-XSHT8B1O-HP43X32L-G80Q89SB-L4DFS8GK";
			NONCE = System.currentTimeMillis( ) / 1000L;
			arguments.put( "nonce", "" + ++NONCE ); // Add the dummy nonce.
			break;
		case "bter":
			SECRET = "8bb02afe59cb4e9a0808f10dc5dc3e15b4ac56a7106d3faf6070efb8e15af52d";
			KEY = "6C7A5F96-9A6E-4E99-B70B-F346F659E57E";
			break;
		case "hitbtc":
			SECRET = "8df04fda965f03119c4643c7c12c88ba";
			KEY = "81f1dcf08c6be3cd3cee087ae26b5f31";
			NONCE = new Date( ).getTime( );
			arguments.put( "nonce", "" + NONCE );
			arguments.put( "apikey", KEY );
			uri = new URIBuilder( uri ).setPath( method ).build( );
			uri = appendURIParams( uri, convertArgsToNameValuePairs( arguments ) ).setPath( method ).build( );
			headerSignatureKey = "X-Signature";
			break;
		default:
			throw new Exception( "Invalid site parameter" );
		}

		URI postData = new URI( "" );
		List< NameValuePair > urlParameters = convertArgsToNameValuePairs( arguments );
		if ( !urlParameters.isEmpty( ) ) {
			postData = new URIBuilder( ).setParameters( urlParameters ).build( );
		}

		if ( !method.isEmpty( ) && !"hitbtc".contains( api ) ) {
			arguments.put( "method", method ); // Add the method to the post data.
		}

		Mac mac = generateMac( );

		// add header
		List< Header > headers = new ArrayList<>( );
		if ( "hitbtc".equalsIgnoreCase( api ) ) {
			postData = appendURIParams( postData, urlParameters ).setPath( method ).build( );
		} else {
			uri = new URIBuilder( uri ).setPath( method ).build( );
			postData = new URI( URLEncodedUtils.format( urlParameters, "UTF-8" ) );
		}

		System.out.println( "Signature: " + postData );

		headers.add( new BasicHeader( headerKeyKey, KEY ) );
		headers.add( new BasicHeader( headerSignatureKey, Hex.encodeHexString( mac.doFinal( postData.toString( ).getBytes( "UTF-8" ) ) ) ) );

		// Now do the actual request
		HttpClient client = HttpClients.createDefault( );
		HttpPost post = null;
		HttpGet get = null;
		HttpResponse response = null;
		
		System.out.println( "URL: " + uri );
		System.out.println( "URLParameters: " + urlParameters.toString( ) );

		if ( "POST".equalsIgnoreCase( requestType ) ) {
			post = new HttpPost( uri );
			post.setEntity( new UrlEncodedFormEntity( urlParameters, "UTF-8" ) );
			post.setHeaders( headers.toArray( new Header[ headers.size( ) ] ) );
			response = client.execute( post );
		} else if ( "GET".equalsIgnoreCase( requestType ) ) {
			get = new HttpGet( uri );
			get.setHeaders( headers.toArray( new Header[ headers.size( ) ] ) );
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

			System.out.println( "Sending '" + requestType + "' request to URL : " + uri );
			if ( post != null ) {
				System.out.println( "Post parameters : " + post.getEntity( ) );
			}
			System.out.println( "Response Code : " + response.getStatusLine( ).getStatusCode( ) );
			System.out.println( "JSON Returned : " + json.toString( ) );
			System.out.println( "" );

		}

		return json;

	}

	private static URIBuilder appendURIParams( URI uri, List< NameValuePair > args ) throws URISyntaxException {
		
		if ( args.isEmpty( ) ) {
			return new URIBuilder( uri );
		} else {
			return new URIBuilder( uri ).setParameters( new URIBuilder( uri ).getQueryParams( ) ).setParameters( args );
		}
		
	}

	private static Mac generateMac( ) {

		Mac mac = null;
		SecretKeySpec key;
		try {

			key = new SecretKeySpec( ( SECRET ).getBytes( "UTF-8" ), "HmacSHA512" );
			mac = Mac.getInstance( "HmacSHA512" );
			mac.init( key );

		} catch ( UnsupportedEncodingException uee ) {
			System.err.println( "Unsupported encoding exception: " + uee.toString( ) );
		} catch ( NoSuchAlgorithmException nsae ) {
			System.err.println( "No such algorithm exception: " + nsae.toString( ) );
		} catch ( InvalidKeyException ike ) {
			System.err.println( "Invalid key exception: " + ike.toString( ) );
		}

		return mac;

	}

	private static List< NameValuePair > convertArgsToNameValuePairs( Map< String, String > arguments ) {

		List< NameValuePair > urlParameters = new ArrayList< NameValuePair >( );

		for ( Iterator< Entry< String, String >> argumentIterator = arguments.entrySet( ).iterator( ); argumentIterator.hasNext( ); ) {
			Map.Entry< String, String > argument = argumentIterator.next( );
			urlParameters.add( new BasicNameValuePair( argument.getKey( ).toString( ), argument.getValue( ).toString( ) ) );
		}

		return urlParameters;

	}

}