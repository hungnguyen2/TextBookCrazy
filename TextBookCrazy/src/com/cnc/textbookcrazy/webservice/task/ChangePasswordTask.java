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
import com.cnc.textbookcrazy.webservice.datainout.ChangePasswordInput;

public class ChangePasswordTask extends AsyncTask< Void, Void, JSONObject >
{
	public interface ChangePasswordTaskImp
	{
		public void onPostExecute( );
	}
	
	private Activity				activity;
	private ChangePasswordTaskImp	changePasswordTaskImp;
	private ChangePasswordInput		changePasswordInput;
	private String					errorCode	= "";
	
	public ChangePasswordTask( Activity activity, ChangePasswordTaskImp changePasswordTaskImp,
			ChangePasswordInput changePasswordInput )
	{
		this.activity = activity;
		this.changePasswordInput = changePasswordInput;
		this.changePasswordTaskImp = changePasswordTaskImp;
	}
	
	@Override
	protected JSONObject doInBackground( Void... params )
	{
		
		JSONObject jsonObject = null;
		
		JSONObject json = new JSONObject( );
		JSONObject manJson = new JSONObject( );
		try
		{
			json.put( WebConstant.OLD_PASSWORD, changePasswordInput.getOldPass( ) );
			json.put( WebConstant.NEW_PASSWORD, changePasswordInput.getNewPass( ) );
			
			manJson.put( WebConstant.AUTHOR_TOKEN, changePasswordInput.getAuthorToken( ) );
			manJson.put( WebConstant.USER, json );
		}
		catch ( JSONException e )
		{
			
			e.printStackTrace( );
		}
		
		WebServiceJSON webService = new WebServiceJSON( URL.CHANGE_PASSWORD );
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
			changePasswordTaskImp.onPostExecute( );
		}
		else
		{
			MyOkDialog.showDialog( activity.getString( R.string.can_not_change ), activity );
		}
	}
	
	@Override
	protected void onPreExecute( )
	{
		super.onPreExecute( );
		MyProgcessDialog.show( activity, activity.getString( R.string.waiting ) );
	}
	
}
