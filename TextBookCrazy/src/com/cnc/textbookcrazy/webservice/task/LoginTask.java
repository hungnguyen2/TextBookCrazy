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
import com.cnc.textbookcrazy.webservice.datainout.LoginInput;
import com.cnc.textbookcrazy.webservice.datainout.LoginOutput;

public class LoginTask extends AsyncTask< Void, Void, JSONObject >
{
	public interface LoginTaskImp
	{
		public void onPostExecute( LoginOutput loginOutput );
	}
	
	private LoginInput		loginInput;
	private Activity		activity;
	private String			errorCode	= "";
	private LoginTaskImp	loginTaskImpl;
	
	public LoginTask( Activity activity, LoginInput loginInput, LoginTaskImp loginTaskImpl )
	{
		this.loginInput = loginInput;
		this.activity = activity;
		this.loginTaskImpl = loginTaskImpl;
	}
	
	@Override
	protected JSONObject doInBackground( Void... params )
	{
		JSONObject jsonObject = null;
		
		JSONObject json = new JSONObject( );
		JSONObject manJson = new JSONObject( );
		try
		{
			json.put( WebConstant.PASSWORD, loginInput.getPassword( ) );
			json.put( WebConstant.EMAIL, loginInput.getLogin( ) );
			json.put( WebConstant.REGISTER_ID, loginInput.getRegisterId( ) );
			
			manJson.put( WebConstant.USER, json );
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		
		WebServiceJSON webService = new WebServiceJSON( URL.LOGIN );
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
			String auth_token = "";
			String username = "";
			String email = "";
			String uid = "";
			String avatar = "";
			try
			{
				JSONObject jsonObject = result.getJSONObject( WebConstant.USER );
				auth_token = jsonObject.getString( WebConstant.AUTHOR_TOKEN );
				email = jsonObject.getString( WebConstant.EMAIL );
				username = jsonObject.getString( WebConstant.USER_NAME );
				uid = jsonObject.getString( WebConstant.UID );
				avatar = jsonObject.getString( WebConstant.AVATAR_URL );
			}
			catch ( JSONException e )
			{
				e.printStackTrace( );
			}
			catch ( NullPointerException nex )
			{
				nex.printStackTrace( );
			}
			LoginOutput loginOutput = new LoginOutput( );
			loginOutput.setAuth_token( auth_token );
			loginOutput.setEmail( email );
			loginOutput.setUsername( username );
			loginOutput.setUser_id( uid );
			loginOutput.setAvatar( avatar );
			loginTaskImpl.onPostExecute( loginOutput );
		}
		else if ( errorCode.equals( "false" ) )
		{
			MyOkDialog.showDialog( activity.getString( R.string.login_fail ), activity );
		}
		else
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
