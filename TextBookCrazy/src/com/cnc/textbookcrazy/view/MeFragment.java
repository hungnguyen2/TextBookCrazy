package com.cnc.textbookcrazy.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.activity.MessageActivity;
import com.cnc.textbookcrazy.activity.MyPostActivity;
import com.cnc.textbookcrazy.activity.SettingActivity;
import com.cnc.textbookcrazy.help.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MeFragment extends Fragment implements OnClickListener
{
	public enum BUTTON
	{
		BTN_MY_POST, BTN_MESSAGE, BTN_SETTING
	}
	
	public interface MeButtonListener
	{
		public void onButtonSelected( BUTTON button );
	}
	
	private MeButtonListener	mCallback;
	
	private ImageView			avatar_2;
	
	@Override
	public void onClick( View v )
	{
		Intent t = null;
		switch ( v.getId( ) )
		{
			case R.id.btn_mypost:
				t = new Intent( getActivity( ), MyPostActivity.class );
				break;
			case R.id.btn_message:
				t = new Intent( getActivity( ), MessageActivity.class );
				break;
			case R.id.btn_setting:
				t = new Intent( getActivity( ), SettingActivity.class );
				break;
			
			default:
				break;
		}
		startActivity( t );
	}
	
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		View view = inflater.inflate( R.layout.fragment_me,
				container, false );
		
		ImageView btn_myPost = ( ImageView ) view.findViewById( R.id.btn_mypost );
		ImageView btn_message = ( ImageView ) view.findViewById( R.id.btn_message );
		ImageView btn_setting = ( ImageView ) view.findViewById( R.id.btn_setting );
		btn_myPost.setOnClickListener( this );
		btn_message.setOnClickListener( this );
		btn_setting.setOnClickListener( this );
		
		avatar_2 = ( ImageView ) view.findViewById( R.id.avatar_2 );
		
		TextView tv_username = ( TextView ) view.findViewById( R.id.tv_username );
		tv_username.setText( Constants.USER_NAME );
		
		return view;
		
	}
	
	@Override
	public void onResume( )
	{
		super.onResume( );
		
		if ( !Constants.AVATA.equals( "" ) )
		{
			ImageLoader.getInstance( ).displayImage( Constants.AVATA, avatar_2 );
		}
	}
}
