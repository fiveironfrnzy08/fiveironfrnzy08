package org.ryangray.airfare;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public class AirfareLogger implements Runnable {

	public static void main( String[] args ) throws ClassNotFoundException, SQLException, InterruptedException, AddressException, MessagingException {

		AirfareLogger al = new AirfareLogger( );
		al.run( );

	}

	@Override
	public void run( ) {

		try {

			ExpediaSite expedia = new ExpediaSite( );
			Map< Integer, String > expediaTripInfo = expedia.getTripInfo( 12,13,14,15 );
			List< List< Flight > > expediaTrips = new ArrayList<>( );

			for ( Integer id: expediaTripInfo.keySet( ) ) {

				String url = expediaTripInfo.get( id );

				System.out.println( "Getting trip JSON for flight " + id );
				String expediaJSON = expedia.getJSON( url );
				System.out.println( "Parsing trip JSON for flight " + id );
				expediaTrips.add( expedia.getFlights( id, expediaJSON ) );

			}

			commitFlights( expediaTrips );

			// KayakSite kayak = new KayakSite( );
			// Map< Integer, String > kayakFlights = kayak.getFlights( null );
			//
			// List< List< Travel > > kayakTravel = new ArrayList<>( );
			//
			// for ( Integer id: kayakFlights.keySet( ) ) {
			// String kayakJSON = kayak.getJSON( kayakFlights.get( id ) );
			// kayakTravel.add( kayak.getSiteTravel( id, kayakJSON ) );
			// }

			// compileAndLogToDB( kayakTravel );
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
