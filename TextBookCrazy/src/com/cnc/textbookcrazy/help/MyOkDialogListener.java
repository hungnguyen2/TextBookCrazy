package com.cnc.textbookcrazy.help;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.cnc.textbookcrazy.R;

public class MyOkDialogListener
{
	public interface MyOkDiaglogImp
	{
		public void onPositive( );
	}
	
	public static boolean	showing	= false;
	
	public static void showDialog( final String message, Context context, final MyOkDiaglogImp myOkDialogListener )
	{
		AlertDialog.Builder builder = new AlertDialog.Builder( context );
		final AlertDialog alert;
		alert = builder.create( );
		showing = true;
		builder.setTitle( R.string.dl_message ).setMessage( message )
				.setNeutralButton( R.string.dl_btn_ok, new DialogInterface.OnClickListener( )
				{
					
					@Override
					public void onClick( DialogInterface dialog, int which )
					{
						showing = false;
						alert.dismiss( );
						myOkDialogListener.onPositive( );
					}
				} ).show( );
	}
}
