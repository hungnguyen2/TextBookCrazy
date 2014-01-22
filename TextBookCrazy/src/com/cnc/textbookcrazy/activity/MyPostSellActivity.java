package com.cnc.textbookcrazy.activity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.cnc.textbookcrazy.MActivity;
import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.gps.GPS;
import com.cnc.textbookcrazy.gps.IGPSActivity;
import com.cnc.textbookcrazy.help.ConnectionDetector;
import com.cnc.textbookcrazy.help.Constants;
import com.cnc.textbookcrazy.help.MyOkDialog;
import com.cnc.textbookcrazy.help.MyOkDialogListener;
import com.cnc.textbookcrazy.help.MyOkDialogListener.MyOkDiaglogImp;
import com.cnc.textbookcrazy.help.MyYesNoDialog;
import com.cnc.textbookcrazy.help.MyYesNoDialog.MyYesNoImp;
import com.cnc.textbookcrazy.webservice.WebConstant;
import com.cnc.textbookcrazy.webservice.task.CancelPostTask;
import com.cnc.textbookcrazy.webservice.task.CancelPostTask.CancelTaskImp;
import com.cnc.textbookcrazy.webservice.task.CancelPostTask.CancelTaskInput;
import com.cnc.textbookcrazy.webservice.task.DetailPostTask;
import com.cnc.textbookcrazy.webservice.task.DetailPostTask.DetailPostImp;
import com.cnc.textbookcrazy.webservice.task.DetailPostTask.DetailPostInput;
import com.cnc.textbookcrazy.webservice.task.UpdatePostTask;
import com.cnc.textbookcrazy.webservice.task.UpdatePostTask.UpdateBookTaskImp;
import com.cnc.textbookcrazy.webservice.task.UpdatePostTask.UpdatePostInput;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyPostSellActivity extends MActivity implements UpdateBookTaskImp, DetailPostImp, CancelTaskImp,
		IGPSActivity
{
	ViewSwitcher		vsw_mypost_request;
	Button				btn_change;
	private Spinner		spin_question, spin_question_2;
	private CheckBox	cb_attack_prev;
	private int			childLayout			= 0;
	private int			position;
	
	private TextView	tv_bookName, tv_author, tv_edition, tv_note, tv_description, tv_price;
	private EditText	edt_bookName, edt_author, edt_edition, edt_note, edt_des, edt_price;
	private String		id, bookName, author, edition, des, note, price, bonus, condition;
	private CheckBox	checkbox;
	
	private ImageView	icon_book, avatar_2;
	
	private String		bitmapToBinary		= "";
	
	private static int	RESULT_LOAD_IMAGE	= 1;
	
	private String		url_image;
	
	private Location	location;
	
	private GPS			gps;
	
	private boolean checkupdate( )
	{
		if ( edt_bookName.length( ) < 1 )
		{
			MyOkDialog.showDialog( getString( R.string.dl_bookname_needed ), this );
			
			return false;
		}
		if ( edt_author.length( ) < 1 )
		{
			MyOkDialog.showDialog( getString( R.string.dl_author_needed ), this );
			
			return false;
		}
		if ( edt_edition.length( ) < 1 )
		{
			MyOkDialog.showDialog( getString( R.string.dl_edition_needed ), this );
			
			return false;
		}
		return true;
	}
	
	@Override
	public void displayGPSSettingsDialog( )
	{
		LocationManager service = ( LocationManager ) getSystemService( LOCATION_SERVICE );
		boolean enabledGPS = service
				.isProviderEnabled( LocationManager.NETWORK_PROVIDER );
		if ( !enabledGPS )
		{
			Toast.makeText( this, getString( R.string.wifi_gps ), Toast.LENGTH_LONG ).show( );
			Intent intent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
			startActivity( intent );
		}
	}
	
	@Override
	public void locationChanged( double longitude, double latitude )
	{
		if ( location == null )
		{
			location = gps.getLocation( );
		}
		location.setLatitude( latitude );
		location.setLongitude( longitude );
		Log.d( this.getClass( ).getName( ), "location: latitude = " + latitude + ", long= " + longitude );
		
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
			
			Cursor cursor = getContentResolver( ).query( selectedImage,
					filePathColumn, null, null, null );
			cursor.moveToFirst( );
			
			int columnIndex = cursor.getColumnIndex( filePathColumn[ 0 ] );
			String picturePath = cursor.getString( columnIndex );
			cursor.close( );
			
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
			Log.d( "", "compressed = " + compressed );
			byte[] array = os.toByteArray( );
			bitmap = BitmapFactory.decodeByteArray( array, 0, array.length );
			bitmapToBinary = Base64.encodeToString( array, Base64.DEFAULT );
			
			icon_book.setImageBitmap( bitmap );
		}
	}
	
	@Override
	public void onBackPressed( )
	{
		Log.d( "", "child = " + childLayout );
		if ( childLayout == 1 )
		{
			vsw_mypost_request.showPrevious( );
			childLayout = 0;
			if ( ConnectionDetector.isConnectingToInternet( MyPostSellActivity.this ) )
			{
				onGetDetail( );
			}
			else
			{
				MyOkDialog.showDialog( getString( R.string.a_connect_internet ), MyPostSellActivity.this );
			}
		}
		else
		{
			super.onBackPressed( );
		}
	}
	
	private void onCancelPost( )
	{
		CancelTaskInput cancelTaskInput = new CancelTaskInput( );
		cancelTaskInput.setAuth_token( Constants.AUTHOR_TOKEN );
		cancelTaskInput.setId( id );
		
		CancelPostTask cancelPostTask = new CancelPostTask( this, this, cancelTaskInput );
		cancelPostTask.execute( );
	}
	
	@Override
	public void onCancelTaskPostExecute( )
	{
		childLayout = 0;
		onBackPressed( );
		MyOkDialogListener.showDialog( getString( R.string.cancel_success ), this, new MyOkDiaglogImp( )
		{
			
			@Override
			public void onPositive( )
			{
				
			}
		} );
	}
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_mypost_sell );
		
		gps = new GPS( this );
		location = gps.getLocation( );
		
		TextView title = ( TextView ) findViewById( R.id.title );
		title.setText( getResources( ).getString( R.string.mypost_title ) );
		
		tv_bookName = ( TextView ) findViewById( R.id.tv_book_name_2 );
		tv_author = ( TextView ) findViewById( R.id.tv_author_2 );
		tv_edition = ( TextView ) findViewById( R.id.tv_edition_2 );
		tv_price = ( TextView ) findViewById( R.id.tv_price );
		tv_note = ( TextView ) findViewById( R.id.tv_note );
		avatar_2 = ( ImageView ) findViewById( R.id.avatar_2 );
		
		tv_description = ( TextView ) findViewById( R.id.tv_des );
		edt_bookName = ( EditText ) findViewById( R.id.edt_book_name );
		edt_author = ( EditText ) findViewById( R.id.edt_author );
		edt_edition = ( EditText ) findViewById( R.id.edt_edition );
		edt_price = ( EditText ) findViewById( R.id.edt_price );
		edt_note = ( EditText ) findViewById( R.id.edt_note );
		edt_des = ( EditText ) findViewById( R.id.edt_des );
		icon_book = ( ImageView ) findViewById( R.id.icon_book );
		checkbox = ( CheckBox ) findViewById( R.id.checkbox_2 );
		spin_question_2 = ( Spinner ) findViewById( R.id.spin_select_2 );
		cb_attack_prev = ( CheckBox ) findViewById( R.id.checkbox );
		spin_question = ( Spinner ) findViewById( R.id.spin_select );
		
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
		
		Intent t = getIntent( );
		position = t.getIntExtra( "position", -1 );
		url_image = t.getStringExtra( WebConstant.BOOK_ICON );
		id = t.getStringExtra( WebConstant.POST_ID );
		
		vsw_mypost_request = ( ViewSwitcher ) findViewById( R.id.vsw_mypost_request );
		btn_change = ( Button ) findViewById( R.id.btn_change );
		btn_change.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				vsw_mypost_request.showNext( );
				childLayout = 1;
				onSettext( );
			}
		} );
		
		Button btn_back = ( Button ) findViewById( R.id.btn_back );
		btn_back.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				vsw_mypost_request.showPrevious( );
				childLayout = 0;
				if ( ConnectionDetector.isConnectingToInternet( MyPostSellActivity.this ) )
				{
					onGetDetail( );
				}
				else
				{
					MyOkDialog.showDialog( getString( R.string.a_connect_internet ), MyPostSellActivity.this );
				}
			}
		} );
		
		Button btn_cancel = ( Button ) findViewById( R.id.btn_cancel );
		btn_cancel.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				// onCancelPost( );
				showDialogAsk( );
			}
		} );
		
		Button btn_update = ( Button ) findViewById( R.id.btn_submit );
		btn_update.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				if ( checkupdate( ) )
				{
					if ( ConnectionDetector.isConnectingToInternet( MyPostSellActivity.this ) )
					{
						onUpdate( );
					}
					else
					{
						MyOkDialog.showDialog( getString( R.string.a_connect_internet ), MyPostSellActivity.this );
					}
				}
			}
		} );
		
		if ( ConnectionDetector.isConnectingToInternet( MyPostSellActivity.this ) )
		{
			onGetDetail( );
		}
		else
		{
			MyOkDialog.showDialog( getString( R.string.a_connect_internet ), MyPostSellActivity.this );
		}
	}
	
	private void onGetDetail( )
	{
		DetailPostInput detailPostInput = new DetailPostInput( );
		detailPostInput.setAuth_token( Constants.AUTHOR_TOKEN );
		detailPostInput.setId( id );
		
		DetailPostTask detailPostTask = new DetailPostTask( this, detailPostInput, this );
		detailPostTask.execute( );
	}
	
	@Override
	public void onPostExecute( )
	{
		
	}
	
	@Override
	public void onPostExecute( JSONObject jsonObject, JSONArray jsonArray, String userAvatar )
	{
		try
		{
			bookName = jsonObject.getString( WebConstant.BOOK_NAME );
			author = jsonObject.getString( WebConstant.AUTHOR );
			edition = jsonObject.getString( WebConstant.EDITION );
			des = jsonObject.getString( WebConstant.DESCIPTION );
			note = jsonObject.getString( WebConstant.NOTE );
			price = jsonObject.getString( WebConstant.PRICE );
			condition = jsonObject.getString( WebConstant.CONDITION );
			bonus = jsonObject.getString( WebConstant.BONUS );
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		
		if ( !Constants.AVATA.equals( "" ) )
		{
			ImageLoader.getInstance( ).displayImage( Constants.AVATA, avatar_2 );
		}
		tv_bookName.setText( bookName );
		tv_author.setText( author );
		tv_edition.setText( edition );
		tv_note.setText( note );
		tv_description.setText( des );
		tv_price.setText( price );
		
		List< String > list = new ArrayList< String >( );
		list.add( "New" );
		list.add( "Like new" );
		list.add( "Good" );
		list.add( "Acceptable" );
		ArrayAdapter< String > dataAdapter = new ArrayAdapter< String >( this,
				R.layout.spinner_item_left, list );
		dataAdapter.setDropDownViewResource( R.layout.spinner_item_left );
		spin_question.setAdapter( dataAdapter );
		checkbox.setChecked( Boolean.valueOf( bonus ) );
		
		cb_attack_prev.setChecked( Boolean.valueOf( bonus ) );
		cb_attack_prev.setClickable( false );
		spin_question_2.setAdapter( dataAdapter );
		spin_question_2.setClickable( false );
		
		for ( int k = 0; k < list.size( ); k++ )
		{
			if ( list.get( k ).equals( condition ) )
			{
				spin_question_2.setSelection( k );
				spin_question.setSelection( k );
				break;
			}
		}
	}
	
	@Override
	protected void onResume( )
	{
		Log.d( "", "gps running = " + gps.isRunning( ) );
		if ( !gps.isRunning( ) )
			gps.resumeGPS( );
		super.onStart( );
	}
	
	private void onSettext( )
	{
		if ( !url_image.equals( "" ) )
		{
			ImageLoader.getInstance( ).displayImage( url_image, icon_book );
		}
		edt_bookName.setText( tv_bookName.getText( ).toString( ) );
		edt_author.setText( tv_author.getText( ).toString( ) );
		edt_edition.setText( tv_edition.getText( ).toString( ) );
		edt_price.setText( tv_price.getText( ).toString( ) );
		edt_note.setText( tv_note.getText( ).toString( ) );
		edt_des.setText( tv_description.getText( ).toString( ) );
	}
	
	@Override
	protected void onStop( )
	{
		gps.stopGPS( );
		super.onStop( );
	}
	
	private void onUpdate( )
	{
		if ( location == null )
		{
			MyOkDialog.showDialog( getString( R.string.can_not_get_location ), this );
			return;
		}
		
		UpdatePostInput updatePostInput = new UpdatePostInput( );
		updatePostInput.setId( id );
		updatePostInput.setAuth_token( Constants.AUTHOR_TOKEN );
		updatePostInput.setBookIcon( bitmapToBinary );
		updatePostInput.setBookName( edt_bookName.getText( ).toString( ) );
		updatePostInput.setAuthor( edt_author.getText( ).toString( ) );
		updatePostInput.setEdition( edt_edition.getText( ).toString( ) );
		updatePostInput.setPrice( edt_price.getText( ).toString( ) );
		updatePostInput.setDescript( edt_des.getText( ).toString( ) );
		updatePostInput.setNote( edt_note.getText( ).toString( ) );
		updatePostInput.setBonus( checkbox.isChecked( ) );
		updatePostInput.setCondition( spin_question.getSelectedItem( ).toString( ) );
		// Random r = new Random( );
		// double latitude = randomDouble( 20.111111111f, 300.111111111f );
		// double longtitude = randomDouble( 100.111111111f, 110.111111111f );
		// Log.d( "lat", "latitude = " + latitude );
		// Log.d( "longti", "longtitude = " + longtitude );
		// updatePostInput.setLatitude( latitude + "" );
		// updatePostInput.setLongtitude( longtitude + "" );
		
		updatePostInput.setLatitude( location.getLatitude( ) + "" );
		updatePostInput.setLongtitude( location.getLongitude( ) + "" );
		
		// sellBookInput.setLatitude( ( ( MainActivity ) getActivity( ) ).getLocation( ).getLatitude( ) + "" );
		// sellBookInput.setLatitude( ( ( MainActivity ) getActivity( ) ).getLocation( ).getLongitude( ) + "" );
		UpdatePostTask updatePostTask = new UpdatePostTask( this, updatePostInput, this );
		updatePostTask.execute( );
	}
	
	public double randomDouble( double min, double max )
	{
		Random rd = new Random( );
		double divider = 1.1111111111111111111111111111111;
		double num = min + ( rd.nextDouble( ) * ( ( max - min ) * divider ) );
		return num;
	}
	
	private void showDialogAsk( )
	{
		MyYesNoDialog.showDialog( getString( R.string.dl_ask_delete ), this, new MyYesNoImp( )
		{
			
			@Override
			public void onNegative( )
			{
			}
			
			@Override
			public void onPositive( )
			{
				onCancelPost( );
				
			}
		} );
		
	}
}
