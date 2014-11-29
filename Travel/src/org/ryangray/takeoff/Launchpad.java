package org.ryangray.takeoff;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ryangray.airfare.AbstractSite;
import org.ryangray.airfare.Constants;
import org.ryangray.airfare.Flight;

public class Launchpad extends AbstractSite {

	private Connection	connection	= null;

	public String getLowestPriceFlight( String jsondata ) throws Exception {

		final JSONArray array = new JSONArray( jsondata );
		Double lowestPrice = 9999.0;

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

			Double price = null;
			price = retrievePrice( obj );
			if ( price != null && price < lowestPrice ) {
				lowestPrice = price;
			}

		}

		DecimalFormat df = new DecimalFormat( "#.00" );
		return df.format( lowestPrice ).toString( );

	}

	private static Double retrievePrice( JSONObject obj ) throws Exception {

		double price = obj.getDouble( "totalPrice" );
		DecimalFormat df = new DecimalFormat( "#.00" );

		if ( df.format( price ).toString( ).length( ) > 10 ) {
//			System.out.println( "\"totalPrice\" object in JSON contains an invalid price. Example seen on webpage was where price said \"VIEW DETAILS\" instead of listing a price... Scurvy dogs.\nContains: " + obj.getDouble( "totalPrice" ) );
			return null;
		}

		return price;

	}

	public void setConnection( Connection con ) {
		connection = con;
	}

	public Connection getConnection( ) throws SQLException, ClassNotFoundException, InterruptedException {

		if ( connection != null && !connection.isClosed( ) ) {
			return connection;
		}

		for ( int i = 0; i < 3; i++ ) {

//			System.out.println( "Getting connection; Attempt: " + ( i + 1 ) );
			Class.forName( "com.mysql.jdbc.Driver" );

			try {

				connection = DriverManager.getConnection( Constants.SERVER + "/" + Constants.DATABASE, Constants.SERVER_USER, Constants.SERVER_PASS );
				return connection;

			} catch ( Exception e ) {

				e.printStackTrace( );
				System.out.println( "Attempt: " + i + 1 );

				if ( i == 2 ) {
					throw e;
				}

			}

			Thread.sleep( 10000 );

		}

		return null;
	}

	@Override
	public List< Flight > getFlights( Integer flightID, String jsondata ) throws IOException, ClassNotFoundException, SQLException, JSONException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ParseException {
		// TODO Auto-generated method stub
		return null;
	}

}