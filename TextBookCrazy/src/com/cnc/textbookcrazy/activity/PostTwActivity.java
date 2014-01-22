package com.cnc.textbookcrazy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.help.Common;
import com.cnc.textbookcrazy.help.Constants;

/**
 * ツイッターに共有する。
 * 
 * @著者 hung<hungnguyen2@cnc.com.vn>
 * 
 */

public class PostTwActivity extends Activity implements OnClickListener
{
	private EditText		etTextShare;
	private LinearLayout	vBack;
	private TextView		tvTweet;
	private String			text1, text2, text;
	private int				mode1, mode2;
	private Handler			handler	= new Handler( );
	
	@Override
	public void onClick( View v )
	{
		switch ( v.getId( ) )
		{
			case R.id.layout_back:
				finish( );
				break;
			case R.id.tv_tweet:
				
				Thread thread = new Thread( )
				{
					@Override
					public void run( )
					{
						try
						{
							Common.twitter.updateStatus( text );
							LocalBroadcastManager.getInstance( PostTwActivity.this )
									.sendBroadcast(
											new Intent( Constants.ACTION_DONE_SHARE ) );
							handler.post( new Runnable( )
							{
								
								@Override
								public void run( )
								{
									Toast toast = Toast.makeText(
											PostTwActivity.this,
											getText( R.string.post_tw_complete ),
											Toast.LENGTH_SHORT );
									toast.show( );
								}
							} );
						}
						catch ( Exception e )
						{
							e.printStackTrace( );
							LocalBroadcastManager.getInstance( PostTwActivity.this )
									.sendBroadcast(
											new Intent( Constants.ACTION_DONE_SHARE ) );
							handler.post( new Runnable( )
							{
								
								@Override
								public void run( )
								{
									Toast toast = Toast.makeText(
											PostTwActivity.this,
											getText( R.string.post_tw_erorr ),
											Toast.LENGTH_SHORT );
									toast.show( );
								}
							} );
						}
					}
				};
				
				thread.start( );
				finish( );
				
				break;
			
			default:
				break;
		}
	}
	
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		this.requestWindowFeature( Window.FEATURE_NO_TITLE );
		
		setContentView( R.layout.twitter_post_dialog );
		Bundle extra = getIntent( ).getExtras( );
		text1 = extra.getString( "txt1" );
		text2 = extra.getString( "txt2" );
		
		mode1 = extra.getInt( "mode1" );
		mode2 = extra.getInt( "mode2" );
		etTextShare = ( EditText ) findViewById( R.id.et_text_share );
		vBack = ( LinearLayout ) findViewById( R.id.layout_back );
		vBack.setOnClickListener( this );
		tvTweet = ( TextView ) findViewById( R.id.tv_tweet );
		tvTweet.setOnClickListener( this );
		if ( mode1 == 1 )
		{
			etTextShare.setText( text1 );
			text = text1;
			
		}
		if ( mode2 == 2 )
		{
			etTextShare.setText( text2 );
			text = text2;
			
		}
		
	}
	
}
