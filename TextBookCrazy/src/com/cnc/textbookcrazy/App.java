package com.cnc.textbookcrazy;

import com.cnc.textbookcrazy.activity.MainActivity;
import com.cnc.textbookcrazy.activity.SigninActivity;
import com.cnc.textbookcrazy.activity.SignupActivity;

public class App
{
	public static SigninActivity	signinActivity;
	public static SignupActivity	signupActivity;
	public static MainActivity		mainActivity;
	
	public static void exit( )
	{
		if ( signinActivity != null )
		{
			signinActivity.finish( );
		}
		
		if ( signupActivity != null )
		{
			signupActivity.finish( );
		}
		
		if ( mainActivity != null )
		{
			mainActivity.finish( );
		}
	}
	
}
