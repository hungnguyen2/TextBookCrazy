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
import com.cnc.textbookcrazy.model.OtherBookModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class OtherBookAdapter extends BaseAdapter
{
	class ViewHolder
	{
		ImageView	icon;
		TextView	name;
	}
	
	List< OtherBookModel >	listOtherBook;
	private LayoutInflater	inflater;
	
	DisplayImageOptions		defaultOptions	= new DisplayImageOptions.Builder( )
													.cacheInMemory( )
													.cacheOnDisc( )
													.build( );
	
	public OtherBookAdapter( Context context, List< OtherBookModel > list )
	{
		inflater = ( LayoutInflater ) context
				.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		this.listOtherBook = list;
	}
	
	@Override
	public int getCount( )
	{
		return listOtherBook.size( );
	}
	
	@Override
	public Object getItem( int position )
	{
		return listOtherBook.get( position );
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
			convertView = inflater.inflate( R.layout.item_other_book, null );
			viewHolder = new ViewHolder( );
			
			viewHolder.icon = ( ImageView ) convertView.findViewById( R.id.icon );
			viewHolder.name = ( TextView ) convertView.findViewById( R.id.name );
			
			convertView.setTag( viewHolder );
		}
		else
		{
			viewHolder = ( ViewHolder ) convertView.getTag( );
		}
		viewHolder = ( ViewHolder ) convertView.getTag( );
		OtherBookModel otherBookModel = listOtherBook.get( position );
		viewHolder.name.setText( otherBookModel.getName( ) );
		if ( !otherBookModel.getIcon( ).equals( "" ) )
		{
			ImageLoader.getInstance( ).displayImage( otherBookModel.getIcon( ), viewHolder.icon, defaultOptions );
		}
		
		return convertView;
	}
}
