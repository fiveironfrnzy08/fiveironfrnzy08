package org.ryangray.takeoff;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TakeOffMain {

	private static String						flight			= "16";
	private static Integer						threadsToRun	= null;
	private static HashMap< String, Double >	flightResults	= new HashMap<>( );

	public static void main( String[] args ) throws InterruptedException, ExecutionException, TimeoutException {

		System.out.println( new Date( ) );

		/**
		 * Top 52 US Airpots according to http://www.nationsonline.org/oneworld/major_US_airports.htm
		 */
		List< String > airports = new LinkedList<>( Arrays.asList( new String[] { "ATL", "ANC", "AUS", "BWI", "BOS", "CLT", "MDW", "ORD", "CVG", "CLE", "CMH", "DFW", "DEN", "DTW", "FLL", "RSW", "BDL", "HNL", "IAH", "HOU", "IND", "MCI", "LAS", "LAX", "MEM", "MIA", "MSP", "BNA", "MSY", "JFK", "LGA", "EWR", "OAK", "ONT", "MCO", "PHL", "PHX", "PIT", "PDX", "RDU", "SMF", "SLC", "SAT", "SAN", "SFO", "SJC", "SNA", "SEA", "STL", "TPA", "IAD", "DCA" } ) );
		if ( threadsToRun == null ) {
			threadsToRun = airports.size( );
		}

		ExecutorService executor = Executors.newFixedThreadPool( threadsToRun );
		ExecutorCompletionService< HashMap< String, Double > > service = new ExecutorCompletionService<>( executor );

		for ( String airport: airports ) {

			Callable< HashMap< String, Double >> takeOff = new TakeOff( flight, airport );
			service.submit( takeOff );

		}

		int remainingTasks = airports.size( );
		while ( remainingTasks > 0 ) {

			Future< HashMap< String, Double >> take = service.take( );
			HashMap< String, Double > get = take.get( 1, TimeUnit.SECONDS );
			for ( Entry< String, Double > entry: get.entrySet( ) ) {
				System.out.println( "Adding: " + entry.getKey( ) + " - $" + entry.getValue( ) + " . Remaining: " + ( remainingTasks - 1 ) + "/" + airports.size( ) );
			}
			flightResults.putAll( get );
			remainingTasks -= 1;

		}
		executor.shutdownNow( );

		flightResults = sortByValue( flightResults );
		for ( Entry< String, Double > entry: flightResults.entrySet( ) ) {
			System.out.println( entry.getKey( ) + ": " + entry.getValue( ) );
		}

		System.out.println( new Date( ) );

	}

	static HashMap< String, Double > sortByValue( HashMap< String, Double > map ) {

		List< Entry< String, Double > > list = new LinkedList<>( map.entrySet( ) );
		Collections.sort( list, new Comparator< Entry< String, Double > >( ) {

			@Override
			public int compare( Entry< String, Double > entry1, Entry< String, Double > entry2 ) {
				return entry1.getValue( ).compareTo( entry2.getValue( ) );
			}

		} );

		Collections.reverse( list );

		HashMap< String, Double > result = new LinkedHashMap<>( );
		for ( Entry< String, Double > entry: list ) {
			result.put( entry.getKey( ), entry.getValue( ) );
		}

		return result;

	}

}
