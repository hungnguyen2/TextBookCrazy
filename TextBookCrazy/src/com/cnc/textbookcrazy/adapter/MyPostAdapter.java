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
import com.cnc.textbookcrazy.model.MyPostModel;

public class MyPostAdapter extends BaseAdapter
{
	class ViewHolder
	{
		ImageView	iconBook;
		TextView	bookName, authorName, price;
	}
	
	private List< MyPostModel >	listMyPost;
	
	private LayoutInflater		inflater;
	
	public MyPostAdapter( Context context, List< MyPostModel > list )
	{
		inflater = ( LayoutInflater ) context
				.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		listMyPost = list;
	}
	
	@Override
	public int getCount( )
	{
		return listMyPost.size( );
	}
	
	@Override
	public Object getItem( int position )
	{
		return listMyPost.get( position );
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
			convertView = inflater.inflate( R.layout.item_mypost, null );
			viewHolder = new ViewHolder( );
			
			viewHolder.iconBook = ( ImageView ) convertView.findViewById( R.id.icon_book );
			viewHolder.bookName = ( TextView ) convertView.findViewById( R.id.tv_bookname );
			viewHolder.authorName = ( TextView ) convertView.findViewById( R.id.tv_author_name );
			viewHolder.price = ( TextView ) convertView.findViewById( R.id.tv_price );
			
			convertView.setTag( viewHolder );
		}
		else
		{
			viewHolder = ( ViewHolder ) convertView.getTag( );
		}
		
		return convertView;
	}
	
}
