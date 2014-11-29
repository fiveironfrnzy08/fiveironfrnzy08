package org.ryangray.airfare;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;

import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.HTMLParserListener;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;

public abstract class AbstractSite {

	public static boolean	debugURL		= false;
	public static boolean	useDebugData	= false;
	public static boolean	setUpDebugData	= false;
	public static int		groupID;
	public static Timestamp	groupDate;

	public abstract List< Flight > getFlights( Integer flightID, String url ) throws IOException, ClassNotFoundException, SQLException, JSONException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ParseException, Exception;

	protected WebClient getSilentWebClient( ) {
		
		WebClient webClient = new WebClient( );
		LogFactory.getFactory( ).setAttribute( "org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog" );

		java.util.logging.Logger.getLogger( "com.gargoylesoftware.htmlunit" ).setLevel( Level.OFF );
		java.util.logging.Logger.getLogger( "org.apache.commons.httpclient" ).setLevel( Level.OFF );

		webClient = new WebClient( );
		webClient.getOptions( ).setCssEnabled( false );

		webClient.setIncorrectnessListener( new IncorrectnessListener( ) {
			public void notify( String arg0, Object arg1 ) {
			}
		} );

		webClient.setCssErrorHandler( new ErrorHandler( ) {
			public void warning( CSSParseException exception ) throws CSSException {
			}
			public void fatalError( CSSParseException exception ) throws CSSException {
			}
			public void error( CSSParseException exception ) throws CSSException {
			}
		} );

		webClient.setJavaScriptErrorListener( new JavaScriptErrorListener( ) {
			public void timeoutError( HtmlPage arg0, long arg1, long arg2 ) {
			}
			public void loadScriptError( HtmlPage arg0, URL arg1, Exception arg2 ) {
			}
			public void malformedScriptURL( HtmlPage arg0, String arg1, MalformedURLException arg2 ) {
			}
			public void scriptException( HtmlPage arg0, ScriptException arg1 ) {
			}
		} );

		webClient.setHTMLParserListener( new HTMLParserListener( ) {
			public void error( String arg0, URL arg1, String arg2, int arg3, int arg4, String arg5 ) {
			}
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
			System.out.println( "Waiting..." );
			
		}
		throw new Exception( "HtmlUnit could not load the page to 'complete' readyState" );
		
//		long end = System.currentTimeMillis( ) + 30000;
//		HtmlPage page = null;
//		try {
//		    page = webClient.getPage(url);
//		} catch (Exception e) {
//		    System.out.println("Get page error");
//		}
//		
//		while ( System.currentTimeMillis( ) < end ) {
//			
//			JavaScriptJobManager manager = page.getEnclosingWindow().getJobManager();
//			if ( manager.getJobCount() == 0 ) {
//				return page;
//			}
//			System.out.println( "Waiting on " + manager.getJobCount( ) + " jobs." );
//			Thread.sleep( 10000 );
//			
//		}
//		page.refresh( );
//		System.out.println( page.asXml( ) );
//		return page;
		
	}

	protected static void debug( String string ) {
		
		if ( debugURL ) {
			System.out.println( string );
		}
		
	}

	public Map< Integer, String > getTripInfo( int... flight_ids ) throws Exception {

		String idStatement = "";

		if ( flight_ids != null ) {

			StringBuffer sb = new StringBuffer( );

			for ( int i = 0; i < flight_ids.length - 1; i++ ) {
				sb.append( flight_ids[ i ] + "," );
			}

			sb.append( flight_ids[ flight_ids.length - 1 ] );
			idStatement = " id in (" + sb.toString( ) + ") and ";

		}

		System.out.println( "Getting trips to query" );
		
		Map< Integer, String > flights = new HashMap<>( );

		try ( Connection con = Utilities.getDefaultCon( ) ) {
			
			PreparedStatement statement = con.prepareStatement( "SELECT * FROM flight WHERE " + idStatement + " FROM_AIRPORT_CODE is not null;" );
			ResultSet recordSet = statement.executeQuery( );
	
			Date now = new Date( );
	
			while ( recordSet.next( ) ) {
	
				Calendar departure = Calendar.getInstance( );
				departure.set( Integer.parseInt( recordSet.getString( "DEPARTURE_YEAR" ) ), Integer.parseInt( recordSet.getString( "DEPARTURE_MONTH" ) ) - 1, Integer.parseInt( recordSet.getString( "DEPARTURE_DAY" ) ) );
	
				if ( now.after( departure.getTime( ) ) ) {
					System.out.println( "Dates of flight to log are already in the past. Skipping!" );
					continue;
				}
	
				int flightID = Integer.parseInt( recordSet.getString( "ID" ) );
				flights = buildURL( flights, recordSet, flightID );
	
			}
			statement.close( );

		} catch (Exception e) {
			throw e;
		}
		
		return flights;

	}

	public abstract Map< Integer, String > buildURL( Map< Integer, String > flights, ResultSet recordSet, int flightID ) throws SQLException;

	public HtmlPage getWebData( String url ) throws Exception {

		/*
		 * Get last group's ID and increment it
		 */
		groupID = ( Utilities.getGroupID( ) + 1 );
		groupDate = new Timestamp( new Date( ).getTime( ) );

		debug( url );
		String data = new String( );

//		if ( useDebugData ) {
//			BufferedReader debugFR = new BufferedReader( new FileReader( new File( "C://users//rgray//Desktop//" + this.getClass( ).getName( ) + ".txt" ) ) );
//			data = debugFR.readLine( );
//			debugFR.close( );
//			return page;
//		} else {

			final WebClient webClient = getSilentWebClient( );
			webClient.getOptions( ).setTimeout( 60000 );
			webClient.getOptions( ).setThrowExceptionOnFailingStatusCode( true );
//			webClient.getOptions( ).setGeolocationEnabled( false );
//			webClient.getOptions( ).setDoNotTrackEnabled( false );
//			webClient.getOptions( ).setPopupBlockerEnabled( true );
//			webClient.waitForBackgroundJavaScript( 15000 );
			HtmlPage page = getSafeHtmlPage( webClient, url );
			List< WebWindow > webWindows = webClient.getWebWindows( );
			
			FileWriter fw = new FileWriter( new File( "C:\\users\\rgray\\Desktop\\kayaktest.txt" ) ); 
			for ( WebWindow window: webWindows ) {
				fw.append( window.getEnclosedPage( ).getWebResponse( ).getContentAsString( ) );
			}
			fw.close( );

			webClient.closeAllWindows( );
			if ( setUpDebugData ) {
				Utilities.outputToFile( page.asText( ), "C://users//rgray//Desktop//" + this.getClass( ).getSimpleName( ) + " - page.asText().txt" );
			}

			if ( setUpDebugData ) {
				Utilities.outputToFile( data, "C://users//rgray//Desktop//" + this.getClass( ).getSimpleName( ) + " - data.txt" );
			}
			return page;

//		}

	}
	
}
