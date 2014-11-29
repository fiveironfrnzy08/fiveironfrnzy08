package org.ryangray.airfare;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ryangray.airfare.AbstractSite;
import org.ryangray.airfare.Travel;
import org.ryangray.airfare.Utilities;

public class KayakSite extends AbstractSite {

	public ArrayList< Travel > getFlights( Integer id, String jsondata ) throws IOException, ClassNotFoundException, SQLException, JSONException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ParseException {

		ArrayList< Travel > expediaRowResults = new ArrayList< Travel >( );
		final JSONArray array = new JSONArray( jsondata );
		for ( int i = 0; i < array.length( ); i++ ) {
			JSONObject obj = array.getJSONObject( i );
			
			/*
			 * Not set up to handle the "Roundtrip - Expedia Bargain Fare" flights. If this row is a bargain trip, it won't find the airline segment and throw an exception.
			 * Until I develop that ability, skip the segment... :(
			 */
			try {
				JSONObject legs = obj.getJSONArray( "legs" ).getJSONObject( 0 );
				JSONArray segments = legs.getJSONArray( "segments" );
				JSONObject segment = segments.getJSONObject( 0 );
				JSONObject airline = segment.getJSONObject( "airline" );
			} catch ( Exception e ) {
				continue;
			}

			Travel travelEntry = new Travel( );
			travelEntry.setFlightID( id );
			travelEntry.setGroupID( groupID );
			travelEntry.setGroupDate( groupDate );
			travelEntry.setTravelSite( "Kayak" );
			travelEntry.setAirline( retrieveAirline( obj ) );
			travelEntry.setPrice( retrievePrice( obj ) );
			travelEntry.setDepartureTime( retrieveDepartureTime( obj ) );
			travelEntry.setArrivalTime( retrieveArrivalTime( obj ) );
			travelEntry.setFlightDuration( retrieveFlightDuration( obj ) );
			travelEntry.setStops( retrieveStops( obj ) );

			/*
			 * Use reflection to set all layover times
			 */
			if ( travelEntry.getStops( ) > 0 ) {
				for ( int j = 0; j < travelEntry.getStops( ); j++ ) {
					Method method = Travel.class.getMethod( "setLayoverTime" + ( j + 1 ), String.class );
					method.invoke( travelEntry, retrieveLayoverTime( obj, j ) );
				}
			}

			/*
			 * Use reflection to set all travel times
			 */
			for ( int j = 0; j < travelEntry.getStops( ) + 1; j++ ) {
				Method method = Travel.class.getMethod( "setTravelTime" + ( j + 1 ), String.class );
				method.invoke( travelEntry, retrieveTravelTime( obj, j ) );
			}

			/*
			 * Use reflection to set all flight numbers
			 */
			for ( int j = 0; j < travelEntry.getStops( ) + 1; j++ ) {
				Method method = Travel.class.getMethod( "setFlightNumber" + ( j + 1 ), String.class );
				method.invoke( travelEntry, retrieveFlightNumber( obj, j ) );
			}

			expediaRowResults.add( travelEntry );

		}

		return expediaRowResults;
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

	public static Map< Integer, String > getFlights( int[] flight_ids ) throws IOException, SQLException {
		String whereStatement = "";
		if ( flight_ids != null ) {
			StringBuffer sb = new StringBuffer( );
			for ( int i = 0; i < flight_ids.length - 1; i++ ) {
				sb.append( flight_ids[ i ] + "," );
			}
			sb.append( flight_ids[ flight_ids.length - 1 ] );
			whereStatement = " WHERE id in (" + sb.toString( ) + ") ";
		}
		Connection con = Utilities.getDefaultCon( );
		PreparedStatement statement = con.prepareStatement( "SELECT * FROM flight" + whereStatement + ";" );
		ResultSet recordSet = statement.executeQuery( );

		Map< Integer, String > flights = new HashMap<>( );
		Date now = new Date( );
		while ( recordSet.next( ) ) {

			Calendar departure = Calendar.getInstance( );
			departure.set( Integer.parseInt( recordSet.getString( "DEPARTURE_YEAR" ) ), Integer.parseInt( recordSet.getString( "DEPARTURE_MONTH" ) ) - 1, Integer.parseInt( recordSet.getString( "DEPARTURE_DAY" ) ) );
			if ( now.after( departure.getTime( ) ) ) {
				System.out.println( "Dates of flight to log are already in the past. Skipping!" );
				continue;
			}

			int flightID = Integer.parseInt( recordSet.getString( "ID" ) );
			
//			String doOrPopulateForm = "&do=y";
//			String flightType;
//			if(	recordSet.getString( "ROUTE" ) == "roundtrip" ) {
//				flightType = "&ft=rt";
//			} else if ( recordSet.getString( "ROUTE" ) == "oneway" ) {
//				flightType = "&ft=ow";
//			}
//			String nonStopPrefered = "&ns=n";
//			String cabinClass = "&cb=e";
//			String travelers = "&pa=" + recordSet.getString( "ADULTS" );
//			String fromAirport = "&l1=" + recordSet.getString( "FROM_AIRPORT_CODE" );
//			String outbountTimeOfDayPrefered = "&t1=a";
//			&df=us1
//			&d1=1%2F10%2F2014
//			&depart_flex=0
//			&r1=y
//			&l2=LAX
//			&t2=a
//			&d2=1%2F17%2F2014
//			&return_flex=0
//			&r2=y
//			&b=
			
			
//			String flightURL = "http://www.kayak.com/s/search/air?ai=kayaksample&p=&do=" + recordSet.getString( "ROUTE" ) + "&" + "leg1=" + "from:(" + recordSet.getString( "FROM_AIRPORT_CODE" ) + ")," + "to:(" + recordSet.getString( "TO_AIRPORT_CODE" ) + ")," + "departure:" + recordSet.getString( "DEPARTURE_MONTH" ) + "%2F" + recordSet.getString( "DEPARTURE_DAY" ) + "%2F" + recordSet.getString( "DEPARTURE_YEAR" ) + "TANYT&" + "leg2=" + "from:(" + recordSet.getString( "TO_AIRPORT_CODE" ) + ")," + "to:(" + recordSet.getString( "FROM_AIRPORT_CODE" ) + ")," + "departure:" + recordSet.getString( "RETURN_MONTH" ) + "%2F" + recordSet.getString( "RETURN_DAY" ) + "%2F" + recordSet.getString( "RETURN_YEAR" ) + "TANYT&" + "passengers=" + "children:" + recordSet.getString( "CHILDREN" ) + "," + "adults:" + recordSet.getString( "ADULTS" ) + "," + "seniors:" + recordSet.getString( "SENIORS" ) + "," + "infantinlap:Y&" + "options=cabinclass:coach," + "nopenalty:N," + "sortby:" + recordSet.getString( "SORT_BY" ) + "&" + "mode=search";
			String flightURL = "http://www.kayak.com/s/search/air?ai=kayaksample&p=&do=y&ft=rt&ns=n&cb=e&pa=2&l1=MSP&t1=a&df=us1&d1=1%2F1%2F2014&depart_flex=0&r1=y&l2=SJU&t2=a&d2=1%2F18%2F2014&return_flex=0&r2=y&b=";			
			flights.put( flightID, flightURL );

		}
		statement.close( );

		return flights;
	}

}
