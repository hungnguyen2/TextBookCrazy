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

public class NearByRequestTask extends AsyncTask< Void, Void, JSONObject >
{
	public static class NearByInput
	{
		private String	auth_token, type, key_search, page, latitude, longtidue, sort;
		
		/**
		 * @return the auth_token
		 */
		public String getAuth_token( )
		{
			return auth_token;
		}
		
		/**
		 * @return the key_search
		 */
		public String getKey_search( )
		{
			return key_search;
		}
		
		/**
		 * @return the latitude
		 */
		public String getLatitude( )
		{
			return latitude;
		}
		
		/**
		 * @return the longtidue
		 */
		public String getLongtidue( )
		{
			return longtidue;
		}
		
		/**
		 * @return the page
		 */
		public String getPage( )
		{
			return page;
		}
		
		/**
		 * @return the sort
		 */
		public String getSort( )
		{
			return sort;
		}
		
		/**
		 * @return the type
		 */
		public String getType( )
		{
			return type;
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
		 * @param key_search
		 *            the key_search to set
		 */
		public void setKey_search( String key_search )
		{
			this.key_search = key_search;
		}
		
		/**
		 * @param latitude
		 *            the latitude to set
		 */
		public void setLatitude( String latitude )
		{
			this.latitude = latitude;
		}
		
		/**
		 * @param longtidue
		 *            the longtidue to set
		 */
		public void setLongtidue( String longtidue )
		{
			this.longtidue = longtidue;
		}
		
		/**
		 * @param page
		 *            the page to set
		 */
		public void setPage( String page )
		{
			this.page = page;
		}
		
		/**
		 * @param sort
		 *            the sort to set
		 */
		public void setSort( String sort )
		{
			this.sort = sort;
		}
		
		/**
		 * @param type
		 *            the type to set
		 */
		public void setType( String type )
		{
			this.type = type;
		}
	}
	
	public interface NearByRequestTaskImp
	{
		public void onPostExecute( JSONArray jsonArray );
	}
	
	// public static AsyncTask< Void, Void, JSONObject > getInstance( Activity activity
	// , NearByInput inputHistoryOrder, NearByRequestTaskImp historyOrdersImplement,
	// boolean showProgdialog )
	// {
	// if ( myAsyncTask != null && myAsyncTask.getStatus( ) == AsyncTask.Status.PENDING )
	// {
	// return myAsyncTask;
	// }
	//
	// if ( myAsyncTask != null && myAsyncTask.getStatus( ) == AsyncTask.Status.RUNNING )
	// {
	// if ( myAsyncTask.isCancelled( ) )
	// {
	// myAsyncTask = new NearByRequestTask( activity, inputHistoryOrder, historyOrdersImplement );
	// }
	// else
	// {
	// return null;
	// }
	// }
	//
	// if ( myAsyncTask != null && myAsyncTask.getStatus( ) == AsyncTask.Status.FINISHED )
	// {
	// myAsyncTask = new NearByRequestTask( activity, inputHistoryOrder, historyOrdersImplement );
	// }
	//
	// if ( myAsyncTask == null )
	// {
	// myAsyncTask = new NearByRequestTask( activity, inputHistoryOrder, historyOrdersImplement );
	// }
	//
	// Log.d( "", "asyntask status = " + myAsyncTask.getStatus( ) );
	// return myAsyncTask;
	// }
	
	private Activity					activity;
	private NearByInput					nearByInput;
	private NearByRequestTaskImp		nearByRequestTaskImp;
	
	private String						errorCode	= "";
	private static NearByRequestTask	myAsyncTask	= null;
	private boolean						show;
	
	public NearByRequestTask( Activity activity, NearByInput nearByInput, NearByRequestTaskImp nearByRequestTaskImp,
			boolean show )
	{
		// TODO Auto-generated constructor stub
		this.activity = activity;
		this.nearByInput = nearByInput;
		this.nearByRequestTaskImp = nearByRequestTaskImp;
		this.show = show;
	}
	
	@Override
	protected JSONObject doInBackground( Void... params )
	{
		JSONObject jsonObject = null;
		
		JSONObject json = new JSONObject( );
		// JSONObject manJson = new JSONObject( );
		try
		{
			json.put( WebConstant.TYPE_POST, nearByInput.getType( ) );
			
			json.put( WebConstant.AUTHOR_TOKEN, nearByInput.getAuth_token( ) );
			json.put( WebConstant.SEARCH, nearByInput.getKey_search( ) );
			json.put( WebConstant.PAGE, nearByInput.getPage( ) );
			json.put( WebConstant.LATITUDE, nearByInput.getLatitude( ) );
			json.put( WebConstant.LONGTITUDE, nearByInput.getLongtidue( ) );
			json.put( WebConstant.SORT, nearByInput.getSort( ) );
			// manJson.put( WebConstant.POST, json );
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		
		WebServiceJSON webService = new WebServiceJSON( URL.LIST_POST_TYPE );
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
		if ( this.show )
			MyProgcessDialog.hide( );
		if ( result == null )
			return;
		if ( errorCode.equals( "true" ) )
		{
			JSONArray array = null;
			try
			{
				array = result.getJSONArray( WebConstant.LIST_POST );
			}
			catch ( JSONException e )
			{
				e.printStackTrace( );
			}
			nearByRequestTaskImp.onPostExecute( array );
		}
		else if ( errorCode.equals( "false" ) )
		{
		}
	}
	
	@Override
	protected void onPreExecute( )
	{
		super.onPreExecute( );
		if ( this.show )
		{
			MyProgcessDialog.show( this.activity, activity.getString( R.string.waiting ) );
			MyProgcessDialog.disableCancel( false );
		}
	}
	
}
