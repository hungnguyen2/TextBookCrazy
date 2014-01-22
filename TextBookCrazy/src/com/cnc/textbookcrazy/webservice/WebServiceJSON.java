package com.cnc.textbookcrazy.webservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class WebServiceJSON
{
	class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase
	{
		public static final String	METHOD_NAME	= "DELETE";
		
		public HttpDeleteWithBody( )
		{
			super( );
		}
		
		public HttpDeleteWithBody( final String uri )
		{
			super( );
			setURI( URI.create( uri ) );
		}
		
		public HttpDeleteWithBody( final URI uri )
		{
			super( );
			setURI( uri );
		}
		
		@Override
		public String getMethod( )
		{
			return METHOD_NAME;
		}
	}
	
	private class MySSLSocketFactory extends SSLSocketFactory
	{
		SSLContext	sslContext	= SSLContext.getInstance( "TLS" );
		
		public MySSLSocketFactory( KeyStore truststore ) throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException
		{
			super( truststore );
			
			TrustManager tm = new X509TrustManager( )
			{
				
				@Override
				public void checkClientTrusted( X509Certificate[] chain, String authType )
						throws java.security.cert.CertificateException
				{
				}
				
				@Override
				public void checkServerTrusted( X509Certificate[] chain, String authType )
						throws java.security.cert.CertificateException
				{
				}
				
				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers( )
				{
					return null;
				}
				
			};
			
			sslContext.init( null, new TrustManager[ ]
			{ tm }, null );
		}
		
		@Override
		public Socket createSocket( ) throws IOException
		{
			return sslContext.getSocketFactory( ).createSocket( );
		}
		
		@Override
		public Socket createSocket( Socket socket, String host, int port, boolean autoClose ) throws IOException,
				UnknownHostException
		{
			return sslContext.getSocketFactory( ).createSocket( socket, host, port, autoClose );
		}
	}
	
	//
	private final static int	CONNECTION_TIMEOUT	= 100000;
	private final static int	SOCKET_TIMEOUT		= 100000;
	
	private static JSONObject getJsonObjectFromMap( Map params ) throws JSONException
	{
		
		// all the passed parameters from the post request
		// iterator used to loop through all the parameters
		// passed in the post request
		@SuppressWarnings( "rawtypes" )
		Iterator iter = params.entrySet( ).iterator( );
		
		// Stores JSON
		JSONObject holder = new JSONObject( );
		
		// using the earlier example your first entry would get email
		// and the inner while would get the value which would be 'foo@bar.com'
		// { fan: { email : 'foo@bar.com' } }
		
		Log.d( "", "ok2" );
		// While there is another entry
		while ( iter.hasNext( ) )
		{
			// gets an entry in the params
			Map.Entry pairs = ( Map.Entry ) iter.next( );
			
			// creates a key for Map
			String key = ( String ) pairs.getKey( );
			Log.d( "", "ok1" );
			
			// Create a new map
			Map m = ( Map ) pairs.getValue( );
			
			// object for storing Json
			JSONObject data = new JSONObject( );
			
			// gets the value
			Iterator iter2 = m.entrySet( ).iterator( );
			while ( iter2.hasNext( ) )
			{
				Map.Entry pairs2 = ( Map.Entry ) iter2.next( );
				data.put( ( String ) pairs2.getKey( ), ( String ) pairs2.getValue( ) );
			}
			
			// puts email and 'foo@bar.com' together in map
			holder.put( key, data );
		}
		return holder;
	}
	
	// variable android object, others
	private InputStream	is		= null;
	// variable primary, string
	private String		url;
	
	private String		result	= null;
	
	public WebServiceJSON( String url )
	{
		this.url = url;
	}
	
	private HttpParams checkConnection( ) throws ConnectTimeoutException
	{
		// time out
		HttpParams httpParameters = new BasicHttpParams( );
		// Set the timeout in milliseconds until a connection is established.
		HttpConnectionParams.setConnectionTimeout( httpParameters, CONNECTION_TIMEOUT );
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout( httpParameters, SOCKET_TIMEOUT );
		
		return httpParameters;
	}
	
	public String delete( List< ? extends NameValuePair > params )
	{
		String result = null;
		
		try
		{
			HttpDeleteWithBody httpDelete = new HttpDeleteWithBody( url );
			UrlEncodedFormEntity data = new UrlEncodedFormEntity( params );
			httpDelete.setEntity( data );
			
			HttpParams httpParameters = checkConnection( );
			
			HttpClient httpclient = new DefaultHttpClient( httpParameters );
			HttpResponse response = httpclient.execute( httpDelete );
			HttpEntity entity = response.getEntity( );
			is = entity.getContent( );
		}
		catch ( SocketTimeoutException stoex )
		{
			return "sto";
		}
		catch ( ConnectTimeoutException cex )
		{
			return "cto";
		}
		catch ( Exception e )
		{
			Log.e( "log_tag", "Error in http connection " + e.toString( ) );
			return "http";
		}
		result = parseIsToString( is );
		
		return result;
	}
	
	public String delete( String json )
	{
		String result = null;
		
		try
		{
			// HttpDelete httpDelete = new HttpDelete( url );
			HttpDeleteWithBody httpDelete = new HttpDeleteWithBody( url );
			// UrlEncodedFormEntity data = new UrlEncodedFormEntity( params );
			
			Log.d( "del", json );
			StringEntity data = new StringEntity( json );
			httpDelete.setHeader( "Accept", "application/json" );
			httpDelete.setHeader( "Content-type", "application/json" );
			httpDelete.setEntity( data );
			
			// HttpParams httpParameters = checkConnection( );
			
			HttpClient httpclient = getNewHttpClient( );
			HttpResponse response = httpclient.execute( httpDelete );
			HttpEntity entity = response.getEntity( );
			is = entity.getContent( );
		}
		catch ( SocketTimeoutException stoex )
		{
			return "sto";
		}
		catch ( ConnectTimeoutException cex )
		{
			return "cto";
		}
		catch ( Exception e )
		{
			Log.e( "log_tag", "Error in http connection " + e.toString( ) );
			return "http";
		}
		result = parseIsToString( is );
		
		return result;
	}
	
	public String get( List< BasicNameValuePair > param )
	{
		// add parameter to url
		StringBuilder newUrl = new StringBuilder( );
		newUrl.append( url );
		for ( int k = 0; k < param.size( ); k++ )
		{
			if ( k == 0 )
			{
				newUrl.append( "?" );
			}
			else
			{
				newUrl.append( "&" );
			}
			newUrl.append( param.get( k ).getName( ) );
			newUrl.append( "=" + param.get( k ).getValue( ) );
		}
		// Phần này tạo kết nối
		try
		{
			// String url2 = url + URLEncoder.encode( newUrl.toString( ), "UTF-8" );
			// Log.d( "Webservice: getMethod", "Url:" + url );
			Log.d( "Webservice: getMethod", "Url:" + newUrl );
			
			HttpGet httpget = new HttpGet( newUrl.toString( ) );
			httpget.addHeader( "accept", "application/json" );
			HttpParams httpParams = checkConnection( );
			
			HttpClient httpclient = new DefaultHttpClient( httpParams );
			// HttpClient httpclient = getNewHttpClient( );
			HttpResponse response = httpclient.execute( httpget );
			HttpEntity entity = response.getEntity( );
			is = entity.getContent( );
		}
		catch ( SocketTimeoutException stoex )
		{
			return "sto";
		}
		catch ( ConnectTimeoutException cex )
		{
			return "cto";
		}
		catch ( Exception e )
		{
			Log.e( "log_tag", "Error in http connection " + e.toString( ) );
			return "http";
		}
		result = parseIsToString( is );
		
		return result;
	}
	
	public HttpClient getNewHttpClient( )
	{
		try
		{
			KeyStore trustStore = KeyStore.getInstance( KeyStore.getDefaultType( ) );
			trustStore.load( null, null );
			
			SSLSocketFactory sf = new MySSLSocketFactory( trustStore );
			sf.setHostnameVerifier( SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER );
			
			HttpParams params = new BasicHttpParams( );
			HttpProtocolParams.setVersion( params, HttpVersion.HTTP_1_1 );
			HttpProtocolParams.setContentCharset( params, HTTP.UTF_8 );
			// Set the timeout in milliseconds until a connection is established.
			HttpConnectionParams.setConnectionTimeout( params, CONNECTION_TIMEOUT );
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			HttpConnectionParams.setSoTimeout( params, SOCKET_TIMEOUT );
			
			SchemeRegistry registry = new SchemeRegistry( );
			registry.register( new Scheme( "http", PlainSocketFactory.getSocketFactory( ), 80 ) );
			registry.register( new Scheme( "https", sf, 443 ) );
			
			ClientConnectionManager ccm = new ThreadSafeClientConnManager( params, registry );
			
			return new DefaultHttpClient( ccm, params );
		}
		catch ( Exception e )
		{
			return new DefaultHttpClient( );
		}
	}
	
	private String parseIsToString( InputStream is )
	{
		String result = "";
		try
		{
			BufferedReader reader = new BufferedReader( new InputStreamReader( is, "iso-8859-1" ), 8 );
			StringBuilder sb = new StringBuilder( );
			sb.append( reader.readLine( ) + "\n" );
			
			String line = "0";
			while ( ( line = reader.readLine( ) ) != null )
			{
				sb.append( line + "\n" );
			}
			is.close( );
			result = sb.toString( );
			Log.d( "result from server", result );
		}
		catch ( Exception e )
		{
			Log.e( "log_tag", "Error converting result " + e.toString( ) );
		}
		return result;
	}
	
	public String post( Map params )
	{
		// create connection
		
		// for ( int k = 0; k < params.size( ); k++ )
		// {
		// Log.d( params.get( k ).getName( ), params.get( k ).getValue( ) );
		// }
		
		try
		{
			HttpPost httppost = new HttpPost( url );
			
			JSONObject jsonObject = getJsonObjectFromMap( params );
			
			// UrlEncodedFormEntity data = new UrlEncodedFormEntity( );
			Log.d( "", jsonObject.toString( ) );
			
			StringEntity data = new StringEntity( jsonObject.toString( ) );
			httppost.setEntity( data );
			httppost.setHeader( "Accept", "application/json" );
			httppost.setHeader( "Content-type", "application/json" );
			
			HttpClient httpclient = getNewHttpClient( );
			HttpResponse response = httpclient.execute( httppost );
			HttpEntity entity = response.getEntity( );
			is = entity.getContent( );
		}
		catch ( SocketTimeoutException stoex )
		{
			return "sto";
		}
		catch ( ConnectTimeoutException cex )
		{
			return "cto";
		}
		catch ( Exception e )
		{
			Log.e( "log_tag_post", "Error in http connection " + e.toString( ) );
			return "http";
		}
		result = parseIsToString( is );
		return result;
	}
	
	public String post( String json )
	{
		try
		{
			HttpPost httppost = new HttpPost( url );
			
			Log.d( "", "send data: " + json );
			StringEntity data = new StringEntity( json );
			httppost.setEntity( data );
			httppost.setHeader( "Accept", "application/json" );
			httppost.setHeader( "Content-type", "application/json" );
			
			HttpClient httpclient = getNewHttpClient( );
			HttpResponse response = httpclient.execute( httppost );
			HttpEntity entity = response.getEntity( );
			is = entity.getContent( );
		}
		catch ( SocketTimeoutException stoex )
		{
			Log.e( "log_tag_post", "SocketTimeoutException " + stoex.toString( ) );
			
			return "sto";
		}
		catch ( ConnectTimeoutException cex )
		{
			Log.e( "log_tag_post", "ConnectTimeoutException " + cex.toString( ) );
			
			return "cto";
		}
		catch ( Exception e )
		{
			Log.e( "log_tag_post", "Error in http connection " + e.toString( ) );
			return "http";
		}
		result = parseIsToString( is );
		return result;
	}
	
	// public String postWithImage( List< BasicNameValuePair > param, BasicNameValuePair imagePair )
	// {
	// /*
	// * request jar: apache-mime4j, httpmime
	// */
	// // create connection
	// try
	// {
	// HttpPost httppost = new HttpPost( url );
	//
	// MultipartEntity multipartEntity = new MultipartEntity( );
	//
	// if ( imagePair.getValue( ) != null && !imagePair.getValue( ).equals( "" ) )
	// {
	// File file = new File( imagePair.getValue( ) );
	// multipartEntity.addPart( imagePair.getName( ), new FileBody( file ) );
	// }
	// for ( int k = 0; k < param.size( ); k++ )
	// {
	// multipartEntity.addPart( param.get( k ).getName( ), new StringBody( param.get( k ).getValue( ) ) );
	// }
	// Log.d( "", multipartEntity.toString( ) );
	// httppost.setEntity( multipartEntity );
	//
	// HttpParams httpParams = checkConnection( );
	//
	// HttpClient httpclient = new DefaultHttpClient( httpParams );
	// HttpResponse response = httpclient.execute( httppost );
	// HttpEntity entity = response.getEntity( );
	// is = entity.getContent( );
	// }
	// catch ( SocketTimeoutException stoex )
	// {
	// return "sto";
	// }
	// catch ( ConnectTimeoutException cex )
	// {
	// return "cto";
	// }
	// catch ( Exception e )
	// {
	// Log.e( "log_tag", "Error in http connection " + e.toString( ) );
	// return "http";
	// }
	// result = parseIsToString( is );
	// return result;
	// }
	//
	public String put( List< BasicNameValuePair > params )
	{
		try
		{
			HttpPut httpPut = new HttpPut( url );
			
			UrlEncodedFormEntity data = new UrlEncodedFormEntity( params );
			httpPut.setEntity( data );
			
			HttpParams httpParams = checkConnection( );
			
			HttpClient httpclient = new DefaultHttpClient( httpParams );
			HttpResponse response = httpclient.execute( httpPut );
			HttpEntity entity = response.getEntity( );
			is = entity.getContent( );
		}
		catch ( SocketTimeoutException stoex )
		{
			return "sto";
		}
		catch ( ConnectTimeoutException cex )
		{
			return "cto";
		}
		catch ( Exception e )
		{
			Log.e( "log_tag", "Error in http connection " + e.toString( ) );
			return "http";
		}
		result = parseIsToString( is );
		return result;
	}
}
