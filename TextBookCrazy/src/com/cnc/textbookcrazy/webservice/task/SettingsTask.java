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

public class SettingsTask extends AsyncTask< Void, Void, JSONObject >
{
	public static class SettingsInput
	{
		private String	avata, alert_email, question, answer, auth_token, username, distance;
		
		public String getAlert_email( )
		{
			return alert_email;
		}
		
		public String getAnswer( )
		{
			return answer;
		}
		
		public String getAuth_token( )
		{
			return auth_token;
		}
		
		public String getAvata( )
		{
			return avata;
		}
		
		/**
		 * @return the distance
		 */
		public String getDistance( )
		{
			return distance;
		}
		
		public String getQuestion( )
		{
			return question;
		}
		
		/**
		 * @return the username
		 */
		public String getUsername( )
		{
			return username;
		}
		
		public void setAlert_email( String alert_email )
		{
			this.alert_email = alert_email;
		}
		
		public void setAnswer( String answer )
		{
			this.answer = answer;
		}
		
		public void setAuth_token( String auth_token )
		{
			this.auth_token = auth_token;
		}
		
		public void setAvata( String avata )
		{
			this.avata = avata;
		}
		
		/**
		 * @param distance
		 *            the distance to set
		 */
		public void setDistance( String distance )
		{
			this.distance = distance;
		}
		
		public void setQuestion( String question )
		{
			this.question = question;
		}
		
		/**
		 * @param username
		 *            the username to set
		 */
		public void setUsername( String username )
		{
			this.username = username;
		}
		
	}
	
	public interface SettingsTaskImp
	{
		public void onPostExecuteSetting( JSONObject jsonObject );
	}
	
	private SettingsInput	settingsInput;
	private Activity		activity;
	private SettingsTaskImp	settingsTaskImp;
	private String			errorCode	= "";
	
	public SettingsTask( Activity activity, SettingsInput settingsInput, SettingsTaskImp settingsTaskImp )
	{
		this.activity = activity;
		this.settingsInput = settingsInput;
		this.settingsTaskImp = settingsTaskImp;
	}
	
	@Override
	protected JSONObject doInBackground( Void... params )
	{
		JSONObject jsonObject = null;
		
		JSONObject json = new JSONObject( );
		JSONObject manJson = new JSONObject( );
		try
		{
			json.put( WebConstant.USER_NAME, settingsInput.getUsername( ) );
			// json.put( WebConstant.PASSWORD, signupInput.getPassword( ) );
			json.put( WebConstant.DISTANCE, settingsInput.getDistance( ) );
			json.put( WebConstant.ALERT, settingsInput.getAlert_email( ) );
			json.put( WebConstant.QUESTION, settingsInput.getQuestion( ) );
			json.put( WebConstant.ANSWER, settingsInput.getAnswer( ) );
			
			manJson.put( WebConstant.USER, json );
			manJson.put( WebConstant.AVATAR, settingsInput.getAvata( ) );
			manJson.put( WebConstant.AUTHOR_TOKEN, settingsInput.getAuth_token( ) );
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		
		WebServiceJSON webService = new WebServiceJSON( URL.SETTINGS );
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
			settingsTaskImp.onPostExecuteSetting( result );
		}
		else if ( errorCode.equals( "false" ) )
		{
			MyOkDialog.showDialog( activity.getString( R.string.update_not_success ), activity );
		}
		else
		{
			MyOkDialog.showDialog( activity.getString( R.string.update_not_success ), activity );
		}
	}
	
	@Override
	protected void onPreExecute( )
	{
		super.onPreExecute( );
		MyProgcessDialog.show( activity, activity.getString( R.string.waiting ) );
		
	}
	
}
