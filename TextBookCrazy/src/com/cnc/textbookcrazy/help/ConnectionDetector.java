package com.cnc.textbookcrazy.help;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector
{
	/**
	 * Checking for all possible internet providers
	 * **/
	public static final boolean isConnectingToInternet( Context context )
	{
		ConnectivityManager connectivity = ( ConnectivityManager ) context
				.getSystemService( Context.CONNECTIVITY_SERVICE );
		if ( connectivity != null )
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo( );
			if ( info != null )
				for ( int i = 0; i < info.length; i++ )
					if ( info[ i ].getState( ) == NetworkInfo.State.CONNECTED )
					{
						return true;
					}
			
		}
		return false;
	}
	
	private Context	_context;
	
	public ConnectionDetector( Context context )
	{
		this._context = context;
	}
}
