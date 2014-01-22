package com.cnc.textbookcrazy.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.model.BuyModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class BuyAdapter extends BaseAdapter
{
	class ViewHolder
	{
		ImageView	iconBook;
		TextView	bookName, authorName, distance, price;
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
	
	private List< BuyModel >		listBuy;
	
	private LayoutInflater			inflater;
	private final List< Integer >	mSeparatorsSet	= new ArrayList< Integer >( );
	
	private static final int		REQUEST			= 1;
	private static final int		SELL			= 2;
	
	DisplayImageOptions				defaultOptions	= new DisplayImageOptions.Builder( )
															.cacheInMemory( )
															.cacheOnDisc( )
															.build( );
	
	public BuyAdapter( Context context, List< BuyModel > list )
	{
		inflater = ( LayoutInflater ) context
				.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		listBuy = list;
	}
	
	public void addItemRequest( BuyModel buyModel )
	{
		listBuy.add( buyModel );
		notifyDataSetChanged( );
	}
	
	public void addItemSell( BuyModel buyModel )
	{
		listBuy.add( buyModel );
		mSeparatorsSet.add( listBuy.size( ) - 1 );
		Log.d( "", "separa = " + ( listBuy.size( ) - 1 ) );
		notifyDataSetChanged( );
	}
	
	@Override
	public int getCount( )
	{
		return listBuy.size( );
	}
	
	@Override
	public BuyModel getItem( int position )
	{
		return listBuy.get( position );
	}
	
	@Override
	public long getItemId( int position )
	{
		return position;
	}
	
	@Override
	public int getItemViewType( int position )
	{
		return mSeparatorsSet.contains( position ) ? SELL : REQUEST;
		
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
			case REQUEST:
				if ( convertView == null )
				{
					result = inflater.inflate( R.layout.item_buy_request, null );
					viewHolder = new ViewHolder( );
					
					viewHolder.iconBook = ( ImageView ) result.findViewById( R.id.icon_book );
					viewHolder.bookName = ( TextView ) result.findViewById( R.id.tv_bookname );
					viewHolder.authorName = ( TextView ) result.findViewById( R.id.tv_author_name );
					viewHolder.distance = ( TextView ) result.findViewById( R.id.tv_distance );
					viewHolder.setType( REQUEST );
					
					result.setTag( viewHolder );
				}
				else
				{
					viewHolder = ( ViewHolder ) convertView.getTag( );
					if ( viewHolder != null && viewHolder.getType( ) == REQUEST )
					{
						result = convertView;
					}
					else
					{
						result = inflater.inflate( R.layout.item_buy_request, null );
						viewHolder = new ViewHolder( );
						
						viewHolder.iconBook = ( ImageView ) result.findViewById( R.id.icon_book );
						viewHolder.bookName = ( TextView ) result.findViewById( R.id.tv_bookname );
						viewHolder.authorName = ( TextView ) result.findViewById( R.id.tv_author_name );
						viewHolder.distance = ( TextView ) result.findViewById( R.id.tv_distance );
						viewHolder.setType( REQUEST );
						
						result.setTag( viewHolder );
					}
				}
				break;
			case SELL:
				if ( convertView == null )
				{
					result = inflater.inflate( R.layout.item_buy_sell, null );
					viewHolder = new ViewHolder( );
					
					viewHolder.iconBook = ( ImageView ) result.findViewById( R.id.icon_book );
					viewHolder.bookName = ( TextView ) result.findViewById( R.id.tv_bookname );
					viewHolder.authorName = ( TextView ) result.findViewById( R.id.tv_author_name );
					viewHolder.distance = ( TextView ) result.findViewById( R.id.tv_distance );
					viewHolder.price = ( TextView ) result.findViewById( R.id.tv_price );
					viewHolder.setType( SELL );
					
					result.setTag( viewHolder );
				}
				else
				{
					viewHolder = ( ViewHolder ) convertView.getTag( );
					if ( viewHolder != null && viewHolder.getType( ) == SELL )
					{
						result = convertView;
					}
					else
					{
						result = inflater.inflate( R.layout.item_buy_sell, null );
						viewHolder = new ViewHolder( );
						
						viewHolder.iconBook = ( ImageView ) result.findViewById( R.id.icon_book );
						viewHolder.bookName = ( TextView ) result.findViewById( R.id.tv_bookname );
						viewHolder.authorName = ( TextView ) result.findViewById( R.id.tv_author_name );
						viewHolder.distance = ( TextView ) result.findViewById( R.id.tv_distance );
						viewHolder.price = ( TextView ) result.findViewById( R.id.tv_price );
						viewHolder.setType( SELL );
						
						result.setTag( viewHolder );
					}
				}
				break;
			default:
				break;
		}
		ViewHolder viewHolder2 = ( ViewHolder ) result.getTag( );
		
		BuyModel buyModel = listBuy.get( position );
		
		if ( buyModel.getBookName( ) != null && !buyModel.getBookName( ).equals( "" ) )
		{
			viewHolder2.bookName.setText( buyModel.getBookName( ) );
		}
		
		if ( buyModel.getAuthorName( ) != null && !buyModel.getAuthorName( ).equals( "" ) )
		{
			viewHolder2.authorName.setText( buyModel.getAuthorName( ) );
		}
		
		if ( buyModel.getDistance( ) != null && !buyModel.getDistance( ).equals( "" ) )
		{
			viewHolder2.distance.setText( buyModel.getDistance( ) );
		}
		
		if ( buyModel.getPrice( ) != null && !buyModel.getPrice( ).equals( "" ) )
		{
			viewHolder2.price.setText( buyModel.getPrice( ) );
		}
		
		// if ( buyModel.getDistance( ) != null && !buyModel.getDistance( ).equals( "" ) )
		// {
		// viewHolder2.distance.setText( buyModel.getDistance( ) );
		// }
		
		if ( buyModel.getIcon( ) != null && !buyModel.getIcon( ).equals( "" ) )
		{
			ImageLoader.getInstance( ).displayImage( buyModel.getIcon( ), viewHolder2.iconBook, defaultOptions );
		}
		else
		{
			viewHolder2.iconBook.setImageResource( R.drawable.icon_book );
		}
		
		return result;
	}
	
	public void removeItemRequest( int position )
	{
		listBuy.remove( position );
		
		if ( mSeparatorsSet.contains( position ) )
		{
			for ( int k = 0; k < mSeparatorsSet.size( ); k++ )
			{
				if ( mSeparatorsSet.get( k ) == position )
				{
					mSeparatorsSet.remove( k );
					break;
				}
			}
			
			for ( int k = 0; k < mSeparatorsSet.size( ); k++ )
			{
				int value = mSeparatorsSet.get( k );
				if ( value > position )
				{
					mSeparatorsSet.set( k, value - 1 );
				}
			}
			
		}
		notifyDataSetChanged( );
	}
	// public void removeItemSell( int position )
	// {
	// listBuy.remove( position );
	// mSeparatorsSet.remove( position );
	// notifyDataSetChanged( );
	// }
	
}
