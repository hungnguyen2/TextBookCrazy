package com.cnc.textbookcrazy.gps;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class GPS
{
	
	public class MyLocationListener implements LocationListener
	{
		
		private final String	TAG	= MyLocationListener.class.getSimpleName( );
		
		@Override
		public void onLocationChanged( Location loc )
		{
			GPS.this.main.locationChanged( loc.getLongitude( ), loc.getLatitude( ) );
		}
		
		@Override
		public void onProviderDisabled( String provider )
		{
			GPS.this.main.displayGPSSettingsDialog( );
		}
		
		@Override
		public void onProviderEnabled( String provider )
		{
			
		}
		
		@Override
		public void onStatusChanged( String provider, int status, Bundle extras )
		{
			
		}
		
	}
	
	private IGPSActivity		main;
	// Helper for GPS-Position
	private LocationListener	mlocListener;
	
	private LocationManager		mlocManager;
	
	private boolean				isRunning;
	private Location			location;
	private String				bestProvider, provider;
	
	public GPS( IGPSActivity main )
	{
		this.main = main;
		
		// GPS Position
		mlocManager = ( LocationManager ) ( ( Activity ) this.main ).getSystemService( Context.LOCATION_SERVICE );
		mlocListener = new MyLocationListener( );
		// mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener );
		// mlocManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener );
		// GPS Position END
		this.isRunning = true;
		
		// GPS
		// Criteria criteria = new Criteria( );
		// criteria.setAccuracy( Criteria.ACCURACY_FINE );
		// criteria.setAltitudeRequired( false );
		// criteria.setBearingRequired( false );
		// criteria.setSpeedRequired( true );
		// criteria.setCostAllowed( true );
		// criteria.setPowerRequirement( Criteria.POWER_HIGH );
		// bestProvider = mlocManager.getBestProvider( criteria, false );
		// location = mlocManager.getLastKnownLocation( bestProvider );
		// mlocManager.requestLocationUpdates( bestProvider, 0, 0, mlocListener );
		
		// network
		// Criteria coarse = new Criteria( );
		// coarse.setAccuracy( Criteria.ACCURACY_COARSE );
		// provider = mlocManager.getBestProvider( coarse, true );
		location = mlocManager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
		Log.d( "location=", location + "" );
		if ( location == null )
		{
			location = mlocManager.getLastKnownLocation( LocationManager.NETWORK_PROVIDER );
			// location = mlocManager.getLastKnownLocation( provider );
			Log.d( "location=", location + "" );
		}
		else
		{
			Log.d( "location=", "not null = " + location );
		}
		mlocManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener );
		// mlocManager.requestLocationUpdates( provider, 0, 0f, mlocListener );
	}
	
	public Location getFirstLocation( )
	{
		return location;
	}
	
	public Location getLocation( )
	{
		if ( location == null )
		{
			location = mlocManager.getLastKnownLocation( LocationManager.NETWORK_PROVIDER );
			// location = mlocManager.getLastKnownLocation( provider );
		}
		return location;
	}
	
	public boolean isRunning( )
	{
		return this.isRunning;
	}
	
	public void resumeGPS( )
	{
		// mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener );
		mlocManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener );
		// mlocManager.requestLocationUpdates( provider, 0, 0f, mlocListener );
		this.isRunning = true;
	}
	
	public void stopGPS( )
	{
		if ( isRunning )
		{
			mlocManager.removeUpdates( mlocListener );
			this.isRunning = false;
		}
	}
	
}
