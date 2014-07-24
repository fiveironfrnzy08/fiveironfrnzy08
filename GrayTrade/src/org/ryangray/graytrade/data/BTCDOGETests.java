package org.ryangray.graytrade.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.ryangray.graytrade.Constants;
import org.ryangray.graytrade.Utilities;

public class BTCDOGETests {

	static LinkedHashMap< Integer, String[] >	tradeIDs;
	static BTCDOGEInfo							info;
	static String								baseQuery		= "SELECT id, datetime, doge_btc_sell, doge_btc_buy, doge_btc_average FROM " + Constants.DATABASE + "." + Constants.DOGE_BTC_DATA_TABLE;
	static Connection							con				= null;
	static double								startingAssets	= 2;

	public static void main( String[] args ) throws Exception {

		con = Utilities.getDefaultCon( );
		try {

			for ( int i = 0; i < 1; i++ ) {

				info = new BTCDOGEInfo( );
				info.setThreshold( .00000000524 );
//				 info.setThreshold( .000000001 * ( i + 1 ) );
				// info.setThreshold( info.getThreshold( ) );
				System.out.println( "Using threshold " + info.getThreshold( ) );
				System.out.println( "Using noTradeThreshold " + info.getNoTradeReset( ) );
				boolean sellDOGE = true;
				boolean resetAverage = false;
				int noTradeCount = 0;
				int noTradeResetCount = 0;
				tradeIDs = new LinkedHashMap<>( );

				if ( !shouldRun( ) ) {
					return;
				}

				StringBuffer preparedStatement = new StringBuffer( );
				preparedStatement.append( baseQuery + " order by datetime asc;" );
				PreparedStatement statement = con.prepareStatement( preparedStatement.toString( ) );
				ResultSet records = statement.executeQuery( );

				while ( records.next( ) != false ) {

//					System.out.println( records.getInt( "id" ) + " " + info.getAverage( ) + " " + records.getDouble( "doge_btc_sell" ) + " " + records.getDouble( "doge_btc_buy" ) );

					if ( ( resetAverage && sellDOGE ) || ( noTradeCount > info.getNoTradeReset( ) && sellDOGE ) ) {

						if ( !resetAverage && ( noTradeCount > info.getNoTradeReset( ) ) ) {
							noTradeResetCount++;
						}
						info.setAverage( resetAverage( records.getInt( "id" ), sellDOGE ) );
						resetAverage = false;
						noTradeCount = 0;

					}

					if ( sellDOGE && records.getDouble( "doge_btc_sell" ) - info.getAverage( ) > info.getThreshold( ) ) {

						trade( records, sellDOGE );
						sellDOGE = false;

					} else if ( !sellDOGE && info.getAverage( ) - records.getDouble( "doge_btc_buy" ) > info.getThreshold( ) ) {

						trade( records, sellDOGE );
						resetAverage = true;
						sellDOGE = true;

					} else {
						noTradeCount++;
					}

				}

				Entry< Integer, String[] > last = null;

				for ( Entry< Integer, String[] > entry: tradeIDs.entrySet( ) ) {

					String[] value = entry.getValue( );
					DecimalFormat df = new DecimalFormat( "#.#####" );
					String ave = df.format( Double.parseDouble( value[ 0 ] ) );
					String dogeBTC = df.format( Double.parseDouble( value[ 1 ] ) );
					String doge = value[ 2 ];
					String btc = value[ 3 ];
					String dogeVal = value[ 4 ].equals( "0.0" ) ? df.format( Double.parseDouble( value[ 5 ] ) * Double.parseDouble( dogeBTC ) ) : df.format( Double.parseDouble( value[ 4 ] ) );
					String btcVal = value[ 5 ].equals( "0.0" ) ? df.format( Double.parseDouble( value[ 4 ] ) * Double.parseDouble( dogeBTC ) ) : df.format( Double.parseDouble( value[ 5 ] ) );
					String tradeDirection = value[ 6 ];

					System.out.println( "ID = " + entry.getKey( ) + ", average = " + ave + ", dogeBTC = " + dogeBTC + ", doge = " + doge + ", btc = " + btc + ", dogeVal = " + dogeVal + ", btcVal=" + btcVal + ", tradeDirection = " + tradeDirection );
					// System.out.println( "ID = " + entry.getKey( ) + ", dogeBTC = " + dogeBTC + ", tradeDirection = " + tradeDirection );

					if ( last == null ) {
						last = entry;
					} else if ( last.getKey( ) < entry.getKey( ) ) {
						last = entry;
					}

				}

				String[] value = last.getValue( );
				DecimalFormat df = new DecimalFormat( "#.#####" );
				String ave = df.format( Double.parseDouble( value[ 0 ] ) );
				String dogeBTC = df.format( Double.parseDouble( value[ 1 ] ) );
				String doge = value[ 2 ];
				String btc = value[ 3 ];
				String dogeVal = value[ 4 ].equals( "0.0" ) ? df.format( Double.parseDouble( value[ 5 ] ) * Double.parseDouble( dogeBTC ) ) : df.format( Double.parseDouble( value[ 4 ] ) );
				String btcVal = value[ 5 ].equals( "0.0" ) ? df.format( Double.parseDouble( value[ 4 ] ) * Double.parseDouble( dogeBTC ) ) : df.format( Double.parseDouble( value[ 5 ] ) );
				String tradeDirection = value[ 6 ];

				System.out.println( "TradeIDs Size: " + tradeIDs.size( ) );
				System.out.println( "No Trade Reset Count: " + noTradeResetCount );
				System.out.println( "ID = " + last.getKey( ) + ", average = " + ave + ", dogeBTC = " + dogeBTC + ", doge = " + doge + ", btc = " + btc + ", dogeVal = " + dogeVal + ", btcVal=" + btcVal + ", tradeDirection = " + tradeDirection );
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

		// StringBuffer preparedStatement = new StringBuffer( );
		// preparedStatement.append( "SELECT doge_asset, btc_asset FROM " + Constants.DATABASE + "." + Constants.INFO_BTC_DOGE_TABLE + " order by id desc;" );
		// PreparedStatement statement = con.prepareStatement( preparedStatement.toString( ) );
		// ResultSet records = statement.executeQuery( );
		// records.next( );
		// if ( records.getDouble( "doge_asset" ) > .001 && records.getDouble( "doge_asset" ) < startingAssets ) {
		// return false;
		// } else {
		return true;
		// }
	}

	private static void trade( ResultSet records, boolean sellDOGE ) throws SQLException {

		BigDecimal ratio;
		String tradeDirection;
		String sell;
		String buy;
		BigDecimal btcAsset = BigDecimal.valueOf( info.getBtcAsset( ) );
		BigDecimal dogeAsset = BigDecimal.valueOf( info.getDogeAsset( ) );
		BigDecimal fee = BigDecimal.valueOf( info.getFee( ) );

		if ( sellDOGE ) {

			ratio = BigDecimal.valueOf( records.getDouble( "doge_btc_sell" ) );
			// info.setBtcAsset( info.getBtcAsset( ) + ( info.getDogeAsset( ) * ratio ) * ( 1 - info.getFee( ) ) );
			info.setBtcAsset( btcAsset.add( dogeAsset.multiply( ratio ).multiply( BigDecimal.valueOf( 1 - fee.doubleValue( ) ) ) ).doubleValue( ) );
			info.setDogeAsset( 0.0 );
			tradeDirection = "DOGE>BTC";
			sell = records.getString( "doge_btc_sell" );
			buy = records.getString( "doge_btc_buy" );

		} else {

			ratio = BigDecimal.valueOf( records.getDouble( "doge_btc_buy" ) );
			// info.setDogeAsset( info.getDogeAsset( ) + ( info.getBtcAsset( ) / ratio ) * ( 1 - info.getFee( ) ) );
			info.setDogeAsset( dogeAsset.add( btcAsset.divide( ratio, 20, RoundingMode.HALF_UP ).multiply( BigDecimal.valueOf( 1 - fee.doubleValue( ) ) ) ).doubleValue( ) );
			info.setBtcAsset( 0.0 );
			tradeDirection = "BTC>DOGE";
			sell = records.getString( "doge_btc_sell" );
			buy = records.getString( "doge_btc_buy" );

		}

		tradeIDs.put( records.getInt( "id" ), new String[] { String.valueOf( info.getAverage( ) ), String.valueOf( ratio ), sell, buy, String.valueOf( info.getDogeAsset( ) ), String.valueOf( info.getBtcAsset( ) ), tradeDirection } );
//		System.out.println( records.getInt( "id" ) + ", " + String.valueOf( info.getAverage( ) ) + ", " + String.valueOf( ratio ) + ", " + sell + ", " + buy + ", " + String.valueOf( info.getDogeAsset( ) ) + ", " + String.valueOf( info.getBtcAsset( ) ) + ", " + tradeDirection );

		if ( info.isLive( ) ) {

			BTCDOGELogger logger = new BTCDOGELogger( );
			// logger.logTrade( );

		}

	}

	private static double resetAverage( int id, boolean sellDOGE ) throws SQLException {

		StringBuffer preparedStatement = new StringBuffer( );
		preparedStatement.append( "SELECT AVG(doge_btc_sell) as doge_btc_sell, AVG(doge_btc_buy) as doge_btc_buy FROM " + Constants.DATABASE + "." + Constants.DOGE_BTC_DATA_TABLE + " where datetime > date_sub(now(), interval 1 day);" );
		// preparedStatement.append( "SELECT AVG(doge_btc_sell) as doge_btc_sell, AVG(doge_btc_buy) as doge_btc_buy FROM " + Constants.DATABASE + "." +
		// Constants.DATA_BTC_DOGE_TABLE + " where id < " + id + " order by id desc limit 25;" );
		PreparedStatement statement = con.prepareStatement( preparedStatement.toString( ) );
		ResultSet records = statement.executeQuery( );

		while ( records.next( ) != false ) {

			if ( sellDOGE ) {
				return records.getDouble( "doge_btc_sell" );
			} else {
				return records.getDouble( "doge_btc_buy" );
			}
		}

		return ( Double ) null;

	}

}
