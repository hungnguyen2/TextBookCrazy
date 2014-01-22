package com.cnc.textbookcrazy.webservice.task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.help.MyProgcessDialog;
import com.cnc.textbookcrazy.webservice.URL;
import com.cnc.textbookcrazy.webservice.WebConstant;
import com.cnc.textbookcrazy.webservice.WebServiceJSON;

public class ListMessageTask extends AsyncTask< Void, Void, JSONObject >
{
	public static class ListMessageInput
	{
		private String	auth_token, page;
		
		/**
		 * @return the auth_token
		 */
		public String getAuth_token( )
		{
			return auth_token;
		}
		
		/**
		 * @return the page
		 */
		public String getPage( )
		{
			return page;
		}
		
		/**
		 * @param auth_token
		 *            the auth_token to set
		 */
		public void setAuth_token( String auth_token )
		{
			this.auth_token = auth_token;
		}
		
		/**
		 * @param page
		 *            the page to set
		 */
		public void setPage( String page )
		{
			this.page = page;
		}
	}
	
	public interface ListMessageTaskImp
	{
		public void onPostExecute( JSONArray jsonArray );
	}
	
	private String				errorCode	= "";
	private Activity			activity;
	private ListMessageInput	listMessageInput;
	private ListMessageTaskImp	listMessageTaskImp;
	private boolean				show;
	
	public ListMessageTask( Activity activity, ListMessageInput listMessageInput,
			ListMessageTaskImp listMessageTaskImp, boolean show )
	{
		this.activity = activity;
		this.listMessageInput = listMessageInput;
		this.listMessageTaskImp = listMessageTaskImp;
		this.show = show;
	}
	
	@Override
	protected JSONObject doInBackground( Void... params )
	{
		JSONObject jsonObject = null;
		
		JSONObject json = new JSONObject( );
		try
		{
			json.put( WebConstant.AUTHOR_TOKEN, listMessageInput.getAuth_token( ) );
			json.put( WebConstant.PAGE, listMessageInput.getPage( ) );
			
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		
		WebServiceJSON webService = new WebServiceJSON( URL.LIST_MESSAGE );
		String result = webService.post( json.toString( ) );
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
		if ( result == null )
			return;
		if ( this.show )
		{
			MyProgcessDialog.hide( );
		}
		if ( errorCode.equals( "true" ) )
		{
			try
			{
				JSONArray jsonArray = result.getJSONArray( WebConstant.MESSAGE );
				listMessageTaskImp.onPostExecute( jsonArray );
				
			}
			catch ( JSONException e )
			{
				e.printStackTrace( );
			}
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
		if ( this.show )
		{
			MyProgcessDialog.show( activity, activity.getString( R.string.waiting ) );
		}
		
	}
	
}
