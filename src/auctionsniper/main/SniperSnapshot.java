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
	
	public SniperSnapshot closed()
	{
		return new SniperSnapshot(itemId, lastPrice, lastBid, state.whenAuctionClosed());
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
		
		return (itemId == other.itemId)
				&& (lastPrice == other.lastPrice)
				&& (lastBid == other.lastBid)
				&& (state.ordinal() == other.state.ordinal());
	}
}