package com.cnc.textbookcrazy.model;

public class ChatModel
{
	private String	name, content, avatar;
	
	/**
	 * @return the avatar
	 */
	public String getAvatar( )
	{
		return avatar;
	}
	
	public String getContent( )
	{
		return content;
	}
	
	public String getName( )
	{
		return name;
	}
	
	/**
	 * @param avatar
	 *            the avatar to set
	 */
	public void setAvatar( String avatar )
	{
		this.avatar = avatar;
	}
	
	public void setContent( String content )
	{
		this.content = content;
	}
	
	public void setName( String name )
	{
		this.name = name;
	}
}
