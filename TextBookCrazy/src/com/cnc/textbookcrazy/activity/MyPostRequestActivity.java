package com.cnc.textbookcrazy.activity;

import java.io.ByteArrayOutputStream;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class MyPostRequestActivity extends MActivity implements UpdateBookTaskImp, DetailPostImp, CancelTaskImp,
		IGPSActivity
{
	ViewSwitcher		vsw_mypost_request;
	Button				btn_change;
	private int			childLayout			= 0;
	
	private int			position;
	private TextView	tv_bookName, tv_author, tv_edition, tv_note, tv_description;
	private EditText	edt_bookName, edt_author, edt_edition, edt_note, edt_des;
	
	private String		id, bookName, author, edition, des, note;
	private ImageView	icon_book, avatar_2;
	private String		bitmapToBinary		= "";
	private String		url_image;
	private static int	RESULT_LOAD_IMAGE	= 1;
	private GPS			gps;
	private Location	location;
	
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
			if ( checkupdate( ) )
			{
				if ( ConnectionDetector.isConnectingToInternet( MyPostRequestActivity.this ) )
				{
					onGetDetail( );
				}
				else
				{
					MyOkDialog.showDialog( getString( R.string.a_connect_internet ), MyPostRequestActivity.this );
				}
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
		setContentView( R.layout.activity_mypost_request );
		
		gps = new GPS( this );
		location = gps.getFirstLocation( );
		
		TextView title = ( TextView ) findViewById( R.id.title );
		title.setText( getResources( ).getString( R.string.mypost_title ) );
		
		Intent t = getIntent( );
		position = t.getIntExtra( "position", -1 );
		url_image = t.getStringExtra( WebConstant.BOOK_ICON );
		
		tv_bookName = ( TextView ) findViewById( R.id.tv_book_name_2 );
		tv_author = ( TextView ) findViewById( R.id.tv_author_2 );
		tv_edition = ( TextView ) findViewById( R.id.tv_edition_2 );
		tv_note = ( TextView ) findViewById( R.id.tv_note );
		tv_description = ( TextView ) findViewById( R.id.tv_description );
		avatar_2 = ( ImageView ) findViewById( R.id.avatar_2 );
		
		edt_bookName = ( EditText ) findViewById( R.id.edt_book_name );
		edt_author = ( EditText ) findViewById( R.id.edt_author );
		edt_edition = ( EditText ) findViewById( R.id.edt_edition );
		edt_note = ( EditText ) findViewById( R.id.edt_note );
		edt_des = ( EditText ) findViewById( R.id.edt_des );
		//
		id = t.getStringExtra( WebConstant.POST_ID );
		// bookName = t.getStringExtra( WebConstant.BOOK_NAME );
		// author = t.getStringExtra( WebConstant.AUTHOR );
		// edition = t.getStringExtra( WebConstant.EDITION );
		// des = t.getStringExtra( WebConstant.DESCIPTION );
		// note = t.getStringExtra( WebConstant.NOTE );
		//
		// tv_bookName.setText( bookName );
		// tv_author.setText( author );
		// tv_edition.setText( edition );
		// tv_note.setText( note );
		// tv_description.setText( des );
		
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
		
		icon_book = ( ImageView ) findViewById( R.id.icon_book );
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
		
		Button btn_back = ( Button ) findViewById( R.id.btn_back );
		btn_back.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				Log.d( "", "back" );
				vsw_mypost_request.showPrevious( );
				childLayout = 0;
				
				if ( ConnectionDetector.isConnectingToInternet( MyPostRequestActivity.this ) )
				{
					onGetDetail( );
				}
				else
				{
					MyOkDialog.showDialog( getString( R.string.a_connect_internet ), MyPostRequestActivity.this );
				}
				
			}
		} );
		
		Button btn_cancel = ( Button ) findViewById( R.id.btn_cancel );
		btn_cancel.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				showDialogAsk( );
			}
		} );
		
		Button btn_update = ( Button ) findViewById( R.id.btn_update );
		btn_update.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				if ( checkupdate( ) )
				{
					if ( ConnectionDetector.isConnectingToInternet( MyPostRequestActivity.this ) )
					{
						onUpdate( );
					}
					else
					{
						MyOkDialog.showDialog( getString( R.string.a_connect_internet ), MyPostRequestActivity.this );
					}
				}
			}
		} );
		
		if ( ConnectionDetector.isConnectingToInternet( MyPostRequestActivity.this ) )
		{
			onGetDetail( );
		}
		else
		{
			MyOkDialog.showDialog( getString( R.string.a_connect_internet ), MyPostRequestActivity.this );
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
		MyOkDialog.showDialog( getString( R.string.update_post_success ), this );
		
	}
	
	@Override
	public void onPostExecute( JSONObject jsonObject, JSONArray jsonArray, String avatarUser )
	{
		try
		{
			bookName = jsonObject.getString( WebConstant.BOOK_NAME );
			author = jsonObject.getString( WebConstant.AUTHOR );
			edition = jsonObject.getString( WebConstant.EDITION );
			des = jsonObject.getString( WebConstant.DESCIPTION );
			note = jsonObject.getString( WebConstant.NOTE );
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
		updatePostInput.setPrice( "" );
		updatePostInput.setDescript( edt_des.getText( ).toString( ) );
		updatePostInput.setNote( edt_note.getText( ).toString( ) );
		updatePostInput.setBonus( false );
		updatePostInput.setCondition( "" );
		// Random r = new Random( );
		// double latitude = r.nextInt( 1001 ) / 1000;
		// double longtitude = r.nextInt( 1001 ) / 1000;
		// updatePostInput.setLatitude( latitude + "" );
		// updatePostInput.setLongtitude( longtitude + "" );
		
		updatePostInput.setLatitude( location.getLatitude( ) + "" );
		updatePostInput.setLongtitude( location.getLongitude( ) + "" );
		
		// sellBookInput.setLatitude( ( ( MainActivity ) getActivity( ) ).getLocation( ).getLatitude( ) + "" );
		// sellBookInput.setLatitude( ( ( MainActivity ) getActivity( ) ).getLocation( ).getLongitude( ) + "" );
		UpdatePostTask updatePostTask = new UpdatePostTask( this, updatePostInput, this );
		updatePostTask.execute( );
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
