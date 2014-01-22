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

public class MyPostTask extends AsyncTask< Void, Void, JSONObject >
{
	public static class MyPostInput
	{
		private String	auth_token, page;
		
		/**
		 * @return the auth_token
		 */
		public String getAuth_token( )
		{
			return auth_token;
		}
		
		/**
		 * @return the page
		 */
		public String getPage( )
		{
			return page;
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
		 * @param page
		 *            the page to set
		 */
		public void setPage( String page )
		{
			this.page = page;
		}
		
	}
	
	public static class MyPostOutput
	{
		private String	auth_token, bookIcon, bookName, author, price, note, descript,
						condition, edition, type_post, latitude, longtitude, id;
		private boolean	bonus;
		
		public String getAuth_token( )
		{
			return auth_token;
		}
		
		public String getAuthor( )
		{
			return author;
		}
		
		public String getBookIcon( )
		{
			return bookIcon;
		}
		
		public String getBookName( )
		{
			return bookName;
		}
		
		public String getCondition( )
		{
			return condition;
		}
		
		public String getDescript( )
		{
			return descript;
		}
		
		public String getEdition( )
		{
			return edition;
		}
		
		public String getId( )
		{
			return id;
		}
		
		public String getLatitude( )
		{
			return latitude;
		}
		
		public String getLongtitude( )
		{
			return longtitude;
		}
		
		public String getNote( )
		{
			return note;
		}
		
		public String getPrice( )
		{
			return price;
		}
		
		public String getType_post( )
		{
			return type_post;
		}
		
		public boolean isBonus( )
		{
			return bonus;
		}
		
		public void setAuth_token( String auth_token )
		{
			this.auth_token = auth_token;
		}
		
		public void setAuthor( String author )
		{
			this.author = author;
		}
		
		public void setBonus( boolean bonus )
		{
			this.bonus = bonus;
		}
		
		public void setBookIcon( String bookIcon )
		{
			this.bookIcon = bookIcon;
		}
		
		public void setBookName( String bookName )
		{
			this.bookName = bookName;
		}
		
		public void setCondition( String condition )
		{
			this.condition = condition;
		}
		
		public void setDescript( String descript )
		{
			this.descript = descript;
		}
		
		public void setEdition( String edition )
		{
			this.edition = edition;
		}
		
		public void setId( String id )
		{
			this.id = id;
		}
		
		public void setLatitude( String latitude )
		{
			this.latitude = latitude;
		}
		
		public void setLongtitude( String longtitude )
		{
			this.longtitude = longtitude;
		}
		
		public void setNote( String note )
		{
			this.note = note;
		}
		
		public void setPrice( String price )
		{
			this.price = price;
		}
		
		public void setType_post( String type_post )
		{
			this.type_post = type_post;
		}
		
	}
	
	public interface MyPostTaskImp
	{
		public void onPostExecute( JSONArray jsonArray );
	}
	
	private Activity		activity;
	private MyPostInput		myPostInput;
	private MyPostTaskImp	myPostTaskImp;
	private String			errorCode	= "";
	private boolean			show;
	
	public MyPostTask( Activity activity, MyPostInput myPostInput, MyPostTaskImp myPostTaskImp, boolean show )
	{
		this.activity = activity;
		this.myPostInput = myPostInput;
		this.myPostTaskImp = myPostTaskImp;
		this.show = show;
	}
	
	@Override
	protected JSONObject doInBackground( Void... params )
	{
		JSONObject jsonObject = null;
		
		JSONObject json = new JSONObject( );
		try
		{
			json.put( WebConstant.AUTHOR_TOKEN, myPostInput.getAuth_token( ) );
			json.put( WebConstant.PAGE, myPostInput.getPage( ) );
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		
		WebServiceJSON webService = new WebServiceJSON( URL.MY_POST );
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
		{
			MyProgcessDialog.hide( );
		}
		if ( result == null )
			return;
		if ( errorCode.equals( "true" ) )
		{
			JSONArray jsonArray = null;
			try
			{
				jsonArray = result.getJSONArray( WebConstant.MY_POST );
			}
			catch ( JSONException e )
			{
				e.printStackTrace( );
			}
			myPostTaskImp.onPostExecute( jsonArray );
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
			MyProgcessDialog.show( this.activity, activity.getString( R.string.pd_logining ) );
		}
	}
	
}
