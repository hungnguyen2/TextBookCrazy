package com.cnc.textbookcrazy.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.cnc.textbookcrazy.MActivity;
import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.adapter.NearbyRequestAdapter;
import com.cnc.textbookcrazy.model.NearbyRequestModel;

public class NearbyRequestActivity extends MActivity
{
	private ListView					lv_nearbyRequest;
	private List< NearbyRequestModel >	listNearby	= new ArrayList< NearbyRequestModel >( );
	private NearbyRequestAdapter		nearbyRequestAdapter;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_nearby_request );
		
		TextView title = ( TextView ) findViewById( R.id.title );
		title.setText( getResources( ).getString( R.string.nearby_title ) );
		
		for ( int k = 0; k < 5; k++ )
		{
			NearbyRequestModel nearbyRequestModel = new NearbyRequestModel( );
			listNearby.add( nearbyRequestModel );
		}
		lv_nearbyRequest = ( ListView ) findViewById( R.id.lv_nearby_request );
		nearbyRequestAdapter = new NearbyRequestAdapter( this, listNearby );
		lv_nearbyRequest.setAdapter( nearbyRequestAdapter );
	}
}
