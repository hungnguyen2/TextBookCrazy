package com.cnc.textbookcrazy.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cnc.textbookcrazy.MActivity;
import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.adapter.BuyAdapter;
import com.cnc.textbookcrazy.gps.GPS;
import com.cnc.textbookcrazy.gps.IGPSActivity;
import com.cnc.textbookcrazy.help.ConnectionDetector;
import com.cnc.textbookcrazy.help.Constants;
import com.cnc.textbookcrazy.help.MyOkDialog;
import com.cnc.textbookcrazy.model.BuyModel;
import com.cnc.textbookcrazy.webservice.WebConstant;
import com.cnc.textbookcrazy.webservice.task.MyPostTask;
import com.cnc.textbookcrazy.webservice.task.MyPostTask.MyPostInput;
import com.cnc.textbookcrazy.webservice.task.MyPostTask.MyPostTaskImp;

public class MyPostActivity extends MActivity implements MyPostTaskImp, IGPSActivity
{
	public static double distFrom( double lat1, double lng1, double lat2, double lng2 )
	{
		double earthRadius = 3958.75;
		double dLat = Math.toRadians( lat2 - lat1 );
		double dLng = Math.toRadians( lng2 - lng1 );
		double a = Math.sin( dLat / 2 ) * Math.sin( dLat / 2 ) +
				Math.cos( Math.toRadians( lat1 ) ) * Math.cos( Math.toRadians( lat2 ) ) *
				Math.sin( dLng / 2 ) * Math.sin( dLng / 2 );
		double c = 2 * Math.atan2( Math.sqrt( a ), Math.sqrt( 1 - a ) );
		double dist = earthRadius * c;
		
		int meterConversion = 1609;
		
		return dist * meterConversion;
	}
	
	private BuyAdapter			buyAdapter;
	private List< BuyModel >	listBuy			= new ArrayList< BuyModel >( );
	private ListView			lv_buy;
	
	private int					position;
	
	// @Override
	// protected void onActivityResult( int requestCode, int resultCode, Intent data )
	// {
	// super.onActivityResult( requestCode, resultCode, data );
	//
	// if ( data != null )
	// {
	// position = data.getIntExtra( "position", -1 );
	// if ( position != -1 )
	// {
	// buyAdapter.removeItemRequest( position );
	// Log.d( "", "position = " + position );
	// }
	// }
	// }
	private GPS					gps;
	
	private Location			location;
	
	private DecimalFormat		decimalFormat	= new DecimalFormat( "#0.0" );
	
	private int					page			= 1;
	
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
	
	private void loadPost( boolean show )
	{
		MyPostInput myPostInput = new MyPostInput( );
		myPostInput.setAuth_token( Constants.AUTHOR_TOKEN );
		myPostInput.setPage( page + "" );
		MyPostTask myPostTask = new MyPostTask( this, myPostInput, this, show );
		myPostTask.execute( );
	}
	
	@Override
	public void locationChanged( double longitude, double latitude )
	{
		if ( location == null )
			location = gps.getLocation( );
		location.setLatitude( latitude );
		location.setLongitude( longitude );
		
	}
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_mypost );
		
		TextView title = ( TextView ) findViewById( R.id.title );
		title.setText( getResources( ).getString( R.string.mypost_title ) );
		
		gps = new GPS( this );
		location = gps.getFirstLocation( );
		
		lv_buy = ( ListView ) findViewById( R.id.lv_mypost );
		lv_buy.setOnItemClickListener( new OnItemClickListener( )
		{
			
			@Override
			public void onItemClick( AdapterView< ? > parent, View view, int position, long id )
			{
				Intent t = null;
				BuyModel buyModel = buyAdapter.getItem( position );
				if ( buyAdapter.getItem( position ).getType( ).equals( "sell" ) )
				{
					t = new Intent( MyPostActivity.this, MyPostSellActivity.class );
					t.putExtra( WebConstant.PRICE, buyModel.getPrice( ) );
					t.putExtra( WebConstant.CONDITION, buyModel.getCondition( ) );
					t.putExtra( WebConstant.BONUS, buyModel.getBonus( ) );
				}
				else if ( buyAdapter.getItem( position ).getType( ).equals( "request" ) )
				{
					t = new Intent( MyPostActivity.this, MyPostRequestActivity.class );
				}
				
				t.putExtra( WebConstant.POST_ID, buyModel.getId( ) );
				// t.putExtra( WebConstant.BOOK_NAME, buyModel.getBookName( ) );
				// t.putExtra( WebConstant.AUTHOR, buyModel.getAuthorName( ) );
				// t.putExtra( WebConstant.EDITION, buyModel.getEdition( ) );
				// t.putExtra( WebConstant.DESCIPTION, buyModel.getDescription( ) );
				// t.putExtra( WebConstant.NOTE, buyModel.getNote( ) );
				
				t.putExtra( WebConstant.BOOK_ICON, buyModel.getIcon( ) );
				
				Log.d( "", "position = " + position );
				t.putExtra( "position", position );
				startActivity( t );
			}
		} );
		
		lv_buy.setOnScrollListener( new OnScrollListener( )
		{
			
			@Override
			public void onScroll( AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount )
			{
				if ( lv_buy.getCount( ) > 0
						&& ( lv_buy.getLastVisiblePosition( ) == ( page * 10 - 1 ) ) )
				{
					page++;
					loadPost( false );
				}
			}
			
			@Override
			public void onScrollStateChanged( AbsListView view, int scrollState )
			{
				
			}
		} );
	}
	
	@Override
	public void onPostExecute( JSONArray jsonArray )
	{
		for ( int k = 0; k < jsonArray.length( ); k++ )
		{
			JSONObject jsonObject = null;
			BuyModel buyModel = new BuyModel( );
			
			String latitude = "", longtitude = "";
			try
			{
				jsonObject = jsonArray.getJSONObject( k );
				
				buyModel.setIcon( jsonObject.getString( WebConstant.BOOK_ICON ) );
				buyModel.setBookName( jsonObject.getString( WebConstant.BOOK_NAME ) );
				buyModel.setAuthorName( jsonObject.getString( WebConstant.AUTHOR ) );
				buyModel.setEdition( jsonObject.getString( WebConstant.EDITION ) );
				buyModel.setDescription( jsonObject.getString( WebConstant.DESCIPTION ) );
				buyModel.setNote( jsonObject.getString( WebConstant.NOTE ) );
				buyModel.setCondition( jsonObject.getString( WebConstant.CONDITION ) );
				buyModel.setBonus( jsonObject.getString( WebConstant.BONUS ) );
				buyModel.setPrice( jsonObject.getString( WebConstant.PRICE ) );
				buyModel.setType( jsonObject.getString( WebConstant.TYPE_POST ) );
				buyModel.setId( jsonObject.getString( WebConstant.POST_ID ) );
				
				latitude = jsonObject.getString( WebConstant.LATITUDE );
				longtitude = jsonObject.getString( WebConstant.LONGTITUDE );
				
				if ( !latitude.equals( "null" ) && !longtitude.equals( "null" ) )
				{
					Log.d( "", "latitue = " + location.getLatitude( ) );
					Log.d( "", "latitue = " + location.getLongitude( ) );
					
					Double dis = distFrom( Double.parseDouble( latitude ), Double.parseDouble( longtitude ),
							location.getLatitude( ),
							location.getLongitude( ) );
					buyModel.setDistance( decimalFormat.format( dis / 1000 ) + "Km" );
				}
				
				if ( jsonObject.getString( WebConstant.TYPE_POST ).equals( "sell" ) )
				{
					buyAdapter.addItemSell( buyModel );
				}
				else if ( jsonObject.getString( WebConstant.TYPE_POST ).equals( "request" ) )
				{
					buyAdapter.addItemRequest( buyModel );
				}
			}
			catch ( JSONException e )
			{
				e.printStackTrace( );
			}
			
		}
	}
	
	@Override
	protected void onResume( )
	{
		super.onResume( );
		
		page = 1;
		if ( !gps.isRunning( ) )
		{
			gps.resumeGPS( );
			location = gps.getFirstLocation( );
		}
		
		listBuy = new ArrayList< BuyModel >( );
		buyAdapter = new BuyAdapter( this, listBuy );
		lv_buy.setAdapter( buyAdapter );
		
		if ( ConnectionDetector.isConnectingToInternet( this ) )
		{
			Log.d( "", "author_token=" + Constants.AUTHOR_TOKEN );
			// MyPostInput myPostInput = new MyPostInput( );
			// myPostInput.setAuth_token( Constants.AUTHOR_TOKEN );
			// myPostInput.setPage( page + "" );
			// MyPostTask myPostTask = new MyPostTask( this, myPostInput, this, true );
			// myPostTask.execute( );
			loadPost( true );
		}
		else
		{
			MyOkDialog.showDialog( getString( R.string.a_connect_internet ), this );
		}
	}
	
	@Override
	protected void onStop( )
	{
		gps.stopGPS( );
		super.onStop( );
	}
}
