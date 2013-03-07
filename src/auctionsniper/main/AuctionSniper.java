package auctionsniper.main;

public class AuctionSniper implements AuctionEventListener
{
	private final Auction auction;
	private SniperSnapshot snapshot;
	
	private final Announcer<SniperListener> sniperListeners =
		Announcer.to(SniperListener.class);
	
	public AuctionSniper(String itemId, Auction auction)
	{
		this.auction = auction;
		this.snapshot = SniperSnapshot.joining(itemId);
	}
	
	public void auctionClosed()
	{
		snapshot = snapshot.closed();
		notifyChange();
	}
	
	public void currentPrice(int price, int increment, PriceSource priceSource)
	{
		switch(priceSource)
		{
			case FromSniper:
				snapshot = snapshot.winning(price);
				break;
			case FromOtherBidder:
				int bid = price + increment;
				auction.bid(bid);
				snapshot = snapshot.bidding(price, bid);
		}
		
		notifyChange();
	}
	
	public void addSniperListener(SniperListener listener)
	{
		sniperListeners.addListener(listener);
	}
	
	public SniperSnapshot getSnapshot()
	{
		return snapshot;
	}
	
	private void notifyChange()
	{
		sniperListeners.announce().sniperStateChanged(snapshot);
	}
}
