package com.cnc.textbookcrazy.model;

public class MyPostModel
{
	private String	urlImage, bookName, authorName, price;
	
	public String getAuthorName( )
	{
		return authorName;
	}
	
	public String getBookName( )
	{
		return bookName;
	}
	
	public String getPrice( )
	{
		return price;
	}
	
	public String getUrlImage( )
	{
		return urlImage;
	}
	
	public void setAuthorName( String authorName )
	{
		this.authorName = authorName;
	}
	
	public void setBookName( String bookName )
	{
		this.bookName = bookName;
	}
	
	public void setPrice( String price )
	{
		this.price = price;
	}
	
	public void setUrlImage( String urlImage )
	{
		this.urlImage = urlImage;
	}
}
