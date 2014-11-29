package org.ryangray.takeoff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TakeOffMain {

	private static List< Thread >	threadsRunning		= new ArrayList< Thread >( );
	private static String			flight				= "9";
	private static int				threadsToRun		= 52;
	private static int				airlinesPerThread	= 1;

	public static void main( String[] args ) throws InterruptedException {

		List< String > airports = new LinkedList<>( Arrays.asList( new String[] { "ATL","ANC","AUS","BWI","BOS","CLT","MDW","ORD","CVG","CLE","CMH","DFW","DEN","DTW","FLL","RSW","BDL","HNL","IAH","HOU","IND","MCI","LAS","LAX","MEM","MIA","MSP","BNA","MSY","JFK","LGA","EWR","OAK","ONT","MCO","PHL","PHX","PIT","PDX","RDU","SMF","SLC","SAT","SAN","SFO","SJC","SNA","SEA","STL","TPA","IAD","DCA" } ) );
		Iterator< String > airportIt = airports.iterator( );
//		threadsToRun = airports.size( );
		System.out.println( threadsToRun );

		while ( airportIt.hasNext( ) ) {

			/**
			 * Run 5 threads
			 */
			if ( threadsRunning.size( ) < threadsToRun ) {

				String[] airlinesToRun = new String[ airlinesPerThread ];
				for ( int i = 0; i < airlinesPerThread; i++ ) {

					if ( airportIt.hasNext( ) ) {

						airlinesToRun[ i ] = airportIt.next( );
						airportIt.remove( );

					}

				}
				Thread thread = new TakeOff( flight, airlinesToRun, "Thread" );
				thread.start( );
				threadsRunning.add( thread );
				Thread.sleep( 2000 );

			}

		}

	}

}
