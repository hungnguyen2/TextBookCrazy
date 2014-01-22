package com.cnc.textbookcrazy;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cnc.textbookcrazy.activity.MessageActivity;
import com.cnc.textbookcrazy.activity.SigninActivity;
import com.cnc.textbookcrazy.help.Constants;
import com.cnc.textbookcrazy.push.CommonUtilities;
import com.cnc.textbookcrazy.push.ServerUtilities;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService
{
	
	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static void generateNotification( Context context, String message )
	{
		int icon = R.drawable.icon;
		long when = System.currentTimeMillis( );
		NotificationManager notificationManager = ( NotificationManager )
				context.getSystemService( Context.NOTIFICATION_SERVICE );
		Notification notification = new Notification( icon, message, when );
		
		String title = context.getString( R.string.app_name );
		
		Intent notificationIntent = null;
		if ( Constants.STARTED_APP )
		{
			notificationIntent = new Intent( context, MessageActivity.class );
		}
		else
		{
			notificationIntent = new Intent( context, SigninActivity.class );
		}
		Log.d( "", "Started App = " + Constants.STARTED_APP );
		
		// set intent so it does not start a new activity
		notificationIntent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP |
				Intent.FLAG_ACTIVITY_SINGLE_TOP );
		PendingIntent intent =
				PendingIntent.getActivity( context, 0, notificationIntent, 0 );
		notification.setLatestEventInfo( context, title, message, intent );
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		// Play default notification sound
		notification.defaults |= Notification.DEFAULT_SOUND;
		
		// Vibrate if vibrate is enabled
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify( 0, notification );
		
		Log.d( "check running", "run notification" );
	}
	
	// private String serverUrl;
	
	// private final GCMLogger mLogger = new GCMLogger( "GCMBroadcastReceiver",
	// "[" + getClass( ).getName( ) + "]: " );
	
	//
	public GCMIntentService( )
	{
		super( CommonUtilities.SENDER_ID );
		// this.serverUrl = serverUrl;
		System.out.println( "gcm intent service" );
	}
	
	/**
	 * Method called on receiving a deleted message
	 * */
	@Override
	protected void onDeletedMessages( Context context, int total )
	{
		Log.i( TAG, "Received deleted messages notification" );
		String message = getString( R.string.gcm_deleted, total );
		CommonUtilities.displayMessage( context, message );
		// notifies user
		generateNotification( context, message );
	}
	
	/**
	 * Method called on Error
	 * */
	@Override
	public void onError( Context context, String errorId )
	{
		Log.i( TAG, "Received error: " + errorId );
		CommonUtilities.displayMessage( context, getString( R.string.gcm_error, errorId ) );
	}
	
	/**
	 * Method called on Receiving a new message
	 * */
	@Override
	protected void onMessage( Context context, Intent intent )
	{
		Log.i( TAG, "Received message hh" );
		String message = intent.getExtras( ).getString( CommonUtilities.EXTRA_MESSAGE );
		
		Log.d( "Extras", message );
		
		if ( message == null )
		{
			Log.d( "message", "null" );
		}
		
		CommonUtilities.displayMessage( context, message );
		// notifies user
		generateNotification( context, message );
	}
	
	@Override
	protected boolean onRecoverableError( Context context, String errorId )
	{
		// log message
		Log.i( TAG, "Received recoverable error: " + errorId );
		CommonUtilities.displayMessage( context, getString( R.string.gcm_recoverable_error,
				errorId ) );
		return super.onRecoverableError( context, errorId );
	}
	
	/**
	 * Method called on device registered
	 **/
	@Override
	protected void onRegistered( Context context, String registrationId )
	{
		Log.i( TAG, "Device registered: regId = " + registrationId );
		CommonUtilities.displayMessage( context, "Your device registred with GCM" );
		// ServerUtilities.register( context, "", "", registrationId,
		// UrlWeb.UPDATE_DEVICE_TOKEN );
	}
	
	/**
	 * Method called on device un registred
	 * */
	@Override
	protected void onUnregistered( Context context, String registrationId )
	{
		Log.i( TAG, "Device unregistered" );
		CommonUtilities.displayMessage( context, getString( R.string.gcm_unregistered ) );
		ServerUtilities.unregister( context, registrationId, CommonUtilities.SERVER_ORDER_URL );
	}
	
}
