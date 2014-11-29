package org.ryangray.airfare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.ryangray.airfare.Utilities;

@SuppressWarnings( "unused" )
public class Test {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main( String[] args ) throws Exception {
		
		addFlight( "23" );
		addFlight( "24" );
		addFlight( "25" );
		
	}

	private static void addFlight( String day ) throws Exception {
		
		Map< String, String > parameters = new HashMap<>( );
		parameters.put( "FROM_AIRPORT_CODE", "CUN" );
		parameters.put( "TO_AIRPORT_CODE", "MSP" );
		parameters.put( "ROUTE", "oneway" );
		parameters.put( "DEPARTURE_MONTH", "10" );
		parameters.put( "DEPARTURE_DAY", day );
		parameters.put( "DEPARTURE_YEAR", "2014" );
		parameters.put( "RETURN_DAY", null );
		parameters.put( "RETURN_MONTH", null );
		parameters.put( "RETURN_YEAR", null );
		parameters.put( "CHILDREN", "0" );
		parameters.put( "ADULTS", "1" );
		parameters.put( "SENIORS", "0" );
		parameters.put( "SORT_BY", "price" );
		
		System.out.println( Utilities.addFlightsToMonitorExpedia( parameters ) );
		
	}

}
