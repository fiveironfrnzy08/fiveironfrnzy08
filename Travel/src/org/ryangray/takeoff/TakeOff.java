package org.ryangray.takeoff;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.ryangray.airfare.Constants;
import org.ryangray.airfare.Utilities;

public class TakeOff extends Thread implements Callable< HashMap< String, Double > > {

	public static Connection	con				= null;
	public String				airport			= null;
	public String				flightID		= null;
	HashMap< String, Double >	flightResults	= new HashMap<>( );

	public TakeOff( String flight, String airport ) {
		this.airport = airport;
		flightID = flight;
	}

	@Override
	public HashMap< String, Double > call( ) {

		try {

			Launchpad launchpad = new Launchpad( );
			Map< String, String > expediaTrips = new HashMap<>( );

			List< String > expediaTripInfo = getTripInfo( airport, flightID );
			for ( String url: expediaTripInfo ) {

				String expediaJSON = launchpad.getJSON( url );
				expediaTrips.put( airport, launchpad.getLowestPriceFlight( expediaJSON ) );
				flightResults.put( airport, Double.parseDouble( launchpad.getLowestPriceFlight( expediaJSON ) ) );
				// System.out.println( airport + ": " + flightResults.get( airport ) );

			}

		} catch ( Exception e ) {

			e.printStackTrace( );
			try {
				Utilities.sendErrorEmail( e );
			} catch ( AddressException e1 ) {
				e1.printStackTrace( );
			} catch ( MessagingException e1 ) {
				e1.printStackTrace( );
			}

		}
		return flightResults;

	}

	public static Connection getDefaultCon( ) throws SQLException, ClassNotFoundException, InterruptedException {

		if ( con != null && !con.isClosed( ) ) {
			return con;
		}

		for ( int i = 0; i < 3; i++ ) {

			Class.forName( "com.mysql.jdbc.Driver" );

			try {

				Connection con = DriverManager.getConnection( Constants.SERVER + "/" + Constants.DATABASE, Constants.SERVER_USER, Constants.SERVER_PASS );
				return con;

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

	public List< String > getTripInfo( String airport, String flightID ) throws Exception {

		String whereStatement = " WHERE id in (" + flightID + ")";
		try (Connection con = getDefaultCon( )) {
			PreparedStatement statement = con.prepareStatement( "SELECT * FROM flight" + whereStatement + ";" );
			ResultSet recordSet = statement.executeQuery( );

			List< String > flights = new ArrayList<>( );
			Date now = new Date( );

			while ( recordSet.next( ) ) {

				Calendar departure = Calendar.getInstance( );
				departure.set( Integer.parseInt( recordSet.getString( "DEPARTURE_YEAR" ) ), Integer.parseInt( recordSet.getString( "DEPARTURE_MONTH" ) ) - 1, Integer.parseInt( recordSet.getString( "DEPARTURE_DAY" ) ) );

				if ( now.after( departure.getTime( ) ) ) {
					System.out.println( "Dates of flight to log are already in the past. Skipping!" );
					continue;
				}

				String flightURL = "http://www.expedia.com/Flights-Search?trip=" + recordSet.getString( "ROUTE" ) + "&" + "leg1=" + "from:(" + airport + ")," + "to:(" + recordSet.getString( "TO_AIRPORT_CODE" ) + ")," + "departure:" + recordSet.getString( "DEPARTURE_MONTH" ) + "%2F" + recordSet.getString( "DEPARTURE_DAY" ) + "%2F" + recordSet.getString( "DEPARTURE_YEAR" ) + "TANYT&" + "leg2=" + "from:(" + recordSet.getString( "TO_AIRPORT_CODE" ) + ")," + "to:(" + recordSet.getString( "FROM_AIRPORT_CODE" ) + ")," + "departure:" + recordSet.getString( "RETURN_MONTH" ) + "%2F" + recordSet.getString( "RETURN_DAY" ) + "%2F" + recordSet.getString( "RETURN_YEAR" ) + "TANYT&" + "passengers=" + "children:" + recordSet.getString( "CHILDREN" ) + "," + "adults:" + recordSet.getString( "ADULTS" ) + "," + "seniors:" + recordSet.getString( "SENIORS" ) + "," + "infantinlap:Y&" + "options=cabinclass:coach," + "nopenalty:N," + "sortby:" + recordSet.getString( "SORT_BY" ) + "&" + "mode=search";
				flights.add( flightURL );

			}
			statement.close( );

			return flights;
		} catch ( Exception e ) {
			e.printStackTrace( );
		}
		return null;

	}

}
