package org.ryangray.graytrade;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class APIQueryHitBTCExample {

public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
String secretKey = "8df04fda965f03119c4643c7c12c88ba";
String publicKey = "81f1dcf08c6be3cd3cee087ae26b5f31";
Date date = new Date();
String message = "/api/1/trading/balance?nonce=" + date.getTime() + "&apikey=" + publicKey;
String sign = hmacDigest(message, secretKey);

 HttpClient client = HttpClientBuilder.create().build();
 HttpGet httpget = new HttpGet("http://demo-api.hitbtc.com" + message);
 httpget.addHeader("X-Signature", sign);
 try {
     HttpResponse response = client.execute(httpget);
     System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

     BufferedReader rd = new BufferedReader(
             new InputStreamReader(response.getEntity().getContent()));

     StringBuilder result = new StringBuilder();
     String line = "";
     while ((line = rd.readLine()) != null) {
         result.append(line);
     }
     System.out.println(result);

 } catch (IOException e) {
     e.printStackTrace();
 }
}

public static String hmacDigest(String message, String secretKey) {
String digest = null;
String algo = "HmacSHA512";
try {
SecretKeySpec key = new SecretKeySpec((secretKey).getBytes("UTF-8"), algo);
Mac mac = Mac.getInstance(algo);
mac.init(key);

     byte[] bytes = mac.doFinal(message.getBytes("UTF-8"));

     StringBuilder hash = new StringBuilder();
     for (int i = 0; i < bytes.length; i++) {
         String hex = Integer.toHexString(0xFF & bytes[i]);
         if (hex.length() == 1) {
             hash.append('0');
         }
         hash.append(hex);
     }
     digest = hash.toString();
 } catch (UnsupportedEncodingException e) {
     e.printStackTrace();
 } catch (InvalidKeyException e) {
     e.printStackTrace();
 } catch (NoSuchAlgorithmException e) {
     e.printStackTrace();
 }
 return digest;
}

}