package com.cnc.textbookcrazy.webservice.task;

import org.json.JSONObject;

import android.os.AsyncTask;

public class DetailBookTask extends AsyncTask< Void, Void, JSONObject >
{
	public interface DetailBookTaskImp
	{
		public void onPostExecute( );
	}
	
	@Override
	protected JSONObject doInBackground( Void... params )
	{
		return null;
	}
	
	@Override
	protected void onPostExecute( JSONObject result )
	{
		super.onPostExecute( result );
	}
	
	@Override
	protected void onPreExecute( )
	{
		super.onPreExecute( );
	}
	
}
