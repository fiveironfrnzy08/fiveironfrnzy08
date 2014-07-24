package org.ryangray.graytrade.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.ryangray.graytrade.Constants;
import org.ryangray.graytrade.Utilities;

public class LTCBTCTests {

	static LinkedHashMap< Integer, String[] > tradeIDs;
	static LTCBTCInfo info;
	static String baseQuery = "SELECT id, datetime, ltc_btc_sell, ltc_btc_buy, ltc_sell, ltc_buy, btc_sell, btc_buy, ltc_btc_average FROM " + Constants.DATABASE + "." + Constants.LTC_BTC_DATA_TABLE;
	static Connection con = null;
	static double startingAssets = 2;
	
	public static void main( String[] args ) throws Exception {
//		Class.forName("com.mysql.jdbc.Driver");
//		Connection con = DriverManager.getConnection("jdbc:mysql://ryangray.org:3306/news_db","root","ryan135244");
//		PreparedStatement statement = con.prepareStatement("SELECT * FROM news_sources;");
//		ResultSet result = statement.executeQuery("SELECT * FROM news_sources;");

		con = Utilities.getDefaultCon( );
		try {
	
			for ( int i = 0; i < 1; i++ ) {
				
				info = new LTCBTCInfo( );
//				info.setThreshold( .0001 );
//				info.setThreshold( info.getThreshold( ) + .00005*i - .00005 );
//				info.setThreshold( info.getThreshold( ) );
				System.out.println( "Using threshold " + info.getThreshold( ) );
				System.out.println( "Using noTradeThreshold " + info.getNoTradeReset( ) );
				boolean sellLTC = true;
				double average = resetAverage( 33+25, sellLTC );
				double ltcAsset = info.getLtcAsset( );
				double btcAsset = info.getBtcAsset( );
				double fee = info.getFee( );
				boolean resetAverage = false;
				int noTradeCount = 0;
				tradeIDs = new LinkedHashMap<>( );
				
				if ( !shouldRun( ) ) {
					return;
				}
	
				StringBuffer preparedStatement = new StringBuffer( );
				preparedStatement.append( baseQuery + " order by datetime asc;" );
				PreparedStatement statement = con.prepareStatement( preparedStatement.toString( ) );
				ResultSet records = statement.executeQuery( );
	
				while ( records.next( ) != false ) {
	
//					System.out.println( records.getInt( "id" ) + " " + average + " " + records.getDouble( "ltc_btc_sell" ) + " " + records.getDouble( "ltc_btc_buy" ) );
	
					if ( resetAverage && sellLTC || ( noTradeCount > info.getNoTradeReset( ) && sellLTC ) ) {
						
						average = resetAverage( records.getInt( "id" ), sellLTC );
						resetAverage = false;
						noTradeCount = 0;
						
					}
	
					if ( sellLTC && records.getDouble( "ltc_btc_sell" ) - average > info.getThreshold( ) ) {
	
						trade( records, ltcAsset, btcAsset, average, fee, sellLTC );
						sellLTC = false;
	
					} else if ( !sellLTC && average - records.getDouble( "ltc_btc_buy" ) > info.getThreshold( ) ) {

						trade( records, ltcAsset, btcAsset, average, fee, sellLTC );
						resetAverage = true;
						sellLTC = true;
	
					} else {
						noTradeCount++;
					}
	
				}
	
				Entry< Integer, String[] > last = null;
	
				for ( Entry< Integer, String[] > entry: tradeIDs.entrySet( ) ) {
					
					String[] value = entry.getValue( );
					DecimalFormat df = new DecimalFormat( "#.#####" );
					String ave = df.format( Double.parseDouble( value[ 0 ] ) );
					String ltcBTC = df.format( Double.parseDouble( value[ 1 ] ) );
					String ltc = value[ 2 ];
					String btc = value[ 3 ];
					String ltcVal = value[ 4 ].equals( "0.0" ) ? df.format( Double.parseDouble( value[ 5 ] ) * Double.parseDouble( ltcBTC ) ) : df.format( Double.parseDouble( value[ 4 ] ) );
					String btcVal = value[ 5 ].equals( "0.0" ) ? df.format( Double.parseDouble( value[ 4 ] ) * Double.parseDouble( ltcBTC ) ) : df.format( Double.parseDouble( value[ 5 ] ) );
					String tradeDirection = value[ 6 ];
					
					System.out.println( "ID = " + entry.getKey( ) + ", average = " + ave + ", ltcBTC = " + ltcBTC + ", ltc = " + ltc + ", btc = " + btc + ", ltcVal = " + ltcVal + ", btcVal=" + btcVal + ", tradeDirection = " + tradeDirection );
//					System.out.println( "ID = " + entry.getKey( ) + ", ltcBTC = " + ltcBTC + ", tradeDirection = " + tradeDirection );
	
					if ( last == null ) {
						last = entry;
					} else if ( last.getKey( ) < entry.getKey( ) ) {
						last = entry;
					}
	
				}
	
				String[] value = last.getValue( );
				DecimalFormat df = new DecimalFormat( "#.#####" );
				String ave = df.format( Double.parseDouble( value[ 0 ] ) );
				String ltcBTC = df.format( Double.parseDouble( value[ 1 ] ) );
				String ltc = value[ 2 ];
				String btc = value[ 3 ];
				String ltcVal = value[ 4 ].equals( "0.0" ) ? df.format( Double.parseDouble( value[ 5 ] ) * Double.parseDouble( ltcBTC ) ) : df.format( Double.parseDouble( value[ 4 ] ) );
				String btcVal = value[ 5 ].equals( "0.0" ) ? df.format( Double.parseDouble( value[ 4 ] ) * Double.parseDouble( ltcBTC ) ) : df.format( Double.parseDouble( value[ 5 ] ) );
				String tradeDirection = value[ 6 ];
				
				System.out.println( "TradeIDs Size: " + tradeIDs.size( ) );
				System.out.println( "ID = " + last.getKey( ) + ", average = " + ave + ", ltcBTC = " + ltcBTC + ", ltc = " + ltc + ", btc = " + btc + ", ltcVal = " + ltcVal + ", btcVal=" + btcVal + ", tradeDirection = " + tradeDirection );
				System.out.println( "" );
				
			}
			
		} catch ( Exception e ) {
			
			e.printStackTrace( );
			Utilities.sendErrorEmail( e );
			
		} finally {
			con.close( );
		}

	}

	private static boolean shouldRun( ) throws SQLException {
		
		StringBuffer preparedStatement = new StringBuffer( );
		preparedStatement.append( "SELECT ltc_asset, btc_asset FROM " + Constants.DATABASE + "." + Constants.LTC_BTC_INFO_TABLE + " order by id desc;" );
		PreparedStatement statement = con.prepareStatement( preparedStatement.toString( ) );
		ResultSet records = statement.executeQuery( );
		records.next( );
		if ( records.getDouble( "ltc_asset" ) > .001 && records.getDouble( "ltc_asset" ) < startingAssets  ) {
			return false;
		} else {
			return true;
		}
	}

	private static void trade( ResultSet records, double ltcAsset, double btcAsset, double average, double fee, boolean sellLTC ) throws SQLException {
		
		double ratio;
		String tradeDirection;
		String sell;
		String buy;
		
		if ( sellLTC ) {
			
			ratio = records.getDouble( "ltc_btc_sell" );
			btcAsset = btcAsset + ( ltcAsset * ratio ) * ( 1 - fee );
			ltcAsset = 0;
			tradeDirection = "LTC>BTC";
			sell = records.getString( "ltc_sell" );
			buy = records.getString( "btc_buy" );
			
		} else {

			ratio = records.getDouble( "ltc_btc_buy" );
			ltcAsset = ltcAsset + ( btcAsset / ratio ) * ( 1 - fee );
			btcAsset = 0;
			tradeDirection = "BTC>LTC";
			sell = records.getString( "btc_sell" );
			buy = records.getString( "ltc_buy" );
			
		}
		
		tradeIDs.put( records.getInt( "id" ), new String[] { String.valueOf( average ), String.valueOf( ratio ), sell, buy, String.valueOf( ltcAsset ), String.valueOf( btcAsset ), tradeDirection } );
		
		if ( info.isLive( ) ) {
			
			LTCBTCLogger logger = new LTCBTCLogger( );
//			logger.logTrade( );
			
		}
	
	}

	private static double resetAverage( int id, boolean sellLTC ) throws SQLException {

		StringBuffer preparedStatement = new StringBuffer( );
//		preparedStatement.append( "SELECT AVG(ltc_btc_sell) as ltc_btc_sell, AVG(ltc_btc_buy) as ltc_btc_buy FROM " + Constants.DATABASE + "." + Constants.DATA_TABLE + " where datetime > date_sub(now(), interval 1 day);" );
		preparedStatement.append( "SELECT AVG(ltc_btc_sell) as ltc_btc_sell, AVG(ltc_btc_buy) as ltc_btc_buy FROM " + Constants.DATABASE + "." + Constants.LTC_BTC_DATA_TABLE + " where id < " + id + " order by id desc limit 25;" );
		PreparedStatement statement = con.prepareStatement( preparedStatement.toString( ) );
		ResultSet records = statement.executeQuery( );
		
		while ( records.next( ) != false ) {
			
			if ( sellLTC ) {
				return records.getDouble( "ltc_btc_sell" );
			} else {
				return records.getDouble( "ltc_btc_buy" );
			}
		}
		
		return ( Double ) null;
		
	}

}
