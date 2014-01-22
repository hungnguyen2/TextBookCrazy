package com.cnc.textbookcrazy.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.model.NearbyRequestModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NearbyRequestAdapter extends BaseAdapter
{
	class ViewHolder
	{
		ImageView	iconBook;
		TextView	bookName, authorName, distance;
	}
	
	private List< NearbyRequestModel >	listNearby;
	
	private LayoutInflater				inflater;
	
	DisplayImageOptions					defaultOptions	= new DisplayImageOptions.Builder( )
																.cacheInMemory( )
																.cacheOnDisc( )
																.build( );
	
	public NearbyRequestAdapter( Context context, List< NearbyRequestModel > list )
	{
		inflater = ( LayoutInflater ) context
				.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		listNearby = list;
	}
	
	@Override
	public int getCount( )
	{
		return listNearby.size( );
	}
	
	@Override
	public Object getItem( int position )
	{
		return listNearby.get( position );
	}
	
	@Override
	public long getItemId( int position )
	{
		return position;
	}
	
	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		ViewHolder viewHolder;
		
		if ( convertView == null )
		{
			convertView = inflater.inflate( R.layout.item_nearby, null );
			viewHolder = new ViewHolder( );
			
			viewHolder.iconBook = ( ImageView ) convertView.findViewById( R.id.icon_book );
			viewHolder.bookName = ( TextView ) convertView.findViewById( R.id.tv_bookname );
			viewHolder.authorName = ( TextView ) convertView.findViewById( R.id.tv_author_name );
			viewHolder.distance = ( TextView ) convertView.findViewById( R.id.tv_distance );
			
			convertView.setTag( viewHolder );
		}
		else
		{
			viewHolder = ( ViewHolder ) convertView.getTag( );
		}
		
		viewHolder = ( ViewHolder ) convertView.getTag( );
		
		NearbyRequestModel nearbyRequestModel = listNearby.get( position );
		
		viewHolder.bookName.setText( nearbyRequestModel.getBookName( ) );
		viewHolder.authorName.setText( nearbyRequestModel.getAuthorName( ) );
		viewHolder.distance.setText( nearbyRequestModel.getDistance( ) );
		if ( nearbyRequestModel.getIcon( ) != null && nearbyRequestModel.getIcon( ).equals( "" ) )
		{
			ImageLoader.getInstance( )
					.displayImage( nearbyRequestModel.getIcon( ), viewHolder.iconBook, defaultOptions );
		}
		
		return convertView;
	}
}
