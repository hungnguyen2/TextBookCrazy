package com.cnc.textbookcrazy.activity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
import android.widget.Spinner;
import android.widget.TextView;

import com.cnc.textbookcrazy.App;
import com.cnc.textbookcrazy.MActivity;
import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.help.ConnectionDetector;
import com.cnc.textbookcrazy.help.Constants;
import com.cnc.textbookcrazy.help.MyOkDialog;
import com.cnc.textbookcrazy.help.MyYesNoDialog;
import com.cnc.textbookcrazy.help.MyYesNoDialog.MyYesNoImp;
import com.cnc.textbookcrazy.webservice.URL;
import com.cnc.textbookcrazy.webservice.datainout.LoginInput;
import com.cnc.textbookcrazy.webservice.datainout.LoginOutput;
import com.cnc.textbookcrazy.webservice.datainout.SignupInput;
import com.cnc.textbookcrazy.webservice.datainout.SignupOutput;
import com.cnc.textbookcrazy.webservice.task.LoginTask.LoginTaskImp;
import com.cnc.textbookcrazy.webservice.task.SignupTask;
import com.cnc.textbookcrazy.webservice.task.SignupTask.SignupTaskImp;

public class SignupActivity extends MActivity implements OnClickListener, SignupTaskImp
{
	public static final Pattern	EMAIL_ADDRESS_PATTERN	= Pattern
																.compile( "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}"
																		+ "\\@"
																		+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}"
																		+ "("
																		+ "\\."
																		+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}"
																		+ ")+" );
	
	public static boolean checkEmail( String email )
	{
		return EMAIL_ADDRESS_PATTERN.matcher( email ).matches( );
	}
	
	private EditText	edt_username, edt_password, edt_email, edt_answer;
	private CheckBox	cb_email;
	
	private Spinner		spin_question;
	private ImageView	avatar_2;
	private String		bitmapToBinary		= "";
	
	private static int	RESULT_LOAD_IMAGE	= 1;
	
	private boolean checkSignup( )
	{
		if ( edt_username.length( ) < 1 )
		{
			MyOkDialog.showDialog( getString( R.string.dl_name_needed ), this );
			return false;
		}
		
		if ( edt_password.length( ) < 1 )
		{
			MyOkDialog.showDialog( getString( R.string.dl_password_needed ), this );
			return false;
		}
		
		if ( edt_password.length( ) < 8 )
		{
			MyOkDialog.showDialog( getString( R.string.dl_password_character ), this );
			return false;
		}
		
		if ( edt_email.length( ) < 1 )
		{
			MyOkDialog.showDialog( getString( R.string.dl_email_needed ), this );
			return false;
		}
		
		if ( edt_answer.length( ) < 1 )
		{
			MyOkDialog.showDialog( getString( R.string.dl_answer_needed ), this );
			return false;
		}
		
		if ( !checkEmail( edt_email.getText( ).toString( ) ) )
		{
			MyOkDialog.showDialog( getString( R.string.dl_email_format ), this );
			return false;
		}
		
		return true;
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
			
			avatar_2.setImageBitmap( bitmap );
		}
	}
	
	@Override
	public void onClick( View v )
	{
		switch ( v.getId( ) )
		{
			case R.id.tv_question:
				break;
			case R.id.avatar_2:
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
				
				startActivityForResult( i, RESULT_LOAD_IMAGE );
				break;
			case R.id.btn_signup:
				onSignup( );
				break;
			default:
				break;
		}
		
	}
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		// TODO Auto-generated method stub
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_signup );
		
		TextView title = ( TextView ) findViewById( R.id.title );
		title.setText( getResources( ).getString( R.string.signup_title ) );
		
		edt_username = ( EditText ) findViewById( R.id.edt_username );
		edt_password = ( EditText ) findViewById( R.id.edt_password );
		edt_email = ( EditText ) findViewById( R.id.edt_email );
		edt_answer = ( EditText ) findViewById( R.id.edt_answer );
		cb_email = ( CheckBox ) findViewById( R.id.cb_email );
		
		Button btn_signup = ( Button ) findViewById( R.id.btn_signup );
		btn_signup.setOnClickListener( this );
		
		avatar_2 = ( ImageView ) findViewById( R.id.avatar_2 );
		avatar_2.setOnClickListener( this );
		
		spin_question = ( Spinner ) findViewById( R.id.tv_question );
		List< String > list = new ArrayList< String >( );
		list.add( "What was your childhood nickname? " );
		list.add( "What time of the day were you born?" );
		list.add( "What is your pet's name?" );
		ArrayAdapter< String > dataAdapter = new ArrayAdapter< String >( this,
				R.layout.spinner_item_left, list );
		dataAdapter.setDropDownViewResource( R.layout.spinner_item_right );
		spin_question.setAdapter( dataAdapter );
	}
	
	@Override
	protected void onDestroy( )
	{
		App.signupActivity = null;
		super.onDestroy( );
	}
	
	@Override
	public void onPostExecute( SignupOutput signupOutput )
	{
		MyYesNoImp myYesNoImp = new MyYesNoImp( )
		{
			
			@Override
			public void onNegative( )
			{
				onBackPressed( );
			}
			
			@Override
			public void onPositive( )
			{
				signIn( );
			}
		};
		
		MyYesNoDialog.showDialog( getString( R.string.signup_success ), SignupActivity.this, myYesNoImp );
	}
	
	private void onSignup( )
	{
		if ( checkSignup( ) )
		{
			if ( ConnectionDetector.isConnectingToInternet( SignupActivity.this ) )
			{
				SignupInput signupInput = new SignupInput( );
				signupInput.setUrlIcon( bitmapToBinary );
				signupInput.setUser( edt_username.getText( ).toString( ) );
				signupInput.setEmail( edt_email.getText( ).toString( ) );
				signupInput.setPassword( edt_password.getText( ).toString( ) );
				signupInput.setEmail( edt_email.getText( ).toString( ) );
				if ( cb_email.isChecked( ) )
				{
					signupInput.setAlert( "true" );
				}
				else
				{
					signupInput.setAlert( "false" );
				}
				signupInput.setQuestion( spin_question.getSelectedItem( ).toString( ) );
				signupInput.setAnswer( edt_answer.getText( ).toString( ) );
				SignupTask signupTask = new SignupTask( SignupActivity.this, signupInput, SignupActivity.this );
				signupTask.execute( );
			}
			else
			{
				MyOkDialog.showDialog( getString( R.string.a_connect_internet ), SignupActivity.this );
			}
		}
	}
	
	@Override
	protected void onStart( )
	{
		// TODO Auto-generated method stub
		super.onStart( );
		App.signupActivity = this;
		
	}
	
	private void signIn( )
	{
		LoginInput loginInput = new LoginInput( edt_email.getText( ).toString( ), edt_password.getText( ).toString( ),
				Constants.GCM_ID );
		
		LoginTaskImp loginTaskImpl = new LoginTaskImp( )
		{
			
			@Override
			public void onPostExecute( LoginOutput loginOutput )
			{
				Constants.AUTHOR_TOKEN = loginOutput.getAuth_token( );
				Constants.USER_NAME = loginOutput.getUsername( );
				Constants.EMAIL = loginOutput.getEmail( );
				Constants.USER_ID = loginOutput.getUser_id( );
				Constants.AVATA = URL.IP + loginOutput.getAvatar( );
				
				SharedPreferences settings2 = getSharedPreferences( Constants.PREF_USER, 0 );
				SharedPreferences.Editor editor2 = settings2.edit( );
				editor2.putString( "author_token", Constants.AUTHOR_TOKEN );
				editor2.putString( "user_name", Constants.USER_NAME );
				editor2.putString( "email", Constants.EMAIL );
				editor2.putString( "user_id", Constants.USER_ID );
				editor2.putString( "avatar", Constants.AVATA );
				editor2.commit( );
				
				Intent t = new Intent( SignupActivity.this, MainActivity.class );
				startActivity( t );
				finish( );
			}
		};
		com.cnc.textbookcrazy.webservice.task.LoginTask loginTask = new com.cnc.textbookcrazy.webservice.task.LoginTask(
				this, loginInput, loginTaskImpl );
		loginTask.execute( );
	}
}
