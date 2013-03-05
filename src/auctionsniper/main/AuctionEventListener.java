package auctionsniper.main;

public interface AuctionEventListener
{
	void auctionClosed();
	void currentPrice(int price, int increment, PriceSource bidder);
	
	enum PriceSource
	{
		FromSniper,
		FromOtherBidder
	};
}
