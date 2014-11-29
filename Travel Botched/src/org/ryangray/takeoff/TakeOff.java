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
import java.util.Map.Entry;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.ryangray.airfare.Constants;
import org.ryangray.airfare.Utilities;

public class TakeOff extends Thread implements Runnable {

	public static Connection	con	= null;
	// Top 52 US Airpots according to http://www.nationsonline.org/oneworld/major_US_airports.htm
//	public String[] airports = new String[] {"ATL","ANC","AUS","BWI","BOS","CLT","MDW","ORD","CVG","CLE","CMH","DFW","DEN","DTW","FLL","RSW","BDL","HNL","IAH","HOU","IND","MCI","LAS","LAX","MEM","MIA","MSP","BNA","MSY","JFK","LGA","EWR","OAK","ONT","MCO","PHL","PHX","PIT","PDX","RDU","SMF","SLC","SAT","SAN","SFO","SJC","SNA","SEA","STL","TPA","IAD","DCA"};
	public String[] airports = null;
	public String flightID = null;
	public String name = null;
	
	public TakeOff( String flight, String[] list, String name ) {
		airports = list;
		flightID = flight;
		this.name = name;
	}

	public static void main( String[] args ) throws ClassNotFoundException, SQLException, InterruptedException, AddressException, MessagingException {

//		TakeOff al = new TakeOff( "8", new String[] {"ATL","ANC","AUS","BWI","BOS","CLT","MDW","ORD","CVG","CLE","CMH","DFW","DEN","DTW","FLL","RSW","BDL","HNL","IAH","HOU","IND","MCI","LAS","LAX","MEM","MIA","MSP","BNA","MSY","JFK","LGA","EWR","OAK","ONT","MCO","PHL","PHX","PIT","PDX","RDU","SMF","SLC","SAT","SAN","SFO","SJC","SNA","SEA","STL","TPA","IAD","DCA"}, new ArrayList<String>( ), "main" );
//		al.run( );

	}

	@Override
	public void run( ) {

		try ( Connection con = TakeOff.getDefaultCon( ) ){

			Launchpad launchpad = new Launchpad( );
			launchpad.setConnection( con );
			Map< String, String > expediaTrips = new HashMap<>( );
			
			for ( String airport: airports ) {
				List< String > expediaTripInfo = getTripInfo( airport, flightID );
				for ( String url: expediaTripInfo ) {
	
//					System.out.println( "Getting trip JSON for flight from " + airport );
					String expediaJSON = launchpad.getJSON( url );
//					System.out.println( "Parsing trip JSON for flight from " + airport );
					expediaTrips.put( airport, launchpad.getLowestPriceFlight( expediaJSON ) );
//					System.out.println( airport + ": " + expediaTrips.get( airport ) );
	
				}
			}
			
			for( Entry< String, String > trip: expediaTrips.entrySet( ) ) {
				System.out.println( this.name + ": " + trip.getKey( ) + ": " + trip.getValue( ) );
			}

		} catch ( Exception e ) {

			e.printStackTrace( );
			try {
				Utilities.sendErrorEmail( e );
			} catch ( AddressException e1 ) {
				// TODO Auto-generated catch block
				e1.printStackTrace( );
			} catch ( MessagingException e1 ) {
				// TODO Auto-generated catch block
				e1.printStackTrace( );
			}

		}

	}

	public static Connection getDefaultCon( ) throws SQLException, ClassNotFoundException, InterruptedException {

		if ( con != null && !con.isClosed( ) ) {
			return con;
		}

		for ( int i = 0; i < 3; i++ ) {

//			System.out.println( "Getting connection; Attempt: " + ( i + 1 ) );
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
		try ( Connection con = getDefaultCon( ) ) {
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
		} catch( Exception e ) {
			e.printStackTrace( );
		}
		return null;

	}

}
