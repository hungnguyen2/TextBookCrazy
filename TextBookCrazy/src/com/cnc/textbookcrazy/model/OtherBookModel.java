package com.cnc.textbookcrazy.model;

public class OtherBookModel
{
	private String	icon, name, postId;
	
	public String getIcon( )
	{
		return icon;
	}
	
	public String getName( )
	{
		return name;
	}
	
	public void setIcon( String icon )
	{
		this.icon = icon;
	}
	
	public void setName( String name )
	{
		this.name = name;
	}

	/**
	 * @return the postId
	 */
	public String getPostId( )
	{
		return postId;
	}

	/**
	 * @param postId the postId to set
	 */
	public void setPostId( String postId )
	{
		this.postId = postId;
	}
	
}
