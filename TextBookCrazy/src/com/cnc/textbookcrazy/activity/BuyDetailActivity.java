package com.cnc.textbookcrazy.activity;

import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.AdapterView.OnItemClickListener;
import it.sephiroth.android.library.widget.HListView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cnc.textbookcrazy.MActivity;
import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.adapter.OtherBookAdapter;
import com.cnc.textbookcrazy.gps.GPS;
import com.cnc.textbookcrazy.gps.IGPSActivity;
import com.cnc.textbookcrazy.help.Common;
import com.cnc.textbookcrazy.help.ConnectionDetector;
import com.cnc.textbookcrazy.help.Constants;
import com.cnc.textbookcrazy.help.MyOkDialog;
import com.cnc.textbookcrazy.model.OtherBookModel;
import com.cnc.textbookcrazy.twitter.TwitterApp;
import com.cnc.textbookcrazy.twitter.TwitterApp.TwDialogListener;
import com.cnc.textbookcrazy.webservice.WebConstant;
import com.cnc.textbookcrazy.webservice.task.DetailPostTask;
import com.cnc.textbookcrazy.webservice.task.DetailPostTask.DetailPostImp;
import com.cnc.textbookcrazy.webservice.task.DetailPostTask.DetailPostInput;
import com.cnc.textbookcrazy.webservice.task.SendChatTask;
import com.cnc.textbookcrazy.webservice.task.SendChatTask.SendChatInput;
import com.cnc.textbookcrazy.webservice.task.SendChatTask.SendChatTaskImp;
import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.nostra13.universalimageloader.core.ImageLoader;

public class BuyDetailActivity extends MActivity implements OnClickListener, DetailPostImp, SendChatTaskImp,
		IGPSActivity
{
	private class SessionStatusCallback implements Session.StatusCallback
	{
		@Override
		public void call( Session session, SessionState state,
				Exception exception )
		{
			if ( exception != null )
			{
				Log.d( "", "section  exception" );
				
				exception.printStackTrace( );
			}
			//
			Session session2 = Session.getActiveSession( );
			
			if ( session2.isOpened( ) )
			{
				Log.d( "", "section  opened" );
				publishFeedDialog( );
			}
			else
			{
				Log.d( "", "section not opened" );
				
				// session.o
				// session.openForRead( new Session.OpenRequest( MainActivity.this ).setPermissions(
				// PERMISSIONS )
				// .setCallback( statusCallback ) );
				// session = Session.openActiveSession(this, true, request);
				//
			}
			// doneLoginFacebook( );
		}
	}
	
	private static final List< String >	PERMISSIONS	= Arrays.asList( "email" );
	
	public static double distFrom( double lat1, double lng1, double lat2, double lng2 )
	{
		double earthRadius = 3958.75;
		double dLat = Math.toRadians( lat2 - lat1 );
		double dLng = Math.toRadians( lng2 - lng1 );
		double a = Math.sin( dLat / 2 ) * Math.sin( dLat / 2 ) +
				Math.cos( Math.toRadians( lat1 ) ) * Math.cos( Math.toRadians( lat2 ) ) *
				Math.sin( dLng / 2 ) * Math.sin( dLng / 2 );
		double c = 2 * Math.atan2( Math.sqrt( a ), Math.sqrt( 1 - a ) );
		double dist = earthRadius * c;
		
		int meterConversion = 1609;
		
		return dist * meterConversion;
	}
	
	private Session.StatusCallback	statusCallback			= new SessionStatusCallback( );
	
	private HListView				hListView1;
	
	private OtherBookAdapter		otherBookAdapter;
	
	private List< OtherBookModel >	listOtherBook			= new ArrayList< OtherBookModel >( );
	private ScrollView				scroll_buy_detail;
	private String					id, user_id;
	private TextView				tv_bookName, tv_author, tv_edition, tv_note, tv_description, tv_price,
									tv_condition, tv_distance;
	private LinearLayout			linear_bonus;
	private EditText				edt_message;
	private ImageView				avatar_2, icon_book;
	private GPS						gps;
	
	private Location				location;
	
	private DecimalFormat			decimalFormat			= new DecimalFormat( "#0.0" );
	private final TwDialogListener	mTwLoginDialogListener	= new TwDialogListener( )
															{
																@Override
																public void onComplete( String value )
																{
																	Intent intent = new Intent(
																			BuyDetailActivity.this,
																			PostTwActivity.class );
																	int mode1 = 1;
																	String txt = BuyDetailActivity.this
																			.getString( R.string.url_share_tw );
																	intent.putExtra( "txt1", txt );
																	intent.putExtra( "mode1", mode1 );
																	BuyDetailActivity.this
																			.startActivity( intent );
																}
																
																@Override
																public void onError( String value )
																{
																	Toast.makeText( BuyDetailActivity.this,
																			"error",
																			Toast.LENGTH_LONG )
																			.show( );
																	Log.d( "error", "can't show dialog share tw" );
																}
															};
	
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
	
	public void facebookLogin( )
	{
		// Login facebook using session
		// Session session = Session.getActiveSession( );
		
		// if ( session == null )
		// {
		Log.d( "", "section = null" );
		Session session = new Session( this );
		// }
		// if ( !session.isOpened( ) && !session.isClosed( ) )
		// {
		// session.openForRead( new Session.OpenRequest( this ).setCallback( statusCallback ) );
		session.openForRead( new Session.OpenRequest( this ).setPermissions(
				PERMISSIONS ).setLoginBehavior( SessionLoginBehavior.SUPPRESS_SSO )
				.setCallback( statusCallback ) );
		// }
		// else
		// {
		// Session.openActiveSession( this, true, statusCallback );
		// }
		Session.setActiveSession( session );
		
	}
	
	@Override
	public void locationChanged( double longitude, double latitude )
	{
		if ( location == null )
			location = gps.getLocation( );
		location.setLatitude( latitude );
		location.setLongitude( longitude );
	}
	
	@Override
	public void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		super.onActivityResult( requestCode, resultCode, data );
		Session.getActiveSession( )
				.onActivityResult( this, requestCode, resultCode, data );
	}
	
	@Override
	public void onClick( View v )
	{
		switch ( v.getId( ) )
		{
			case R.id.btn_message_2:
				showDialogMessage( );
				break;
			
			default:
				break;
		}
		
	}
	
	@SuppressLint( "NewApi" )
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_detail_buy );
		
		gps = new GPS( this );
		location = gps.getFirstLocation( );
		
		if ( Build.VERSION.SDK_INT > 9 )
		{
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder( ).permitAll( ).build( );
			StrictMode.setThreadPolicy( policy );
		}
		
		TextView title = ( TextView ) findViewById( R.id.title );
		title.setText( getResources( ).getString( R.string.detail_title ) );
		
		tv_bookName = ( TextView ) findViewById( R.id.tv_book_name_2 );
		tv_author = ( TextView ) findViewById( R.id.tv_author_2 );
		tv_edition = ( TextView ) findViewById( R.id.tv_edition_2 );
		tv_price = ( TextView ) findViewById( R.id.tv_price );
		tv_note = ( TextView ) findViewById( R.id.tv_note );
		tv_description = ( TextView ) findViewById( R.id.tv_des );
		tv_condition = ( TextView ) findViewById( R.id.tv_condition );
		tv_distance = ( TextView ) findViewById( R.id.tv_distance );
		linear_bonus = ( LinearLayout ) findViewById( R.id.bonus );
		avatar_2 = ( ImageView ) findViewById( R.id.avatar_2 );
		icon_book = ( ImageView ) findViewById( R.id.icon_book );
		
		Intent t = getIntent( );
		id = t.getStringExtra( WebConstant.POST_ID );
		
		ImageView btn_message_2 = ( ImageView ) findViewById( R.id.btn_message_2 );
		btn_message_2.setOnClickListener( this );
		scroll_buy_detail = ( ScrollView ) findViewById( R.id.scroll_book_detail );
		
		hListView1 = ( HListView ) findViewById( R.id.hListView1 );
		otherBookAdapter = new OtherBookAdapter( this, listOtherBook );
		hListView1.setAdapter( otherBookAdapter );
		
		hListView1.setOnTouchListener( new OnTouchListener( )
		{
			
			@Override
			public boolean onTouch( View v, MotionEvent event )
			{
				v.getParent( ).requestDisallowInterceptTouchEvent( true );
				return false;
			}
		} );
		
		scroll_buy_detail.setOnTouchListener( new OnTouchListener( )
		{
			
			@Override
			public boolean onTouch( View v, MotionEvent event )
			{
				hListView1.getParent( ).requestDisallowInterceptTouchEvent( false );
				return false;
			}
		} );
		
		hListView1.setOnItemClickListener( new OnItemClickListener( )
		{
			
			@Override
			public void onItemClick( AdapterView< ? > parent, View view, int position, long id2 )
			{
				id = listOtherBook.get( position ).getPostId( );
				onGetDetail( );
			}
		} );
		
		if ( ConnectionDetector.isConnectingToInternet( this ) )
		{
			onGetDetail( );
		}
		else
		{
			MyOkDialog.showDialog( getString( R.string.a_connect_internet ), this );
		}
		
		ImageView btn_twitter = ( ImageView ) findViewById( R.id.btn_twitter );
		btn_twitter.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				twitterLogin( );
			}
		} );
		
		ImageView btn_facebook = ( ImageView ) findViewById( R.id.btn_facebook );
		btn_facebook.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				facebookLogin( );
			}
		} );
		
		LinearLayout btn_refresh = ( LinearLayout ) findViewById( R.id.btn_refresh );
		btn_refresh.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				onGetDetail( );
			}
		} );
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
	public void onPostExecute( JSONObject jsonObject, JSONArray jOtherBook, String avatar )
	{
		String bookName = "";
		String author = "";
		String edition = "";
		String des = "";
		String note = "";
		String price = "";
		String condition = "";
		String bonus = "";
		String latitude = "";
		String longtitude = "";
		String bookIcon = "";
		try
		{
			bookIcon = jsonObject.getString( WebConstant.BOOK_ICON );
			bookName = jsonObject.getString( WebConstant.BOOK_NAME );
			author = jsonObject.getString( WebConstant.AUTHOR );
			edition = jsonObject.getString( WebConstant.EDITION );
			des = jsonObject.getString( WebConstant.DESCIPTION );
			note = jsonObject.getString( WebConstant.NOTE );
			price = jsonObject.getString( WebConstant.PRICE );
			condition = jsonObject.getString( WebConstant.CONDITION );
			bonus = jsonObject.getString( WebConstant.BONUS );
			latitude = jsonObject.getString( WebConstant.LATITUDE );
			longtitude = jsonObject.getString( WebConstant.LONGTITUDE );
			
			user_id = jsonObject.getString( WebConstant.USER_ID );
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		
		if ( !latitude.equals( "null" ) && !longtitude.equals( "null" ) )
		{
			Double dis = distFrom( Double.parseDouble( latitude ), Double.parseDouble( longtitude ),
					location.getLatitude( ),
					location.getLongitude( ) );
			tv_distance.setText( decimalFormat.format( dis / 1000 ) + "Km" );
		}
		tv_bookName.setText( bookName );
		tv_author.setText( author );
		tv_edition.setText( edition );
		tv_note.setText( note );
		tv_description.setText( des );
		tv_price.setText( price );
		tv_condition.setText( condition );
		
		if ( bonus.equals( "true" ) )
		{
			linear_bonus.setVisibility( View.VISIBLE );
		}
		if ( !avatar.equals( "" ) )
		{
			ImageLoader.getInstance( ).displayImage( avatar, avatar_2 );
		}
		if ( !bookIcon.equals( "" ) )
		{
			ImageLoader.getInstance( ).displayImage( bookIcon, icon_book );
		}
		else
		{
			icon_book.setImageResource( R.drawable.icon_book );
		}
		
		try
		{
			listOtherBook.clear( );
			
			for ( int k = 0; k < jOtherBook.length( ); k++ )
			{
				String post_id = "", name = "", url = "";
				JSONObject jsonObject2 = jOtherBook.getJSONObject( k );
				post_id = jsonObject2.getString( WebConstant.POST_ID );
				name = jsonObject2.getString( WebConstant.BOOK_NAME );
				url = jsonObject2.getString( WebConstant.BOOK_ICON );
				Log.d( "", "name=" + name );
				OtherBookModel otherBookModel = new OtherBookModel( );
				otherBookModel.setIcon( url );
				otherBookModel.setName( name );
				otherBookModel.setPostId( post_id );
				listOtherBook.add( otherBookModel );
				otherBookAdapter.notifyDataSetChanged( );
			}
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
	}
	
	@Override
	public void onPostExecuteFailed( JSONObject jsonObject )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onResume( )
	{
		// TODO Auto-generated method stub
		super.onResume( );
		if ( !gps.isRunning( ) )
			gps.resumeGPS( );
		
	}
	
	private void onSendChat( )
	{
		SendChatInput sendChatInput = new SendChatInput( );
		sendChatInput.setAuth_token( Constants.AUTHOR_TOKEN );
		sendChatInput.setContent( edt_message.getText( ).toString( ) );
		sendChatInput.setPostId( id );
		sendChatInput.setUserReceive( user_id );
		
		SendChatTask sendChatTask = new SendChatTask( this, sendChatInput, this );
		sendChatTask.execute( );
	}
	
	@Override
	public void onSendChatPostExecute( JSONObject jsonObject )
	{
		MyOkDialog.showDialog( getString( R.string.send_chat_success ), this );
		
	}
	
	@Override
	protected void onStop( )
	{
		gps.stopGPS( );
		super.onStop( );
	}
	
	private void publishFeedDialog( )
	{
		Bundle params = new Bundle( );
		params.putString( "name", this.getString( R.string.app_name ) );
		params.putString( "caption", this.getString( R.string.company ) );
		params.putString( "description", this.getString( R.string.text_facebook ) );
		params.putString( "link", "https://play.google.com/store/apps/details?id=com.cnc.textbookcrazy" );
		params.putString( "picture", "http://s11.postimg.org/ep9x0kjkz/Icon_Text_Book_Crazy_512.png" );
		
		WebDialog feedDialog = ( new WebDialog.FeedDialogBuilder( this,
				Session.getActiveSession( ), params ) ).setOnCompleteListener(
				new OnCompleteListener( )
				{
					
					@Override
					public void onComplete( Bundle values,
							FacebookException error )
					{
						if ( error == null )
						{
							// When the story is posted, echo the success
							// and the post Id.
							final String postId = values.getString( "post_id" );
							if ( postId != null )
							{
								Toast.makeText( BuyDetailActivity.this,
										BuyDetailActivity.this.getString( R.string.share_fb_succes ),
										Toast.LENGTH_SHORT ).show( );
							}
							else
							{
								Toast.makeText(
										BuyDetailActivity.this.getApplicationContext( ),
										BuyDetailActivity.this.getString( R.string.share_fb_error ),
										Toast.LENGTH_SHORT ).show( );
							}
						}
					}
					
				} ).build( );
		feedDialog.show( );
	}
	
	private void showDialogMessage( )
	{
		final Dialog dialog = new Dialog( this );
		dialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
		dialog.setContentView( R.layout.dialog_send_message );
		
		edt_message = ( EditText ) dialog.findViewById( R.id.edt_message );
		
		Button btn_submit = ( Button ) dialog.findViewById( R.id.btn_submit );
		btn_submit.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				if ( edt_message.length( ) < 1 )
				{
					MyOkDialog.showDialog( BuyDetailActivity.this.getString( R.string.type_message ),
							BuyDetailActivity.this );
					return;
				}
				onSendChat( );
				dialog.dismiss( );
				
			}
		} );
		
		Button btn_cancel = ( Button ) dialog.findViewById( R.id.btn_cancel );
		btn_cancel.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				dialog.dismiss( );
			}
		} );
		
		dialog.show( );
	}
	
	private void twitterLogin( )
	{
		Common.twitter = new TwitterApp( this, Constants.CONSUMER_KEY,
				Constants.CONSUMER_SECRETE );
		Common.twitter.setListener( mTwLoginDialogListener );
		Common.twitter.authorize( );
	}
}
