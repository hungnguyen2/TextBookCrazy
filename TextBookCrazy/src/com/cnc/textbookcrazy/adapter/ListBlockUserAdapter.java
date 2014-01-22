package com.cnc.textbookcrazy.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.activity.ListBlockUserActivity;
import com.cnc.textbookcrazy.help.MyYesNoDialog;
import com.cnc.textbookcrazy.help.MyYesNoDialog.MyYesNoImp;
import com.cnc.textbookcrazy.model.ListBlockUserModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ListBlockUserAdapter extends BaseAdapter
{
	class ViewHolder
	{
		ImageView	avatar;
		TextView	userName;
		Button		unblock;
	}
	
	private List< ListBlockUserModel >	listBlockUser;
	private LayoutInflater				inflater;
	
	DisplayImageOptions					defaultOptions	= new DisplayImageOptions.Builder( )
																.cacheInMemory( )
																.cacheOnDisc( )
																.build( );
	private Activity					context;
	
	public ListBlockUserAdapter( Activity context, List< ListBlockUserModel > listBlockUser )
	{
		this.listBlockUser = listBlockUser;
		inflater = ( LayoutInflater ) context
				.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		this.context = context;
	}
	
	@Override
	public int getCount( )
	{
		return listBlockUser.size( );
	}
	
	@Override
	public Object getItem( int position )
	{
		return listBlockUser.get( position );
	}
	
	@Override
	public long getItemId( int position )
	{
		return position;
	}
	
	@Override
	public View getView( final int position, View convertView, ViewGroup parent )
	{
		ViewHolder viewHolder;
		
		if ( convertView == null )
		{
			convertView = inflater.inflate( R.layout.item_list_block_user, null );
			viewHolder = new ViewHolder( );
			
			viewHolder.avatar = ( ImageView ) convertView.findViewById( R.id.avatar );
			viewHolder.userName = ( TextView ) convertView.findViewById( R.id.tv_username );
			viewHolder.unblock = ( Button ) convertView.findViewById( R.id.btn_unblock );
			convertView.setTag( viewHolder );
		}
		else
		{
			viewHolder = ( ViewHolder ) convertView.getTag( );
		}
		
		viewHolder = ( ViewHolder ) convertView.getTag( );
		ListBlockUserModel listBlockUserModel = listBlockUser.get( position );
		
		if ( listBlockUserModel.getUserName( ) != null && !listBlockUserModel.getUserName( ).equals( "" ) )
		{
			viewHolder.userName.setText( listBlockUserModel.getUserName( ) );
		}
		
		if ( listBlockUserModel.getAvatar( ) != null && !listBlockUserModel.getAvatar( ).equals( "" ) )
		{
			ImageLoader.getInstance( )
					.displayImage( listBlockUserModel.getAvatar( ), viewHolder.avatar, defaultOptions );
		}
		
		viewHolder.unblock.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				MyYesNoImp myYesNoImp = new MyYesNoImp( )
				{
					
					@Override
					public void onNegative( )
					{
						
					}
					
					@Override
					public void onPositive( )
					{
						( ( ListBlockUserActivity ) context ).unblock( position );
					}
				};
				MyYesNoDialog.showDialog( "Do you want to unblock this user?", context, myYesNoImp );
			}
		} );
		
		return convertView;
	}
}
