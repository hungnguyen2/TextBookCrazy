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

public class ListBlockUserTask extends AsyncTask< Void, Void, JSONObject >
{
	public interface ListBlockUserImp
	{
		public void postExecute( JSONArray jsonArray );
	}
	
	public static class ListBlockUserInput
	{
		private String	auth_token;
		
		/**
		 * @return the auth_token
		 */
		public String getAuth_token( )
		{
			return auth_token;
		}
		
		/**
		 * @param auth_token
		 *            the auth_token to set
		 */
		public void setAuth_token( String auth_token )
		{
			this.auth_token = auth_token;
		}
		
	}
	
	private String				errorCode	= "";
	private ListBlockUserImp	listBlockUserImp;
	private ListBlockUserInput	listBlockUserInput;
	private Activity			activity;
	
	public ListBlockUserTask( Activity activity, ListBlockUserInput listBlockUserInput,
			ListBlockUserImp listBlockUserImp )
	{
		this.activity = activity;
		this.listBlockUserImp = listBlockUserImp;
		this.listBlockUserInput = listBlockUserInput;
	}
	
	@Override
	protected JSONObject doInBackground( Void... params )
	{
		JSONObject jsonObject = null;
		
		JSONObject manJson = new JSONObject( );
		try
		{
			manJson.put( WebConstant.AUTHOR_TOKEN, listBlockUserInput.getAuth_token( ) );
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		
		WebServiceJSON webService = new WebServiceJSON( URL.LIST_BLOCK_USER );
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
		MyProgcessDialog.hide( );
		
		if ( errorCode.equals( "true" ) )
		{
			JSONArray jsonArray = null;
			try
			{
				jsonArray = result.getJSONArray( "list_block" );
			}
			catch ( JSONException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace( );
			}
			listBlockUserImp.postExecute( jsonArray );
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
		MyProgcessDialog.show( activity, activity.getString( R.string.waiting ) );
	}
	
}
