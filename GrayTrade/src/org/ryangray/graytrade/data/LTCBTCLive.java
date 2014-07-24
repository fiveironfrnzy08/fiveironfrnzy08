package org.ryangray.graytrade.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.ryangray.graytrade.APIQuery;
import org.ryangray.graytrade.Constants;
import org.ryangray.graytrade.Utilities;

public class LTCBTCLive {

	static LinkedHashMap< Integer, String[] >	tradeIDs;
	static LTCBTCInfo							info;
	static String								baseQuery		= "SELECT id, datetime, ltc_btc_sell, ltc_btc_buy, ltc_sell, ltc_buy, btc_sell, btc_buy, ltc_btc_average FROM " + Constants.DATABASE + "." + Constants.LTC_BTC_DATA_TABLE;
	static Connection							con				= null;
	static double								startingAssets	= 2;

	public static void main( String[] args ) throws Exception {

		con = Utilities.getDefaultCon( );

		if ( !shouldRun( ) ) {
			Utilities.sendErrorEmail( new Exception( "GrayTrade has been terminated and is not running" ) );
			return;
		}

		try {

			info = new LTCBTCInfo( );
			JSONObject records = APIQuery.doRequest( "hitbtc", "get", "http://demo-api.hitbtc.com/api/1/public/LTCBTC/ticker" );

			if ( info.getResetAverage( ) && info.getSellLTC( ) || ( info.getNoTradeCount( ) > info.getNoTradeReset( ) && info.getSellLTC( ) ) ) {

				resetAverage( info.getSellLTC( ) );
				info.setResetAverage( false );
				info.setNoTradeCount( 0 );

			}

			if ( info.getSellLTC( ) && records.getDouble( "ask" ) - info.getAverage( ) > info.getThreshold( ) ) {

				trade( records );
				info.setSellLTC( false );

			} else if ( !info.getSellLTC( ) && info.getAverage( ) - records.getDouble( "bid" ) > info.getThreshold( ) ) {

				trade( records );
				info.setResetAverage( true );
				info.setSellLTC( true );

			} else {
				info.setNoTradeCount( info.getNoTradeCount( ) + 1 );
			}
			
			info.save( );

		} catch ( Exception e ) {

			e.printStackTrace( );
			Utilities.sendErrorEmail( e );
			info.setHaltDate( new Timestamp( new Date( ).getTime( ) ) );
			info.setHaltReason( e.toString( ) );
			info.save( );

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
		if ( records.getDouble( "ltc_asset" ) > .001 && records.getDouble( "ltc_asset" ) < startingAssets ) {
			return false;
		} else {
			return true;
		}
	}

	private static void trade( JSONObject records ) throws Exception {

		double ratio;
		String tradeDirection;
		String sell;
		String buy;

		if ( info.getSellLTC( ) ) {

			ratio = records.getDouble( "ask" );
			info.setBtcAsset( info.getBtcAsset( ) + ( info.getLtcAsset( ) * ratio ) * ( 1 - info.getFee( ) ) );
			info.setLtcAsset( 0.0 );
			tradeDirection = "LTC>BTC";
			sell = records.getString( "ask" );
			buy = records.getString( "bid" );

		} else {

			ratio = records.getDouble( "bid" );
			info.setLtcAsset( info.getLtcAsset( ) + ( info.getBtcAsset( ) / ratio ) * ( 1 - info.getFee( ) ) );
			info.setBtcAsset( 0.0 );
			tradeDirection = "BTC>LTC";
			sell = records.getString( "ask" );
			buy = records.getString( "bid" );

		}

		if ( info.isLive( ) ) {

			LTCBTCLogger logger = new LTCBTCLogger( );

			Map< String, String > args = new HashMap<>( );
			
			args.put( "clientOrderId", String.valueOf( new Date( ).getTime( ) ) );
			args.put( "symbol", "LTCBTC" );
			if ( info.getSellLTC( ) ) {
				args.put( "side", "sell" );
				args.put( "price", records.getString( "ask" ) );
			} else {
				args.put( "side", "buy" );
				args.put( "price", records.getString( "bid" ) );
			}
			args.put( "timeInForce", "IOC" );
			
			JSONObject response = logger.logTrade( args );
			System.out.println( response );

		}

	}

	private static void resetAverage( boolean sellLTC ) throws JSONException, Exception {

		StringBuffer preparedStatement = new StringBuffer( );
		preparedStatement.append( "SELECT AVG(ltc_btc_sell) as ltc_btc_sell, AVG(ltc_btc_buy) as ltc_btc_buy FROM " + Constants.DATABASE + "." + Constants.LTC_BTC_DATA_TABLE + " where datetime > date_sub(now(), interval 1 day);" );
		// preparedStatement.append( "SELECT AVG(ltc_btc_sell) as ltc_btc_sell, AVG(ltc_btc_buy) as ltc_btc_buy FROM " + Constants.DATABASE + "." +
		// Constants.DATA_LTC_BTC_TABLE + " where id < " + id + " order by id desc limit 25;" );
		PreparedStatement statement = con.prepareStatement( preparedStatement.toString( ) );
		ResultSet records = statement.executeQuery( );

		while ( records.next( ) != false ) {

			if ( sellLTC ) {
				
				double sell = records.getDouble( "ltc_btc_sell" );
				if ( sell > 0.0 ) {
					info.setAverage( sell );
				} else {
					info.setAverage( APIQuery.doRequest( "hitbtc", "get", "http://demo-api.hitbtc.com/api/1/public/LTCBTC/ticker" ).getDouble( "last" ) );
				}
				
			} else {
				
				double buy = records.getDouble( "ltc_btc_buy" );
				if ( buy > 0.0 ) {
					info.setAverage( buy );
				} else {
					info.setAverage( APIQuery.doRequest( "hitbtc", "get", "http://demo-api.hitbtc.com/api/1/public/LTCBTC/ticker" ).getDouble( "last" ) );
				}
				
			}
			
		}
		
	}

}
