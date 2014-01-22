package com.cnc.textbookcrazy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.cnc.textbookcrazy.MActivity;
import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.webservice.WebConstant;

public class MessageDetailActivity extends MActivity implements OnClickListener
{
	int	type_message	= -1;
	
	private String	name, time, content, postId, user2Id, type_post;
	
	private TextView	tv_name, tv_time, tv_content;
	
	@Override
	public void onClick( View v )
	{
		Intent t = null;
		switch ( v.getId( ) )
		{
			case R.id.btn_link_detail:
				if ( type_post.equals( "sell" ) )
				{
					t = new Intent( MessageDetailActivity.this, BuyDetailActivity.class );
				}
				else if ( type_post.equals( "request" ) )
				{
					t = new Intent( MessageDetailActivity.this, DetailRequestActivity.class );
					t.putExtra( WebConstant.POST_ID, postId );
					t.putExtra( WebConstant.USER_ID, user2Id );
				}
				startActivity( t );
				break;
			case R.id.btn_reply:
				t = new Intent( MessageDetailActivity.this, ChatActivity.class );
				t.putExtra( WebConstant.POST_ID_2, postId );
				t.putExtra( WebConstant.USER_RECEIVE_ID, user2Id );
				startActivity( t );
				break;
			default:
				break;
		}
		
	}
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_message_detail );
		
		TextView title = ( TextView ) findViewById( R.id.title );
		title.setText( getResources( ).getString( R.string.message_title ) );
		
		tv_name = ( TextView ) findViewById( R.id.tv_name );
		tv_time = ( TextView ) findViewById( R.id.tv_time );
		tv_content = ( TextView ) findViewById( R.id.tv_content );
		
		Intent t = getIntent( );
		name = t.getStringExtra( WebConstant.USER_POST );
		time = t.getStringExtra( WebConstant.TIME_UPDATE );
		content = t.getStringExtra( WebConstant.CONTENT_LAST );
		postId = t.getStringExtra( WebConstant.POST_ID_2 );
		user2Id = t.getStringExtra( WebConstant.USER_RECEIVE_ID );
		type_post = t.getStringExtra( WebConstant.TYPE_POST );
		
		tv_name.setText( name );
		tv_time.setText( time );
		tv_content.setText( content );
		
		Button btn_reply = ( Button ) findViewById( R.id.btn_reply );
		Button btn_link_detail = ( Button ) findViewById( R.id.btn_link_detail );
		btn_link_detail.setOnClickListener( this );
		btn_reply.setOnClickListener( this );
	}
}
