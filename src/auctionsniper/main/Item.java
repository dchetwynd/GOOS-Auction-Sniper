package auctionsniper.main;

public class Item
{
	public final String identifier;
	public final int stopPrice;
	
	public Item(String identifier, int stopPrice)
	{
		this.identifier = identifier;
		this.stopPrice = stopPrice;
	}
	
	public boolean allowsBid(int bid)
	{
		return bid <= stopPrice;
	}
	
	public String toString()
	{
		return "Identifier: " + identifier + ", Stop Price: " + stopPrice;
	}
	
	public boolean equals(Object obj)
	{
		if ((obj == null) || (obj.getClass() != getClass()))
			return false;
		
		Item other = (Item) obj;
		
		return (identifier.equals(other.identifier)
				&& (stopPrice == other.stopPrice));
	}
}
