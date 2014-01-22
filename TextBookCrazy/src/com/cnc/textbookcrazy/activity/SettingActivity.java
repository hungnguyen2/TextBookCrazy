package com.cnc.textbookcrazy.activity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.cnc.textbookcrazy.MActivity;
import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.help.ConnectionDetector;
import com.cnc.textbookcrazy.help.Constants;
import com.cnc.textbookcrazy.help.MyOkDialog;
import com.cnc.textbookcrazy.help.MyYesNoDialog;
import com.cnc.textbookcrazy.help.MyYesNoDialog.MyYesNoImp;
import com.cnc.textbookcrazy.webservice.URL;
import com.cnc.textbookcrazy.webservice.WebConstant;
import com.cnc.textbookcrazy.webservice.task.GetInfoUserTask;
import com.cnc.textbookcrazy.webservice.task.GetInfoUserTask.GetInfoUserInput;
import com.cnc.textbookcrazy.webservice.task.GetInfoUserTask.GetInfoUserTaskImp;
import com.cnc.textbookcrazy.webservice.task.SettingsTask;
import com.cnc.textbookcrazy.webservice.task.SettingsTask.SettingsInput;
import com.cnc.textbookcrazy.webservice.task.SettingsTask.SettingsTaskImp;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SettingActivity extends MActivity implements SettingsTaskImp, GetInfoUserTaskImp
{
	private RadioButton	radio_distance;
	private RadioGroup	radioGroup_distance;
	private String		distance			= "5";
	private Spinner		spin_question;
	private ImageView	avatar_2;
	private String		bitmapToBinary;
	private static int	RESULT_LOAD_IMAGE	= 1;
	
	private EditText	edt_username, edt_answer;
	private TextView	tv_email;
	
	private CheckBox	cb_alert;
	
	List< String >		list;
	
	private void getInfo( )
	{
		GetInfoUserInput getInfoUserInput = new GetInfoUserInput( );
		getInfoUserInput.setAuth_token( Constants.AUTHOR_TOKEN );
		
		GetInfoUserTask getInfoUserTask = new GetInfoUserTask( this, getInfoUserInput, this );
		getInfoUserTask.execute( );
		
	}
	
	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		super.onActivityResult( requestCode, resultCode, data );
		
		if ( requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data )
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
			BitmapFactory.Options options = new BitmapFactory.Options( );
			options.inSampleSize = 2;
			try
			{
				bitmap = BitmapFactory.decodeFile( picturePath );
			}
			catch ( OutOfMemoryError oex )
			{
				oex.printStackTrace( );
				try
				{
					bitmap = BitmapFactory.decodeFile( picturePath, options );
				}
				catch ( OutOfMemoryError oex2 )
				{
					oex2.printStackTrace( );
				}
			}
			ByteArrayOutputStream os = new ByteArrayOutputStream( );
			Boolean compressed = bitmap.compress( Bitmap.CompressFormat.JPEG, 75, os );
			Log.d( "", "compressed = " + compressed );
			byte[] array = os.toByteArray( );
			bitmap = BitmapFactory.decodeByteArray( array, 0, array.length );
			bitmapToBinary = Base64.encodeToString( array, Base64.DEFAULT );
			
			avatar_2.setImageBitmap( bitmap );
		}
	}
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_setting );
		
		TextView title = ( TextView ) findViewById( R.id.title );
		title.setText( getResources( ).getString( R.string.setting_title ) );
		
		radioGroup_distance = ( RadioGroup ) findViewById( R.id.radio_distance );
		edt_username = ( EditText ) findViewById( R.id.edt_username );
		edt_answer = ( EditText ) findViewById( R.id.edt_answer );
		cb_alert = ( CheckBox ) findViewById( R.id.cb_email );
		tv_email = ( TextView ) findViewById( R.id.tv_email );
		
		avatar_2 = ( ImageView ) findViewById( R.id.avatar_2 );
		if ( !Constants.AVATA.equals( "" ) )
		{
			ImageLoader.getInstance( ).displayImage( Constants.AVATA, avatar_2 );
		}
		avatar_2.setOnClickListener( new OnClickListener( )
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
		
		spin_question = ( Spinner ) findViewById( R.id.spinner_question );
		list = new ArrayList< String >( );
		list.add( "What was your childhood nickname? " );
		list.add( "What time of the day were you born?" );
		list.add( "What is your pet's name?" );
		ArrayAdapter< String > dataAdapter = new ArrayAdapter< String >( this,
				R.layout.spinner_item_left, list );
		dataAdapter.setDropDownViewResource( R.layout.spinner_item_left );
		spin_question.setAdapter( dataAdapter );
		
		Button btn_change_pass = ( Button ) findViewById( R.id.btn_change_pass );
		btn_change_pass.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				Intent t = new Intent( SettingActivity.this, ChangePasswordActivity.class );
				startActivity( t );
			}
		} );
		
		radioGroup_distance.setOnCheckedChangeListener( new OnCheckedChangeListener( )
		{
			
			@Override
			public void onCheckedChanged( RadioGroup group, int checkedId )
			{
				radio_distance = ( RadioButton ) findViewById( checkedId );
				distance = radio_distance.getText( ).toString( );
				// Toast.makeText( SettingActivity.this,
				// distance, Toast.LENGTH_SHORT ).show( );
			}
		} );
		
		Button btn_submit = ( Button ) findViewById( R.id.btn_submit );
		btn_submit.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				onUpdate( );
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
		
		Button btn_logout = ( Button ) findViewById( R.id.btn_logout );
		btn_logout.setOnClickListener( new OnClickListener( )
		{
			@Override
			public void onClick( View v )
			{
				MyYesNoDialog.showDialog( "Do you want to logout app?", SettingActivity.this, new MyYesNoImp( )
				{
					@Override
					public void onNegative( )
					{
						
					}
					
					@Override
					public void onPositive( )
					{
						SharedPreferences settings2 = getSharedPreferences( Constants.PREF_USER, 0 );
						SharedPreferences.Editor editor2 = settings2.edit( );
						editor2.clear( );
						editor2.commit( );
						
						finish( );
						Intent t = new Intent( SettingActivity.this, SigninActivity.class );
						startActivity( t );
					}
				} );
			}
		} );
		
		if ( ConnectionDetector.isConnectingToInternet( this ) )
		{
			GetInfoUserInput getInfoUserInput = new GetInfoUserInput( );
			getInfoUserInput.setAuth_token( Constants.AUTHOR_TOKEN );
			GetInfoUserTask getInfoUserTask = new GetInfoUserTask( this, getInfoUserInput, this );
			getInfoUserTask.execute( );
		}
		else
		{
			MyOkDialog.showDialog( getString( R.string.a_connect_internet ), this );
		}
		
	}
	
	@Override
	public void onPostExecute( JSONObject jsonObject )
	{
		try
		{
			edt_username.setText( jsonObject.getString( WebConstant.USER_NAME ) );
			edt_answer.setText( jsonObject.getString( WebConstant.ANSWER ) );
			cb_alert.setChecked( Boolean.valueOf( jsonObject.getString( WebConstant.ALERT ) ) );
			tv_email.setText( jsonObject.getString( WebConstant.EMAIL ) );
			
			String question = jsonObject.getString( WebConstant.QUESTION );
			for ( int k = 0; k < list.size( ); k++ )
			{
				if ( question.equals( list.get( k ) ) )
				{
					spin_question.setSelection( k );
					break;
				}
			}
			
			String distance = jsonObject.getString( WebConstant.DISTANCE );
			if ( !distance.equals( "null" ) )
			{
				switch ( Integer.parseInt( distance ) )
				{
					case 5:
						radioGroup_distance.check( R.id.rad_5 );
						break;
					case 10:
						radioGroup_distance.check( R.id.rad_10 );
						break;
					case 15:
						radioGroup_distance.check( R.id.rad_15 );
						break;
					default:
						break;
				}
			}
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
	}
	
	@Override
	public void onPostExecuteSetting( JSONObject jsonObject )
	{
		try
		{
			JSONObject jsonObject2 = jsonObject.getJSONObject( WebConstant.DATA );
			Constants.AVATA = URL.IP + jsonObject2.getString( WebConstant.AVATAR_URL );
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		MyOkDialog.showDialog( getString( R.string.update_success ), this );
	}
	
	private void onUpdate( )
	{
		SettingsInput settingsInput = new SettingsInput( );
		settingsInput.setAvata( bitmapToBinary );
		settingsInput.setUsername( edt_username.getText( ).toString( ) );
		settingsInput.setAlert_email( cb_alert.isChecked( ) + "" );
		settingsInput.setQuestion( spin_question.getSelectedItem( ).toString( ) );
		settingsInput.setAnswer( edt_answer.getText( ).toString( ) );
		settingsInput.setAuth_token( Constants.AUTHOR_TOKEN );
		settingsInput.setDistance( distance );
		
		SettingsTask settingsTask = new SettingsTask( this, settingsInput, this );
		settingsTask.execute( );
	}
}
