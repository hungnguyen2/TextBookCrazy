package com.cnc.textbookcrazy.model;

public class MessageModel
{
	private String	name, time, content, postId, user2Id, type_post, receiveRead;
	
	public String getContent( )
	{
		return content;
	}
	
	public String getName( )
	{
		return name;
	}
	
	/**
	 * @return the postId
	 */
	public String getPostId( )
	{
		return postId;
	}
	
	/**
	 * @return the receiveRead
	 */
	public String getReceiveRead( )
	{
		return receiveRead;
	}
	
	public String getTime( )
	{
		return time;
	}
	
	/**
	 * @return the type_post
	 */
	public String getType_post( )
	{
		return type_post;
	}
	
	/**
	 * @return the user2Id
	 */
	public String getUser2Id( )
	{
		return user2Id;
	}
	
	public void setContent( String content )
	{
		this.content = content;
	}
	
	public void setName( String name )
	{
		this.name = name;
	}
	
	/**
	 * @param postId
	 *            the postId to set
	 */
	public void setPostId( String postId )
	{
		this.postId = postId;
	}
	
	/**
	 * @param receiveRead
	 *            the receiveRead to set
	 */
	public void setReceiveRead( String receiveRead )
	{
		this.receiveRead = receiveRead;
	}
	
	public void setTime( String time )
	{
		this.time = time;
	}
	
	/**
	 * @param type_post
	 *            the type_post to set
	 */
	public void setType_post( String type_post )
	{
		this.type_post = type_post;
	}
	
	/**
	 * @param user2Id
	 *            the user2Id to set
	 */
	public void setUser2Id( String user2Id )
	{
		this.user2Id = user2Id;
	}
	
}
