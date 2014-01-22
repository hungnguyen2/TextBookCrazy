package com.cnc.textbookcrazy.twitter;

import twitter4j.auth.AccessToken;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * ツイッターセッション.
 * 
 * @著者 Lorensius W. L. T <lorenz@londatiga.net>
 * 
 */
public class TwitterSession
{
	private SharedPreferences	sharedPref;
	
	private Editor				editor;
	
	private static final String	TWEET_AUTH_KEY			= "auth_key";
	
	private static final String	TWEET_AUTH_SECRET_KEY	= "auth_secret_key";
	
	private static final String	TWEET_USER_NAME			= "user_name";
	
	private static final String	TWEET_USER_ID			= "user_id";
	
	private static final String	TWEET_AVATAR			= "user_avatar";
	
	private static final String	TWEET_EMAIL				= "user_email";
	
	private static final String	SHARED					= "Twitter_Preferences";
	
	public TwitterSession( Context context )
	{
		sharedPref = context.getSharedPreferences( SHARED, Context.MODE_PRIVATE );
		editor = sharedPref.edit( );
	}
	
	public AccessToken getAccessToken( )
	{
		String token = sharedPref.getString( TWEET_AUTH_KEY, null );
		String tokenSecret = sharedPref.getString( TWEET_AUTH_SECRET_KEY, null );
		
		if ( token != null && tokenSecret != null )
			return new AccessToken( token, tokenSecret );
		else
			return null;
	}
	
	public String getUserId( )
	{
		return sharedPref.getString( TWEET_USER_ID, "" );
	}
	
	public String getUsername( )
	{
		return sharedPref.getString( TWEET_USER_NAME, "" );
	}
	
	public void resetAccessToken( )
	{
		editor.clear( );
		editor.commit( );
	}
	
	public void storeAccessToken( AccessToken accessToken, String userId,
			String name )
	{
		editor.putString( TWEET_AUTH_KEY, accessToken.getToken( ) );
		editor.putString( TWEET_AUTH_SECRET_KEY, accessToken.getTokenSecret( ) );
		editor.putString( TWEET_USER_ID, userId );
		editor.putString( TWEET_USER_NAME, name );
		
		editor.commit( );
	}
}
