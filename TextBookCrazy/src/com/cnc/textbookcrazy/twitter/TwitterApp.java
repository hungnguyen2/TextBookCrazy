package com.cnc.textbookcrazy.twitter;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;

/**
 * ツイッターのメインクラス.
 * 
 * @著者 Lorensius W. L. T <lorenz@londatiga.net>
 * 
 */
public class TwitterApp
{
	public interface TwDialogListener
	{
		public void onComplete( String value );
		
		public void onError( String value );
	}
	
	private Twitter						mTwitter;
	
	private TwitterSession				mSession;
	
	private AccessToken					mAccessToken;
	
	private CommonsHttpOAuthConsumer	mHttpOauthConsumer;
	
	private OAuthProvider				mHttpOauthprovider;
	
	private String						mConsumerKey;
	
	private String						mSecretKey;
	
	private ProgressDialog				mProgressDlg;
	
	private TwDialogListener			mListener;
	
	private Context						context;
	
	public static final String			REQUEST_URL		= "https://api.twitter.com/oauth/request_token";
	
	public static final String			ACCESS_URL		= "https://api.twitter.com/oauth/access_token";
	
	public static final String			AUTHORIZE_URL	= "https://api.twitter.com/oauth/authorize";
	
	public static final String			CALLBACK_URL	= "https://google.com";
	
	private static final String			TAG				= "TwitterApp";
	
	private Handler						mHandler		= new Handler( )
														{
															@Override
															public void handleMessage( Message msg )
															{
																mProgressDlg.dismiss( );
																
																if ( msg.what == 1 )
																{
																	if ( msg.arg1 == 1 )
																		mListener
																				.onError( "Error getting request token" );
																	else
																		mListener
																				.onError( "Error getting access token" );
																}
																else
																{
																	if ( msg.arg1 == 1 )
																		showLoginDialog( ( String ) msg.obj );
																	else
																		mListener.onComplete( "" );
																}
															}
														};
	
	public TwitterApp( Context context, String consumerKey, String secretKey )
	{
		this.context = context;
		mTwitter = new TwitterFactory( ).getInstance( );
		mSession = new TwitterSession( context );
		mProgressDlg = new ProgressDialog( context );
		
		mProgressDlg.requestWindowFeature( Window.FEATURE_NO_TITLE );
		
		mConsumerKey = consumerKey;
		mSecretKey = secretKey;
		
		mHttpOauthConsumer = new CommonsHttpOAuthConsumer( mConsumerKey,
				mSecretKey );
		mHttpOauthprovider = new DefaultOAuthProvider( REQUEST_URL, ACCESS_URL,
				AUTHORIZE_URL );
		
	}
	
	public void authorize( )
	{
		mProgressDlg.setMessage( "Loading..." );
		mProgressDlg.show( );
		
		new Thread( )
		{
			@Override
			public void run( )
			{
				String authUrl = "";
				int what = 1;
				
				try
				{
					authUrl = mHttpOauthprovider.retrieveRequestToken(
							mHttpOauthConsumer, CALLBACK_URL );
					
					what = 0;
					
					Log.d( TAG, "Request token url " + authUrl );
				}
				catch ( Exception e )
				{
					Log.d( TAG, "Failed to get request token" );
					
					e.printStackTrace( );
				}
				
				mHandler.sendMessage( mHandler
						.obtainMessage( what, 1, 0, authUrl ) );
			}
		}.start( );
	}
	
	private void configureToken( )
	{
		if ( mAccessToken != null )
		{
			mTwitter.setOAuthConsumer( mConsumerKey, mSecretKey );
			
			mTwitter.setOAuthAccessToken( mAccessToken );
		}
	}
	
	public Twitter getTwitter( )
	{
		return mTwitter;
	}
	
	public String getUserId( )
	{
		return mSession.getUserId( );
	}
	
	public String getUsername( )
	{
		return mSession.getUsername( );
	}
	
	private String getVerifier( String callbackUrl )
	{
		String verifier = "";
		
		try
		{
			callbackUrl = callbackUrl.replace( "twitterapp", "http" );
			
			URL url = new URL( callbackUrl );
			String query = url.getQuery( );
			
			String array[] = query.split( "&" );
			
			for ( String parameter : array )
			{
				String v[] = parameter.split( "=" );
				
				if ( URLDecoder.decode( v[ 0 ] ).equals(
						oauth.signpost.OAuth.OAUTH_VERIFIER ) )
				{
					verifier = URLDecoder.decode( v[ 1 ] );
					break;
				}
			}
		}
		catch ( MalformedURLException e )
		{
			e.printStackTrace( );
		}
		
		return verifier;
	}
	
	public boolean hasAccessToken( )
	{
		return ( mAccessToken == null ) ? false : true;
	}
	
	public void processToken( String callbackUrl )
	{
		
		final String verifier = getVerifier( callbackUrl );
		
		new Thread( )
		{
			@Override
			public void run( )
			{
				int what = 1;
				
				try
				{
					mHttpOauthprovider.retrieveAccessToken( mHttpOauthConsumer,
							verifier );
					
					mAccessToken = new AccessToken(
							mHttpOauthConsumer.getToken( ),
							mHttpOauthConsumer.getTokenSecret( ) );
					
					configureToken( );
					User user = mTwitter.verifyCredentials( );
					long userId = user.getId( );
					String name = user.getName( );
					
					mSession.storeAccessToken( mAccessToken,
							String.valueOf( userId ), name );
					
					what = 0;
				}
				catch ( Exception e )
				{
					Log.d( TAG, "Error getting access token" );
					
					e.printStackTrace( );
				}
				
				mHandler.sendMessage( mHandler.obtainMessage( what, 2, 0 ) );
			}
		}.start( );
	}
	
	public void resetAccessToken( )
	{
		if ( mAccessToken != null )
		{
			mSession.resetAccessToken( );
			
			mAccessToken = null;
		}
	}
	
	public void setListener( TwDialogListener listener )
	{
		mListener = listener;
	}
	
	private void showLoginDialog( String url )
	{
		final TwDialogListener listener = new TwDialogListener( )
		{
			@Override
			public void onComplete( String value )
			{
				processToken( value );
			}
			
			@Override
			public void onError( String value )
			{
				mListener.onError( "Failed opening authorization page" );
			}
		};
		
		new TwitterDialog( context, url, listener ).show( );
	}
	
	public void updateStatus( String status ) throws Exception
	{
		try
		{
			mTwitter.updateStatus( status );
		}
		catch ( TwitterException e )
		{
			throw e;
		}
	}
}
