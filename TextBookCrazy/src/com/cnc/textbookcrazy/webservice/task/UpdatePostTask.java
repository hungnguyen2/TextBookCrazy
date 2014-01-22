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

public class UpdatePostTask extends AsyncTask< Void, Void, JSONObject >
{
	public interface UpdateBookTaskImp
	{
		public void onPostExecute( );
	}
	
	public static class UpdatePostInput
	{
		private String	auth_token, bookIcon, bookName, author, price, note, descript,
						condition, edition, latitude, longtitude, id;
		private boolean	bonus;
		
		public String getAuth_token( )
		{
			return auth_token;
		}
		
		public String getAuthor( )
		{
			return author;
		}
		
		public boolean getBonus( )
		{
			return bonus;
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
		
		/**
		 * @return the id
		 */
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
		
		/**
		 * @param id
		 *            the id to set
		 */
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
		
	}
	
	private String				errorCode	= "";
	private Activity			activity;
	private UpdateBookTaskImp	updateBookTaskImp;
	private UpdatePostInput		updatePostInput;
	
	public UpdatePostTask( Activity activity, UpdatePostInput updatePostInput, UpdateBookTaskImp updateBookTaskImp )
	{
		this.activity = activity;
		this.updatePostInput = updatePostInput;
		this.updateBookTaskImp = updateBookTaskImp;
	}
	
	@Override
	protected JSONObject doInBackground( Void... params )
	{
		JSONObject jsonObject = null;
		
		JSONObject json = new JSONObject( );
		JSONObject manJson = new JSONObject( );
		try
		{
			json.put( WebConstant.BOOK_NAME, updatePostInput.getBookName( ) );
			json.put( WebConstant.AUTHOR, updatePostInput.getAuthor( ) );
			json.put( WebConstant.EDITION, updatePostInput.getEdition( ) );
			json.put( WebConstant.PRICE, updatePostInput.getPrice( ) );
			json.put( WebConstant.DESCIPTION, updatePostInput.getDescript( ) );
			json.put( WebConstant.NOTE, updatePostInput.getNote( ) );
			json.put( WebConstant.CONDITION, updatePostInput.getCondition( ) );
			json.put( WebConstant.BONUS, updatePostInput.getBonus( ) + "" );
			json.put( WebConstant.LATITUDE, updatePostInput.getLatitude( ) );
			json.put( WebConstant.LONGTITUDE, updatePostInput.getLongtitude( ) );
			json.put( WebConstant.POST_ID, updatePostInput.getId( ) );
			
			manJson.put( WebConstant.AUTHOR_TOKEN, updatePostInput.getAuth_token( ) );
			manJson.put( WebConstant.BOOK_ICON, updatePostInput.getBookIcon( ) );
			manJson.put( WebConstant.POST, json );
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		
		WebServiceJSON webService = new WebServiceJSON( URL.UPDATE_POST );
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
			updateBookTaskImp.onPostExecute( );
		}
		else if ( errorCode.equals( "false" ) )
		{
			MyOkDialog.showDialog( activity.getString( R.string.update_post_not_success ), activity );
		}
		else
		{
			MyOkDialog.showDialog( activity.getString( R.string.update_post_not_success ), activity );
		}
	}
	
	@Override
	protected void onPreExecute( )
	{
		super.onPreExecute( );
		MyProgcessDialog.show( activity, activity.getString( R.string.waiting ) );
		
	}
	
}
