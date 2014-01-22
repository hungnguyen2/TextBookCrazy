package com.cnc.textbookcrazy.model;

public class ListBlockUserModel
{
	private String	avatar, userName, blocked_id;
	
	/**
	 * @return the avatar
	 */
	public String getAvatar( )
	{
		return avatar;
	}
	
	/**
	 * @return the blocked_id
	 */
	public String getBlocked_id( )
	{
		return blocked_id;
	}
	
	/**
	 * @return the userName
	 */
	public String getUserName( )
	{
		return userName;
	}
	
	/**
	 * @param avatar
	 *            the avatar to set
	 */
	public void setAvatar( String avatar )
	{
		this.avatar = avatar;
	}
	
	/**
	 * @param blocked_id
	 *            the blocked_id to set
	 */
	public void setBlocked_id( String blocked_id )
	{
		this.blocked_id = blocked_id;
	}
	
	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName( String userName )
	{
		this.userName = userName;
	}
	
}
