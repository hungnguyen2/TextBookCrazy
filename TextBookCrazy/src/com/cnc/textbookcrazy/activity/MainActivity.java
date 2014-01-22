package com.cnc.textbookcrazy.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cnc.textbookcrazy.App;
import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.gps.GPS;
import com.cnc.textbookcrazy.gps.IGPSActivity;
import com.cnc.textbookcrazy.help.MyYesNoDialog;
import com.cnc.textbookcrazy.help.MyYesNoDialog.MyYesNoImp;
import com.cnc.textbookcrazy.view.BuyFragment;
import com.cnc.textbookcrazy.view.MeFragment;
import com.cnc.textbookcrazy.view.SellFragment;

public class MainActivity extends FragmentActivity implements OnClickListener,
		IGPSActivity
{
	private Button			btn_me, btn_buy, btn_sell;
	private ImageView		btn_refresh_2, btn_request;
	private MeFragment		meFragment;
	private SellFragment	sellFragment;
	private BuyFragment		buyFragment;
	
	private FragmentManager	fragmentManager;
	private GPS				gps;
	private Location		location;
	
	@Override
	public void displayGPSSettingsDialog( )
	{
		LocationManager service = ( LocationManager ) getSystemService( LOCATION_SERVICE );
		boolean enabledGPS = service
				.isProviderEnabled( LocationManager.NETWORK_PROVIDER );
		if ( !enabledGPS )
		{
			Toast.makeText( this, getString( R.string.wifi_gps ), Toast.LENGTH_LONG ).show( );
			Intent intent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
			startActivity( intent );
		}
	}
	
	public Location getLocation( )
	{
		return location;
	}
	
	@Override
	public void locationChanged( double longitude, double latitude )
	{
		if ( location == null )
		{
			location = gps.getLocation( );
			Log.d( "", "new location = " + location );
		}
		location.setLatitude( latitude );
		location.setLongitude( longitude );
		
		Log.d( this.getClass( ).getName( ), "location: latitude = " + latitude + ", long= " + longitude );
	}
	
	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		super.onActivityResult( requestCode, resultCode, data );
		sellFragment.onActivityResult( requestCode, resultCode, data );
	}
	
	@Override
	public void onBackPressed( )
	{
		if ( sellFragment.isVisible( ) )
		{
			boolean check = sellFragment.check( );
			if ( check )
			{
				MyYesNoDialog.showDialog( "Do you want to exit app?", this, new MyYesNoImp( )
				{
					
					@Override
					public void onNegative( )
					{
						
					}
					
					@Override
					public void onPositive( )
					{
						finish( );
						App.exit( );
					}
				} );
				// super.onBackPressed( );
			}
		}
		else
		{
			MyYesNoDialog.showDialog( "Do you want to exit app?", this, new MyYesNoImp( )
			{
				
				@Override
				public void onNegative( )
				{
					
				}
				
				@Override
				public void onPositive( )
				{
					finish( );
					App.exit( );
				}
			} );
			// super.onBackPressed( );
		}
	}
	
	@Override
	public void onClick( View v )
	{
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction( );
		
		switch ( v.getId( ) )
		{
			case R.id.btn_me:
				try
				{
					InputMethodManager imm = ( InputMethodManager ) getSystemService( Context.INPUT_METHOD_SERVICE );
					imm.hideSoftInputFromWindow( getWindow( ).getCurrentFocus( ).getWindowToken( ), 0 );
				}
				catch ( NullPointerException nex )
				{
					nex.printStackTrace( );
				}
				btn_refresh_2.setVisibility( View.GONE );
				btn_request.setVisibility( View.GONE );
				resetButtonPager( );
				btn_me.setBackgroundResource( R.drawable.btn_pager_selected );
				btn_me.setTextColor( getResources( ).getColor( R.color.yellow ) );
				fragmentTransaction.hide( sellFragment ).show( meFragment ).hide( buyFragment ).commit( );
				break;
			case R.id.btn_buy:
				try
				{
					InputMethodManager imm = ( InputMethodManager ) getSystemService( Context.INPUT_METHOD_SERVICE );
					imm.hideSoftInputFromWindow( getWindow( ).getCurrentFocus( ).getWindowToken( ), 0 );
				}
				catch ( NullPointerException nex )
				{
					nex.printStackTrace( );
				}
				
				btn_refresh_2.setVisibility( View.VISIBLE );
				btn_request.setVisibility( View.VISIBLE );
				resetButtonPager( );
				btn_buy.setBackgroundResource( R.drawable.btn_pager_selected );
				btn_buy.setTextColor( getResources( ).getColor( R.color.yellow ) );
				
				fragmentTransaction.hide( sellFragment ).hide( meFragment ).show( buyFragment ).commit( );
				buyFragment.onRefresh( );
				break;
			case R.id.btn_sell:
				try
				{
					InputMethodManager imm = ( InputMethodManager ) getSystemService( Context.INPUT_METHOD_SERVICE );
					imm.hideSoftInputFromWindow( getWindow( ).getCurrentFocus( ).getWindowToken( ), 0 );
				}
				catch ( NullPointerException nex )
				{
					nex.printStackTrace( );
				}
				
				btn_refresh_2.setVisibility( View.GONE );
				btn_request.setVisibility( View.GONE );
				resetButtonPager( );
				btn_sell.setBackgroundResource( R.drawable.btn_pager_selected );
				btn_sell.setTextColor( getResources( ).getColor( R.color.yellow ) );
				
				fragmentTransaction.hide( meFragment ).show( sellFragment ).hide( buyFragment ).commit( );
				break;
			
			default:
				break;
		}
		
	}
	
	@Override
	protected void onCreate( Bundle arg0 )
	{
		super.onCreate( arg0 );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		
		setContentView( R.layout.activity_main );
		
		gps = new GPS( this );
		location = gps.getFirstLocation( );
		
		fragmentManager = getSupportFragmentManager( );
		meFragment = ( MeFragment ) fragmentManager.findFragmentById( R.id.fragment_me );
		sellFragment = ( SellFragment ) fragmentManager.findFragmentById( R.id.fragment_sell );
		buyFragment = ( BuyFragment ) fragmentManager.findFragmentById( R.id.fragment_buy );
		
		TextView title = ( TextView ) findViewById( R.id.title );
		title.setText( getResources( ).getString( R.string.textbook ) );
		
		btn_me = ( Button ) findViewById( R.id.btn_me );
		btn_buy = ( Button ) findViewById( R.id.btn_buy );
		btn_sell = ( Button ) findViewById( R.id.btn_sell );
		btn_me.setOnClickListener( this );
		btn_buy.setOnClickListener( this );
		btn_sell.setOnClickListener( this );
		
		btn_me.setBackgroundResource( R.drawable.btn_pager_selected );
		btn_me.setTextColor( getResources( ).getColor( R.color.yellow ) );
		
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction( );
		fragmentTransaction.hide( sellFragment ).hide( buyFragment ).show( meFragment ).commit( );
		
		btn_refresh_2 = ( ImageView ) findViewById( R.id.btn_refresh_2 );
		btn_refresh_2.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				buyFragment.onRefresh( );
			}
		} );
		
		btn_request = ( ImageView ) findViewById( R.id.btn_cross );
		btn_request.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				Intent t = new Intent( MainActivity.this, RequestBookActivity.class );
				startActivity( t );
			}
		} );
		
	}
	
	@Override
	protected void onDestroy( )
	{
		App.mainActivity = null;
		super.onDestroy( );
	}
	
	@Override
	protected void onResume( )
	{
		Log.d( "", "gps running = " + gps.isRunning( ) );
		if ( !gps.isRunning( ) )
			gps.resumeGPS( );
		super.onStart( );
	}
	
	@Override
	protected void onStart( )
	{
		// TODO Auto-generated method stub
		super.onStart( );
		App.mainActivity = this;
	}
	
	@Override
	protected void onStop( )
	{
		gps.stopGPS( );
		super.onStop( );
	}
	
	private void resetButtonPager( )
	{
		btn_me.setBackgroundResource( R.drawable.btn_pager_unselected );
		btn_buy.setBackgroundResource( R.drawable.btn_pager_unselected );
		btn_sell.setBackgroundResource( R.drawable.btn_pager_unselected );
		btn_me.setTextColor( Color.BLACK );
		btn_buy.setTextColor( Color.BLACK );
		btn_sell.setTextColor( Color.BLACK );
		
	}
}
