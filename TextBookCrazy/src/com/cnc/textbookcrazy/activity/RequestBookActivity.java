package com.cnc.textbookcrazy.activity;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
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

import com.cnc.textbookcrazy.MActivity;
import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.gps.GPS;
import com.cnc.textbookcrazy.gps.IGPSActivity;
import com.cnc.textbookcrazy.help.ConnectionDetector;
import com.cnc.textbookcrazy.help.Constants;
import com.cnc.textbookcrazy.help.MyOkDialog;
import com.cnc.textbookcrazy.webservice.task.SellBookTask;
import com.cnc.textbookcrazy.webservice.task.SellBookTask.SellBookInput;
import com.cnc.textbookcrazy.webservice.task.SellBookTask.SellBookTaskImp;

public class RequestBookActivity extends MActivity implements SellBookTaskImp, IGPSActivity
{
	private EditText	edt_bookName, edt_author, edt_edition, edt_des, edt_not;
	
	private String		bitmapToBinary		= "";
	
	private static int	RESULT_LOAD_IMAGE	= 1;
	
	private ImageView	icon_book;
	
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
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_request_book );
		
		gps = new GPS( this );
		location = gps.getFirstLocation( );
		
		edt_bookName = ( EditText ) findViewById( R.id.edt_bookName );
		edt_author = ( EditText ) findViewById( R.id.edt_author );
		edt_edition = ( EditText ) findViewById( R.id.edt_edition );
		edt_des = ( EditText ) findViewById( R.id.edt_des );
		edt_not = ( EditText ) findViewById( R.id.edt_note );
		icon_book = ( ImageView ) findViewById( R.id.icon_book );
		
		TextView title = ( TextView ) findViewById( R.id.title );
		title.setText( getResources( ).getString( R.string.request_title ) );
		
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
		
		Button btn_submit = ( Button ) findViewById( R.id.btn_submit );
		btn_submit.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				if ( checkupdate( ) )
				{
					if ( ConnectionDetector.isConnectingToInternet( RequestBookActivity.this ) )
					{
						onRequest( );
					}
					else
					{
						MyOkDialog.showDialog( getString( R.string.a_connect_internet ), RequestBookActivity.this );
					}
				}
			}
		} );
		
		Button btn_cancel = ( Button ) findViewById( R.id.btn_cancel );
		btn_cancel.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				onBackPressed( );
			}
		} );
	}
	
	@Override
	public void onPostExecute( )
	{
		MyOkDialog.showDialog( getString( R.string.post_success ), this );
	}
	
	private void onRequest( )
	{
		if ( location == null )
		{
			MyOkDialog.showDialog( getString( R.string.can_not_get_location ), this );
			return;
		}
		
		SellBookInput sellBookInput = new SellBookInput( );
		sellBookInput.setAuth_token( Constants.AUTHOR_TOKEN );
		sellBookInput.setBookIcon( bitmapToBinary );
		sellBookInput.setBookName( edt_bookName.getText( ).toString( ) );
		sellBookInput.setAuthor( edt_author.getText( ).toString( ) );
		sellBookInput.setEdition( edt_edition.getText( ).toString( ) );
		sellBookInput.setPrice( "" );
		sellBookInput.setDescript( edt_des.getText( ).toString( ) );
		sellBookInput.setNote( edt_not.getText( ).toString( ) );
		sellBookInput.setBonus( false );
		sellBookInput.setCondition( "" );
		sellBookInput.setType_post( "request" );
		// Random r = new Random( );
		// double latitude = r.nextInt( 1001 ) / 1000;
		// double longtitude = r.nextInt( 1001 ) / 1000;
		// sellBookInput.setLatitude( latitude + "" );
		// sellBookInput.setLongtitude( longtitude + "" );
		
		sellBookInput.setLatitude( location.getLatitude( ) + "" );
		sellBookInput.setLongtitude( location.getLongitude( ) + "" );
		
		// sellBookInput.setLatitude( ( ( MainActivity ) getActivity( ) ).getLocation( ).getLatitude( ) + "" );
		// sellBookInput.setLatitude( ( ( MainActivity ) getActivity( ) ).getLocation( ).getLongitude( ) + "" );
		SellBookTask sellBookTask = new SellBookTask( this, sellBookInput, this );
		sellBookTask.execute( );
	}
	
	@Override
	protected void onResume( )
	{
		Log.d( "", "gps running = " + gps.isRunning( ) );
		if ( !gps.isRunning( ) )
			gps.resumeGPS( );
		super.onStart( );
	}
	
	@Override
	protected void onStop( )
	{
		gps.stopGPS( );
		super.onStop( );
	}
}
