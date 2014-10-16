package com.utilitiespackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class CustomHttpClient {
	//public static String thisIp = "www.vnit.ac.in/farmbook";
	public static String thisIp = "10.0.2.2/farmbook";

	/** The time it takes for our client to timeout */
	public static final int HTTP_TIMEOUT = 15 * 1000; // milliseconds

	/** Single instance of our HttpClient */
	private static HttpClient mHttpClient;

	/**
	 * Get our single instance of our HttpClient object.
	 *
	 * @return an HttpClient object with connection parameters set
	 */

	private static HttpClient getHttpClient() {
		if (mHttpClient == null) {
			mHttpClient = new DefaultHttpClient();
			final HttpParams params = mHttpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);
			ConnManagerParams.setTimeout(params, HTTP_TIMEOUT);
		}
		return mHttpClient;
	}

	/**
	 * Performs an HTTP Post request to the specified url with the
	 * specified parameters.
	 *
	 * @param url The web address to post the request to
	 * @param postParameters The parameters to send via the request
	 * @return The result of the request
	 * @throws Exception
	 */
	public static String executeHttpPost(String url, ArrayList<NameValuePair> postParameters) throws Exception {

		BufferedReader in = null;
		try {
			HttpClient client = getHttpClient();
			HttpPost request = new HttpPost("http://"+thisIp+"/"+url);
			request.setEntity(new UrlEncodedFormEntity(postParameters));
			HttpResponse response = client.execute(request);

			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();

			return sb.toString().trim();
			//return sb.toString().replaceAll("\\s+","");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Log.e("CustomHttpClient", e.getMessage());
				}
			}
		}
	}

	/**
	 * Performs an HTTP GET request to the specified url.
	 *
	 * @param url The web address to post the request to
	 * @return The result of the request
	 * @throws Exception
	 */
	public static String executeHttpGet(String url) throws Exception {

		BufferedReader in = null;
		try {
			HttpClient client = getHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI("http://"+thisIp+"/"+url));
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();

			return sb.toString().replaceAll("\\s+","");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Log.e("CustomHttpClient", e.getMessage());
				}
			}
		}
	}
	
	public static Bitmap downloadImage(String fileUrl){
		URL myFileUrl =null;          
		try {
			myFileUrl= new URL("http://"+CustomHttpClient.thisIp+"/images/"+fileUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn= (HttpURLConnection)myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();

			Bitmap picture = BitmapFactory.decodeStream(is);
			return picture;
		} catch (IOException e) {
			Log.e("downloadImage",e.getMessage());
		}
		return null;
	}
}
