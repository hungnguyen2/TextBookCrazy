package com.cnc.textbookcrazy.webservice.datainout;

public class LoginOutput
{
	private String	auth_token, username, email, user_id, avatar;
	
	public String getAuth_token( )
	{
		return auth_token;
	}
	
	/**
	 * @return the avatar
	 */
	public String getAvatar( )
	{
		return avatar;
	}
	
	public String getEmail( )
	{
		return email;
	}
	
	/**
	 * @return the user_id
	 */
	public String getUser_id( )
	{
		return user_id;
	}
	
	public String getUsername( )
	{
		return username;
	}
	
	public void setAuth_token( String auth_token )
	{
		this.auth_token = auth_token;
	}
	
	/**
	 * @param avatar
	 *            the avatar to set
	 */
	public void setAvatar( String avatar )
	{
		this.avatar = avatar;
	}
	
	public void setEmail( String email )
	{
		this.email = email;
	}
	
	/**
	 * @param user_id
	 *            the user_id to set
	 */
	public void setUser_id( String user_id )
	{
		this.user_id = user_id;
	}
	
	public void setUsername( String username )
	{
		this.username = username;
	}
	
}
