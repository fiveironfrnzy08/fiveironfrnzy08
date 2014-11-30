package org.ryangray.airfare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;

import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParserListener;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;

public abstract class AbstractSite {

	public static boolean	debugURL		= false;
	public static boolean	useDebugJSON	= false;
	public static boolean	setUpDebugJSON	= false;
	public static int		groupID;
	public static Timestamp	groupDate;

	protected static void main( String[] args ) throws Exception {
	}

	public abstract List< Flight > getFlights( Integer flightID, String jsondata ) throws IOException, ClassNotFoundException, SQLException, JSONException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ParseException;

	public String getJSON( String url ) throws Exception {

		/*
		 * Get last group's ID and increment it
		 */
		groupID = ( Utilities.getGroupID( ) + 1 );
		Date date = new Date( );
		groupDate = new Timestamp( date.getTime( ) );

		debug( url );
		String jsondata = new String( );

		if ( useDebugJSON ) {
			BufferedReader debugFR = new BufferedReader( new FileReader( new File( "C://users//rgray//Desktop//" + this.getClass( ).getName( ) + ".txt" ) ) );
			jsondata = debugFR.readLine( );
			debugFR.close( );
		} else {

			final WebClient webClient = getSilentWebClient( );
			webClient.getOptions( ).setTimeout( 60000 );
			webClient.getOptions( ).setThrowExceptionOnFailingStatusCode( true );
			HtmlPage page = getSafeHtmlPage( webClient, url );
			webClient.closeAllWindows( );
			if ( setUpDebugJSON ) {
				Utilities.outputToFile( page.asXml( ), "C://users//rgray//Desktop//" + this.getClass( ).getSimpleName( ) + " - page.asText().txt" );
			}
			
			System.out.println( page.getElementById( "flightModule0" ) );
			jsondata = page.getElementById( "bCol" ).asXml( );

			if ( setUpDebugJSON ) {
				Utilities.outputToFile( jsondata, "C://users//rgray//Desktop//" + this.getClass( ).getSimpleName( ) + " - jsondata.txt" );
			}

		}
		return jsondata;

	};

	private WebClient getSilentWebClient( ) {
		WebClient webClient = new WebClient( );
		LogFactory.getFactory( ).setAttribute( "org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog" );

		java.util.logging.Logger.getLogger( "com.gargoylesoftware.htmlunit" ).setLevel( Level.OFF );
		java.util.logging.Logger.getLogger( "org.apache.commons.httpclient" ).setLevel( Level.OFF );

		webClient = new WebClient( );
		webClient.getOptions( ).setCssEnabled( false );

		webClient.setIncorrectnessListener( new IncorrectnessListener( ) {

			@Override
			public void notify( String arg0, Object arg1 ) {
			}

		} );

		webClient.setCssErrorHandler( new ErrorHandler( ) {

			@Override
			public void warning( CSSParseException exception ) throws CSSException {
			}

			@Override
			public void fatalError( CSSParseException exception ) throws CSSException {
			}

			@Override
			public void error( CSSParseException exception ) throws CSSException {
			}

		} );

		webClient.setJavaScriptErrorListener( new JavaScriptErrorListener( ) {

			@Override
			public void timeoutError( HtmlPage arg0, long arg1, long arg2 ) {
			}

			@Override
			public void loadScriptError( HtmlPage arg0, URL arg1, Exception arg2 ) {
			}

			@Override
			public void malformedScriptURL( HtmlPage arg0, String arg1, MalformedURLException arg2 ) {
			}

			@Override
			public void scriptException( HtmlPage arg0, ScriptException arg1 ) {
			}
		} );

		webClient.setHTMLParserListener( new HTMLParserListener( ) {

			@Override
			public void error( String arg0, URL arg1, String arg2, int arg3, int arg4, String arg5 ) {
			}

			@Override
			public void warning( String arg0, URL arg1, String arg2, int arg3, int arg4, String arg5 ) {
			}

		} );

		webClient.getOptions( ).setThrowExceptionOnFailingStatusCode( false );
		webClient.getOptions( ).setThrowExceptionOnScriptError( false );

		return webClient;

	}

	protected static HtmlPage getSafeHtmlPage( WebClient webClient, String url ) throws Exception {
		long end = System.currentTimeMillis( ) + 30000;
		HtmlPage page = null;
		while ( System.currentTimeMillis( ) < end ) {
			page = webClient.getPage( url );
			if ( "complete".equals( page.getReadyState( ) ) ) {
				return page;
			}
		}
		throw new Exception( "HtmlUnit could not load the page to 'complete' readyState" );
	}

	protected static void debug( String string ) {
		if ( debugURL ) {
			System.out.println( string );
		}
	}
}
