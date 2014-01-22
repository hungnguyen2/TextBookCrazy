package com.cnc.textbookcrazy.help;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.cnc.textbookcrazy.R;

public class MyOkDialog
{
	public static boolean	showing	= false;
	
	public static void showDialog( final String message, Context context )
	{
		AlertDialog.Builder builder = new AlertDialog.Builder( context );
		final AlertDialog alert;
		alert = builder.create( );
		
		builder.setTitle( R.string.dl_message ).setMessage( message )
				.setNeutralButton( R.string.dl_btn_ok, new DialogInterface.OnClickListener( )
				{
					
					@Override
					public void onClick( DialogInterface dialog, int which )
					{
						alert.dismiss( );
						showing = false;
					}
				} ).show( );
	}
}
