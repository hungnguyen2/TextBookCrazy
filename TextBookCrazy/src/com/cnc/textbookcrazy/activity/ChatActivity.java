package com.cnc.textbookcrazy.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cnc.textbookcrazy.MActivity;
import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.adapter.ChatAdapter;
import com.cnc.textbookcrazy.help.ConnectionDetector;
import com.cnc.textbookcrazy.help.Constants;
import com.cnc.textbookcrazy.help.MyOkDialog;
import com.cnc.textbookcrazy.help.MyOkDialogListener;
import com.cnc.textbookcrazy.help.MyOkDialogListener.MyOkDiaglogImp;
import com.cnc.textbookcrazy.help.MyYesNoDialog;
import com.cnc.textbookcrazy.help.MyYesNoDialog.MyYesNoImp;
import com.cnc.textbookcrazy.model.ChatModel;
import com.cnc.textbookcrazy.webservice.WebConstant;
import com.cnc.textbookcrazy.webservice.task.BlockUserTask;
import com.cnc.textbookcrazy.webservice.task.BlockUserTask.BlockUserTaskImp;
import com.cnc.textbookcrazy.webservice.task.BlockUserTask.BlockUsetInput;
import com.cnc.textbookcrazy.webservice.task.HistoryChatTask;
import com.cnc.textbookcrazy.webservice.task.HistoryChatTask.HistoryChatInput;
import com.cnc.textbookcrazy.webservice.task.HistoryChatTask.HistoryChatTaskImp;
import com.cnc.textbookcrazy.webservice.task.SendChatTask;
import com.cnc.textbookcrazy.webservice.task.SendChatTask.SendChatInput;
import com.cnc.textbookcrazy.webservice.task.SendChatTask.SendChatTaskImp;

public class ChatActivity extends MActivity implements HistoryChatTaskImp, SendChatTaskImp, BlockUserTaskImp
{
	private ListView			lv_chat;
	private ChatAdapter			chatAdapter;
	private List< ChatModel >	listChat	= new ArrayList< ChatModel >( );
	private String				user2Id, postId;
	private EditText			edt_content;
	
	private void blockUser( )
	{
		BlockUsetInput blockUsetInput = new BlockUsetInput( );
		blockUsetInput.setAuth_token( Constants.AUTHOR_TOKEN );
		blockUsetInput.setBlock_id( user2Id );
		blockUsetInput.setPost_id( postId );
		
		BlockUserTask blockUserTask = new BlockUserTask( this, blockUsetInput, this );
		blockUserTask.execute( );
	}
	
	@Override
	public void onBackPressed( )
	{
		if ( !MyOkDialogListener.showing )
		{
			super.onBackPressed( );
		}
	}
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_chat );
		
		nameActivity = "ChatActivity";
		
		TextView title = ( TextView ) findViewById( R.id.title );
		title.setText( getResources( ).getString( R.string.chat_title ) );
		
		edt_content = ( EditText ) findViewById( R.id.content );
		Intent t = getIntent( );
		postId = t.getStringExtra( WebConstant.POST_ID_2 );
		user2Id = t.getStringExtra( WebConstant.USER_RECEIVE_ID );
		
		lv_chat = ( ListView ) findViewById( R.id.lv_chat );
		chatAdapter = new ChatAdapter( this, listChat );
		lv_chat.setAdapter( chatAdapter );
		
		Button btn_send = ( Button ) findViewById( R.id.btn_send );
		btn_send.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				if ( ConnectionDetector.isConnectingToInternet( ChatActivity.this ) )
				{
					if ( edt_content.length( ) >= 1 )
					{
						ChatModel chatModel = new ChatModel( );
						chatModel.setContent( edt_content.getText( ).toString( ) );
						chatModel.setName( "Me" );
						chatModel.setAvatar( Constants.AVATA );
						chatAdapter.addItemChatMe( chatModel );
						lv_chat.setSelection( chatAdapter.getCount( ) - 1 );
						onSendChat( );
						edt_content.setText( "" );
						
					}
				}
				else
				{
					MyOkDialog.showDialog( getString( R.string.a_connect_internet ), ChatActivity.this );
				}
			}
		} );
		
		LinearLayout btn_block = ( LinearLayout ) findViewById( R.id.btn_block );
		btn_block.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				MyYesNoDialog.showDialog( "Do you want to block this user?", ChatActivity.this, new MyYesNoImp( )
				{
					
					@Override
					public void onNegative( )
					{
						
					}
					
					@Override
					public void onPositive( )
					{
						blockUser( );
					}
				} );
			}
		} );
		if ( ConnectionDetector.isConnectingToInternet( this ) )
		{
			onGetChat( );
		}
		else
		{
			MyOkDialog.showDialog( getString( R.string.a_connect_internet ), this );
		}
	}
	
	private void onGetChat( )
	{
		HistoryChatInput historyChatInput = new HistoryChatInput( );
		historyChatInput.setAuth_token( Constants.AUTHOR_TOKEN );
		historyChatInput.setPostId( postId );
		historyChatInput.setUser_receive_id( user2Id );
		
		HistoryChatTask historyChatTask = new HistoryChatTask( this, historyChatInput, this );
		historyChatTask.execute( );
	}
	
	private void onLoadHistoryChat( JSONObject jsonObject )
	{
		listChat.clear( );
		chatAdapter.notifyDataSetChanged( );
		
		String name1 = "";
		String name2 = "";
		String avatar1 = "", avatar2 = "";
		JSONArray jsonArray = null;
		try
		{
			jsonArray = jsonObject.getJSONArray( WebConstant.MESSAGE );
		}
		catch ( JSONException e1 )
		{
			// TODO Auto-generated catch block
			e1.printStackTrace( );
		}
		
		for ( int k = 0; k < jsonArray.length( ); k++ )
		{
			String content_chat, userId, name, typeMessage, user_start_post_id;
			ChatModel chatModel = new ChatModel( );
			
			try
			{
				JSONObject jsonObject2 = jsonArray.getJSONObject( k );
				content_chat = jsonObject2.getString( WebConstant.CONTENT_CHAT );
				userId = jsonObject2.getString( WebConstant.USER_ID_SENT );
				typeMessage = jsonObject2.getString( WebConstant.TYPE_MESSAGE );
				
				name1 = jsonObject.getString( WebConstant.USER_POST );
				name2 = jsonObject.getString( WebConstant.USER_SENT );
				avatar1 = jsonObject.getString( WebConstant.USER_START_POST_AVATAR );
				avatar2 = jsonObject.getString( WebConstant.USER_START_SENT_AVATAR );
				// user_start_post_id = jsonObject.getString( WebConstant.USER_ID_POST );
				
				if ( typeMessage.equals( "sent" ) )
				{
					chatModel.setAvatar( avatar2 );
					
					// if ( user2Id.equals( user_start_post_id ) )
					// {
					if ( Constants.USER_ID.equals( userId ) )
					{
						name = "Me";
						chatModel.setName( name );
						chatModel.setContent( content_chat );
						chatAdapter.addItemChatMe( chatModel );
					}
					else
					{
						if ( Constants.USER_NAME.equals( name2 ) )
						{
							name = name1;
						}
						else
						{
							name = name2;
						}
						chatModel.setName( name );
						chatModel.setContent( content_chat );
						chatAdapter.addItemChatOther( chatModel );
					}
					// }
					// else
					// {
					// if ( user2Id.equals( userId ) )
					// {
					// name = "Me";
					// chatModel.setName( name );
					// chatModel.setContent( content_chat );
					// chatAdapter.addItemChatMe( chatModel );
					// }
					// else
					// {
					// if ( Constants.USER_NAME.equals( name2 ) )
					// {
					// name = name1;
					// }
					// else
					// {
					// name = name2;
					// }
					// chatModel.setName( name );
					// chatModel.setContent( content_chat );
					// chatAdapter.addItemChatOther( chatModel );
					// }
					// }
					
				}
				else if ( typeMessage.equals( "receive" ) )
				{
					chatModel.setAvatar( avatar1 );
					
					// if ( user2Id.equals( user_start_post_id ) )
					// {
					if ( !Constants.USER_ID.equals( userId ) )
					{
						name = "Me";
						chatModel.setName( name );
						chatModel.setContent( content_chat );
						chatAdapter.addItemChatMe( chatModel );
					}
					else
					{
						if ( Constants.USER_NAME.equals( name2 ) )
						{
							name = name1;
						}
						else
						{
							name = name2;
						}
						chatModel.setName( name );
						chatModel.setContent( content_chat );
						chatAdapter.addItemChatOther( chatModel );
					}
					// }
				}
				
			}
			catch ( JSONException e )
			{
				e.printStackTrace( );
			}
		}
	}
	
	@Override
	public void onPostExecute( JSONObject jsonObject )
	{
		onLoadHistoryChat( jsonObject );
	}
	
	@Override
	public void onPostExecuteBlockUser( )
	{
		MyOkDialogListener.showDialog( "Block user is successfull", this, new MyOkDiaglogImp( )
		{
			
			@Override
			public void onPositive( )
			{
				Intent t = new Intent( ChatActivity.this, MessageActivity.class );
				startActivity( t );
				finish( );
			}
		} );
	}
	
	@Override
	public void onPostExecuteFailed( JSONObject jsonObject )
	{
		edt_content.setText( "" );
	}
	
	private void onSendChat( )
	{
		SendChatInput sendChatInput = new SendChatInput( );
		sendChatInput.setAuth_token( Constants.AUTHOR_TOKEN );
		sendChatInput.setContent( edt_content.getText( ).toString( ) );
		sendChatInput.setPostId( postId );
		sendChatInput.setUserReceive( user2Id );
		
		SendChatTask sendChatTask = new SendChatTask( this, sendChatInput, this );
		sendChatTask.execute( );
	}
	
	@Override
	public void onSendChatPostExecute( JSONObject jsonObject )
	{
		onLoadHistoryChat( jsonObject );
		// MyOkDialog.showDialog( getString( R.string.send_chat_success ), this );
	}
	
	@Override
	public void processPush( )
	{
		onGetChat( );
	}
}
