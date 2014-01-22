package com.cnc.textbookcrazy.model;

public class NearbyRequestModel
{
	private String	icon, bookName, authorName, distance, id, userId;
	
	public String getAuthorName( )
	{
		return authorName;
	}
	
	public String getBookName( )
	{
		return bookName;
	}
	
	public String getDistance( )
	{
		return distance;
	}
	
	public String getIcon( )
	{
		return icon;
	}
	
	/**
	 * @return the id
	 */
	public String getId( )
	{
		return id;
	}
	
	/**
	 * @return the userId
	 */
	public String getUserId( )
	{
		return userId;
	}
	
	public void setAuthorName( String authorName )
	{
		this.authorName = authorName;
	}
	
	public void setBookName( String bookName )
	{
		this.bookName = bookName;
	}
	
	public void setDistance( String distance )
	{
		this.distance = distance;
	}
	
	public void setIcon( String icon )
	{
		this.icon = icon;
	}
	
	/**
	 * @param id
	 *            the id to set
	 */
	public void setId( String id )
	{
		this.id = id;
	}
	
	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId( String userId )
	{
		this.userId = userId;
	}
	
}
