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

public class ExpediaSite extends AbstractSite {

	public ArrayList< Flight > getFlights( Integer id, String jsondata ) throws IOException, ClassNotFoundException, SQLException, JSONException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ParseException {

		ArrayList< Flight > expediaRowResults = new ArrayList< Flight >( );
		final JSONArray array = new JSONArray( jsondata );

		for ( int i = 0; i < array.length( ); i++ ) {

			JSONObject obj = array.getJSONObject( i );

			/*
			 * Not set up to handle the "Roundtrip - Expedia Bargain Fare" flights. If this row is a bargain trip, it won't find the airline segment and throw
			 * an exception. Until I develop that ability, skip the segment... :(
			 */
			try {

				JSONObject legs = obj.getJSONArray( "legs" ).getJSONObject( 0 );
				JSONArray segments = legs.getJSONArray( "segments" );
				JSONObject segment = segments.getJSONObject( 0 );
				JSONObject airline = segment.getJSONObject( "airline" );

			} catch ( Exception e ) {
				continue;
			}

			Flight travelEntry = new Flight( );
			travelEntry.setFlightID( id );
			travelEntry.setGroupID( groupID );
			travelEntry.setGroupDate( groupDate );
			travelEntry.setTravelSite( "Expedia" );
			travelEntry.setAirline( retrieveAirline( obj ) );
			String price = retrievePrice( obj );
			if( price != null) {
				travelEntry.setPrice( price );
			} else {
				continue;
			}
			travelEntry.setDepartureTime( retrieveDepartureTime( obj ) );
			travelEntry.setArrivalTime( retrieveArrivalTime( obj ) );
			travelEntry.setFlightDuration( retrieveFlightDuration( obj ) );
			travelEntry.setStops( retrieveStops( obj ) );

			if ( travelEntry.getStops( ) > 0 ) {

				for ( int j = 0; j < travelEntry.getStops( ); j++ ) {

					Method method = Flight.class.getMethod( "setLayoverTime" + ( j + 1 ), String.class );
					method.invoke( travelEntry, retrieveLayoverTime( obj, j ) );

				}

			}

			for ( int j = 0; j < travelEntry.getStops( ) + 1; j++ ) {

				Method method = Flight.class.getMethod( "setTravelTime" + ( j + 1 ), String.class );
				method.invoke( travelEntry, retrieveTravelTime( obj, j ) );

			}

			for ( int j = 0; j < travelEntry.getStops( ) + 1; j++ ) {

				Method method = Flight.class.getMethod( "setFlightNumber" + ( j + 1 ), String.class );
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
				String flightURL = "http://www.expedia.com/Flights-Search?trip=" + recordSet.getString( "ROUTE" ) + "&" + "leg1=" + "from:(" + recordSet.getString( "FROM_AIRPORT_CODE" ) + ")," + "to:(" + recordSet.getString( "TO_AIRPORT_CODE" ) + ")," + "departure:" + recordSet.getString( "DEPARTURE_MONTH" ) + "%2F" + recordSet.getString( "DEPARTURE_DAY" ) + "%2F" + recordSet.getString( "DEPARTURE_YEAR" ) + "TANYT&" + "leg2=" + "from:(" + recordSet.getString( "TO_AIRPORT_CODE" ) + ")," + "to:(" + recordSet.getString( "FROM_AIRPORT_CODE" ) + ")," + "departure:" + recordSet.getString( "RETURN_MONTH" ) + "%2F" + recordSet.getString( "RETURN_DAY" ) + "%2F" + recordSet.getString( "RETURN_YEAR" ) + "TANYT&" + "passengers=" + "children:" + recordSet.getString( "CHILDREN" ) + "," + "adults:" + recordSet.getString( "ADULTS" ) + "," + "seniors:" + recordSet.getString( "SENIORS" ) + "," + "infantinlap:Y&" + "options=cabinclass:coach," + "nopenalty:N," + "sortby:" + recordSet.getString( "SORT_BY" ) + "&" + "mode=search";
				flights.put( flightID, flightURL );
	
			}
			statement.close( );

		} catch (Exception e) {
			throw e;
		}
		
		return flights;

	}
	
}