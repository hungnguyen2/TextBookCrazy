package com.cnc.textbookcrazy.webservice.datainout;

public class SignupInput
{
	private String	user, password, email, alert, question, answer;
	private String	urlIcon;
	
	public String getAlert( )
	{
		return alert;
	}
	
	public String getAnswer( )
	{
		return answer;
	}
	
	public String getEmail( )
	{
		return email;
	}
	
	public String getPassword( )
	{
		return password;
	}
	
	public String getQuestion( )
	{
		return question;
	}
	
	public String getUrlIcon( )
	{
		return urlIcon;
	}
	
	public String getUser( )
	{
		return user;
	}
	
	public void setAlert( String alert )
	{
		this.alert = alert;
	}
	
	public void setAnswer( String answer )
	{
		this.answer = answer;
	}
	
	public void setEmail( String email )
	{
		this.email = email;
	}
	
	public void setPassword( String password )
	{
		this.password = password;
	}
	
	public void setQuestion( String question )
	{
		this.question = question;
	}
	
	public void setUrlIcon( String urlIcon )
	{
		this.urlIcon = urlIcon;
	}
	
	public void setUser( String user )
	{
		this.user = user;
	}
	
}
