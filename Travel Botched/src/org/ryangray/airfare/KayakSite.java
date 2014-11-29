package org.ryangray.airfare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class KayakSite extends AbstractSite {
	
	public static void main( String[] args ) throws Exception {
		new KayakSite( ).getFlights( 10, null);
	}

	public ArrayList< Flight > getFlights( Integer id, String url ) throws Exception {		

		KayakSite kayak = new KayakSite( );
		System.out.println( url );
		HtmlPage page = kayak.getWebData( url );
		List< HtmlAnchor > as = ( List< HtmlAnchor > ) page.getByXPath( "//a" );
		for ( HtmlAnchor a: as ) {
			if ( a.getAttribute( "class" ).startsWith( "results_price bookitprice" ) ) {
				System.out.println( a.getFirstChild( ).toString( ) );
				return null;
			}
			
		}
//		FileWriter fw = new FileWriter( new File( "C:\\users\\rgray\\Desktop\\kayaktest.txt" ) ); 
//		fw.append( page.asXml( ) );
//		fw.append( page.getWebResponse( ).getContentAsString( ) );
//		List< ? > airlines = page.getByXPath( "//div[@class='airlineName']" ) ;
//		fw.append( String.valueOf( airlines.size( ) ) );
//		fw.append( String.valueOf( page.getBody( ).getByXPath( "//div[@class='airlineName']" ).size( ) ) );
//		fw.close( );
//		ArrayList< Flight > rowResults = new ArrayList< Flight >( );
//		JSONTokener htmldata = null;
//		final JSONArray array = new JSONArray( htmldata );
//
//		for ( int i = 0; i < array.length( ); i++ ) {
//
//			JSONObject obj = array.getJSONObject( i );
//
//			try {
//
//				JSONObject legs = obj.getJSONArray( "legs" ).getJSONObject( 0 );
//				JSONArray segments = legs.getJSONArray( "segments" );
//				JSONObject segment = segments.getJSONObject( 0 );
//				JSONObject airline = segment.getJSONObject( "airline" );
//
//			} catch ( Exception e ) {
//				continue;
//			}
//
//			Flight travelEntry = new Flight( );
//			travelEntry.setFlightID( id );
//			travelEntry.setGroupID( groupID );
//			travelEntry.setGroupDate( groupDate );
//			travelEntry.setTravelSite( "Kayak" );
//			travelEntry.setAirline( retrieveAirline( obj ) );
//			String price = retrievePrice( obj );
//			if ( price != null ) {
//				travelEntry.setPrice( price );
//			} else {
//				continue;
//			}
//			travelEntry.setDepartureTime( retrieveDepartureTime( obj ) );
//			travelEntry.setArrivalTime( retrieveArrivalTime( obj ) );
//			travelEntry.setFlightDuration( retrieveFlightDuration( obj ) );
//			travelEntry.setStops( retrieveStops( obj ) );
//
//			if ( travelEntry.getStops( ) > 0 ) {
//
//				for ( int j = 0; j < travelEntry.getStops( ); j++ ) {
//
//					Method method = Flight.class.getMethod( "setLayoverTime" + ( j + 1 ), String.class );
//					method.invoke( travelEntry, retrieveLayoverTime( obj, j ) );
//
//				}
//
//			}
//
//			for ( int j = 0; j < travelEntry.getStops( ) + 1; j++ ) {
//
//				Method method = Flight.class.getMethod( "setTravelTime" + ( j + 1 ), String.class );
//				method.invoke( travelEntry, retrieveTravelTime( obj, j ) );
//
//			}
//
//			for ( int j = 0; j < travelEntry.getStops( ) + 1; j++ ) {
//
//				Method method = Flight.class.getMethod( "setFlightNumber" + ( j + 1 ), String.class );
//				method.invoke( travelEntry, retrieveFlightNumber( obj, j ) );
//
//			}
//
//			rowResults.add( travelEntry );
//
//		}

		return null;

	}

	private String retrieveFlightDuration( JSONObject obj ) throws JSONException {

		JSONObject legs = obj.getJSONArray( "legs" ).getJSONObject( 0 );
		JSONObject totalTravelTime = legs.getJSONObject( "totalTravelingHours" );
		String minutes = StringUtils.rightPad( totalTravelTime.get( "minutes" ).toString( ), 2, "0" );
		String hours = StringUtils.leftPad( totalTravelTime.get( "hours" ).toString( ), 2, "0" );
		String days = StringUtils.leftPad( totalTravelTime.get( "numOfDays" ).toString( ), 2, "0" );

		return days + ":" + hours + ":" + minutes;

	}

	private long retrieveArrivalTime( JSONObject obj ) throws JSONException {

		JSONObject legs = obj.getJSONArray( "legs" ).getJSONObject( 0 );

		return legs.getLong( "arrivalDate" );

	}

	private long retrieveDepartureTime( JSONObject obj ) throws JSONException {

		JSONObject legs = obj.getJSONArray( "legs" ).getJSONObject( 0 );

		return legs.getLong( "departureDate" );

	}

	private static Object retrieveLayoverTime( JSONObject obj, int j ) throws JSONException {

		JSONObject legs = obj.getJSONArray( "legs" ).getJSONObject( 0 );
		JSONArray segments = legs.getJSONArray( "segments" );
		JSONObject segment = segments.getJSONObject( j );
		JSONObject travelTime = segment.getJSONObject( "layover" );
		String minutes = StringUtils.rightPad( travelTime.get( "minutes" ).toString( ), 2, "0" );
		String hours = StringUtils.leftPad( travelTime.get( "hours" ).toString( ), 2, "0" );
		String days = StringUtils.leftPad( travelTime.get( "numOfDays" ).toString( ), 2, "0" );

		return days + ":" + hours + ":" + minutes;

	}

	private static String retrieveTravelTime( JSONObject obj, int j ) throws JSONException {

		JSONObject legs = obj.getJSONArray( "legs" ).getJSONObject( 0 );
		JSONArray segments = legs.getJSONArray( "segments" );
		JSONObject segment = segments.getJSONObject( j );
		JSONObject travelTime = segment.getJSONObject( "totalTravelingTime" );
		String minutes = StringUtils.rightPad( travelTime.get( "minutes" ).toString( ), 2, "0" );
		String hours = StringUtils.leftPad( travelTime.get( "hours" ).toString( ), 2, "0" );
		String days = StringUtils.leftPad( travelTime.get( "numOfDays" ).toString( ), 2, "0" );

		return days + ":" + hours + ":" + minutes;

	}

	private String retrieveFlightNumber( JSONObject obj, int j ) throws JSONException {

		JSONObject legs = obj.getJSONArray( "legs" ).getJSONObject( 0 );
		JSONArray segments = legs.getJSONArray( "segments" );
		JSONObject segment = segments.getJSONObject( j );

		return segment.getString( "flightNumber" );

	}

	private static int retrieveStops( JSONObject obj ) throws NumberFormatException, JSONException {

		JSONObject legs = obj.getJSONArray( "legs" ).getJSONObject( 0 );
		JSONArray segments = legs.getJSONArray( "segments" );

		return segments.length( ) - 1;

	}

	private static String retrievePrice( JSONObject obj ) throws JSONException {

		double price = obj.getDouble( "totalPrice" );
		DecimalFormat df = new DecimalFormat( "#.00" );

		if ( df.format( price ).toString( ).length( ) > 10 ) {
			System.out.println( "\"totalPrice\" object in JSON contains an invalid price. Example seen on webpage was where price said \"VIEW DETAILS\" instead of listing a price... Scurvy dogs.\nContains: " + obj.getDouble( "totalPrice" ) );
			return null;
		}

		return df.format( price ).toString( );

	}

	private static String retrieveAirline( JSONObject obj ) throws JSONException {

		JSONObject legs = obj.getJSONArray( "legs" ).getJSONObject( 0 );
		JSONArray segments = legs.getJSONArray( "segments" );
		JSONObject segment = segments.getJSONObject( 0 );
		JSONObject airline = segment.getJSONObject( "airline" );
		String airlineName = airline.get( "airlineName" ).toString( );

		return airlineName;

	}

	public Map< Integer, String > getTripInfo( ) throws Exception {
		return getTripInfo( null );
	}

	@Override
	public Map< Integer, String > buildURL( Map< Integer, String > flights, ResultSet recordSet, int flightID ) throws SQLException {

		StringBuffer flightURL = new StringBuffer( );
		flightURL.append( "http://www.kayak.com/flights/" + recordSet.getString( "FROM_AIRPORT_CODE" ) + ",nearby-" + recordSet.getString( "TO_AIRPORT_CODE" ) + ",nearby/" + recordSet.getString( "DEPARTURE_YEAR" ) + "-" + recordSet.getString( "DEPARTURE_MONTH" ) + "-" + recordSet.getString( "DEPARTURE_DAY" ) );

		if ( recordSet.getString( "ROUTE" ).equalsIgnoreCase( "roundtrip" ) ) {
			flightURL.append( "/" + recordSet.getString( "RETURN_YEAR" ) + "-" + recordSet.getString( "RETURN_MONTH" ) + "-" + recordSet.getString( "RETURN_DAY" ) );
		}

		int children = Integer.parseInt( recordSet.getString( "CHILDREN" ) );
		if ( children > 0 ) {
			flightURL.append( "/" + "children" );
			for ( int i = 0; i < Integer.parseInt( recordSet.getString( "CHILDREN" ) ); i++ ) {
				flightURL.append( "-11" );
			}
		}

		flightURL.append( "/" + recordSet.getString( "ADULTS" ) + "adults/" + recordSet.getString( "SENIORS" ) + "seniors" );
		flights.put( flightID, flightURL.toString( ) );

		return flights;

	}

}