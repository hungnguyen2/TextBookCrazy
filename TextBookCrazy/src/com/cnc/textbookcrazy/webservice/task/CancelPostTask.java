package com.cnc.textbookcrazy.webservice.task;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.help.MyOkDialog;
import com.cnc.textbookcrazy.help.MyProgcessDialog;
import com.cnc.textbookcrazy.webservice.URL;
import com.cnc.textbookcrazy.webservice.WebConstant;
import com.cnc.textbookcrazy.webservice.WebServiceJSON;

public class CancelPostTask extends AsyncTask< Void, Void, JSONObject >
{
	public interface CancelTaskImp
	{
		public void onCancelTaskPostExecute( );
	}
	
	public static class CancelTaskInput
	{
		private String	auth_token, id;
		
		public String getAuth_token( )
		{
			return auth_token;
		}
		
		public String getId( )
		{
			return id;
		}
		
		public void setAuth_token( String auth_token )
		{
			this.auth_token = auth_token;
		}
		
		public void setId( String id )
		{
			this.id = id;
		}
	}
	
	private CancelTaskInput	cancelTaskInput;
	private CancelTaskImp	cancelTaskImp;
	private Activity		activity;
	private String			errorCode	= "";
	
	public CancelPostTask( Activity activity, CancelTaskImp cancelTaskImp, CancelTaskInput cancelTaskInput )
	{
		this.activity = activity;
		this.cancelTaskImp = cancelTaskImp;
		this.cancelTaskInput = cancelTaskInput;
	}
	
	@Override
	protected JSONObject doInBackground( Void... params )
	{
		JSONObject jsonObject = null;
		
		JSONObject json = new JSONObject( );
		JSONObject manJson = new JSONObject( );
		try
		{
			json.put( WebConstant.POST_ID_2, cancelTaskInput.getId( ) );
			manJson.put( WebConstant.AUTHOR_TOKEN, cancelTaskInput.getAuth_token( ) );
			manJson.put( WebConstant.POST, json );
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		
		WebServiceJSON webService = new WebServiceJSON( URL.CANCEL_POST );
		String result = webService.delete( manJson.toString( ) );
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
			cancelTaskImp.onCancelTaskPostExecute( );
		}
		else if ( errorCode.equals( "false" ) )
		{
			MyOkDialog.showDialog( activity.getString( R.string.can_not_cancel ), activity );
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
