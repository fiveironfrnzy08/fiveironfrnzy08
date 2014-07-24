package org.ryangray.graytrade.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.ryangray.graytrade.APIQuery;
import org.ryangray.graytrade.Constants;
import org.ryangray.graytrade.CopyOfAPIQuery;
import org.ryangray.graytrade.Utilities;

/**
 * 
 * Using the API provided by BTC-e. Calls need a method and a url to send to.
 * 
 * getInfo - https://btc-e.com/tapi currency_currency - https://btc-e.com/api/2/ltc_btc/ticker https://btc-e.com/api/2/ltc_usd/ticker
 * 
 */

public class LTCBTCLogger {

	static Connection	con	= null;

	public static void main( String[] args ) throws Exception {

		try {

			con = Utilities.getDefaultCon( );
			LTCBTCLogger logger = new LTCBTCLogger( );
			logger.logBTCEData( );
			logger.setInfo( );
			logger.test( );

		} catch ( Exception e ) {

			Utilities.sendErrorEmail( e );
			e.printStackTrace( );

		} finally {
			con.close( );
		}

	}

	private void test( ) throws Exception {

		APIQuery get = new APIQuery( );

		HashMap< String, String > parameters = new HashMap<>( );
		parameters.put( "order_id", "1" );
		// JSONObject response = get.doRequest( "btce", "https://btc-e.com/api", "TradeHistory", parameters );
		JSONObject response = get.doRequest( "btce", "https://btc-e.com/tapi", "TradeHistory" );
		System.out.println( response );

		// JSONObject response = get.doRequest( "btce", "https://btc-e.com/api/2/ltc_btc/ticker", "currency_currency" );
		// System.out.println( response );
		// String ltcBTCBuy = response.get( "buy" ).toString( );
		// String ltcBTCSell = response.get( "sell" ).toString( );
		// String ltcBTCAverage = response.get( "avg" ).toString( );
		// String ltcBTCVolume = response.get( "vol_cur" ).toString( );

		// JSONObject response = get.doRequest( "btce", "https://btc-e.com/api/2/ltc_btc/ticker", "currency_currency" );
		// System.out.println( response );
		// String ltcBTCBuy = response.get( "buy" ).toString( );
		// String ltcBTCSell = response.get( "sell" ).toString( );
		// String ltcBTCAverage = response.get( "avg" ).toString( );
		// String ltcBTCVolume = response.get( "vol_cur" ).toString( );
		//
		// response = get.doRequest( "btce", "https://btc-e.com/api/2/ltc_usd/ticker", "currency_currency" );
		// System.out.println( response );
		// String ltcBuy = response.get( "buy" ).toString( );
		// String ltcSell = response.get( "sell" ).toString( );
		// String ltcAverage = response.get( "avg" ).toString( );
		// String ltcVolume = response.get( "vol_cur" ).toString( );
		//
		// response = get.doRequest( "btce", "https://btc-e.com/api/2/btc_usd/ticker", "currency_currency" );
		// System.out.println( response );
		// String btcBuy = response.get( "buy" ).toString( );
		// String btcSell = response.get( "sell" ).toString( );
		// String btcAverage = response.get( "avg" ).toString( );
		// String btcVolume = response.get( "vol_cur" ).toString( );
		//
		// response = get.doRequest( "btce", "https://btc-e.com/api/2/btc_usd/ticker", "currency_currency" );
		// System.out.println( response );
		// String btcBuy = response.get( "buy" ).toString( );
		// String btcSell = response.get( "sell" ).toString( );
		// String btcAverage = response.get( "avg" ).toString( );
		// String btcVolume = response.get( "vol_cur" ).toString( );
		//
		// System.out.println( "" );
		// System.out.println( "ltcBTCBuy: " + ltcBTCBuy );
		// System.out.println( "ltcBTCSell: " + ltcBTCSell );
		// System.out.println( "ltcBTCAverage: " + ltcBTCAverage );
		// System.out.println( "ltcBTCVolume: " + ltcBTCVolume );
		// System.out.println( "ltcBuy: " + ltcBuy );
		// System.out.println( "ltcSell: " + ltcSell );
		// System.out.println( "ltcAverage: " + ltcAverage );
		// System.out.println( "ltcVolume: " + ltcVolume );
		// System.out.println( "btcBuy: " + btcBuy );
		// System.out.println( "btcSell: " + btcSell );
		// System.out.println( "btcAverage: " + btcAverage );
		// System.out.println( "btcVolume: " + btcVolume );
		//
		// StringBuffer preparedStatement = new StringBuffer( );
		// preparedStatement.append(
		// "SELECT id, datetime, ltc_sell, btc_buy, ltc_sell*100 / btc_buy as 'LTC_BTC Value', ltc_vol*100 / btc_vol as 'LTC_BTC Volume' FROM master.graytrade order by id desc limit 25;"
		// );
		// PreparedStatement statement = con.prepareStatement( preparedStatement.toString( ) );
		// ResultSet records = statement.executeQuery( );
		// int count = 0;
		// double total = 0;
		// while ( records.next( ) != false ) {
		// count++;
		// total = total + records.getDouble( "LTC_BTC Value" );
		// }
		// Double average = total / ( double ) count;

	}

	protected void setInfo( ) throws Exception {

		APIQuery get = new APIQuery( );

		JSONObject responseGetInfo = get.doRequest( "btce", "https://btc-e.com/tapi", "getInfo" );
		System.out.println( "Get Info: " + responseGetInfo );

		StringBuffer preparedStatement = new StringBuffer( );

		preparedStatement.append( "SELECT id, datetime, AVG(ltc_btc_sell) as ltc_btc_sell, AVG(ltc_btc_buy) as ltc_btc_buy FROM " + Constants.DATABASE + "." + Constants.LTC_BTC_DATA_TABLE + " where datetime > date_sub(now(), interval 1 day);" );
		PreparedStatement statement = con.prepareStatement( preparedStatement.toString( ) );
		ResultSet records = statement.executeQuery( );
		records.next( );

		preparedStatement = new StringBuffer( );
		preparedStatement.append( "UPDATE " + Constants.DATABASE + "." + Constants.LTC_BTC_INFO_TABLE + " SET average = ?, threshold = ?, ltc_asset = ?, btc_asset = ?, fee = ?, live = ?, no_trade_reset = ? WHERE id = 1;" );
		statement = con.prepareStatement( preparedStatement.toString( ) );
		statement.setDouble( 1, records.getDouble( "ltc_btc_sell" ) );
		statement.setDouble( 2, .0001 );
		statement.setDouble( 3, responseGetInfo.getJSONObject( "return" ).getJSONObject( "funds" ).getDouble( "ltc" ) );
		statement.setDouble( 4, responseGetInfo.getJSONObject( "return" ).getJSONObject( "funds" ).getDouble( "btc" ) );
		statement.setDouble( 5, .002 );
		statement.setString( 6, "false" );
		statement.setInt( 7, 25 );
		statement.executeUpdate( );

	}

	protected void logBTCEData( ) throws Exception {

		APIQuery get = new APIQuery( );

		JSONObject response = get.doRequest( "btce", "https://btc-e.com/api/2/ltc_btc/ticker", "currency_currency" );
		System.out.println( response );
		String ltcBTCBuy = response.get( "buy" ).toString( );
		String ltcBTCSell = response.get( "sell" ).toString( );
		String ltcBTCAverage = response.get( "avg" ).toString( );
		String ltcBTCVolume = response.get( "vol_cur" ).toString( );

		response = get.doRequest( "btce", "https://btc-e.com/api/2/ltc_usd/ticker", "currency_currency" );
		System.out.println( response );
		String ltcBuy = response.get( "buy" ).toString( );
		String ltcSell = response.get( "sell" ).toString( );
		String ltcAverage = response.get( "avg" ).toString( );
		String ltcVolume = response.get( "vol_cur" ).toString( );

		response = get.doRequest( "btce", "https://btc-e.com/api/2/btc_usd/ticker", "currency_currency" );
		System.out.println( response );
		String btcBuy = response.get( "buy" ).toString( );
		String btcSell = response.get( "sell" ).toString( );
		String btcAverage = response.get( "avg" ).toString( );
		String btcVolume = response.get( "vol_cur" ).toString( );

		System.out.println( "" );
		System.out.println( "ltcBTCSell: " + ltcBTCSell );
		System.out.println( "ltcBTCBuy: " + ltcBTCBuy );
		System.out.println( "ltcBTCAverage: " + ltcBTCAverage );
		System.out.println( "ltcBTCVolume: " + ltcBTCVolume );
		System.out.println( "ltcSell: " + ltcSell );
		System.out.println( "ltcBuy: " + ltcBuy );
		System.out.println( "ltcAverage: " + ltcAverage );
		System.out.println( "ltcVolume: " + ltcVolume );
		System.out.println( "btcSell: " + btcSell );
		System.out.println( "btcBuy: " + btcBuy );
		System.out.println( "btcAverage: " + btcAverage );
		System.out.println( "btcVolume: " + btcVolume );

		StringBuffer preparedStatement = new StringBuffer( );
		preparedStatement.append( "INSERT INTO " + Constants.DATABASE + "." + Constants.LTC_BTC_DATA_TABLE + " ( datetime, ltc_btc_buy, ltc_btc_sell, ltc_btc_average, ltc_btc_vol, ltc_buy, ltc_sell, ltc_average, ltc_vol, btc_buy, btc_sell, btc_average, btc_vol) VALUES (?, ?,?,?,?, ?,?,?,?, ?,?,?,?);" );
		PreparedStatement statement = con.prepareStatement( preparedStatement.toString( ) );

		statement = con.prepareStatement( preparedStatement.toString( ) );
		statement.setTimestamp( 1, new Timestamp( new Date( ).getTime( ) ) );
		statement.setDouble( 2, Double.parseDouble( ltcBTCSell ) );
		statement.setDouble( 3, Double.parseDouble( ltcBTCBuy ) );
		statement.setDouble( 4, Double.parseDouble( ltcBTCAverage ) );
		statement.setDouble( 5, Double.parseDouble( ltcBTCVolume ) );
		statement.setDouble( 6, Double.parseDouble( ltcSell ) );
		statement.setDouble( 7, Double.parseDouble( ltcBuy ) );
		statement.setDouble( 8, Double.parseDouble( ltcAverage ) );
		statement.setDouble( 9, Double.parseDouble( ltcVolume ) );
		statement.setDouble( 10, Double.parseDouble( btcSell ) );
		statement.setDouble( 11, Double.parseDouble( btcBuy ) );
		statement.setDouble( 12, Double.parseDouble( btcAverage ) );
		statement.setDouble( 13, Double.parseDouble( btcVolume ) );
		statement.executeUpdate( );

	}

	public JSONObject logTrade( Map< String, String > args ) throws Exception {

		JSONObject responseGetInfo = APIQuery.doRequest( "hitbtc", "post", "http://demo-api.hitbtc.com", "/api/1/trading/new_order", args );
		System.out.println( "Get Info: " + responseGetInfo );

		StringBuffer preparedStatement = new StringBuffer( );

		preparedStatement.append( "INSERT INTO " + Constants.DATABASE + "." + Constants.LTC_BTC_TRADES_TABLE + " ( orderId, clientOrderId, execReportType, orderStatus, symbol, side, timestamp, price, quantity, type, timeInForce, lastQuantity, lastPrice, leavesQuantity, cumQuantity, averagePrice ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? );" );
		PreparedStatement statement = con.prepareStatement( preparedStatement.toString( ) );
		
		Set< String > keys = responseGetInfo.keySet( );
		int i = 1;
		for ( String key: keys ) {
			statement.setString( i++, String.valueOf( responseGetInfo.get( key ) ) );
		}

		statement.executeUpdate( );
		
		return responseGetInfo;

	}

}
