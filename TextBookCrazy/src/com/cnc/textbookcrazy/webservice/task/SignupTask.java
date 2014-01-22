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
import com.cnc.textbookcrazy.webservice.datainout.SignupInput;
import com.cnc.textbookcrazy.webservice.datainout.SignupOutput;

public class SignupTask extends AsyncTask< Void, Void, JSONObject >
{
	public interface SignupTaskImp
	{
		public void onPostExecute( SignupOutput signupOutput );
	}
	
	private SignupInput		signupInput;
	private SignupTaskImp	signupTaskImp;
	private Activity		activity;
	private String			errorCode	= "";
	
	public SignupTask( Activity activity, SignupInput signupInput, SignupTaskImp signupTaskImp )
	{
		this.signupInput = signupInput;
		this.activity = activity;
		this.signupTaskImp = signupTaskImp;
	}
	
	@Override
	protected JSONObject doInBackground( Void... params )
	{
		JSONObject jsonObject = null;
		
		JSONObject json = new JSONObject( );
		JSONObject manJson = new JSONObject( );
		try
		{
			json.put( WebConstant.USER_NAME, signupInput.getUser( ) );
			json.put( WebConstant.PASSWORD, signupInput.getPassword( ) );
			json.put( WebConstant.EMAIL, signupInput.getEmail( ) );
			json.put( WebConstant.ALERT, signupInput.getAlert( ) );
			json.put( WebConstant.QUESTION, signupInput.getQuestion( ) );
			json.put( WebConstant.ANSWER, signupInput.getAnswer( ) );
			
			manJson.put( WebConstant.USER, json );
			manJson.put( WebConstant.AVATAR, signupInput.getUrlIcon( ) );
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		
		WebServiceJSON webService = new WebServiceJSON( URL.SIGN_UP );
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
			try
			{
				JSONObject jsonObject = result.getJSONObject( WebConstant.DATA );
			}
			catch ( JSONException e )
			{
				e.printStackTrace( );
			}
			SignupOutput signupOutput = new SignupOutput( );
			signupTaskImp.onPostExecute( signupOutput );
		}
		else if ( errorCode.equals( "false" ) )
		{
			MyOkDialog.showDialog( activity.getString( R.string.email_get ), activity );
		}
		else
		{
			MyOkDialog.showDialog( activity.getString( R.string.signup_fail ), activity );
		}
	}
	
	@Override
	protected void onPreExecute( )
	{
		super.onPreExecute( );
		MyProgcessDialog.show( activity, activity.getString( R.string.signuping ) );
	}
}
