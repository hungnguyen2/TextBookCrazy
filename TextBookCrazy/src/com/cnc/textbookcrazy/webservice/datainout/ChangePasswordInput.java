package com.cnc.textbookcrazy.webservice.datainout;

public class ChangePasswordInput
{
	private String	oldPass, newPass, authorToken;
	
	/**
	 * @return the newPass
	 */
	public String getNewPass( )
	{
		return newPass;
	}
	
	/**
	 * @return the oldPass
	 */
	public String getOldPass( )
	{
		return oldPass;
	}
	
	/**
	 * @param newPass
	 *            the newPass to set
	 */
	public void setNewPass( String newPass )
	{
		this.newPass = newPass;
	}
	
	/**
	 * @param oldPass
	 *            the oldPass to set
	 */
	public void setOldPass( String oldPass )
	{
		this.oldPass = oldPass;
	}

	/**
	 * @return the authorToken
	 */
	public String getAuthorToken( )
	{
		return authorToken;
	}

	/**
	 * @param authorToken the authorToken to set
	 */
	public void setAuthorToken( String authorToken )
	{
		this.authorToken = authorToken;
	}
	
}
