package com.cnc.textbookcrazy.model;

public class BuyModel
{
	private String	icon, bookName, authorName, distance, price, id, type, edition, description, note, bonus,
					condition, userId;
	
	public String getAuthorName( )
	{
		return authorName;
	}
	
	public String getBonus( )
	{
		return bonus;
	}
	
	public String getBookName( )
	{
		return bookName;
	}
	
	/**
	 * @return the condition
	 */
	public String getCondition( )
	{
		return condition;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription( )
	{
		return description;
	}
	
	public String getDistance( )
	{
		return distance;
	}
	
	public String getEdition( )
	{
		return edition;
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
	
	public String getNote( )
	{
		return note;
	}
	
	public String getPrice( )
	{
		return price;
	}
	
	/**
	 * @return the type
	 */
	public String getType( )
	{
		return type;
	}
	
	public void setAuthorName( String authorName )
	{
		this.authorName = authorName;
	}
	
	public void setBonus( String bonus )
	{
		this.bonus = bonus;
	}
	
	public void setBookName( String bookName )
	{
		this.bookName = bookName;
	}
	
	/**
	 * @param condition
	 *            the condition to set
	 */
	public void setCondition( String condition )
	{
		this.condition = condition;
	}
	
	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription( String description )
	{
		this.description = description;
	}
	
	public void setDistance( String distance )
	{
		this.distance = distance;
	}
	
	public void setEdition( String edition )
	{
		this.edition = edition;
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
	
	public void setNote( String note )
	{
		this.note = note;
	}
	
	public void setPrice( String price )
	{
		this.price = price;
	}
	
	/**
	 * @param type
	 *            the type to set
	 */
	public void setType( String type )
	{
		this.type = type;
	}

	/**
	 * @return the userId
	 */
	public String getUserId( )
	{
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId( String userId )
	{
		this.userId = userId;
	}
	
}
