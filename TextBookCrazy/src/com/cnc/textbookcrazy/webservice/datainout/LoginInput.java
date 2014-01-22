package com.cnc.textbookcrazy.webservice.datainout;

public class LoginInput
{
	private String	login, password, registerId;
	
	public LoginInput( String login, String password, String registerId )
	{
		this.login = login;
		this.password = password;
		this.registerId = registerId;
	}
	
	public String getLogin( )
	{
		return login;
	}
	
	public String getPassword( )
	{
		return password;
	}
	
	/**
	 * @return the registerId
	 */
	public String getRegisterId( )
	{
		return registerId;
	}
	
	public void setLogin( String login )
	{
		this.login = login;
	}
	
	public void setPassword( String password )
	{
		this.password = password;
	}
	
	/**
	 * @param registerId
	 *            the registerId to set
	 */
	public void setRegisterId( String registerId )
	{
		this.registerId = registerId;
	}
	
}
