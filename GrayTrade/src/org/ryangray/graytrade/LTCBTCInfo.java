package org.ryangray.graytrade;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.commons.lang3.text.WordUtils;

public class LTCBTCInfo {

	private Timestamp	lastRun;
	private double	average;
	private double	threshold;
	private double	ltcAsset;
	private double	btcAsset;
	private double	fee;
	private boolean	live;
	private int noTradeReset;
	private int noTradeCount;
	private Timestamp	haltDate;
	private String haltReason;
	private boolean sellLTC;
	private boolean resetAverage;
	
	public Timestamp getHaltDate( ) {
		return haltDate;
	}

	public void setHaltDate( Timestamp haltDate ) {
		this.haltDate = haltDate;
	}

	public String getHaltReason( ) {
		return haltReason;
	}

	public void setHaltReason( String haltReason ) {
		this.haltReason = haltReason;
	}

	public boolean getResetAverage( ) {
		return resetAverage;
	}

	public void setResetAverage( boolean resetAverage ) {
		this.resetAverage = resetAverage;
	}

	public Date getLastRun( ) {
		return lastRun;
	}

	public void setLastRun( Timestamp lastRun ) {
		this.lastRun = lastRun;
	}

	public Double getAverage( ) {
		return average;
	}

	public void setAverage( Double average ) {
		this.average = average;
	}

	public Double getThreshold( ) {
		return threshold;
	}

	public void setThreshold( Double threshold ) {
		this.threshold = threshold;
	}

	public Double getLtcAsset( ) {
		return ltcAsset;
	}

	public void setLtcAsset( Double ltcAsset ) {
		this.ltcAsset = ltcAsset;
	}

	public Double getBtcAsset( ) {
		return btcAsset;
	}

	public void setBtcAsset( Double btcAsset ) {
		this.btcAsset = btcAsset;
	}

	public Double getFee( ) {
		return fee;
	}

	public void setFee( Double fee ) {
		this.fee = fee;
	}

	public boolean isLive( ) {
		return live;
	}

	public void setLive( boolean live ) {
		this.live = live;
	}

	public int getNoTradeReset( ) {
		return noTradeReset;
	}

	public void setNoTradeReset( int noTradeReset ) {
		this.noTradeReset = noTradeReset;
	}

	public int getNoTradeCount( ) {
		return noTradeCount;
	}

	public void setNoTradeCount( int noTradeCount ) {
		this.noTradeCount = noTradeCount;
	}

	public boolean getSellLTC( ) {
		return sellLTC;
	}

	public void setSellLTC( boolean sellLTC ) {
		this.sellLTC = sellLTC;
	}

	public LTCBTCInfo( ) throws SQLException {

		Connection con = DriverManager.getConnection( "jdbc:mysql://ryangray.org:3306/master", "root", "ryan135244" );
		StringBuffer preparedStatement = new StringBuffer( );

		preparedStatement.append( "SELECT * FROM master.graytrade_info order by id desc limit 1;" );
		PreparedStatement statement = con.prepareStatement( preparedStatement.toString( ) );
		ResultSet records = statement.executeQuery( );

		while ( records.next( ) != false ) {

			this.lastRun = records.getDate( "last_run" ) != null ? new Timestamp( records.getDate( "last_run" ).getTime( ) ) : null;
			this.average = records.getDouble( "average" );
			this.threshold = records.getDouble( "threshold" );
			this.ltcAsset = records.getDouble( "ltc_asset" );
			this.btcAsset = records.getDouble( "btc_asset" );
			this.fee = records.getDouble( "fee" );
			this.live = Arrays.asList( new String[] { "TRUE", "1", "YES" } ).contains( records.getString( "live" ).toUpperCase( ) ) ? true : false;
			this.noTradeReset = records.getInt( "no_trade_reset" );
			this.noTradeCount = records.getInt( "no_trade_count" );
			this.haltDate = records.getDate( "halt_date" ) != null ? new Timestamp( records.getDate( "halt_date" ).getTime( ) ) : null;
			this.haltReason = records.getString( "halt_reason" );
			this.sellLTC = Arrays.asList( new String[] { "TRUE", "1", "YES" } ).contains( records.getString( "sell_ltc" ).toUpperCase( ) ) ? true : false;
			this.resetAverage = Arrays.asList( new String[] { "TRUE", "1", "YES" } ).contains( records.getString( "reset_average" ).toUpperCase( ) ) ? true : false;
			
		}

	}
	
	public void save( ) throws SQLException, AddressException, MessagingException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, InterruptedException {
		
		Connection con = Utilities.getDefaultCon( );
		try {
			
			StringBuffer preparedStatement = new StringBuffer( );
			preparedStatement.append( "UPDATE `" + Constants.DATABASE + "`.`" + Constants.LTC_BTC_INFO_TABLE + "` SET " );
			DatabaseMetaData meta = con.getMetaData( );
			ResultSet rs = meta.getColumns( null, Constants.DATABASE, Constants.LTC_BTC_INFO_TABLE, null );
			rs.next( ); // SKIP FIRST COLUMN, ID
			while ( rs.next( ) ) {
				preparedStatement.append( rs.getString( "COLUMN_NAME" ) + " = ?, " );
			}
			preparedStatement.delete( preparedStatement.length( ) - 2, preparedStatement.length( ) );
			preparedStatement.append( " ORDER BY id DESC LIMIT 1 " );
			Field[] fields = LTCBTCInfo.class.getDeclaredFields( );

			PreparedStatement statement = con.prepareStatement( preparedStatement.toString( ) );
			int i = 1;
			for ( Field field: fields ) {
//				if ( field.get( this ) instanceof java.sql.Date ) {
//					statement.setDate( i, java.sql.Date.valueOf( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format( this.lastRun ) ) );
//				}
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

	public static void main( String[] args ) throws SQLException {

		LTCBTCInfo info = new LTCBTCInfo( );

		System.out.println( info.getLastRun( ) );
		System.out.println( info.getAverage( ) );
		System.out.println( info.getThreshold( ) );
		System.out.println( info.getLtcAsset( ) );
		System.out.println( info.getBtcAsset( ) );
		System.out.println( info.getFee( ) );
		System.out.println( info.getNoTradeReset( ) );
		System.out.println( info.getNoTradeCount( ) );
		System.out.println( info.isLive( ) );
		System.out.println( info.getSellLTC( ) );

	}

}
