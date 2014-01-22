package com.cnc.textbookcrazy.webservice.task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;

import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.help.MyProgcessDialog;
import com.cnc.textbookcrazy.webservice.URL;
import com.cnc.textbookcrazy.webservice.WebConstant;
import com.cnc.textbookcrazy.webservice.WebServiceJSON;

public class DetailPostTask extends AsyncTask< Void, Void, JSONObject >
{
	public interface DetailPostImp
	{
		public void onPostExecute( JSONObject jsonObject, JSONArray jsonArray, String user_avatar );
	}
	
	public static class DetailPostInput
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
	
	private Activity		activity;
	private DetailPostInput	detailPostInput;
	private DetailPostImp	detailPostImp;
	private String			errorCode	= "";
	
	public DetailPostTask( Activity activity, DetailPostInput detailPostInput, DetailPostImp detailPostImp )
	{
		this.activity = activity;
		this.detailPostInput = detailPostInput;
		this.detailPostImp = detailPostImp;
	}
	
	@Override
	protected JSONObject doInBackground( Void... params )
	{
		JSONObject jsonObject = null;
		
		JSONObject json = new JSONObject( );
		JSONObject manJson = new JSONObject( );
		try
		{
			json.put( WebConstant.POST_ID_2, detailPostInput.getId( ) );
			
			manJson.put( WebConstant.AUTHOR_TOKEN, detailPostInput.getAuth_token( ) );
			manJson.put( WebConstant.POST, json );
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		
		WebServiceJSON webService = new WebServiceJSON( URL.DETAIL_POST );
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
		if ( result == null )
			return;
		if ( errorCode.equals( "true" ) )
		{
			JSONObject jsonObject = null;
			JSONArray jOtherBook = null;
			JSONObject jAvatar = null;
			String user_avatar = "";
			try
			{
				jsonObject = result.getJSONObject( WebConstant.POST );
				jOtherBook = result.getJSONArray( WebConstant.OTHER_POST );
				user_avatar = result.getString( WebConstant.USER_POST_AVATAR );
			}
			catch ( JSONException e )
			{
				e.printStackTrace( );
			}
			
			detailPostImp.onPostExecute( jsonObject, jOtherBook, user_avatar );
		}
		else if ( errorCode.equals( "false" ) )
		{
		}
	}
	
	@Override
	protected void onPreExecute( )
	{
		super.onPreExecute( );
		MyProgcessDialog.show( this.activity, activity.getString( R.string.waiting ) );
	}
	
}
