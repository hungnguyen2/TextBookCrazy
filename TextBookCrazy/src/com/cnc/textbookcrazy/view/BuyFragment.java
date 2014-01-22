package com.cnc.textbookcrazy.view;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.ViewSwitcher;

import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.activity.BuyDetailActivity;
import com.cnc.textbookcrazy.activity.MainActivity;
import com.cnc.textbookcrazy.adapter.BuyAdapter;
import com.cnc.textbookcrazy.help.Constants;
import com.cnc.textbookcrazy.help.MyOkDialog;
import com.cnc.textbookcrazy.model.BuyModel;
import com.cnc.textbookcrazy.webservice.WebConstant;
import com.cnc.textbookcrazy.webservice.task.NearByRequestTask;
import com.cnc.textbookcrazy.webservice.task.NearByRequestTask.NearByInput;
import com.cnc.textbookcrazy.webservice.task.NearByRequestTask.NearByRequestTaskImp;

public class BuyFragment extends Fragment implements NearByRequestTaskImp
{
	public enum BUTTON
	{
		BTN_REQUEST
	}
	
	public interface BuyButtonListener
	{
		public void onButtonSelected( BUTTON button );
	}
	
	private BuyButtonListener	callBack;
	
	private Spinner				spin_question;
	private BuyAdapter			buyAdapter;
	private List< BuyModel >	listBuy	= new ArrayList< BuyModel >( );
	private ListView			lv_buy;
	private ViewSwitcher		vsw_search;
	// @Override
	// public void onAttach( Activity activity )
	// {
	// super.onAttach( activity );
	// callBack = ( BuyButtonListener ) activity;
	// }
	
	int							page	= 1;
	private EditText			edt_search;
	
	boolean						scroll	= false, allow = false;
	
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		View view = inflater.inflate( R.layout.fragment_buy,
				container, false );
		
		spin_question = ( Spinner ) view.findViewById( R.id.spin_filter );
		List< String > list = new ArrayList< String >( );
		list.add( "All" );
		list.add( "Lastest" );
		list.add( "ABC" );
		ArrayAdapter< String > dataAdapter = new ArrayAdapter< String >( getActivity( ),
				R.layout.spinner_item_left, list );
		dataAdapter.setDropDownViewResource( R.layout.spinner_item_left );
		spin_question.setAdapter( dataAdapter );
		spin_question.setOnItemSelectedListener( new OnItemSelectedListener( )
		{
			
			@Override
			public void onItemSelected( AdapterView< ? > parent, View view, int position, long id )
			{
				if ( listBuy.size( ) > 0 )
				{
					allow = false;
					scroll = false;
					page = 1;
					listBuy.clear( );
					onSearch( true );
				}
				
			}
			
			@Override
			public void onNothingSelected( AdapterView< ? > parent )
			{
				// TODO Auto-generated method stub
				
			}
		} );
		
		buyAdapter = new BuyAdapter( getActivity( ), listBuy );
		lv_buy = ( ListView ) view.findViewById( R.id.lv_buy );
		lv_buy.setAdapter( buyAdapter );
		vsw_search = ( ViewSwitcher ) view.findViewById( R.id.vsw_search );
		
		edt_search = ( EditText ) view.findViewById( R.id.edt_search );
		Button btn_search = ( Button ) view.findViewById( R.id.btn_search );
		btn_search.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				listBuy.clear( );
				// if ( edt_search.length( ) >= 1 )
				// {
				page = 1;
				onSearch( true );
				// }
				// else
				// {
				// MyOkDialog.showDialog( getActivity( ).getString( R.string.name_book_needed ), getActivity( ) );
				// }
			}
		} );
		
		lv_buy.setOnItemClickListener( new OnItemClickListener( )
		{
			
			@Override
			public void onItemClick( AdapterView< ? > parent, View view, int position, long id )
			{
				Intent t = new Intent( getActivity( ), BuyDetailActivity.class );
				t.putExtra( WebConstant.POST_ID, listBuy.get( position ).getId( ) );
				startActivity( t );
			}
		} );
		
		lv_buy.setOnTouchListener( new OnTouchListener( )
		{
			
			@Override
			public boolean onTouch( View v, MotionEvent event )
			{
				if ( !allow )
				{
					allow = true;
					Log.d( "", "true1 = " + allow );
				}
				return false;
			}
		} );
		
		lv_buy.setOnScrollListener( new OnScrollListener( )
		{
			
			@Override
			public void onScroll( AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount )
			{
				Log.d( "", "lv_buy = " + lv_buy.getCount( ) );
				Log.d( "", "lv_buy2 = " + lv_buy.getLastVisiblePosition( ) );
				Log.d( "", "listBuy = " + listBuy.size( ) );
				Log.d( "", "allow2 = " + allow );
				Log.d( "", "scroll = " + scroll );
				
				if ( scroll && allow && lv_buy.getCount( ) > 0
						&& ( lv_buy.getLastVisiblePosition( ) == ( page * 10 - 1 ) ) )
				{
					allow = false;
					scroll = false;
					page++;
					Log.d( "", "page = " + page );
					onSearch( false );
				}
			}
			
			@Override
			public void onScrollStateChanged( AbsListView view, int scrollState )
			{
				
			}
		} );
		
		return view;
		
	}
	
	@Override
	public void onPostExecute( JSONArray jsonArray )
	{
		for ( int k = 0; k < jsonArray.length( ); k++ )
		{
			try
			{
				JSONObject jsonObject = jsonArray.getJSONObject( k );
				
				BuyModel buyModel = new BuyModel( );
				buyModel.setBookName( jsonObject.getString( WebConstant.BOOK_NAME ) );
				buyModel.setAuthorName( jsonObject.getString( WebConstant.AUTHOR ) );
				buyModel.setId( jsonObject.getString( WebConstant.POST_ID ) );
				// buyModel.setUserId( jsonObject.getString( WebConstant.USER_ID ) );
				buyModel.setIcon( jsonObject.getString( WebConstant.BOOK_ICON ) );
				buyModel.setPrice( jsonObject.getString( WebConstant.PRICE ) );
				buyModel.setDistance( jsonObject.getString( WebConstant.DISTANCE ) );
				// nearbyRequestModel.setDistance( jsonObject.getString( WebConstant.DISTANCE ) );
				buyAdapter.addItemSell( buyModel );
			}
			catch ( JSONException e )
			{
				e.printStackTrace( );
			}
		}
		
		if ( jsonArray.length( ) == 0 )
		{
			if ( page == 1 )
				vsw_search.setDisplayedChild( 1 );
			scroll = false;
		}
		else
		{
			if ( page == 1 )
				lv_buy.setSelection( 0 );
			vsw_search.setDisplayedChild( 0 );
			scroll = true;
		}
	}
	
	public void onRefresh( )
	{
		page = 1;
		allow = false;
		scroll = false;
		listBuy.clear( );
		onSearch( true );
	}
	
	@Override
	public void onResume( )
	{
		super.onResume( );
		Log.d( "", "size = " + listBuy.size( ) );
		// listBuy.clear( );
		try
		{
			InputMethodManager imm = ( InputMethodManager ) getActivity( ).getSystemService(
					Context.INPUT_METHOD_SERVICE );
			imm.hideSoftInputFromWindow( getActivity( ).getWindow( ).getCurrentFocus( ).getWindowToken( ), 0 );
		}
		catch ( NullPointerException nex )
		{
			nex.printStackTrace( );
		}
	}
	
	@SuppressLint( "DefaultLocale" )
	private void onSearch( boolean show )
	{
		if ( ( ( MainActivity ) getActivity( ) ).getLocation( ) == null )
		{
			MyOkDialog.showDialog( getString( R.string.can_not_get_location ), getActivity( ) );
			return;
		}
		
		NearByInput nearByInput = new NearByInput( );
		nearByInput.setAuth_token( Constants.AUTHOR_TOKEN );
		nearByInput.setType( "sell" );
		nearByInput.setKey_search( edt_search.getText( ).toString( ) );
		nearByInput.setPage( page + "" );
		nearByInput.setLatitude( ( ( MainActivity ) getActivity( ) ).getLocation( ).getLatitude( ) + "" );
		nearByInput.setLongtidue( ( ( MainActivity ) getActivity( ) ).getLocation( ).getLongitude( ) + "" );
		nearByInput.setSort( spin_question.getSelectedItem( ).toString( ).toLowerCase( ) );
		
		NearByRequestTask nearByRequestTask = new NearByRequestTask( getActivity( ), nearByInput, this, show );
		nearByRequestTask.execute( );
	}
	
}
