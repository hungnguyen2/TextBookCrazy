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

public class SellBookTask extends AsyncTask< Void, Void, JSONObject >
{
	public static class SellBookInput
	{
		private String	auth_token, bookIcon, bookName, author, price, note, descript,
						condition, edition, type_post, latitude, longtitude;
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
	
	public interface SellBookTaskImp
	{
		public void onPostExecute( );
	}
	
	private String			errorCode	= "";
	private Activity		activity;
	private SellBookTaskImp	sellBookTaskImp;
	private SellBookInput	sellBookInput;
	
	public SellBookTask( Activity activity, SellBookInput sellBookInput, SellBookTaskImp sellBookTaskImp )
	{
		this.activity = activity;
		this.sellBookInput = sellBookInput;
		this.sellBookTaskImp = sellBookTaskImp;
	}
	
	@Override
	protected JSONObject doInBackground( Void... params )
	{
		JSONObject jsonObject = null;
		
		JSONObject json = new JSONObject( );
		JSONObject manJson = new JSONObject( );
		try
		{
			json.put( WebConstant.BOOK_NAME, sellBookInput.getBookName( ) );
			json.put( WebConstant.AUTHOR, sellBookInput.getAuthor( ) );
			json.put( WebConstant.EDITION, sellBookInput.getEdition( ) );
			json.put( WebConstant.PRICE, sellBookInput.getPrice( ) );
			json.put( WebConstant.DESCIPTION, sellBookInput.getDescript( ) );
			json.put( WebConstant.NOTE, sellBookInput.getNote( ) );
			json.put( WebConstant.CONDITION, sellBookInput.getCondition( ) );
			json.put( WebConstant.BONUS, sellBookInput.getBonus( ) + "" );
			json.put( WebConstant.LATITUDE, sellBookInput.getLatitude( ) );
			json.put( WebConstant.LONGTITUDE, sellBookInput.getLongtitude( ) );
			json.put( WebConstant.TYPE_POST, sellBookInput.getType_post( ) );
			
			manJson.put( WebConstant.AUTHOR_TOKEN, sellBookInput.getAuth_token( ) );
			manJson.put( WebConstant.BOOK_ICON, sellBookInput.getBookIcon( ) );
			manJson.put( WebConstant.POSTS, json );
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		
		WebServiceJSON webService = new WebServiceJSON( URL.SELL_BOOK );
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
			sellBookTaskImp.onPostExecute( );
		}
		else if ( errorCode.equals( "false" ) )
		{
			MyOkDialog.showDialog( activity.getString( R.string.post_not_success ), activity );
		}
		else
		{
			MyOkDialog.showDialog( activity.getString( R.string.post_not_success ), activity );
		}
	}
	
	@Override
	protected void onPreExecute( )
	{
		super.onPreExecute( );
		MyProgcessDialog.show( activity, activity.getString( R.string.waiting ) );
		
	}
	
}
