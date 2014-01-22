package com.cnc.textbookcrazy;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class MActivity extends Activity implements IPushNotificationListener
{
	
	protected String	nameActivity	= "";
	
	@Override
	public boolean dispatchTouchEvent( MotionEvent event )
	{
		View view = getCurrentFocus( );
		boolean ret = super.dispatchTouchEvent( event );
		
		if ( view instanceof EditText )
		{
			View w = getCurrentFocus( );
			int scrcoords[] = new int[ 2 ];
			w.getLocationOnScreen( scrcoords );
			float x = event.getRawX( ) + w.getLeft( ) - scrcoords[ 0 ];
			float y = event.getRawY( ) + w.getTop( ) - scrcoords[ 1 ];
			
			if ( event.getAction( ) == MotionEvent.ACTION_UP
					&& ( x < w.getLeft( ) || x >= w.getRight( )
							|| y < w.getTop( ) || y > w.getBottom( ) ) )
			{
				InputMethodManager imm = ( InputMethodManager ) getSystemService( Context.INPUT_METHOD_SERVICE );
				imm.hideSoftInputFromWindow( getWindow( ).getCurrentFocus( ).getWindowToken( ), 0 );
			}
		}
		return ret;
	}
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		getWindow( ).setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN );
		
	}
	
	@Override
	public void processPush( )
	{
		
	}
	
}
