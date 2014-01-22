package com.cnc.textbookcrazy.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cnc.textbookcrazy.App;
import com.cnc.textbookcrazy.MActivity;
import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.help.ConnectionDetector;
import com.cnc.textbookcrazy.help.Constants;
import com.cnc.textbookcrazy.help.MyOkDialog;
import com.cnc.textbookcrazy.push.CommonUtilities;
import com.cnc.textbookcrazy.webservice.URL;
import com.cnc.textbookcrazy.webservice.datainout.LoginInput;
import com.cnc.textbookcrazy.webservice.datainout.LoginOutput;
import com.cnc.textbookcrazy.webservice.task.LoginTask;
import com.cnc.textbookcrazy.webservice.task.LoginTask.LoginTaskImp;
import com.google.android.gcm.GCMRegistrar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class SigninActivity extends MActivity implements LoginTaskImp
{
	/*
	 * hung
	 */
	public class NetworkChangeReceiver extends BroadcastReceiver
	{
		
		private boolean isNetworkAvailable( Context context )
		{
			ConnectivityManager connectivity = ( ConnectivityManager )
					context.getSystemService( Context.CONNECTIVITY_SERVICE );
			if ( connectivity != null )
			{
				NetworkInfo[] info = connectivity.getAllNetworkInfo( );
				if ( info != null )
				{
					for ( int i = 0; i < info.length; i++ )
					{
						if ( info[ i ].getState( ) == NetworkInfo.State.CONNECTED )
						{
							
							return true;
						}
					}
				}
			}
			return false;
		}
		
		@SuppressWarnings( "unused" )
		@Override
		public void onReceive( final Context context, final Intent intent )
		{
			if ( !isNetworkAvailable( context ) )
			{
				if ( count == 0 )
				{
					builder.setMessage( getText( R.string.network_not_available ) );
					builder.setTitle( getText( R.string.network_not_found_title ) );
					builder.setPositiveButton( getText( R.string.str_btnok ), new DialogInterface.OnClickListener( )
					{
						
						@Override
						public void onClick( DialogInterface dialog, int which )
						{
							if ( alert != null )
								alert.dismiss( );
							if ( timer != null )
								timer.cancel( );
						}
					} );
					alert = builder.create( );
					alert.show( );
					count = 1;
					
					timer = new Timer( );
					
					timer.scheduleAtFixedRate( new TimerTask( )
					{
						@Override
						public void run( )
						{
							runOnUiThread( new Runnable( )
							{
								@Override
								public void run( )
								{
									if ( count == 1 )
									{
										Log.d( "", "dismis" );
										if ( alert != null )
											alert.dismiss( );
										if ( timer != null )
											timer.cancel( );
									}
								}
							} );
						}
					}, 5000, 5000 );
				}
			}
			else
			{
				if ( !Constants.REGISTERED_GCM )
				{
					registGCM( );
				}
				if ( alert != null )
				{
					alert.dismiss( );
				}
				if ( timer != null )
				{
					timer.cancel( );
				}
				count = 0;
			}
		}
	}
	
	private int							count					= 0;
	
	AlertDialog							alert					= null;
	Timer								timer;
	AlertDialog.Builder					builder;
	protected NetworkChangeReceiver		networkChangeReceiver;
	
	private Button						btn_signin, btn_signup;
	private TranslateAnimation			animationBtnSignup, animationBtnSignin, animationLinear, animationLinear2,
										animationBtnSignup2, animationBtnSignin2;
	private EditText					edt_userName, edt_password;
	private int							Measuredwidth, Measuredheight;
	private LinearLayout				linearSignin;
	//
	private LoginInput					loginInput;
	int									back					= 0;
	private float						btn_signin_x, btn_signup_x;
	private Button						btn_signup_2;
	
	protected final BroadcastReceiver	mHandleMessageReceiver	= new BroadcastReceiver( )
																{
																	
																	@Override
																	public void onReceive( Context context,
																			Intent intent )
																	{
																		// if ( nameActivity.equals( "ChatActivity" ) )
																		// {
																		// MActivity.this.processPush( );
																		// }
																		// else
																		// {
																		// Log.d( MActivity.this.getClass( ).getName( ),
																		// "on broadcast receiver = "
																		// + context );
																		// }
																	}
																	
																};
	
	public static final String			PREFS_REMEMBER			= "remember account";
	
	private boolean checkLogin( )
	{
		if ( edt_userName.length( ) < 1 )
		{
			MyOkDialog.showDialog( getString( R.string.dl_email_needed ), this );
			return false;
		}
		
		if ( edt_password.length( ) < 1 )
		{
			MyOkDialog.showDialog( getString( R.string.dl_password_needed ), this );
			return false;
		}
		return true;
	}
	
	private void initGpsNetwork( )
	{
		LocationManager service = ( LocationManager ) getSystemService( LOCATION_SERVICE );
		boolean enabledGPS = service
				.isProviderEnabled( LocationManager.NETWORK_PROVIDER );
		if ( !enabledGPS )
		{
			// Toast.makeText( this, "GPS signal not found", Toast.LENGTH_LONG ).show( );
			Toast.makeText( this, getString( R.string.wifi_gps ), Toast.LENGTH_LONG ).show( );
			Intent intent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
			startActivityForResult( intent, 1 );
		}
		else
		{
			if ( !ConnectionDetector.isConnectingToInternet( this ) )
			{
				MyOkDialog.showDialog( getString( R.string.a_connect_internet ), this );
			}
		}
	}
	
	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		initGpsNetwork( );
		
		super.onActivityResult( requestCode, resultCode, data );
	}
	
	@Override
	public void onBackPressed( )
	{
		if ( back == 0 )
		{
			super.onBackPressed( );
		}
		else
		{
			// back = 0;
			// linearSignin.setVisibility( View.INVISIBLE );
			transitionLinear2( );
		}
	}
	
	@SuppressLint( "NewApi" )
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_signin );
		
		if ( !Constants.REGISTERED_GCM )
		{
			registGCM( );
		}
		builder = new AlertDialog.Builder(
				this );
		IntentFilter filter = new IntentFilter( ConnectivityManager.CONNECTIVITY_ACTION );
		networkChangeReceiver = new NetworkChangeReceiver( );
		registerReceiver( networkChangeReceiver, filter );
		
		ImageLoader.getInstance( ).init( ImageLoaderConfiguration.createDefault( this ) );
		initGpsNetwork( );
		
		Point size = new Point( );
		WindowManager w = getWindowManager( );
		
		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB )
		{
			w.getDefaultDisplay( ).getSize( size );
			Measuredwidth = size.x;
			Measuredheight = size.y;
		}
		else
		{
			Display d = w.getDefaultDisplay( );
			Measuredwidth = d.getWidth( );
			Measuredheight = d.getHeight( );
		}
		Log.d( this.getClass( ).getName( ), "Width = " + Measuredwidth );
		Log.d( this.getClass( ).getName( ), "height = " + Measuredheight );
		
		btn_signin = ( Button ) findViewById( R.id.btn_signin );
		btn_signin.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				btn_signin_x = btn_signin.getLeft( );
				Log.d( "", "width signin= " + btn_signin.getWidth( ) );
				Log.d( "", "x  signin = " + btn_signin.getLeft( ) );
				Log.d( "", "y signin = " + btn_signin.getTop( ) );
				onTransition( );
			}
		} );
		
		btn_signup = ( Button ) findViewById( R.id.btn_signup );
		btn_signup.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				btn_signup_x = btn_signup.getLeft( );
				Log.d( "", "width = " + btn_signup.getWidth( ) );
				Log.d( "", "x = " + btn_signup.getLeft( ) );
				Log.d( "", "y = " + btn_signup.getTop( ) );
				// onTransition( );
				Intent t = new Intent( SigninActivity.this, SignupActivity.class );
				startActivity( t );
			}
		} );
		
		linearSignin = ( LinearLayout ) findViewById( R.id.linearSignin );
		
		ImageView imv_forgot_password = ( ImageView ) findViewById( R.id.imv_forgot_password );
		imv_forgot_password.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				Intent t = new Intent( SigninActivity.this, ForgotPasswordActivity.class );
				startActivity( t );
			}
		} );
		
		edt_password = ( EditText ) findViewById( R.id.edt_password );
		edt_userName = ( EditText ) findViewById( R.id.edt_username );
		btn_signup_2 = ( Button ) findViewById( R.id.btn_signup_2 );
		btn_signup_2.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				if ( checkLogin( ) )
				{
					if ( ConnectionDetector.isConnectingToInternet( SigninActivity.this ) )
					{
						loginInput = new LoginInput( edt_userName.getText( ).toString( ),
								edt_password.getText( ).toString( ), Constants.GCM_ID );
						LoginTask loginTask = new LoginTask( SigninActivity.this, loginInput, SigninActivity.this );
						loginTask.execute( );
					}
					else
					{
						MyOkDialog.showDialog( getString( R.string.a_connect_internet ), SigninActivity.this );
					}
				}
				// Intent t = new Intent( SigninActivity.this, MainActivity.class );
				// startActivity( t );
			}
		} );
	}
	
	@Override
	protected void onDestroy( )
	{
		Log.d( SigninActivity.this.nameActivity, "on destroy" );
		Constants.STARTED_APP = false;
		// unregisterReceiver( mHandleMessageReceiver );
		unregisterReceiver( networkChangeReceiver );
		App.signinActivity = null;
		super.onDestroy( );
	}
	
	@Override
	public void onPostExecute( LoginOutput loginOutput )
	{
		Constants.AUTHOR_TOKEN = loginOutput.getAuth_token( );
		Constants.USER_NAME = loginOutput.getUsername( );
		Constants.EMAIL = loginOutput.getEmail( );
		Constants.USER_ID = loginOutput.getUser_id( );
		Constants.AVATA = URL.IP + loginOutput.getAvatar( );
		
		SharedPreferences settings = getSharedPreferences( PREFS_REMEMBER, 0 );
		SharedPreferences.Editor editor = settings.edit( );
		editor.putString( "username", edt_userName.getText( ).toString( ) );
		editor.putString( "password", edt_password.getText( ).toString( ) );
		editor.commit( );
		
		SharedPreferences settings2 = getSharedPreferences( Constants.PREF_USER, 0 );
		SharedPreferences.Editor editor2 = settings2.edit( );
		editor2.putString( "author_token", Constants.AUTHOR_TOKEN );
		editor2.putString( "user_name", Constants.USER_NAME );
		editor2.putString( "email", Constants.EMAIL );
		editor2.putString( "user_id", Constants.USER_ID );
		editor2.putString( "avatar", Constants.AVATA );
		editor2.commit( );
		
		Intent t = new Intent( SigninActivity.this, MainActivity.class );
		startActivity( t );
		finish( );
	}
	
	@Override
	protected void onResume( )
	{
		super.onResume( );
		Constants.STARTED_APP = true;
		
		SharedPreferences settings = getSharedPreferences( PREFS_REMEMBER, 0 );
		String username = settings.getString( "username", "" );
		String password = settings.getString( "password", "" );
		
		SharedPreferences settings2 = getSharedPreferences( Constants.PREF_USER, 0 );
		String author_token = settings2.getString( "author_token", "" );
		String user_name = settings2.getString( "user_name", "" );
		String email = settings2.getString( "email", "" );
		String user_id = settings2.getString( "user_id", "" );
		String avatar = settings2.getString( "avatar", "" );
		
		if ( !author_token.equals( "" ) )
		{
			Constants.AUTHOR_TOKEN = author_token;
			Constants.USER_NAME = user_name;
			Constants.EMAIL = email;
			Constants.USER_ID = user_id;
			Constants.AVATA = avatar;
			
			Intent t = new Intent( this, MainActivity.class );
			startActivity( t );
		}
		
		// edt_password.setText( password );
		// edt_userName.setText( username );
	}
	
	@Override
	protected void onStart( )
	{
		// TODO Auto-generated method stub
		super.onStart( );
		App.signinActivity = this;
		
	}
	
	private void onTransition( )
	{
		btn_signup.setClickable( false );
		btn_signin.setClickable( false );
		
		if ( animationBtnSignup == null )
		{
			animationBtnSignup = new TranslateAnimation( 0, Measuredwidth - btn_signup.getLeft( ),
					btn_signup.getTop( ), btn_signup.getTop( ) );
			animationBtnSignup.setAnimationListener( new AnimationListener( )
			{
				
				@Override
				public void onAnimationEnd( Animation animation )
				{
					btn_signup.setVisibility( View.INVISIBLE );
					btn_signin.setVisibility( View.INVISIBLE );
					linearSignin.setVisibility( View.VISIBLE );
					transitionLinear( );
					back = 1;
				}
				
				@Override
				public void onAnimationRepeat( Animation animation )
				{
					
				}
				
				@Override
				public void onAnimationStart( Animation animation )
				{
					
				}
			} );
			animationBtnSignup.setDuration( 500 ); // duartion in ms
			animationBtnSignup.setFillAfter( false );
		}
		
		if ( animationBtnSignin == null )
		{
			animationBtnSignin = new TranslateAnimation( 0, -btn_signin.getLeft( ) - btn_signin.getWidth( ),
					btn_signin.getTop( ), btn_signin.getTop( ) );
			animationBtnSignin.setAnimationListener( new AnimationListener( )
			{
				
				@Override
				public void onAnimationEnd( Animation animation )
				{
					btn_signup.setVisibility( View.INVISIBLE );
					btn_signin.setVisibility( View.INVISIBLE );
					linearSignin.setVisibility( View.VISIBLE );
					transitionLinear( );
					back = 1;
				}
				
				@Override
				public void onAnimationRepeat( Animation animation )
				{
					
				}
				
				@Override
				public void onAnimationStart( Animation animation )
				{
				}
			} );
			animationBtnSignin.setDuration( 500 ); // duartion in ms
			animationBtnSignin.setFillAfter( false );
		}
		
		btn_signup.startAnimation( animationBtnSignup );
		btn_signin.startAnimation( animationBtnSignin );
		
	}
	
	private void onTransition2( )
	{
		if ( animationBtnSignup2 == null )
		{
			animationBtnSignup2 = new TranslateAnimation( Measuredwidth - btn_signup.getLeft( ), 0
					,
					btn_signup.getTop( ), btn_signup.getTop( ) );
			animationBtnSignup2.setAnimationListener( new AnimationListener( )
			{
				
				@Override
				public void onAnimationEnd( Animation animation )
				{
					// linearSignin.setVisibility( View.GONE );
					back = 0;
				}
				
				@Override
				public void onAnimationRepeat( Animation animation )
				{
					
				}
				
				@Override
				public void onAnimationStart( Animation animation )
				{
					
				}
			} );
			animationBtnSignup2.setDuration( 500 ); // duartion in ms
			animationBtnSignup2.setFillAfter( false );
		}
		
		if ( animationBtnSignin2 == null )
		{
			animationBtnSignin2 = new TranslateAnimation( -btn_signin.getLeft( ) - btn_signin.getWidth( ), 0,
					btn_signin.getTop( ), btn_signin.getTop( ) );
			animationBtnSignin2.setAnimationListener( new AnimationListener( )
			{
				
				@Override
				public void onAnimationEnd( Animation animation )
				{
					btn_signup.setClickable( true );
					btn_signin.setClickable( true );
					back = 0;
				}
				
				@Override
				public void onAnimationRepeat( Animation animation )
				{
					
				}
				
				@Override
				public void onAnimationStart( Animation animation )
				{
					
				}
			} );
			animationBtnSignin2.setDuration( 500 ); // duartion in ms
			animationBtnSignin2.setFillAfter( true );
		}
		
		btn_signup.setClickable( false );
		btn_signin.setClickable( false );
		
		btn_signup.startAnimation( animationBtnSignup2 );
		btn_signin.startAnimation( animationBtnSignin2 );
	}
	
	private void registGCM( )
	{
		GCMRegistrar.checkDevice( this );
		GCMRegistrar.checkManifest( this );
		registerReceiver( mHandleMessageReceiver, new IntentFilter(
				CommonUtilities.DISPLAY_MESSAGE_ACTION ) );
		final String regId = GCMRegistrar.getRegistrationId( this );
		
		if ( regId.equals( "" ) )
		{
			GCMRegistrar.register( this, CommonUtilities.SENDER_ID );
			Constants.GCM_ID = GCMRegistrar.getRegistrationId( this );
			System.out.println( "registering with gcm " );
		}
		else
		{
			Constants.GCM_ID = regId;
			if ( GCMRegistrar.isRegisteredOnServer( this ) )
			{
				Toast.makeText( getApplicationContext( ), "Already registered with GCM on server",
						Toast.LENGTH_LONG ).show( );
				System.out.println( "registered with gcm on your server " );
			}
			else
			{
				System.out.println( "not registered gcm on your server " );
			}
		}
		
		if ( Constants.GCM_ID.length( ) > 0 )
		{
			Log.d( "register id", regId );
			Constants.REGISTERED_GCM = true;
		}
	}
	
	private void transitionLinear( )
	{
		if ( animationLinear == null )
		{
			animationLinear = new TranslateAnimation( 0, 0
					, linearSignin.getHeight( ) + 20, 0 );
			animationLinear.setAnimationListener( new AnimationListener( )
			{
				
				@Override
				public void onAnimationEnd( Animation animation )
				{
					// linearSignin.setVisibility( View.INVISIBLE );
					// back = 0;
				}
				
				@Override
				public void onAnimationRepeat( Animation animation )
				{
					
				}
				
				@Override
				public void onAnimationStart( Animation animation )
				{
					
				}
			} );
			animationLinear.setDuration( 500 ); // duartion in ms
			animationLinear.setFillAfter( false );
		}
		linearSignin.startAnimation( animationLinear );
	}
	
	private void transitionLinear2( )
	{
		if ( animationLinear2 == null )
		{
			animationLinear2 = new TranslateAnimation( 0, 0
					, 0, linearSignin.getHeight( ) + 20 );
			animationLinear2.setAnimationListener( new AnimationListener( )
			{
				
				@Override
				public void onAnimationEnd( Animation animation )
				{
					btn_signup.setVisibility( View.VISIBLE );
					btn_signin.setVisibility( View.VISIBLE );
					linearSignin.setVisibility( View.INVISIBLE );
					// back = 0;
					onTransition2( );
				}
				
				@Override
				public void onAnimationRepeat( Animation animation )
				{
					
				}
				
				@Override
				public void onAnimationStart( Animation animation )
				{
					
				}
			} );
			animationLinear2.setDuration( 500 ); // duartion in ms
			animationLinear2.setFillAfter( false );
		}
		linearSignin.startAnimation( animationLinear2 );
	}
	
}
