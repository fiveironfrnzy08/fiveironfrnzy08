package org.ryangray.airfare;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class AirfareLogger implements Runnable {

	public static void main( String[] args ) throws ClassNotFoundException, SQLException, InterruptedException, AddressException, MessagingException {

		AirfareLogger al = new AirfareLogger( );
		al.run( );

	}

	@Override
	public void run( ) {

		try {

//			ExpediaSite expedia = new ExpediaSite( );
//			Map< Integer, String > expediaTripInfo = expedia.getTripInfo( );
//			List< List< Flight > > expediaTrips = new ArrayList<>( );
//
//			for ( Integer id: expediaTripInfo.keySet( ) ) {
//
//				String url = expediaTripInfo.get( id );
//
//				System.out.println( "Getting trip JSON for flight " + id );
//				expediaTrips.add( expedia.getFlights( id, url ) );
//
//			}
//
//			commitFlights( expediaTrips );


			KayakSite kayak = new KayakSite( );
			Map< Integer, String > kayakTripInfo = kayak.getTripInfo( 10 );
			List< List< Flight > > kayakTrips = new ArrayList<>( );

			for ( Integer id: kayakTripInfo.keySet( ) ) {

				String url = kayakTripInfo.get( id );

				List< String > airports = new LinkedList<>( Arrays.asList( new String[] { "ATL", "ANC", "AUS", "BWI", "BOS", "CLT", "MDW", "ORD", "CVG", "CLE", "CMH", "DFW", "DEN", "DTW", "FLL", "RSW", "BDL", "HNL", "IAH", "HOU", "IND", "MCI", "LAS", "LAX", "MEM", "MIA", "MSP", "BNA", "MSY", "JFK", "LGA", "EWR", "OAK", "ONT", "MCO", "PHL", "PHX", "PIT", "PDX", "RDU", "SMF", "SLC", "SAT", "SAN", "SFO", "SJC", "SNA", "SEA", "STL", "TPA", "IAD", "DCA" } ) );
				System.out.println( "Getting trip JSON for flight " + id );
				for( String airport: airports) {
					kayakTrips.add( kayak.getFlights( id, url.replace( "IAD", airport ) ) );
				}
				

			}

			// compileAndLogToDB( kayakTravel );by 
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

	}

	private void commitFlights( List< List< Flight > > trips ) throws NumberFormatException, Exception {

		Double lowestPrice;

		for ( List< Flight > flights: trips ) {

			lowestPrice = Utilities.getMinimumPrice( flights.get( 0 ).getFlightID( ) );

			for ( Flight flight: flights ) {

				double pricePerTicket = Double.parseDouble( flight.getPrice( ) ) / flight.getPassengerCount( );

				if ( pricePerTicket < lowestPrice ) {

					lowestPrice = pricePerTicket;
					Utilities.setMinimumPrice( flight, pricePerTicket );

				}

				System.out.println( "Saving flight to database" );

				flight.save( );

			}

			lowestPrice = 0.0;

		}

	}

}
