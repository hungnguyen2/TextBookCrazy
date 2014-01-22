package com.cnc.textbookcrazy.view;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.ViewFlipper;

import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.activity.DetailRequestActivity;
import com.cnc.textbookcrazy.activity.MainActivity;
import com.cnc.textbookcrazy.adapter.NearbyRequestAdapter;
import com.cnc.textbookcrazy.help.ConnectionDetector;
import com.cnc.textbookcrazy.help.Constants;
import com.cnc.textbookcrazy.help.MyOkDialog;
import com.cnc.textbookcrazy.model.NearbyRequestModel;
import com.cnc.textbookcrazy.webservice.WebConstant;
import com.cnc.textbookcrazy.webservice.task.NearByRequestTask;
import com.cnc.textbookcrazy.webservice.task.NearByRequestTask.NearByInput;
import com.cnc.textbookcrazy.webservice.task.NearByRequestTask.NearByRequestTaskImp;
import com.cnc.textbookcrazy.webservice.task.SellBookTask;
import com.cnc.textbookcrazy.webservice.task.SellBookTask.SellBookInput;
import com.cnc.textbookcrazy.webservice.task.SellBookTask.SellBookTaskImp;

public class SellFragment extends Fragment implements SellBookTaskImp, NearByRequestTaskImp
{
	public enum BUTTON
	{
		BTN_NEARBY
	}
	
	public interface SellButtonListenter
	{
		public void onSellButtonSelected( BUTTON button );
	}
	
	SellButtonListenter					callBack;
	
	private Spinner						spin_question, spin_question_2;
	
	private ListView					lv_nearbyRequest;
	private List< NearbyRequestModel >	listNearby			= new ArrayList< NearbyRequestModel >( );
	private NearbyRequestAdapter		nearbyRequestAdapter;
	
	private ViewFlipper					viewflip;
	private int							children			= 0;
	
	private ImageView					icon_book;
	
	private String						bitmapToBinary		= "";
	
	private static int					RESULT_LOAD_IMAGE	= 1;
	
	private EditText					edt_bookName, edt_author, edt_price, edt_edition, edt_des, edt_not;
	
	private CheckBox					cb_bonus;
	
	// @Override
	// public void onAttach( Activity activity )
	// {
	// super.onAttach( activity );
	// callBack = ( SellButtonListenter ) activity;
	// }
	
	boolean								allowUpdate			= true;
	
	int									page				= 1;
	
	private String						picturePath			= "";
	
	private EditText					edt_search;
	
	private boolean						allowChangeText		= true;
	
	public boolean check( )
	{
		if ( children == 1 )
		{
			children = 0;
			viewflip.showPrevious( );
			listNearby.clear( );
			return false;
		}
		return true;
	}
	
	private boolean checkupdate( )
	{
		if ( edt_bookName.length( ) < 1 )
		{
			MyOkDialog.showDialog( getString( R.string.dl_bookname_needed ), getActivity( ) );
			
			return false;
		}
		if ( edt_author.length( ) < 1 )
		{
			MyOkDialog.showDialog( getString( R.string.dl_author_needed ), getActivity( ) );
			
			return false;
		}
		if ( edt_edition.length( ) < 1 )
		{
			MyOkDialog.showDialog( getString( R.string.dl_edition_needed ), getActivity( ) );
			
			return false;
		}
		return true;
	}
	
	// delay button
	private boolean delayButton( )
	{
		return new Handler( ).postDelayed( new Runnable( )
		{
			@Override
			public void run( )
			{
				allowUpdate = true;
			}
		}, 180000 );
	}
	
	private Bitmap fixImage( String pathOfInputImage, int dstWidth, int dstHeight, ByteArrayOutputStream os )
	{
		Bitmap resizedBitmap = null;
		try
		{
			int inWidth = 0;
			int inHeight = 0;
			
			InputStream in = new FileInputStream( pathOfInputImage );
			
			// decode image size (decode metadata only, not the whole image)
			BitmapFactory.Options options = new BitmapFactory.Options( );
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream( in, null, options );
			in.close( );
			in = null;
			
			// save width and height
			inWidth = options.outWidth;
			inHeight = options.outHeight;
			
			// decode full image pre-resized
			in = new FileInputStream( pathOfInputImage );
			options = new BitmapFactory.Options( );
			// calc rought re-size (this is no exact resize)
			options.inSampleSize = Math.max( inWidth / dstWidth, inHeight / dstHeight );
			// decode full image
			Bitmap roughBitmap = BitmapFactory.decodeStream( in, null, options );
			
			// calc exact destination size
			Matrix m = new Matrix( );
			RectF inRect = new RectF( 0, 0, roughBitmap.getWidth( ), roughBitmap.getHeight( ) );
			RectF outRect = new RectF( 0, 0, dstWidth, dstHeight );
			m.setRectToRect( inRect, outRect, Matrix.ScaleToFit.CENTER );
			float[] values = new float[ 9 ];
			m.getValues( values );
			
			// resize bitmap
			resizedBitmap = Bitmap.createScaledBitmap( roughBitmap,
					( int ) ( roughBitmap.getWidth( ) * values[ 0 ] ),
					( int ) ( roughBitmap.getHeight( ) * values[ 4 ] ), true );
			
			// save image
			try
			{
				resizedBitmap.compress( Bitmap.CompressFormat.JPEG, 80, os );
			}
			catch ( Exception e )
			{
				Log.e( "Image", e.getMessage( ), e );
			}
		}
		catch ( IOException e )
		{
			Log.e( "Image", e.getMessage( ), e );
		}
		return resizedBitmap;
	}
	
	@Override
	public void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		super.onActivityResult( requestCode, resultCode, data );
		
		if ( requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data )
		{
			Uri selectedImage = data.getData( );
			String[] filePathColumn =
			{ MediaStore.Images.Media.DATA };
			
			Cursor cursor = getActivity( ).getContentResolver( ).query( selectedImage,
					filePathColumn, null, null, null );
			cursor.moveToFirst( );
			
			int columnIndex = cursor.getColumnIndex( filePathColumn[ 0 ] );
			String picturePath = cursor.getString( columnIndex );
			cursor.close( );
			
			Bitmap bitmap = null;
			// BitmapFactory.Options options = new BitmapFactory.Options( );
			// options.inSampleSize = 2;
			// try
			// {
			// bitmap = BitmapFactory.decodeFile( picturePath );
			// }
			// catch ( OutOfMemoryError oex )
			// {
			// oex.printStackTrace( );
			// try
			// {
			// bitmap = BitmapFactory.decodeFile( picturePath, options );
			// }
			// catch ( OutOfMemoryError oex2 )
			// {
			// oex2.printStackTrace( );
			// }
			// }
			//
			// if ( bitmap == null )
			// {
			// MyOkDialog.showDialog( getActivity( ).getString( R.string.file_not_found ), getActivity( ) );
			// return;
			// }
			this.picturePath = picturePath;
			ByteArrayOutputStream os = new ByteArrayOutputStream( );
			// Boolean compressed = bitmap.compress( Bitmap.CompressFormat.JPEG, 75, os );
			// Log.d( "", "compressed = " + compressed );
			bitmap = fixImage( picturePath, 200, 400, os );
			byte[] array = os.toByteArray( );
			bitmap = BitmapFactory.decodeByteArray( array, 0, array.length );
			bitmapToBinary = Base64.encodeToString( array, Base64.DEFAULT );
			
			icon_book.setImageBitmap( bitmap );
		}
	}
	
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		View view = inflater.inflate( R.layout.fragment_sell,
				container, false );
		
		viewflip = ( ViewFlipper ) view.findViewById( R.id.viewflip );
		
		edt_bookName = ( EditText ) view.findViewById( R.id.edt_bookName );
		edt_author = ( EditText ) view.findViewById( R.id.edt_author );
		edt_price = ( EditText ) view.findViewById( R.id.edt_price );
		edt_edition = ( EditText ) view.findViewById( R.id.edt_edition );
		edt_des = ( EditText ) view.findViewById( R.id.edt_des );
		edt_not = ( EditText ) view.findViewById( R.id.edt_note );
		cb_bonus = ( CheckBox ) view.findViewById( R.id.checkbox );
		
		// SharedPreferences settings = getActivity( ).getSharedPreferences( PREFS_REMEMBER, 0 );
		// String bookName = settings.getString( "bookname", "" );
		// String author = settings.getString( "author", "" );
		// String edition = settings.getString( "edition", "" );
		// String price = settings.getString( "price", "" );
		// String des = settings.getString( "des", "" );
		// String note = settings.getString( "note", "" );
		// String condition = settings.getString( "condition", "New" );
		// String attach = settings.getString( "attach", "false" );
		// String picturePath = settings.getString( "picturepath", "" );
		// bitmapToBinary = settings.getString( "picturebinary", "" );
		
		spin_question = ( Spinner ) view.findViewById( R.id.spin_select );
		List< String > list = new ArrayList< String >( );
		list.add( "New" );
		list.add( "Like new" );
		list.add( "Good" );
		list.add( "Acceptable" );
		ArrayAdapter< String > dataAdapter = new ArrayAdapter< String >( getActivity( ),
				R.layout.spinner_item_left, list );
		dataAdapter.setDropDownViewResource( R.layout.spinner_item_left );
		spin_question.setAdapter( dataAdapter );
		// for ( int k = 0; k < list.size( ); k++ )
		// {
		// if ( condition.equals( list.get( k ) ) )
		// {
		// spin_question.setSelection( k );
		// break;
		// }
		// }
		// cb_bonus.setChecked( Boolean.parseBoolean( attach ) );
		// edt_bookName.setText( bookName );
		// edt_author.setText( author );
		// edt_price.setText( price );
		// edt_edition.setText( edition );
		// edt_des.setText( des );
		// edt_not.setText( note );
		
		icon_book = ( ImageView ) view.findViewById( R.id.icon_book );
		icon_book.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
				
				startActivityForResult( i, RESULT_LOAD_IMAGE );
			}
		} );
		if ( !picturePath.equals( "" ) )
		{
			Bitmap bitmap = null;
			try
			{
				bitmap = BitmapFactory.decodeFile( picturePath );
			}
			catch ( OutOfMemoryError oex )
			{
				oex.printStackTrace( );
			}
			
			ByteArrayOutputStream os = new ByteArrayOutputStream( );
			Boolean compressed = bitmap.compress( Bitmap.CompressFormat.JPEG, 75, os );
			byte[] array = os.toByteArray( );
			bitmap = BitmapFactory.decodeByteArray( array, 0, array.length );
			icon_book.setImageBitmap( bitmap );
		}
		
		edt_price.addTextChangedListener( new TextWatcher( )
		{
			
			@Override
			public void afterTextChanged( Editable s )
			{
				
			}
			
			@Override
			public void beforeTextChanged( CharSequence s, int start, int count, int after )
			{
			}
			
			@Override
			public void onTextChanged( CharSequence s, int start, int before, int count )
			{
				if ( allowChangeText && !edt_price.getText( ).toString( ).contains( "$" ) )
				{
					String l = edt_price.getText( ).toString( );
					edt_price.setText( "$" + l );
				}
				Editable etext = edt_price.getText( );
				Selection.setSelection( etext, edt_price.getText( ).toString( ).length( ) );
				allowChangeText = true;
			}
		} );
		
		Button btn_nearby = ( Button ) view.findViewById( R.id.btn_nearby );
		btn_nearby.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				// callBack.onSellButtonSelected( BUTTON.BTN_NEARBY );
				if ( ConnectionDetector.isConnectingToInternet( getActivity( ) ) )
				{
					onNearbyRequest( true );
					viewflip.showNext( );
					children = 1;
				}
				else
				{
					MyOkDialog.showDialog( getString( R.string.a_connect_internet ), getActivity( ) );
				}
			}
		} );
		
		Button btn_submit = ( Button ) view.findViewById( R.id.btn_submit );
		btn_submit.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				
				if ( ConnectionDetector.isConnectingToInternet( getActivity( ) ) )
				{
					
					if ( ( ( MainActivity ) getActivity( ) ).getLocation( ) != null )
					{
						if ( allowUpdate )
						{
							if ( checkupdate( ) )
							{
								allowUpdate = false;
								delayButton( );
							}
							else
							{
								return;
							}
						}
						else
						{
							MyOkDialog.showDialog( getString( R.string.post_3_minutes ), getActivity( ) );
							return;
						}
						
						onSelling( );
					}
					else
					{
						MyOkDialog.showDialog( getString( R.string.can_not_get_location ), getActivity( ) );
					}
				}
				else
				{
					MyOkDialog.showDialog( getString( R.string.a_connect_internet ), getActivity( ) );
				}
			}
		} );
		
		lv_nearbyRequest = ( ListView ) view.findViewById( R.id.lv_nearby_request );
		nearbyRequestAdapter = new NearbyRequestAdapter( getActivity( ), listNearby );
		lv_nearbyRequest.setAdapter( nearbyRequestAdapter );
		
		lv_nearbyRequest.setOnItemClickListener( new OnItemClickListener( )
		{
			
			@Override
			public void onItemClick( AdapterView< ? > parent, View view, int position, long id )
			{
				Intent t = new Intent( getActivity( ), DetailRequestActivity.class );
				t.putExtra( WebConstant.POST_ID, listNearby.get( position ).getId( ) );
				// t.putExtra( WebConstant.USER_ID, listNearby.get( position ).getUserId( ) );
				startActivity( t );
				Log.d( "", "on click item" );
			}
		} );
		
		lv_nearbyRequest.setOnScrollListener( new OnScrollListener( )
		{
			@Override
			public void onScroll( AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount )
			{
				Log.d( "", "lv_nearbyRequest = " + lv_nearbyRequest.getCount( ) );
				Log.d( "", "lv_nearbyRequest = " + lv_nearbyRequest.getLastVisiblePosition( ) );
				
				if ( lv_nearbyRequest.getCount( ) > 0
						&& ( lv_nearbyRequest.getLastVisiblePosition( ) == page * 10 - 1 ) )
				{
					page++;
					Log.d( "", "page = " + page );
					onNearbyRequest( false );
				}
			}
			
			@Override
			public void onScrollStateChanged( AbsListView view, int scrollState )
			{
			}
		} );
		//
		
		spin_question_2 = ( Spinner ) view.findViewById( R.id.spin_filter );
		List< String > list_2 = new ArrayList< String >( );
		list_2.add( "All" );
		list_2.add( "Lastest" );
		list_2.add( "ABC" );
		ArrayAdapter< String > dataAdapter_2 = new ArrayAdapter< String >( getActivity( ),
				R.layout.spinner_item_left, list_2 );
		dataAdapter.setDropDownViewResource( R.layout.spinner_item_left );
		spin_question_2.setAdapter( dataAdapter_2 );
		spin_question_2.setOnItemSelectedListener( new OnItemSelectedListener( )
		{
			
			@Override
			public void onItemSelected( AdapterView< ? > parent, View view, int position, long id )
			{
				if ( listNearby.size( ) > 0 )
				{
					listNearby.clear( );
					nearbyRequestAdapter.notifyDataSetChanged( );
					page = 1;
					onNearbyRequest( true );
				}
				
			}
			
			@Override
			public void onNothingSelected( AdapterView< ? > parent )
			{
				// TODO Auto-generated method stub
				
			}
		} );
		
		edt_search = ( EditText ) view.findViewById( R.id.edt_search );
		Button btn_search = ( Button ) view.findViewById( R.id.btn_search );
		btn_search.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				listNearby.clear( );
				nearbyRequestAdapter.notifyDataSetChanged( );
				page = 1;
				onNearbyRequest( true );
			}
		} );
		
		return view;
		
	}
	
	@Override
	public void onDestroy( )
	{
		// SharedPreferences settings = getActivity( ).getSharedPreferences( PREFS_REMEMBER, 0 );
		// SharedPreferences.Editor editor = settings.edit( );
		// editor.putString( "bookname", edt_bookName.getText( ).toString( ) );
		// editor.putString( "author", edt_author.getText( ).toString( ) );
		// editor.putString( "edition", edt_edition.getText( ).toString( ) );
		// editor.putString( "price", edt_price.getText( ).toString( ) );
		// editor.putString( "des", edt_des.getText( ).toString( ) );
		// editor.putString( "note", edt_not.getText( ).toString( ) );
		// editor.putString( "condition", spin_question.getSelectedItem( ).toString( ) );
		// editor.putString( "attach", cb_bonus.isChecked( ) + "" );
		// editor.putString( "picturepath", picturePath );
		// editor.putString( "picturebinary", bitmapToBinary );
		// editor.commit( );
		super.onDestroy( );
	}
	
	@SuppressLint( "DefaultLocale" )
	private void onNearbyRequest( boolean show )
	{
		if ( ( ( MainActivity ) getActivity( ) ).getLocation( ) == null )
		{
			MyOkDialog.showDialog( getString( R.string.can_not_get_location ), getActivity( ) );
			return;
		}
		
		NearByInput nearByInput = new NearByInput( );
		nearByInput.setAuth_token( Constants.AUTHOR_TOKEN );
		nearByInput.setType( "request" );
		nearByInput.setKey_search( edt_search.getText( ).toString( ) );
		nearByInput.setPage( page + "" );
		
		nearByInput.setLatitude( ( ( MainActivity ) getActivity( ) ).getLocation( ).getLatitude( ) + "" );
		nearByInput.setLongtidue( ( ( MainActivity ) getActivity( ) ).getLocation( ).getLongitude( ) + "" );
		
		nearByInput.setSort( spin_question_2.getSelectedItem( ).toString( ).toLowerCase( ) );
		
		NearByRequestTask nearByRequestTask = new NearByRequestTask( getActivity( ), nearByInput, this, show );
		nearByRequestTask.execute( );
	}
	
	@Override
	public void onPostExecute( )
	{
		MyOkDialog.showDialog( getActivity( ).getString( R.string.post_success ), getActivity( ) );
		
		allowChangeText = false;
		edt_bookName.setText( "" );
		edt_author.setText( "" );
		edt_price.setText( "" );
		edt_edition.setText( "" );
		edt_des.setText( "" );
		edt_not.setText( "" );
		cb_bonus.setText( "" );
		icon_book.setImageResource( R.drawable.icon_book );
		bitmapToBinary = "";
		spin_question.setSelection( 0 );
		if ( cb_bonus.isChecked( ) )
		{
			cb_bonus.setChecked( false );
		}
		Log.d( "", "price" + edt_price.getText( ).toString( ) );
	}
	
	@Override
	public void onPostExecute( JSONArray jsonArray )
	{
		for ( int k = 0; k < jsonArray.length( ); k++ )
		{
			NearbyRequestModel nearbyRequestModel = new NearbyRequestModel( );
			try
			{
				JSONObject jsonObject = jsonArray.getJSONObject( k );
				
				nearbyRequestModel.setBookName( jsonObject.getString( WebConstant.BOOK_NAME ) );
				nearbyRequestModel.setAuthorName( jsonObject.getString( WebConstant.AUTHOR ) );
				nearbyRequestModel.setId( jsonObject.getString( WebConstant.POST_ID ) );
				// nearbyRequestModel.setUserId( jsonObject.getString( WebConstant.USER_ID ) );
				nearbyRequestModel.setIcon( jsonObject.getString( WebConstant.BOOK_ICON ) );
				nearbyRequestModel.setDistance( jsonObject.getString( WebConstant.DISTANCE ) );
				listNearby.add( nearbyRequestModel );
				nearbyRequestAdapter.notifyDataSetChanged( );
			}
			catch ( JSONException e )
			{
				e.printStackTrace( );
			}
			
		}
		
	}
	
	@Override
	public void onResume( )
	{
		super.onResume( );
		// listNearby.clear( );
	}
	
	private void onSelling( )
	{
		SellBookInput sellBookInput = new SellBookInput( );
		sellBookInput.setAuth_token( Constants.AUTHOR_TOKEN );
		sellBookInput.setBookIcon( bitmapToBinary );
		sellBookInput.setBookName( edt_bookName.getText( ).toString( ) );
		sellBookInput.setAuthor( edt_author.getText( ).toString( ) );
		sellBookInput.setEdition( edt_edition.getText( ).toString( ) );
		sellBookInput.setPrice( edt_price.getText( ).toString( ) );
		sellBookInput.setDescript( edt_des.getText( ).toString( ) );
		sellBookInput.setNote( edt_not.getText( ).toString( ) );
		sellBookInput.setBonus( cb_bonus.isChecked( ) );
		sellBookInput.setCondition( spin_question.getSelectedItem( ).toString( ) );
		sellBookInput.setType_post( "sell" );
		// Random r = new Random( );
		// double latitude = r.nextInt( 1001 ) / 1000;
		// double longtitude = r.nextInt( 1001 ) / 1000;
		// sellBookInput.setLatitude( latitude + "" );
		// sellBookInput.setLongtitude( longtitude + "" );
		sellBookInput.setLatitude( ( ( MainActivity ) getActivity( ) ).getLocation( ).getLatitude( ) + "" );
		sellBookInput.setLongtitude( ( ( MainActivity ) getActivity( ) ).getLocation( ).getLongitude( ) + "" );
		SellBookTask sellBookTask = new SellBookTask( getActivity( ), sellBookInput, this );
		sellBookTask.execute( );
	}
}
