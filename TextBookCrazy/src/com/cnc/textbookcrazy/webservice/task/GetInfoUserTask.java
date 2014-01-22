package com.cnc.textbookcrazy.webservice.task;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;

import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.help.MyOkDialog;
import com.cnc.textbookcrazy.help.MyProgcessDialog;
import com.cnc.textbookcrazy.webservice.URL;
import com.cnc.textbookcrazy.webservice.WebConstant;
import com.cnc.textbookcrazy.webservice.WebServiceJSON;

public class GetInfoUserTask extends AsyncTask< Void, Void, JSONObject >
{
	public static class GetInfoUserInput
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
	
	public interface GetInfoUserTaskImp
	{
		public void onPostExecute( JSONObject jsonObject );
	}
	
	private String				errorCode;
	private Activity			activity;
	private GetInfoUserTaskImp	getInfoUserTaskImp;
	private GetInfoUserInput	getInfoUserInput;
	
	public GetInfoUserTask( Activity activity, GetInfoUserInput getInfoUserInput, GetInfoUserTaskImp getInfoUserTaskImp )
	{
		this.activity = activity;
		this.getInfoUserInput = getInfoUserInput;
		this.getInfoUserTaskImp = getInfoUserTaskImp;
	}
	
	@Override
	protected JSONObject doInBackground( Void... params )
	{
		JSONObject jsonObject = null;
		
		JSONObject json = new JSONObject( );
		// JSONObject manJson = new JSONObject( );
		try
		{
			json.put( WebConstant.AUTHOR_TOKEN, getInfoUserInput.getAuth_token( ) );
			
			// manJson.put( WebConstant.USER, json );
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		
		WebServiceJSON webService = new WebServiceJSON( URL.GET_INFO_USER );
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
		}
		
		try
		{
			jsonObject = new JSONObject( result );
			errorCode = jsonObject.getString( WebConstant.ERROR_CODE );
		}
		catch ( NullPointerException nex )
		{
			
		}
		catch ( JSONException jex )
		{
			
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
			JSONObject jsonObject = null;
			try
			{
				jsonObject = result.getJSONObject( WebConstant.USER );
			}
			catch ( JSONException e )
			{
				e.printStackTrace( );
			}
			getInfoUserTaskImp.onPostExecute( jsonObject );
		}
		else if ( errorCode.equals( "false" ) )
		{
			MyOkDialog.showDialog( activity.getString( R.string.login_fail ), activity );
		}
		
	}
	
	@Override
	protected void onPreExecute( )
	{
		super.onPreExecute( );
		MyProgcessDialog.show( this.activity, activity.getString( R.string.pd_logining ) );
	}
	
}
