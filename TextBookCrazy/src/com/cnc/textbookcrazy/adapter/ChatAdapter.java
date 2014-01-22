package com.cnc.textbookcrazy.adapter;

import java.util.List;
import java.util.TreeSet;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.model.ChatModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ChatAdapter extends BaseAdapter
{
	class ViewHolder
	{
		ImageView	icon;
		TextView	name, content;
		int			type;
		
		public int getType( )
		{
			return type;
		}
		
		public void setType( int type )
		{
			this.type = type;
		}
	}
	
	private List< ChatModel >			listChat;
	
	private LayoutInflater				inflater;
	private final TreeSet< Integer >	mSeparatorsSet	= new TreeSet< Integer >( );
	
	private static final int			ME				= 1;
	private static final int			OTHER			= 2;
	
	DisplayImageOptions					defaultOptions	= new DisplayImageOptions.Builder( )
																.cacheInMemory( )
																.cacheOnDisc( )
																.build( );
	
	public ChatAdapter( Context context, List< ChatModel > list )
	{
		inflater = ( LayoutInflater ) context
				.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		listChat = list;
	}
	
	public void addItemChatMe( ChatModel buyModel )
	{
		listChat.add( buyModel );
		notifyDataSetChanged( );
	}
	
	public void addItemChatOther( ChatModel buyModel )
	{
		listChat.add( buyModel );
		mSeparatorsSet.add( listChat.size( ) - 1 );
		notifyDataSetChanged( );
	}
	
	@Override
	public int getCount( )
	{
		return listChat.size( );
	}
	
	@Override
	public Object getItem( int position )
	{
		return listChat.get( position );
	}
	
	@Override
	public long getItemId( int position )
	{
		return position;
	}
	
	@Override
	public int getItemViewType( int position )
	{
		return mSeparatorsSet.contains( position ) ? OTHER : ME;
		
	}
	
	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		ViewHolder viewHolder = new ViewHolder( );
		View result = null;
		int type = getItemViewType( position );
		Log.d( "", "type = " + type );
		switch ( type )
		{
			case ME:
				if ( convertView == null )
				{
					result = inflater.inflate( R.layout.item_chat_me, null );
					viewHolder = new ViewHolder( );
					
					viewHolder.icon = ( ImageView ) result.findViewById( R.id.avatar_2 );
					viewHolder.name = ( TextView ) result.findViewById( R.id.name );
					viewHolder.content = ( TextView ) result.findViewById( R.id.content );
					viewHolder.setType( ME );
					
					result.setTag( viewHolder );
				}
				else
				{
					viewHolder = ( ViewHolder ) convertView.getTag( );
					if ( viewHolder != null && viewHolder.getType( ) == ME )
					{
						result = convertView;
					}
					else
					{
						result = inflater.inflate( R.layout.item_chat_me, null );
						viewHolder = new ViewHolder( );
						
						viewHolder.icon = ( ImageView ) result.findViewById( R.id.avatar_2 );
						viewHolder.name = ( TextView ) result.findViewById( R.id.name );
						viewHolder.content = ( TextView ) result.findViewById( R.id.content );
						viewHolder.setType( ME );
						
						result.setTag( viewHolder );
					}
				}
				break;
			case OTHER:
				if ( convertView == null )
				{
					result = inflater.inflate( R.layout.item_chat_other, null );
					viewHolder = new ViewHolder( );
					
					viewHolder.icon = ( ImageView ) result.findViewById( R.id.avatar_2 );
					viewHolder.name = ( TextView ) result.findViewById( R.id.name );
					viewHolder.content = ( TextView ) result.findViewById( R.id.content );
					viewHolder.setType( OTHER );
					
					result.setTag( viewHolder );
				}
				else
				{
					viewHolder = ( ViewHolder ) convertView.getTag( );
					if ( viewHolder != null && viewHolder.getType( ) == OTHER )
					{
						result = convertView;
					}
					else
					{
						result = inflater.inflate( R.layout.item_chat_other, null );
						viewHolder = new ViewHolder( );
						
						viewHolder.icon = ( ImageView ) result.findViewById( R.id.avatar_2 );
						viewHolder.name = ( TextView ) result.findViewById( R.id.name );
						viewHolder.content = ( TextView ) result.findViewById( R.id.content );
						viewHolder.setType( OTHER );
						
						result.setTag( viewHolder );
					}
				}
				break;
			default:
				break;
		}
		
		viewHolder = ( ViewHolder ) result.getTag( );
		
		ChatModel chatModel = listChat.get( position );
		
		if ( chatModel.getContent( ) != null && !chatModel.getContent( ).equals( "" ) )
		{
			viewHolder.content.setText( chatModel.getContent( ) );
		}
		
		if ( !chatModel.getAvatar( ).equals( "" ) )
		{
			ImageLoader.getInstance( ).displayImage( chatModel.getAvatar( ), viewHolder.icon, defaultOptions );
		}
		
		viewHolder.name.setText( chatModel.getName( ) );
		return result;
	}
	
	@Override
	public boolean isEnabled( int position )
	{
		return false;
	}
	
}
