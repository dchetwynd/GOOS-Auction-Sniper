package auctionsniper.main;

public class AuctionSniper implements AuctionEventListener
{
	private final Item item;
	private final Auction auction;
	private SniperSnapshot snapshot;
	
	private final Announcer<SniperListener> sniperListeners =
		Announcer.to(SniperListener.class);
	
	public AuctionSniper(Item item, Auction auction)
	{
		this.item = item;
		this.auction = auction;
		this.snapshot = SniperSnapshot.joining(item.identifier);
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
				if (item.allowsBid(bid))
				{
					auction.bid(bid);
					snapshot = snapshot.bidding(price, bid);
				}
				else
				{
					snapshot = snapshot.losing(price);
				}
		}
		
		notifyChange();
	}
	
	public void auctionFailed()
	{
		snapshot = snapshot.failed();
		sniperListeners.announce().sniperStateChanged(snapshot);
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
