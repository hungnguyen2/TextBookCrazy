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

public class BlockUserTask extends AsyncTask< Void, Void, JSONObject >
{
	public interface BlockUserTaskImp
	{
		public void onPostExecuteBlockUser( );
	}
	
	public static class BlockUsetInput
	{
		private String	auth_token, block_id, post_id;
		
		/**
		 * @return the auth_token
		 */
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
		
		/**
		 * @return the post_id
		 */
		public String getPost_id( )
		{
			return post_id;
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
		 * @param block_id
		 *            the block_id to set
		 */
		public void setBlock_id( String block_id )
		{
			this.block_id = block_id;
		}
		
		/**
		 * @param post_id
		 *            the post_id to set
		 */
		public void setPost_id( String post_id )
		{
			this.post_id = post_id;
		}
		
	}
	
	private BlockUserTaskImp	blockUserTaskImp;
	private BlockUsetInput		blockUsetInput;
	private Activity			activity;
	private String				errorCode;
	
	public BlockUserTask( Activity activity, BlockUsetInput blockUsetInput, BlockUserTaskImp blockUserTaskImp )
	{
		this.activity = activity;
		this.blockUsetInput = blockUsetInput;
		this.blockUserTaskImp = blockUserTaskImp;
	}
	
	@Override
	protected JSONObject doInBackground( Void... params )
	{
		JSONObject jsonObject = null;
		
		JSONObject jObject = new JSONObject( );
		JSONObject manJson = new JSONObject( );
		try
		{
			jObject.put( WebConstant.BLOCKED_ID, blockUsetInput.getBlock_id( ) );
			jObject.put( WebConstant.POST_ID_2, blockUsetInput.getPost_id( ) );
			
			manJson.put( WebConstant.BLOCK, jObject );
			manJson.put( WebConstant.AUTHOR_TOKEN, blockUsetInput.getAuth_token( ) );
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		
		WebServiceJSON webService = new WebServiceJSON( URL.BLOCK_USER );
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
			blockUserTaskImp.onPostExecuteBlockUser( );
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
