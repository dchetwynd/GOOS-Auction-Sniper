package auctionsniper.main;

public class SniperLauncher implements UserRequestListener
{
	private final AuctionHouse auctionHouse;
	private final SniperCollector collector;
	
	public SniperLauncher(AuctionHouse auctionHouse, SniperCollector collector)
	{
		this.auctionHouse = auctionHouse;
		this.collector = collector;
	}
	
	public void joinAuction(Item item)
	{
		Auction auction = auctionHouse.auctionFor(item);
		AuctionSniper sniper = new AuctionSniper(item, auction);
		collector.addSniper(sniper);
		
		auction.addAuctionEventListener(sniper);
		auction.join();
	}
}
