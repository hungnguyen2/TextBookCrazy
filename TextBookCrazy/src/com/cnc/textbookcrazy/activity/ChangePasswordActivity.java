package com.cnc.textbookcrazy.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cnc.textbookcrazy.MActivity;
import com.cnc.textbookcrazy.R;
import com.cnc.textbookcrazy.help.ConnectionDetector;
import com.cnc.textbookcrazy.help.Constants;
import com.cnc.textbookcrazy.help.MyOkDialog;
import com.cnc.textbookcrazy.help.MyOkDialogListener;
import com.cnc.textbookcrazy.help.MyOkDialogListener.MyOkDiaglogImp;
import com.cnc.textbookcrazy.webservice.datainout.ChangePasswordInput;
import com.cnc.textbookcrazy.webservice.task.ChangePasswordTask;
import com.cnc.textbookcrazy.webservice.task.ChangePasswordTask.ChangePasswordTaskImp;

public class ChangePasswordActivity extends MActivity implements OnClickListener, ChangePasswordTaskImp
{
	private EditText	edt_oldPass, edt_newPass, edt_confirmPass;
	
	private boolean check( )
	{
		if ( edt_oldPass.length( ) < 1 || edt_newPass.length( ) < 1 || edt_confirmPass.length( ) < 1 )
		{
			MyOkDialog.showDialog( getString( R.string.type_full ), this );
			return false;
		}
		
		if ( !edt_newPass.getText( ).toString( ).equals( edt_confirmPass.getText( ).toString( ) ) )
		{
			MyOkDialog.showDialog( getString( R.string.password_not_confirm ), this );
			return false;
		}
		return true;
	}
	
	@Override
	public void onClick( View v )
	{
		switch ( v.getId( ) )
		{
			case R.id.btn_update:
				if ( check( ) )
				{
					if ( ConnectionDetector.isConnectingToInternet( ChangePasswordActivity.this ) )
					{
						ChangePasswordInput changePasswordInput = new ChangePasswordInput( );
						changePasswordInput.setNewPass( edt_newPass.getText( ).toString( ) );
						changePasswordInput.setOldPass( edt_oldPass.getText( ).toString( ) );
						changePasswordInput.setAuthorToken( Constants.AUTHOR_TOKEN );
						
						ChangePasswordTask changePasswordTask = new ChangePasswordTask( ChangePasswordActivity.this,
								ChangePasswordActivity.this, changePasswordInput );
						changePasswordTask.execute( );
					}
					else
					{
						MyOkDialog.showDialog( getString( R.string.a_connect_internet ), ChangePasswordActivity.this );
					}
				}
				// onBackPressed( );
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
		setContentView( R.layout.activity_change_password );
		
		TextView title = ( TextView ) findViewById( R.id.title );
		title.setText( getResources( ).getString( R.string.change_pass ) );
		
		edt_oldPass = ( EditText ) findViewById( R.id.edt_old_pass );
		edt_newPass = ( EditText ) findViewById( R.id.edt_new_pass );
		edt_confirmPass = ( EditText ) findViewById( R.id.edt_new_pass_2 );
		
		//
		Button btn_update = ( Button ) findViewById( R.id.btn_update );
		Button btn_cancel = ( Button ) findViewById( R.id.btn_cancel );
		btn_update.setOnClickListener( this );
		btn_cancel.setOnClickListener( this );
		
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
