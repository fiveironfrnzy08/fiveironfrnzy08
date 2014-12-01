package org.ryangray.graytrade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;

public class BTCDOGEInfo {

	private Date	lastRun;
	private Double	average;
	private Double	threshold;
	private Double	dogeAsset;
	private Double	btcAsset;
	private Double	fee;
	private int		noTradeReset;
	private boolean	live;

	public Date getLastRun( ) {
		return lastRun;
	}

	public void setLastRun( Date lastRun ) {
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

	public Double getDogeAsset( ) {
		return dogeAsset;
	}

	public void setDogeAsset( Double dogeAsset ) {
		this.dogeAsset = dogeAsset;
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

	public BTCDOGEInfo( ) throws SQLException {

		Connection con = Utilities.getDefaultCon( );
		StringBuffer preparedStatement = new StringBuffer( );

		preparedStatement.append( "SELECT * FROM " + Constants.DATABASE + "." + Constants.DOGE_BTC_INFO + " order by id desc limit 1;" );
		PreparedStatement statement = con.prepareStatement( preparedStatement.toString( ) );
		ResultSet records = statement.executeQuery( );

		while ( records.next( ) != false ) {

			this.lastRun = records.getDate( "last_run" ) != null ? new Date( records.getDate( "last_run" ).getTime( ) ) : null;
			this.average = records.getDouble( "average" );
			this.threshold = records.getDouble( "threshold" );
			this.dogeAsset = records.getDouble( "doge_asset" );
			this.btcAsset = records.getDouble( "btc_asset" );
			this.fee = records.getDouble( "fee" );
			this.live = Arrays.asList( new String[] { "TRUE", "1", "YES" } ).contains( records.getString( "live" ).toUpperCase( ) ) ? true : false;
			this.noTradeReset = records.getInt( "no_trade_reset" );

		}

	}

	public static void main( String[] args ) throws SQLException {

		BTCDOGEInfo info = new BTCDOGEInfo( );

		System.out.println( info.getLastRun( ) );
		System.out.println( info.getAverage( ) );
		System.out.println( info.getThreshold( ) );
		System.out.println( info.getDogeAsset( ) );
		System.out.println( info.getBtcAsset( ) );
		System.out.println( info.getFee( ) );
		System.out.println( info.isLive( ) );
		System.out.println( info.getNoTradeReset( ) );

	}

}
