package org.ryangray.airfare;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.commons.lang3.text.WordUtils;

public class Flight {

	private int			flightID;
	private int			groupID;
	private Timestamp	groupDate;
	private String		travelSite;
	private String		airline;
	private String		price;
	private Timestamp	departureTime;
	private Timestamp	arrivalTime;
	private String		flightDuration;
	private int			stops;
	private String		layoverTime1;
	private String		layoverTime2;
	private String		layoverTime3;
	private String		layoverTime4;
	private String		layoverTime5;
	private String		travelTime1;
	private String		travelTime2;
	private String		travelTime3;
	private String		travelTime4;
	private String		travelTime5;
	private String		flightNumber1;
	private String		flightNumber2;
	private String		flightNumber3;
	private String		flightNumber4;
	private String		flightNumber5;

	public int getFlightID( ) {
		return flightID;
	}

	public int getGroupID( ) {
		return groupID;
	}

	public String getTravelSite( ) {
		return travelSite;
	}

	public String getAirline( ) {
		return airline;
	}

	public String getPrice( ) {
		return price;
	}

	public Timestamp getDepartureTime( ) {
		return departureTime;
	}

	public Timestamp getArrivalTime( ) {
		return arrivalTime;
	}

	public String getFlightDuration( ) {
		return flightDuration;
	}

	public int getStops( ) {
		return stops;
	}

	public String getLayoverTime1( ) {
		return layoverTime1;
	}

	public String getLayoverTime2( ) {
		return layoverTime2;
	}

	public String getLayoverTime3( ) {
		return layoverTime3;
	}

	public String getLayoverTime4( ) {
		return layoverTime4;
	}

	public String getLayoverTime5( ) {
		return layoverTime5;
	}

	public String getTravelTime1( ) {
		return travelTime1;
	}

	public String getTravelTime2( ) {
		return travelTime2;
	}

	public String getTravelTime3( ) {
		return travelTime3;
	}

	public String getTravelTime4( ) {
		return travelTime4;
	}

	public String getTravelTime5( ) {
		return travelTime5;
	}

	public String getFlightNumber1( ) {
		return flightNumber1;
	}

	public String getFlightNumber2( ) {
		return flightNumber2;
	}

	public String getFlightNumber3( ) {
		return flightNumber3;
	}

	public String getFlightNumber4( ) {
		return flightNumber4;
	}

	public String getFlightNumber5( ) {
		return flightNumber5;
	}

	public Timestamp getGroupDate( ) {
		return groupDate;
	}

	public void setFlightID( int flightID ) {
		this.flightID = flightID;
	}

	public void setGroupID( int groupID ) {
		this.groupID = groupID;
	}

	public void setTravelSite( String travelSite ) {
		this.travelSite = travelSite;
	}

	public void setAirline( String airline ) {
		this.airline = airline;
	}

	public void setPrice( String price ) {
		this.price = price;
	}

	public void setDepartureTime( long departureTime ) throws ParseException {
		java.sql.Timestamp sqlDate = new Timestamp( departureTime );
		this.departureTime = sqlDate;
	}

	public void setArrivalTime( long arrivalTime ) throws ParseException {
		java.sql.Timestamp sqlDate = new Timestamp( arrivalTime );
		this.arrivalTime = sqlDate;
	}

	public void setFlightDuration( String flightDuration ) {
		this.flightDuration = flightDuration;
	}

	public void setStops( int stops ) {
		this.stops = stops;
	}

	public void setLayoverTime1( String layoverTime1 ) {
		this.layoverTime1 = layoverTime1;
	}

	public void setLayoverTime2( String layoverTime2 ) {
		this.layoverTime2 = layoverTime2;
	}

	public void setLayoverTime3( String layoverTime3 ) {
		this.layoverTime3 = layoverTime3;
	}

	public void setLayoverTime4( String layoverTime4 ) {
		this.layoverTime4 = layoverTime4;
	}

	public void setLayoverTime5( String layoverTime5 ) {
		this.layoverTime5 = layoverTime5;
	}

	public void setTravelTime1( String travelTime1 ) {
		this.travelTime1 = travelTime1;
	}

	public void setTravelTime2( String travelTime2 ) {
		this.travelTime2 = travelTime2;
	}

	public void setTravelTime3( String travelTime3 ) {
		this.travelTime3 = travelTime3;
	}

	public void setTravelTime4( String travelTime4 ) {
		this.travelTime4 = travelTime4;
	}

	public void setTravelTime5( String travelTime5 ) {
		this.travelTime5 = travelTime5;
	}

	public void setFlightNumber1( String flightNumber1 ) {
		this.flightNumber1 = flightNumber1;
	}

	public void setFlightNumber2( String flightNumber2 ) {
		this.flightNumber2 = flightNumber2;
	}

	public void setFlightNumber3( String flightNumber3 ) {
		this.flightNumber3 = flightNumber3;
	}

	public void setFlightNumber4( String flightNumber4 ) {
		this.flightNumber4 = flightNumber4;
	}

	public void setFlightNumber5( String flightNumber5 ) {
		this.flightNumber5 = flightNumber5;
	}

	public void setGroupDate( Timestamp groupDate ) {
		this.groupDate = groupDate;
	}

	public void save( ) throws SQLException, AddressException, MessagingException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, InterruptedException {
		
		Connection con = Utilities.getDefaultCon( );
		try {
			
			StringBuffer preparedStatement = new StringBuffer( );
			preparedStatement.append( "INSERT INTO `" + Constants.DATABASE + "`.`travel` ( " );
			DatabaseMetaData meta = con.getMetaData( );
			ResultSet rs = meta.getColumns( null, Constants.DATABASE, "travel", null );
			rs.next( ); // SKIP FIRST COLUMN, ID
			while ( rs.next( ) ) {
				preparedStatement.append( rs.getString( "COLUMN_NAME" ) + ", " );
			}
			preparedStatement.delete( preparedStatement.length( ) - 2, preparedStatement.length( ) );
			preparedStatement.append( " ) VALUES ( " );
			Field[] fields = Flight.class.getDeclaredFields( );
			for ( int i = 0; fields.length > i; i++ ) {
				preparedStatement.append( "?, " );
			}
			preparedStatement.delete( preparedStatement.length( ) - 2, preparedStatement.length( ) );
			preparedStatement.append( " );" );
			PreparedStatement statement = con.prepareStatement( preparedStatement.toString( ) );
			int i = 1;
			for ( Field field: fields ) {
				Method method = PreparedStatement.class.getMethod( "set" + WordUtils.capitalize( field.getType( ).getSimpleName( ) ), int.class, field.getType( ) );
				method.invoke( statement, i++, field.get( this ) );
			}
			statement.executeUpdate( );
			statement.close( );
			
		} catch (Exception e) {
			throw e;
		} finally {
			con.close( );
		}

	}

	public int getPassengerCount( ) throws Exception {

		try ( Connection con = Utilities.getDefaultCon( ) ) {
			
			PreparedStatement statement = con.prepareStatement( "SELECT flight.children, flight.adults, flight.seniors FROM flight where flight.id = " + this.flightID + ";" );
			ResultSet result = statement.executeQuery( );
			int passengers = 0;
			while ( result.next( ) ) {
				passengers = result.getInt( 1 ) + result.getInt( 2 ) + result.getInt( 3 );
			}
			statement.close( );
			if ( passengers > 0 ) {
				return passengers;
			} else {
				throw new Exception( "Invalid passengers count" );
			}
			
		} catch (Exception e) {
			throw e;
		}

	}

}
