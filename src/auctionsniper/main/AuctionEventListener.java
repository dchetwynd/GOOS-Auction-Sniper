package auctionsniper.main;

import java.util.EventListener;

public interface AuctionEventListener extends EventListener
{
	void auctionClosed();
	void currentPrice(int price, int increment, PriceSource bidder);
	
	enum PriceSource
	{
		FromSniper,
		FromOtherBidder
	};
}
