package org.ryangray.graytrade;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.abwaters.cryptotrade.CryptoTradeException;
import com.abwaters.cryptotrade.btce.models.Info;
import com.google.gson.Gson;

public class BTCE {
	private boolean initialized = false;
	private long nonce ;
	private String secret, key ;
	private Mac mac ;
	private DefaultHttpClient http ;
	private Gson gson ;

	public BTCE() {
	}

	public BTCE(Properties p) {
		this();
		initializeProperties(p);
	}

	public Info getInfo() throws CryptoTradeException {
		return gson.fromJson(request("getInfo",null),Info.class) ;
	}
	
	public String getTransactionHistory() throws CryptoTradeException {
		return getTransactionHistory(0,0,0,0,null,0,0) ;
	}
	
	public String getTransactionHistory(int from,int count,int from_id,int end_id,String order,long since,long end) throws CryptoTradeException {
		List<NameValuePair> args = new ArrayList<NameValuePair>() ;		
		if( from > 0 ) args.add(new BasicNameValuePair("from", Integer.toString(from))) ;
		if( count > 0 ) args.add(new BasicNameValuePair("count", Integer.toString(count))) ;
		if( from_id > 0 ) args.add(new BasicNameValuePair("from_id", Integer.toString(from_id))) ;
		if( end_id > 0 ) args.add(new BasicNameValuePair("end_id", Integer.toString(end_id))) ;
		if( order != null && order.length() > 0 ) args.add(new BasicNameValuePair("order", order)) ;
		if( since > 0 ) args.add(new BasicNameValuePair("since", Long.toString(since))) ;
		if( end > 0 ) args.add(new BasicNameValuePair("end", Long.toString(end))) ;
		return request("TransHistory",args) ;
	}

	public String getTradeHistory() throws CryptoTradeException {
		return getTradeHistory(0,0,0,0,null,0,0,null) ;
	}

	public String getTradeHistory(int from,int count,int from_id,int end_id,String order,long since,long end,String pair) throws CryptoTradeException {
		List<NameValuePair> args = new ArrayList<NameValuePair>() ;		
		if( from > 0 ) args.add(new BasicNameValuePair("from", Integer.toString(from))) ;
		if( count > 0 ) args.add(new BasicNameValuePair("count", Integer.toString(count))) ;
		if( from_id > 0 ) args.add(new BasicNameValuePair("from_id", Integer.toString(from_id))) ;
		if( end_id > 0 ) args.add(new BasicNameValuePair("end_id", Integer.toString(end_id))) ;
		if( order != null && order.length() > 0 ) args.add(new BasicNameValuePair("order", order)) ;
		if( since > 0 ) args.add(new BasicNameValuePair("since", Long.toString(since))) ;
		if( end > 0 ) args.add(new BasicNameValuePair("end", Long.toString(end))) ;
		if( pair != null && pair.length() > 0 ) args.add(new BasicNameValuePair("pair", pair)) ;
		return request("TradeHistory",args) ;
	}
	
	public String getOrderList() throws CryptoTradeException {
		return getOrderList(0,0,0,0,null,0,0,null,0) ;
	}
	
	public String getOrderList(int from,int count,int from_id,int end_id,String order,long since,long end,String pair,int active) throws CryptoTradeException {
		List<NameValuePair> args = new ArrayList<NameValuePair>() ;		
		if( from > 0 ) args.add(new BasicNameValuePair("from", Integer.toString(from))) ;
		if( count > 0 ) args.add(new BasicNameValuePair("count", Integer.toString(count))) ;
		if( from_id > 0 ) args.add(new BasicNameValuePair("from_id", Integer.toString(from_id))) ;
		if( end_id > 0 ) args.add(new BasicNameValuePair("end_id", Integer.toString(end_id))) ;
		if( order != null && order.length() > 0 ) args.add(new BasicNameValuePair("order", order)) ;
		if( since > 0 ) args.add(new BasicNameValuePair("since", Long.toString(since))) ;
		if( end > 0 ) args.add(new BasicNameValuePair("end", Long.toString(end))) ;
		if( pair != null && pair.length() > 0 ) args.add(new BasicNameValuePair("pair", pair)) ;
		if( active > 0 ) args.add(new BasicNameValuePair("active", Long.toString(active))) ;
		return request("OrderList",args) ;
	}
	
	public String executeTrade(String pair,String type,double rate,double amount) throws CryptoTradeException {
		List<NameValuePair> args = new ArrayList<NameValuePair>() ;		
		args.add(new BasicNameValuePair("pair", pair)) ;
		args.add(new BasicNameValuePair("type", type)) ;
		args.add(new BasicNameValuePair("rate", Double.toString(rate))) ;
		args.add(new BasicNameValuePair("amount", Double.toString(amount))) ;
		return request("Trade",args) ;
	}
	
	public String cancelTrade(int order_id) throws CryptoTradeException {
		List<NameValuePair> args = new ArrayList<NameValuePair>() ;		
		args.add(new BasicNameValuePair("order_id", Integer.toString(order_id))) ;
		return request("CancelTrade",args) ;
	}
	
	public void initializeProperties(Properties p) {
		gson = new Gson() ;
		key = p.getProperty("btce.key") ;
		secret = p.getProperty("btce.secret") ;
		http = new DefaultHttpClient() ;
		nonce = (System.currentTimeMillis() / 1000) ;
		
		SecretKeySpec keyspec = null ;
		try {
			keyspec = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA512") ;
		} catch (UnsupportedEncodingException uee) {
			System.err.println("Unsupported encoding exception: "+ uee.toString()) ;
		}

		try {
			mac = Mac.getInstance("HmacSHA512") ;
		} catch (NoSuchAlgorithmException nsae) {
			System.err.println("No such algorithm exception: "
					+ nsae.toString());
		}

		try {
			mac.init(keyspec) ;
		} catch (InvalidKeyException ike) {
			System.err.println("Invalid key exception: " + ike.toString());
		}
		
		initialized = true ;
	}
	
	private final String request(String method, List<NameValuePair> args) throws CryptoTradeException {
		if( !initialized ) return null ;
		
		if (args == null) args = new ArrayList<NameValuePair>() ;

		args.add(new BasicNameValuePair("method", method)) ;
		args.add(new BasicNameValuePair("nonce",""+ ++nonce)) ;
		
		String postData = "";
		for (Iterator<NameValuePair> iter = args.iterator(); iter.hasNext();) {
			NameValuePair arg = iter.next() ;
			if (postData.length() > 0) postData += "&" ;
			postData += arg.getName() + "=" + arg.getValue() ;
		}

		HttpPost post = new HttpPost("https://btc-e.com/tapi") ;
        try {
			post.setEntity(new UrlEncodedFormEntity(args, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
			throw new CryptoTradeException("Invalid encoding for arguments.") ;
		}
        post.setHeader("Key",key) ;
        try {
			post.setHeader("Sign",Hex.encodeHexString(mac.doFinal(postData.getBytes("UTF-8")))) ;
		} catch (IllegalStateException e) {
			throw new CryptoTradeException("Illegal state exception signing request.") ;
		} catch (UnsupportedEncodingException e) {
			throw new CryptoTradeException("Unsupported encoding exception.") ;
		}
        HttpResponse r = null ;
		try {
			r = http.execute(post) ;
			int code = r.getStatusLine().getStatusCode() ;
			if( code != 200 ) throw new CryptoTradeException("Error connecting to BTC-E: "+code) ;
		} catch (ClientProtocolException e1) {
			throw new CryptoTradeException("Client protocol exception.") ;
		} catch (IOException e1) {
			throw new CryptoTradeException("IOException.") ;
		}
        HttpEntity e = r.getEntity();
        try {
			return EntityUtils.toString(e) ;
		} catch (ParseException e1) {
			throw new CryptoTradeException("ParseException.") ;
		} catch (IOException e1) {
			throw new CryptoTradeException("IOException.") ;
		}
	}
}
