package org.ryangray.airfare;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class KayakSite extends AbstractSite implements Runnable {

	private static String	FROM_AIRPORT_CODE	= "MSP";
	private static String	TO_AIRPORT_CODE		= "SJU";
	private static String	ROUTE				= "roundtrip";
	// private static final String ROUTE = "oneway";
	private static String	DEPARTURE_DAY		= "01";
	private static String	DEPARTURE_MONTH		= "01";
	private static String	DEPARTURE_YEAR		= "2014";
	private static String	RETURN_DAY			= "13";
	private static String	RETURN_MONTH		= "01";
	private static String	RETURN_YEAR			= "2014";
	private static String	CHILDREN			= "0";
	private static String	ADULTS				= "1";
	private static String	SENIORS				= "0";
	private static String	SORT_BY				= "price";

	public static void main( String[] args ) throws Exception {
		KayakSite kayak = new KayakSite();
		kayak.run();
	}

	@Override
	public void run( ) {
		KayakSite kayak = new KayakSite();
		String jsondata;
		jsondata = kayak.getJSON( );
		kayak.getSiteTravel( jsondata );
	}

	public ArrayList< Travel > getSiteTravel( String jsondata ) throws IOException, ClassNotFoundException, SQLException, JSONException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ArrayList< Travel > kayakRowResults = new ArrayList< Travel >();
		final JSONArray array = new JSONArray( jsondata );
		for ( int i = 0; i < array.length( ); i++ ) {
			JSONObject obj = array.getJSONObject( i );

			Travel travelEntry = new Travel( );
			travelEntry.setAirline( retrieveAirline( obj ) );
			travelEntry.setTravelSite( "Kayak" );
			travelEntry.setPrice( retrievePrice( obj ) );
			travelEntry.setLayoverCount( retrieveLayoverCount( obj ) );
			/*
			 * Use reflection to set all travel times
			 */
			for ( int j = 0; j < travelEntry.getLayoverCount( ) + 1; j++ ) {
				Method method = Travel.class.getMethod( "setTravelTime" + ( j + 1 ), String.class );
				method.invoke( travelEntry, getTravelTime( obj, j ) );
			}
			/*
			 * Use reflection to set all layover times
			 */
			for ( int j = 0; j < travelEntry.getLayoverCount( ); j++ ) {
				Method method = Travel.class.getMethod( "setLayoverTime" + ( j + 1 ), String.class );
				method.invoke( travelEntry, getLayoverTime( obj, j ) );
			}
			System.out.println( "" );
			// travelEntry.setFlightDuration( travelDurationSegmentsHashMap.get(
			// string ) );
			travelEntry.setGroupID( groupID );
			travelEntry.setGroupDate( groupDate );
			// travelEntry.save();
			kayakRowResults.add( travelEntry );
		}
		return kayakRowResults;
	}

	private static Object getLayoverTime( JSONObject obj, int j ) throws JSONException {
		JSONObject legs = obj.getJSONArray( "legs" ).getJSONObject( 0 );
		JSONArray segments = legs.getJSONArray( "segments" );
		JSONObject segment = segments.getJSONObject( j );
		JSONObject travelTime = segment.getJSONObject( "layover" );
		String minutes = StringUtils.rightPad( travelTime.get( "minutes" ).toString( ), 2, "0" );
		String hours = StringUtils.leftPad( travelTime.get( "hours" ).toString( ), 2, "0" );
		String days = StringUtils.leftPad( travelTime.get( "numOfDays" ).toString( ), 2, "0" );
		return days + ":" + hours + ":" + minutes;
	}

	private static String getTravelTime( JSONObject obj, int j ) throws JSONException {
		JSONObject legs = obj.getJSONArray( "legs" ).getJSONObject( 0 );
		JSONArray segments = legs.getJSONArray( "segments" );
		JSONObject segment = segments.getJSONObject( j );
		JSONObject travelTime = segment.getJSONObject( "totalTravelingTime" );
		String minutes = StringUtils.rightPad( travelTime.get( "minutes" ).toString( ), 2, "0" );
		String hours = StringUtils.leftPad( travelTime.get( "hours" ).toString( ), 2, "0" );
		String days = StringUtils.leftPad( travelTime.get( "numOfDays" ).toString( ), 2, "0" );
		return days + ":" + hours + ":" + minutes;
	}

	private static int retrieveLayoverCount( JSONObject obj ) throws NumberFormatException, JSONException {
		return Integer.parseInt( obj.get( "stops" ).toString( ) );
	}

	private static String retrievePrice( JSONObject obj ) throws JSONException {
		return obj.get( "totalPrice" ).toString( );
	}

	private static String retrieveAirline( JSONObject obj ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSiteURL( ) {
		return "http://www.kayak.com/#/flights/MSP-SJU/2014-01-01/2014-01-13";
	}

}