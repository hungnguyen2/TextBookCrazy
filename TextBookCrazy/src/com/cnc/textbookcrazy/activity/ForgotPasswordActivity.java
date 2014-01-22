package com.cnc.textbookcrazy.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.cnc.textbookcrazy.MActivity;
import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.help.ConnectionDetector;
import com.cnc.textbookcrazy.help.MyOkDialog;
import com.cnc.textbookcrazy.help.MyOkDialogListener;
import com.cnc.textbookcrazy.help.MyOkDialogListener.MyOkDiaglogImp;
import com.cnc.textbookcrazy.webservice.datainout.SignupInput;
import com.cnc.textbookcrazy.webservice.task.ForgotPasswordTask;
import com.cnc.textbookcrazy.webservice.task.ForgotPasswordTask.ForgotPasswordTaskImp;

public class ForgotPasswordActivity extends MActivity implements OnClickListener, ForgotPasswordTaskImp
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
	
	private Spinner	spin_question;
	private Button	btn_submit, btn_cancel;
	private EditText	edt_email, edt_answer;
	
	private boolean checkForgotPassword( )
	{
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
		
		if ( checkEmail( edt_email.getText( ).toString( ) ) )
		{
			MyOkDialog.showDialog( getString( R.string.dl_email_format ), this );
			return false;
		}
		
		return true;
	}
	
	@Override
	public void onClick( View v )
	{
		switch ( v.getId( ) )
		{
			case R.id.btn_submit:
				if ( checkForgotPassword( ) )
				{
					if ( ConnectionDetector.isConnectingToInternet( ForgotPasswordActivity.this ) )
					{
						SignupInput signupInput = new SignupInput( );
						signupInput.setEmail( edt_email.getText( ).toString( ) );
						signupInput.setQuestion( spin_question.getSelectedItem( ).toString( ) );
						signupInput.setAnswer( edt_answer.getText( ).toString( ) );
						
						ForgotPasswordTask forgotPasswordTask = new ForgotPasswordTask( ForgotPasswordActivity.this,
								signupInput, ForgotPasswordActivity.this );
						forgotPasswordTask.execute( );
					}
					else
					{
						MyOkDialog.showDialog( getString( R.string.a_connect_internet ), ForgotPasswordActivity.this );
					}
				}
				break;
			case R.id.btn_cancel:
				onBackPressed( );
				break;
			
			default:
				break;
		}
		
	}
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_forgot_pass );
		TextView title = ( TextView ) findViewById( R.id.title );
		title.setText( getResources( ).getString( R.string.forgot_password_title ) );
		
		btn_submit = ( Button ) findViewById( R.id.btn_submit );
		btn_cancel = ( Button ) findViewById( R.id.btn_cancel );
		btn_cancel.setOnClickListener( this );
		btn_submit.setOnClickListener( this );
		
		spin_question = ( Spinner ) findViewById( R.id.tv_question );
		List< String > list = new ArrayList< String >( );
		list.add( "What was your childhood nickname? " );
		list.add( "What time of the day were you born?" );
		list.add( "What is your pet's name?" );
		ArrayAdapter< String > dataAdapter = new ArrayAdapter< String >( this,
				R.layout.spinner_item_right, list );
		dataAdapter.setDropDownViewResource( R.layout.spinner_item_right );
		spin_question.setAdapter( dataAdapter );
	}
	
	@Override
	public void onPostExecute( )
	{
		MyOkDialogListener.showDialog( getString( R.string.get_pass_success ), this, new MyOkDiaglogImp( )
		{
			
			@Override
			public void onPositive( )
			{
				onBackPressed( );
			}
		} );
	}
}
