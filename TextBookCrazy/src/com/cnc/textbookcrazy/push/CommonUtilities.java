package com.cnc.textbookcrazy.push;

import android.content.Context;
import android.content.Intent;

public class CommonUtilities
{
	static final String			SERVER_URL				= "http://10.0.2.2/gcm_server_php/register.php";
	public static final String	SERVER_ORDER_URL		= "http://192.168.0.238:8083/index.php/api/order/create";
	public static final String	SERVER_MESSAGE_URL		= "http://192.168.0.238:8083/index.php/api/chat/send";
	
	// Google project id
	public static final String	SENDER_ID				= "1031367535760";
	
	/**
	 * Tag used on log messages.
	 */
	public static final String	TAG						= "AndroidHive GCM";
	
	public static final String	DISPLAY_MESSAGE_ACTION	=
																"org.cnc.mtaxi.pushnotification.DISPLAY_MESSAGE";
	
	public static final String	EXTRA_MESSAGE			= "message";
	
	/**
	 * Notifies UI to display a message.
	 * <p>
	 * This method is defined in the common helper because it's used both by the UI and the background service.
	 * 
	 * @param context
	 *            application's context.
	 * @param message
	 *            message to be displayed.
	 */
	public static void displayMessage( Context context, String message )
	{
		Intent intent = new Intent( DISPLAY_MESSAGE_ACTION );
		intent.putExtra( EXTRA_MESSAGE, message );
		context.sendBroadcast( intent );
	}
}
