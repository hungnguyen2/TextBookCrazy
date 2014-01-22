package com.cnc.textbookcrazy.webservice.task;

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

public class UnBlockUserTask extends AsyncTask< Void, Void, JSONObject >
{
	public interface UnBlockUserImp
	{
		public void onPostExecute( );
	}
	
	public static class UnBlockUserInput
	{
		private String	auth_token, block_id;
		
		public String getAuth_token( )
		{
			return auth_token;
		}
		
		/**
		 * @return the block_id
		 */
		public String getBlock_id( )
		{
			return block_id;
		}
		
		public void setAuth_token( String auth_token )
		{
			this.auth_token = auth_token;
		}
		
		/**
		 * @param block_id
		 *            the block_id to set
		 */
		public void setBlock_id( String block_id )
		{
			this.block_id = block_id;
		}
		
	}
	
	private Activity			activity;
	private UnBlockUserInput	unBlockUserInput;
	private UnBlockUserImp		blockUserImp;
	private String				errorCode	= "";
	
	public UnBlockUserTask( Activity activity, UnBlockUserInput blockUserInput, UnBlockUserImp unBlockUserImp )
	{
		// TODO Auto-generated constructor stub
		this.activity = activity;
		this.blockUserImp = unBlockUserImp;
		this.unBlockUserInput = blockUserInput;
	}
	
	@Override
	protected JSONObject doInBackground( Void... params )
	{
		JSONObject jsonObject = null;
		
		JSONObject jObject = new JSONObject( );
		JSONObject manJson = new JSONObject( );
		try
		{
			jObject.put( WebConstant.BLOCKED_ID, unBlockUserInput.getBlock_id( ) );
			
			manJson.put( WebConstant.BLOCK, jObject );
			manJson.put( WebConstant.AUTHOR_TOKEN, unBlockUserInput.getAuth_token( ) );
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		
		WebServiceJSON webService = new WebServiceJSON( URL.UNBLOCK_USER );
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
			blockUserImp.onPostExecute( );
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
