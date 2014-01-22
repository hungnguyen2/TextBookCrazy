package com.cnc.textbookcrazy.webservice.task;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.cnc.textbookcrazy.webservice.URL;
import com.cnc.textbookcrazy.webservice.WebConstant;
import com.cnc.textbookcrazy.webservice.WebServiceJSON;

public class HistoryChatTask extends AsyncTask< Void, Void, JSONObject >
{
	public static class HistoryChatInput
	{
		private String	user_receive_id, postId, auth_token;
		
		/**
		 * @return the auth_token
		 */
		public String getAuth_token( )
		{
			return auth_token;
		}
		
		public String getPostId( )
		{
			return postId;
		}
		
		public String getUser_receive_id( )
		{
			return user_receive_id;
		}
		
		/**
		 * @param auth_token
		 *            the auth_token to set
		 */
		public void setAuth_token( String auth_token )
		{
			this.auth_token = auth_token;
		}
		
		public void setPostId( String postId )
		{
			this.postId = postId;
		}
		
		public void setUser_receive_id( String user_receive_id )
		{
			this.user_receive_id = user_receive_id;
		}
		
	}
	
	public interface HistoryChatTaskImp
	{
		public void onPostExecute( JSONObject jsonArray );
	}
	
	private String				errorCode	= "";
	private Activity			activity;
	private HistoryChatInput	historyChatInput;
	private HistoryChatTaskImp	historyChatTaskImp;
	
	public HistoryChatTask( Activity activity, HistoryChatInput historyChatInput, HistoryChatTaskImp historyChatTaskImp )
	{
		this.activity = activity;
		this.historyChatInput = historyChatInput;
		this.historyChatTaskImp = historyChatTaskImp;
	}
	
	@Override
	protected JSONObject doInBackground( Void... params )
	{
		JSONObject jsonObject = null;
		
		JSONObject json = new JSONObject( );
		JSONObject manJson = new JSONObject( );
		try
		{
			json.put( WebConstant.USER_RECEIVE_ID, historyChatInput.getUser_receive_id( ) );
			json.put( WebConstant.POST_ID_2, historyChatInput.getPostId( ) );
			
			manJson.put( WebConstant.AUTHOR_TOKEN, historyChatInput.getAuth_token( ) );
			manJson.put( WebConstant.SENT_MESSAGE, json );
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		
		WebServiceJSON webService = new WebServiceJSON( URL.LOG_CHAT );
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
		if ( result == null )
			return;
		if ( errorCode.equals( "true" ) )
		{
			// try
			// {
			// JSONArray jsonObject = result.getJSONArray( WebConstant.MESSAGE );
			historyChatTaskImp.onPostExecute( result );
			
			// }
			// catch ( JSONException e )
			// {
			// e.printStackTrace( );
			// }
		}
		else if ( errorCode.equals( "false" ) )
		{
			// MyOkDialog.showDialog( activity.getString( R.string.email_get ), activity );
		}
		else
		{
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
