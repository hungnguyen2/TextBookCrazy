package com.cnc.textbookcrazy.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnc.textbookcrazy.MActivity;
import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.help.ConnectionDetector;
import com.cnc.textbookcrazy.help.Constants;
import com.cnc.textbookcrazy.help.MyOkDialog;
import com.cnc.textbookcrazy.webservice.WebConstant;
import com.cnc.textbookcrazy.webservice.task.DetailPostTask;
import com.cnc.textbookcrazy.webservice.task.DetailPostTask.DetailPostImp;
import com.cnc.textbookcrazy.webservice.task.DetailPostTask.DetailPostInput;
import com.cnc.textbookcrazy.webservice.task.SendChatTask;
import com.cnc.textbookcrazy.webservice.task.SendChatTask.SendChatInput;
import com.cnc.textbookcrazy.webservice.task.SendChatTask.SendChatTaskImp;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DetailRequestActivity extends MActivity implements OnClickListener, DetailPostImp, SendChatTaskImp
{
	private String		id, userId;
	private TextView	tv_bookName;
	private TextView	tv_author;
	private TextView	tv_edition;
	private TextView	tv_note;
	private TextView	tv_description;
	private ImageView	avatar_2;
	private EditText	edt_message;
	
	@Override
	public void onClick( View v )
	{
		switch ( v.getId( ) )
		{
			case R.id.btn_message:
				showDialogMessage( );
				break;
			
			default:
				break;
		}
		
	}
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_detail_request );
		
		TextView title = ( TextView ) findViewById( R.id.title );
		title.setText( getResources( ).getString( R.string.message ) );
		
		tv_bookName = ( TextView ) findViewById( R.id.tv_book_name );
		tv_author = ( TextView ) findViewById( R.id.tv_author );
		tv_edition = ( TextView ) findViewById( R.id.tv_edition );
		tv_note = ( TextView ) findViewById( R.id.tv_note );
		tv_description = ( TextView ) findViewById( R.id.tv_des );
		avatar_2 = ( ImageView ) findViewById( R.id.avatar_2 );
		
		Intent t = getIntent( );
		id = t.getStringExtra( WebConstant.POST_ID );
		userId = t.getStringExtra( WebConstant.USER_ID );
		
		Button btn_message = ( Button ) findViewById( R.id.btn_message );
		btn_message.setOnClickListener( this );
		
		if ( ConnectionDetector.isConnectingToInternet( this ) )
		{
			onGetDetail( );
		}
		else
		{
			MyOkDialog.showDialog( getString( R.string.a_connect_internet ), this );
		}
	}
	
	private void onGetDetail( )
	{
		DetailPostInput detailPostInput = new DetailPostInput( );
		detailPostInput.setAuth_token( Constants.AUTHOR_TOKEN );
		detailPostInput.setId( id );
		
		DetailPostTask detailPostTask = new DetailPostTask( this, detailPostInput, this );
		detailPostTask.execute( );
	}
	
	@Override
	public void onPostExecute( JSONObject jsonObject, JSONArray jsonArray, String avatar )
	{
		String bookName = "", author = "", edition = "", des = "", note = "";
		try
		{
			bookName = jsonObject.getString( WebConstant.BOOK_NAME );
			author = jsonObject.getString( WebConstant.AUTHOR );
			edition = jsonObject.getString( WebConstant.EDITION );
			des = jsonObject.getString( WebConstant.DESCIPTION );
			note = jsonObject.getString( WebConstant.NOTE );
			userId = jsonObject.getString( WebConstant.USER_ID );
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		tv_bookName.setText( bookName );
		tv_author.setText( author );
		tv_edition.setText( edition );
		tv_note.setText( note );
		tv_description.setText( des );
		if ( !avatar.equals( "" ) )
		{
			ImageLoader.getInstance( ).displayImage( avatar, avatar_2 );
		}
	}
	
	@Override
	public void onPostExecuteFailed( JSONObject jsonObject )
	{
		// TODO Auto-generated method stub
		
	}
	
	private void onSendChat( )
	{
		SendChatInput sendChatInput = new SendChatInput( );
		sendChatInput.setAuth_token( Constants.AUTHOR_TOKEN );
		sendChatInput.setContent( edt_message.getText( ).toString( ) );
		sendChatInput.setPostId( id );
		sendChatInput.setUserReceive( userId );
		
		SendChatTask sendChatTask = new SendChatTask( this, sendChatInput, this );
		sendChatTask.execute( );
	}
	
	@Override
	public void onSendChatPostExecute( JSONObject jsonObject )
	{
		MyOkDialog.showDialog( getString( R.string.send_chat_success ), this );
	}
	
	private void showDialogMessage( )
	{
		final Dialog dialog = new Dialog( this );
		dialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
		dialog.setContentView( R.layout.dialog_send_message );
		
		edt_message = ( EditText ) dialog.findViewById( R.id.edt_message );
		
		Button btn_submit = ( Button ) dialog.findViewById( R.id.btn_submit );
		btn_submit.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				if ( edt_message.length( ) < 1 )
				{
					MyOkDialog.showDialog( DetailRequestActivity.this.getString( R.string.type_message ),
							DetailRequestActivity.this );
					return;
				}
				onSendChat( );
				dialog.dismiss( );
				
			}
		} );
		
		Button btn_cancel = ( Button ) dialog.findViewById( R.id.btn_cancel );
		btn_cancel.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				dialog.dismiss( );
			}
		} );
		
		dialog.show( );
	}
	
}
