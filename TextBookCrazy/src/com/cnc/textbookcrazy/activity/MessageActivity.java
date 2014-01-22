package com.cnc.textbookcrazy.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cnc.textbookcrazy.MActivity;
import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.adapter.MessageAdapter;
import com.cnc.textbookcrazy.help.ConnectionDetector;
import com.cnc.textbookcrazy.help.Constants;
import com.cnc.textbookcrazy.help.MyOkDialog;
import com.cnc.textbookcrazy.model.MessageModel;
import com.cnc.textbookcrazy.webservice.WebConstant;
import com.cnc.textbookcrazy.webservice.task.ListMessageTask;
import com.cnc.textbookcrazy.webservice.task.ListMessageTask.ListMessageInput;
import com.cnc.textbookcrazy.webservice.task.ListMessageTask.ListMessageTaskImp;

public class MessageActivity extends MActivity implements ListMessageTaskImp
{
	private ListView				lv_message;
	private MessageAdapter			messageAdapter;
	private List< MessageModel >	listMessage	= new ArrayList< MessageModel >( );
	
	private int						page		= 1;
	
	private void loadMessage( boolean show )
	{
		ListMessageInput listMessageInput = new ListMessageInput( );
		listMessageInput.setAuth_token( Constants.AUTHOR_TOKEN );
		listMessageInput.setPage( page + "" );
		ListMessageTask listMessageTask = new ListMessageTask( this, listMessageInput, this, show );
		listMessageTask.execute( );
	}
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_message );
		
		TextView title = ( TextView ) findViewById( R.id.title );
		title.setText( getResources( ).getString( R.string.message_title ) );
		
		LinearLayout btn_blockers = ( LinearLayout ) findViewById( R.id.btn_blockers );
		btn_blockers.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				Intent t = new Intent( MessageActivity.this, ListBlockUserActivity.class );
				startActivity( t );
			}
		} );
		
		lv_message = ( ListView ) findViewById( R.id.lv_message );
		messageAdapter = new MessageAdapter( this, listMessage );
		lv_message.setAdapter( messageAdapter );
		
		lv_message.setOnItemClickListener( new OnItemClickListener( )
		{
			
			@Override
			public void onItemClick( AdapterView< ? > parent, View view, int position, long id )
			{
				Intent t = new Intent( MessageActivity.this, MessageDetailActivity.class );
				
				MessageModel messageModel = listMessage.get( position );
				
				t.putExtra( WebConstant.USER_POST, messageModel.getName( ) );
				t.putExtra( WebConstant.TIME_UPDATE, messageModel.getTime( ) );
				t.putExtra( WebConstant.CONTENT_LAST, messageModel.getContent( ) );
				t.putExtra( WebConstant.POST_ID_2, messageModel.getPostId( ) );
				t.putExtra( WebConstant.USER_RECEIVE_ID, messageModel.getUser2Id( ) );
				t.putExtra( WebConstant.TYPE_POST, messageModel.getType_post( ) );
				
				startActivity( t );
			}
		} );
		
		lv_message.setOnScrollListener( new OnScrollListener( )
		{
			
			@Override
			public void onScroll( AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount )
			{
				if ( lv_message.getCount( ) > 0
						&& ( lv_message.getLastVisiblePosition( ) == ( page * 10 - 1 ) ) )
				{
					page++;
					loadMessage( false );
				}
			}
			
			@Override
			public void onScrollStateChanged( AbsListView view, int scrollState )
			{
				// TODO Auto-generated method stub
				
			}
		} );
	}
	
	@Override
	public void onPostExecute( JSONArray jsonArray )
	{
		
		for ( int k = 0; k < jsonArray.length( ); k++ )
		{
			JSONObject jsonObject = null;
			
			String name = "", receiveRead = "";
			String time = "", type_post = "";
			String content = "", postId = "", user2Id = "", type_message = "";
			try
			{
				jsonObject = jsonArray.getJSONObject( k );
				if ( jsonObject.isNull( WebConstant.USER_POST ) )
					continue;
				
				String userIdSent = jsonObject.getString( WebConstant.USER_ID_SENT );
				type_message = jsonObject.getString( WebConstant.TYPE_MESSAGE );
				if ( type_message.equals( "sent" ) )
				{
					if ( Constants.USER_ID.equals( userIdSent ) )
					{
						name = jsonObject.getString( WebConstant.USER_POST );
						user2Id = jsonObject.getString( WebConstant.USER_ID_POST );
					}
					else
					{
						name = jsonObject.getString( WebConstant.USER_SENT );
						receiveRead = jsonObject.getString( WebConstant.USER_POST_READ );
						user2Id = userIdSent;
					}
				}
				else
				{
					if ( !Constants.USER_ID.equals( userIdSent ) )
					{
						// name = jsonObject.getString( WebConstant.USER_POST );
						name = jsonObject.getString( WebConstant.USER_SENT );
						user2Id = userIdSent;
					}
					else
					{
						name = jsonObject.getString( WebConstant.USER_POST );
						receiveRead = jsonObject.getString( WebConstant.USER_SENT_READ );
						user2Id = jsonObject.getString( WebConstant.USER_ID_POST );
					}
				}
				
				time = jsonObject.getString( WebConstant.TIME_UPDATE );
				content = jsonObject.getString( WebConstant.CONTENT_LAST );
				postId = jsonObject.getString( WebConstant.POST_ID_2 );
				type_post = jsonObject.getString( WebConstant.TYPE_POST );
				Log.d( "", "name=" + name );
			}
			catch ( JSONException e )
			{
				e.printStackTrace( );
			}
			
			MessageModel messageModel = new MessageModel( );
			messageModel.setName( name );
			messageModel.setTime( time );
			messageModel.setContent( content );
			messageModel.setPostId( postId );
			messageModel.setUser2Id( user2Id );
			messageModel.setType_post( type_post );
			messageModel.setReceiveRead( receiveRead );
			listMessage.add( messageModel );
			messageAdapter.notifyDataSetChanged( );
			
		}
		
	}
	
	@Override
	protected void onResume( )
	{
		super.onResume( );
		page = 1;
		listMessage.clear( );
		messageAdapter.notifyDataSetChanged( );
		
		if ( ConnectionDetector.isConnectingToInternet( this ) )
		{
			Log.d( "", "author_token=" + Constants.AUTHOR_TOKEN );
			loadMessage( true );
		}
		else
		{
			MyOkDialog.showDialog( getString( R.string.a_connect_internet ), this );
		}
	}
}
