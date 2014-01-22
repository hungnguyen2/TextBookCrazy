package com.cnc.textbookcrazy.webservice.task;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.help.MyOkDialog;
import com.cnc.textbookcrazy.webservice.URL;
import com.cnc.textbookcrazy.webservice.WebConstant;
import com.cnc.textbookcrazy.webservice.WebServiceJSON;

public class SendChatTask extends AsyncTask< Void, Void, JSONObject >
{
	public static class SendChatInput
	{
		private String	auth_token, userReceive, content, postId;
		
		public String getAuth_token( )
		{
			return auth_token;
		}
		
		public String getContent( )
		{
			return content;
		}
		
		public String getPostId( )
		{
			return postId;
		}
		
		public String getUserReceive( )
		{
			return userReceive;
		}
		
		public void setAuth_token( String auth_token )
		{
			this.auth_token = auth_token;
		}
		
		public void setContent( String content )
		{
			this.content = content;
		}
		
		public void setPostId( String postId )
		{
			this.postId = postId;
		}
		
		public void setUserReceive( String userReceive )
		{
			this.userReceive = userReceive;
		}
		
	}
	
	public interface SendChatTaskImp
	{
		public void onPostExecuteFailed( JSONObject jsonObject );
		
		public void onSendChatPostExecute( JSONObject jsonObject );
	}
	
	private Activity		activity;
	
	private SendChatInput	sendChatInput;
	private SendChatTaskImp	sendChatTaskImp;
	private String			errorCode	= "";
	
	public SendChatTask( Activity activity, SendChatInput sendChatInput, SendChatTaskImp sendChatTaskImp )
	{
		this.activity = activity;
		this.sendChatInput = sendChatInput;
		this.sendChatTaskImp = sendChatTaskImp;
	}
	
	@Override
	protected JSONObject doInBackground( Void... params )
	{
		JSONObject jsonObject = null;
		
		JSONObject json = new JSONObject( );
		JSONObject manJson = new JSONObject( );
		try
		{
			json.put( WebConstant.POST_ID_2, sendChatInput.getPostId( ) );
			json.put( WebConstant.USER_RECEIVE_ID, sendChatInput.getUserReceive( ) );
			json.put( WebConstant.CONTENT_CHAT, sendChatInput.getContent( ) );
			
			manJson.put( WebConstant.AUTHOR_TOKEN, sendChatInput.getAuth_token( ) );
			manJson.put( WebConstant.SENT_MESSAGE, json );
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		
		WebServiceJSON webService = new WebServiceJSON( URL.SEND_MESSAGE );
		String result = webService.post( manJson.toString( ) );
		if ( result.equals( "cto" ) )
		{
			errorCode = "CTO";
			return null;
		}
		else if ( result.equals( "sto" ) )
		{
			errorCode = "STO";
			return null;
		}
		else if ( result.equals( "http" ) )
		{
			errorCode = "HTTP";
			return null;
		}
		
		try
		{
			jsonObject = new JSONObject( result );
			errorCode = jsonObject.getString( WebConstant.ERROR_CODE );
		}
		catch ( NullPointerException nex )
		{
			Log.d( "", "NullPointerException" );
			nex.printStackTrace( );
		}
		catch ( JSONException jex )
		{
			Log.d( "", "JSONException" );
			jex.printStackTrace( );
			
		}
		return jsonObject;
	}
	
	@Override
	protected void onPostExecute( JSONObject result )
	{
		super.onPostExecute( result );
		// MyProgcessDialog.hide( );
		
		if ( errorCode.equals( "true" ) )
		{
			sendChatTaskImp.onSendChatPostExecute( result );
		}
		else if ( errorCode.equals( "false" ) )
		{
			MyOkDialog.showDialog( activity.getString( R.string.send_chat_failed ), activity );
			
			// MyOkDialog.showDialog( activity.getString( R.string.signup_fail ), activity );
		}
	}
	
	@Override
	protected void onPreExecute( )
	{
		super.onPreExecute( );
		// MyProgcessDialog.show( activity, activity.getString( R.string.waiting ) );
		
	}
	
}
