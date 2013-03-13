package auctionsniper.main;

public class SniperSnapshot
{
	public final String itemId;
	public final int lastPrice;
	public final int lastBid;
	public final SniperState state;
	
	public SniperSnapshot(String itemId, int lastPrice, int lastBid, SniperState state)
	{
		this.itemId = itemId;
		this.lastPrice = lastPrice;
		this.lastBid = lastBid;
		this.state = state;
	}
	
	public static SniperSnapshot joining(String itemId)
	{
		return new SniperSnapshot(itemId, 0, 0, SniperState.JOINING);
	}
	
	public SniperSnapshot winning(int newLastPrice)
	{
		return new SniperSnapshot(itemId, newLastPrice, lastBid, SniperState.WINNING);
	}
	
	public SniperSnapshot bidding(int newLastPrice, int newLastBid)
	{
		return new SniperSnapshot(itemId, newLastPrice, newLastBid, SniperState.BIDDING);
	}
	
	public SniperSnapshot losing(int newLastPrice)
	{
		return new SniperSnapshot(itemId, newLastPrice, lastBid, SniperState.LOSING);
	}
	
	public SniperSnapshot closed()
	{
		return new SniperSnapshot(itemId, lastPrice, lastBid, state.whenAuctionClosed());
	}
	
	public SniperSnapshot failed()
	{
		return new SniperSnapshot(itemId, 0, 0, SniperState.FAILED);
	}
	
	public boolean isForSameItemAs(SniperSnapshot otherSniper)
	{
		return itemId == otherSniper.itemId;
	}
	
	public String toString()
	{
		return "Item ID: " + itemId + ", Last Price: "
				+ lastPrice + ", Last Bid: " + lastBid + ", state = " + state;
	}
	
	public boolean equals(Object obj)
	{
		if ((obj == null) || (obj.getClass() != getClass()))
			return false;
		
		SniperSnapshot other = (SniperSnapshot) obj;
		
		return (itemId.equals(other.itemId)
				&& (lastPrice == other.lastPrice)
				&& (lastBid == other.lastBid)
				&& (state.ordinal() == other.state.ordinal()));
	}
}
