package com.cnc.textbookcrazy.help;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.cnc.textbookcrazy.R;

public class MyYesNoDialog
{
	public interface MyYesNoImp
	{
		public void onNegative( );
		
		public void onPositive( );
	}
	
	public static void showDialog( final String message, Activity activity, final MyYesNoImp myYesNoImp )
	{
		AlertDialog.Builder builder = new AlertDialog.Builder( activity );
		final AlertDialog alert;
		alert = builder.create( );
		
		builder.setTitle( R.string.dl_message ).setMessage( message )
				.setPositiveButton( R.string.dl_yes, new DialogInterface.OnClickListener( )
				{
					
					@Override
					public void onClick( DialogInterface dialog, int which )
					{
						alert.dismiss( );
						myYesNoImp.onPositive( );
					}
				} ).
				setNegativeButton( R.string.dl_no, new DialogInterface.OnClickListener( )
				{
					
					@Override
					public void onClick( DialogInterface dialog, int which )
					{
						
						alert.dismiss( );
						myYesNoImp.onNegative( );
					}
				} ).
				show( );
	}
}
