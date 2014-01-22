package com.cnc.textbookcrazy.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.model.MessageModel;

public class MessageAdapter extends BaseAdapter
{
	class ViewHolder
	{
		TextView	name, time, content;
		ImageView	tick;
	}
	
	private List< MessageModel >	listMessage;
	
	private LayoutInflater			inflater;
	
	public MessageAdapter( Context context, List< MessageModel > list )
	{
		inflater = ( LayoutInflater ) context
				.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		listMessage = list;
	}
	
	@Override
	public int getCount( )
	{
		return listMessage.size( );
	}
	
	@Override
	public Object getItem( int position )
	{
		return listMessage.get( position );
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
			convertView = inflater.inflate( R.layout.item_message, null );
			viewHolder = new ViewHolder( );
			
			viewHolder.name = ( TextView ) convertView.findViewById( R.id.tv_name );
			viewHolder.time = ( TextView ) convertView.findViewById( R.id.tv_time );
			viewHolder.content = ( TextView ) convertView.findViewById( R.id.tv_content );
			viewHolder.tick = ( ImageView ) convertView.findViewById( R.id.tick );
			
			convertView.setTag( viewHolder );
		}
		else
		{
			viewHolder = ( ViewHolder ) convertView.getTag( );
		}
		
		viewHolder = ( ViewHolder ) convertView.getTag( );
		
		MessageModel messageModel = listMessage.get( position );
		viewHolder.name.setText( messageModel.getName( ) );
		viewHolder.time.setText( messageModel.getTime( ) );
		viewHolder.content.setText( messageModel.getContent( ) );
		
		Log.d( "", "read = " + messageModel.getReceiveRead( ) );
		
		if ( messageModel.getReceiveRead( ).equals( "true" ) )
		{
			Log.d( "", "read2 = " + messageModel.getReceiveRead( ) );
			
			viewHolder.tick.setVisibility( View.VISIBLE );
			AlphaAnimation alpha = new AlphaAnimation( 0.2F, 0.2F );
			alpha.setDuration( 0 );
			alpha.setFillAfter( true );
			viewHolder.tick.startAnimation( alpha );
		}
		else if ( messageModel.getReceiveRead( ).equals( "false" ) )
		{
			viewHolder.tick.setVisibility( View.VISIBLE );
		}
		return convertView;
	}
	
}
