package org.ryangray.graytrade.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;

import org.json.JSONObject;
import org.ryangray.graytrade.APIQuery;
import org.ryangray.graytrade.Constants;
import org.ryangray.graytrade.Utilities;

/**
 * 
 * Using the API provided by BTC-e. Calls need a method and a url to send to.
 * 
 * getInfo - https://btc-e.com/tapi currency_currency - https://btc-e.com/api/2/doge_btc/ticker https://btc-e.com/api/2/doge_usd/ticker
 * 
 */

public class BTCDOGELogger {

	static Connection	con	= null;

	public static void main( String[] args ) throws Exception {

		try {

			con = Utilities.getDefaultCon( );
			BTCDOGELogger logger = new BTCDOGELogger( );
			logger.logBTERData( );
//			logger.setInfo( );
//			logger.test( );

		} catch ( Exception e ) {

			Utilities.sendErrorEmail( e );
			e.printStackTrace( );

		} finally {
			con.close( );
		}

	}

	private void test( ) throws Exception {

		APIQuery get = new APIQuery( );

		JSONObject response = get.doRequest( "bter", "get", "http://data.bter.com/api/1/ticker/doge_btc" );
		System.out.println( response );
		// String dogeBTCBuy = response.get( "buy" ).toString( );
		// String dogeBTCSell = response.get( "sell" ).toString( );
		// String dogeBTCAverage = response.get( "avg" ).toString( );
		// String dogeBTCVolume = response.get( "vol_cur" ).toString( );
		//
		// System.out.println( "" );
		// System.out.println( "dogeBTCBuy: " + dogeBTCBuy );
		// System.out.println( "dogeBTCSell: " + dogeBTCSell );
		// System.out.println( "dogeBTCAverage: " + dogeBTCAverage );
		// System.out.println( "dogeBTCVolume: " + dogeBTCVolume );
		// System.out.println( "dogeBuy: " + dogeBuy );
		// System.out.println( "dogeSell: " + dogeSell );
		// System.out.println( "dogeAverage: " + dogeAverage );
		// System.out.println( "dogeVolume: " + dogeVolume );
		// System.out.println( "btcBuy: " + btcBuy );
		// System.out.println( "btcSell: " + btcSell );
		// System.out.println( "btcAverage: " + btcAverage );
		// System.out.println( "btcVolume: " + btcVolume );
		//
		// StringBuffer preparedStatement = new StringBuffer( );
		// preparedStatement.append(
		// "SELECT id, datetime, doge_sell, btc_buy, doge_sell*100 / btc_buy as 'DOGE_BTC Value', doge_vol*100 / btc_vol as 'DOGE_BTC Volume' FROM master.graytrade order by id desc limit 25;"
		// );
		// PreparedStatement statement = con.prepareStatement( preparedStatement.toString( ) );
		// ResultSet records = statement.executeQuery( );
		// int count = 0;
		// double total = 0;
		// while ( records.next( ) != false ) {
		// count++;
		// total = total + records.getDouble( "DOGE_BTC Value" );
		// }
		// Double average = total / ( double ) count;
		
//		statement.close( );

	}

	protected void setInfo( ) throws Exception {

		APIQuery get = new APIQuery( );

		JSONObject responseGetInfo = get.doRequest( "bter", "get", "http://data.bter.com/api/1/ticker/doge_btc" );
		// JSONObject responseGetInfo = get.doRequest( "btce", "post", "https://btc-e.com/tapi", "getInfo" );
		System.out.println( "Get Info: " + responseGetInfo );

		StringBuffer preparedStatement = new StringBuffer( );

		preparedStatement.append( "SELECT id, datetime, AVG(doge_btc_sell) as doge_btc_sell, AVG(doge_btc_buy) as doge_btc_buy FROM " + Constants.DATABASE + "." + Constants.DOGE_BTC_PING + " where datetime > date_sub(now(), interval 1 day);" );
		PreparedStatement statement = con.prepareStatement( preparedStatement.toString( ) );
		ResultSet records = statement.executeQuery( );
		records.next( );

		preparedStatement = new StringBuffer( );
		preparedStatement.append( "UPDATE " + Constants.DATABASE + "." + Constants.DOGE_BTC_INFO + " SET average = ?, threshold = ?, doge_asset = ?, btc_asset = ?, fee = ?, live = ?, no_trade_reset = ? WHERE id = 1;" );
		statement = con.prepareStatement( preparedStatement.toString( ) );
		statement.setDouble( 1, records.getDouble( "doge_btc_sell" ) );
		statement.setDouble( 2, .000000005 );
		statement.setDouble( 3, .0000000655 );
		statement.setDouble( 4, .0 );
		statement.setDouble( 5, .002 );
		statement.setString( 6, "false" );
		statement.setInt( 7, 25 );
		statement.executeUpdate( );
		statement.close( );

	}

	protected void logBTCEData( ) throws Exception {

		APIQuery get = new APIQuery( );

		JSONObject response = get.doRequest( "btce", "post", "https://btc-e.com/api/2/doge_btc/ticker", "currency_currency" );
		System.out.println( response );
		String dogeBTCBuy = response.get( "buy" ).toString( );
		String dogeBTCSell = response.get( "sell" ).toString( );
		String dogeBTCAverage = response.get( "avg" ).toString( );
		String dogeBTCVolume = response.get( "vol_cur" ).toString( );

		response = get.doRequest( "btce", "post", "https://btc-e.com/api/2/doge_usd/ticker", "currency_currency" );
		System.out.println( response );
		String dogeBuy = response.get( "buy" ).toString( );
		String dogeSell = response.get( "sell" ).toString( );
		String dogeAverage = response.get( "avg" ).toString( );
		String dogeVolume = response.get( "vol_cur" ).toString( );

		response = get.doRequest( "btce", "post", "https://btc-e.com/api/2/btc_usd/ticker", "currency_currency" );
		System.out.println( response );
		String btcBuy = response.get( "buy" ).toString( );
		String btcSell = response.get( "sell" ).toString( );
		String btcAverage = response.get( "avg" ).toString( );
		String btcVolume = response.get( "vol_cur" ).toString( );

		System.out.println( "" );
		System.out.println( "dogeBTCSell: " + dogeBTCSell );
		System.out.println( "dogeBTCBuy: " + dogeBTCBuy );
		System.out.println( "dogeBTCAverage: " + dogeBTCAverage );
		System.out.println( "dogeBTCVolume: " + dogeBTCVolume );
		System.out.println( "dogeSell: " + dogeSell );
		System.out.println( "dogeBuy: " + dogeBuy );
		System.out.println( "dogeAverage: " + dogeAverage );
		System.out.println( "dogeVolume: " + dogeVolume );
		System.out.println( "btcSell: " + btcSell );
		System.out.println( "btcBuy: " + btcBuy );
		System.out.println( "btcAverage: " + btcAverage );
		System.out.println( "btcVolume: " + btcVolume );

		StringBuffer preparedStatement = new StringBuffer( );
		preparedStatement.append( "INSERT INTO " + Constants.DATABASE + "." + Constants.DOGE_BTC_PING + " ( datetime, doge_btc_buy, doge_btc_sell, doge_btc_average, doge_btc_vol, doge_buy, doge_sell, doge_average, doge_vol, btc_buy, btc_sell, btc_average, btc_vol) VALUES (?, ?,?,?,?, ?,?,?,?, ?,?,?,?);" );
		PreparedStatement statement = con.prepareStatement( preparedStatement.toString( ) );

		statement = con.prepareStatement( preparedStatement.toString( ) );
		statement.setTimestamp( 1, new Timestamp( new Date( ).getTime( ) ) );
		statement.setDouble( 2, Double.parseDouble( dogeBTCSell ) );
		statement.setDouble( 3, Double.parseDouble( dogeBTCBuy ) );
		statement.setDouble( 4, Double.parseDouble( dogeBTCAverage ) );
		statement.setDouble( 5, Double.parseDouble( dogeBTCVolume ) );
		statement.setDouble( 6, Double.parseDouble( dogeSell ) );
		statement.setDouble( 7, Double.parseDouble( dogeBuy ) );
		statement.setDouble( 8, Double.parseDouble( dogeAverage ) );
		statement.setDouble( 9, Double.parseDouble( dogeVolume ) );
		statement.setDouble( 10, Double.parseDouble( btcSell ) );
		statement.setDouble( 11, Double.parseDouble( btcBuy ) );
		statement.setDouble( 12, Double.parseDouble( btcAverage ) );
		statement.setDouble( 13, Double.parseDouble( btcVolume ) );
		statement.executeUpdate( );
		statement.close( );

	}

	private void logBTERData( ) throws Exception {

		APIQuery request = new APIQuery( );

		JSONObject response = request.doRequest( "bter", "get", "http://data.bter.com/api/1/ticker/doge_btc" );
		System.out.println( response );
		String dogeBTCBuy = response.get( "buy" ).toString( );
		String dogeBTCSell = response.get( "sell" ).toString( );
		String dogeBTCAverage = response.get( "avg" ).toString( );
		String dogeBTCVolume = response.get( "vol_doge" ).toString( );

		System.out.println( "" );
		System.out.println( "dogeBTCSell: " + dogeBTCSell );
		System.out.println( "dogeBTCBuy: " + dogeBTCBuy );
		System.out.println( "dogeBTCAverage: " + dogeBTCAverage );
		System.out.println( "dogeBTCVolume: " + dogeBTCVolume );

		StringBuffer preparedStatement = new StringBuffer( );
		preparedStatement.append( "INSERT INTO " + Constants.DATABASE + "." + Constants.DOGE_BTC_PING + " ( datetime, doge_btc_sell, doge_btc_buy, doge_btc_average, doge_btc_vol ) VALUES (?, ?,?,?,?);" );
		PreparedStatement statement = con.prepareStatement( preparedStatement.toString( ) );

		statement = con.prepareStatement( preparedStatement.toString( ) );
		statement.setTimestamp( 1, new Timestamp( new Date( ).getTime( ) ) );
		statement.setDouble( 2, Double.parseDouble( dogeBTCSell ) );
		statement.setDouble( 3, Double.parseDouble( dogeBTCBuy ) );
		statement.setDouble( 4, Double.parseDouble( dogeBTCAverage ) );
		statement.setDouble( 5, Double.parseDouble( dogeBTCVolume ) );
		statement.executeUpdate( );
		statement.close( );

	}

	protected void logTrade( ) throws Exception {

		APIQuery get = new APIQuery( );

		JSONObject responseGetInfo = get.doRequest( "btce", "post", "https://btc-e.com/tapi", "getInfo" );
		System.out.println( "Get Info: " + responseGetInfo );

		StringBuffer preparedStatement = new StringBuffer( );

		preparedStatement.append( "SELECT id, datetime, doge_sell, btc_buy, doge_sell*100 / btc_buy as 'DOGE_BTC Value', doge_vol*100 / btc_vol as 'DOGE_BTC Volume' FROM master.graytrade order by id desc limit 25;" );
		PreparedStatement statement = con.prepareStatement( preparedStatement.toString( ) );
		ResultSet records = statement.executeQuery( );
		int count = 0;
		double total = 0;
		while ( records.next( ) != false ) {
			count++;
			total = total + records.getDouble( "DOGE_BTC Value" );
		}
		Double average = total / ( double ) count;

		preparedStatement = new StringBuffer( );

		preparedStatement.append( "UPDATE " + Constants.DATABASE + "." + Constants.DOGE_BTC_INFO + " SET average = ?, threshold = ?, doge_asset = ?, btc_asset = ?, fee = ?, live = ?, no_trade_reset = ? WHERE id = 1;" );
		statement = con.prepareStatement( preparedStatement.toString( ) );
		statement.setDouble( 1, average );
		statement.setDouble( 2, .0001 );
		statement.setDouble( 3, responseGetInfo.getJSONObject( "return" ).getJSONObject( "funds" ).getDouble( "doge" ) );
		statement.setDouble( 4, responseGetInfo.getJSONObject( "return" ).getJSONObject( "funds" ).getDouble( "btc" ) );
		statement.setDouble( 5, .002 );
		statement.setString( 6, "false" );
		statement.setInt( 7, 25 );
		statement.executeUpdate( );
		statement.close( );

	}

}
