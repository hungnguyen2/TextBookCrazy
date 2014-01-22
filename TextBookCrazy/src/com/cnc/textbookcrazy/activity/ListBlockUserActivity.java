package com.cnc.textbookcrazy.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.cnc.textbookcrazy.MActivity;
import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.adapter.ListBlockUserAdapter;
import com.cnc.textbookcrazy.help.Constants;
import com.cnc.textbookcrazy.help.MyOkDialogListener;
import com.cnc.textbookcrazy.help.MyOkDialogListener.MyOkDiaglogImp;
import com.cnc.textbookcrazy.model.ListBlockUserModel;
import com.cnc.textbookcrazy.webservice.task.ListBlockUserTask;
import com.cnc.textbookcrazy.webservice.task.ListBlockUserTask.ListBlockUserImp;
import com.cnc.textbookcrazy.webservice.task.ListBlockUserTask.ListBlockUserInput;
import com.cnc.textbookcrazy.webservice.task.UnBlockUserTask;
import com.cnc.textbookcrazy.webservice.task.UnBlockUserTask.UnBlockUserImp;
import com.cnc.textbookcrazy.webservice.task.UnBlockUserTask.UnBlockUserInput;

public class ListBlockUserActivity extends MActivity implements ListBlockUserImp, UnBlockUserImp
{
	List< ListBlockUserModel >	listBlockUserModels	= new ArrayList< ListBlockUserModel >( );
	ListBlockUserAdapter		listBlockUserAdapter;
	ListView					listView;
	
	private void listBlock( )
	{
		ListBlockUserInput listBlockUserInput = new ListBlockUserInput( );
		listBlockUserInput.setAuth_token( Constants.AUTHOR_TOKEN );
		ListBlockUserTask listBlockUserTask = new ListBlockUserTask( this, listBlockUserInput, this );
		listBlockUserTask.execute( );
	}
	
	@Override
	public void onBackPressed( )
	{
		if ( !MyOkDialogListener.showing )
		{
			super.onBackPressed( );
		}
	}
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.activity_list_block_user );
		
		TextView title = ( TextView ) findViewById( R.id.title );
		title.setText( getResources( ).getString( R.string.blockers ) );
		
		listBlockUserAdapter = new ListBlockUserAdapter( this, listBlockUserModels );
		listView = ( ListView ) findViewById( R.id.lv_block );
		listView.setAdapter( listBlockUserAdapter );
		listView.setOnItemClickListener( new OnItemClickListener( )
		{
			
			@Override
			public void onItemClick( AdapterView< ? > parent, View view, int position, long id )
			{
				unblock( position );
			}
		} );
		listBlock( );
	}
	
	@Override
	public void onPostExecute( )
	{
		Log.d( "", "fdjslfkdsf" );
		MyOkDialogListener.showDialog( "Unblock was successfull", this, new MyOkDiaglogImp( )
		{
			
			@Override
			public void onPositive( )
			{
				listBlock( );
			}
		} );
	}
	
	@Override
	public void postExecute( JSONArray jsonArray )
	{
		listBlockUserModels.clear( );
		listBlockUserAdapter.notifyDataSetChanged( );
		for ( int k = 0; k < jsonArray.length( ); k++ )
		{
			String name = "", blocked_id = "", avatar = "";
			try
			{
				JSONObject jsonObject = jsonArray.getJSONObject( k );
				name = jsonObject.getString( "blocked_name" );
				blocked_id = jsonObject.getString( "blocked_id" );
				avatar = jsonObject.getString( "blocked_avatar" );
			}
			catch ( JSONException e )
			{
				
				e.printStackTrace( );
			}
			ListBlockUserModel listBlockUserModel = new ListBlockUserModel( );
			listBlockUserModel.setUserName( name );
			listBlockUserModel.setBlocked_id( blocked_id );
			listBlockUserModel.setAvatar( avatar );
			listBlockUserModels.add( listBlockUserModel );
			listBlockUserAdapter.notifyDataSetChanged( );
		}
	}
	
	public void unblock( int position )
	{
		UnBlockUserInput blockUserInput = new UnBlockUserInput( );
		blockUserInput.setAuth_token( Constants.AUTHOR_TOKEN );
		blockUserInput.setBlock_id( listBlockUserModels.get( position ).getBlocked_id( ) );
		
		UnBlockUserTask unBlockUserTask = new UnBlockUserTask( this, blockUserInput, this );
		unBlockUserTask.execute( );
	}
}
