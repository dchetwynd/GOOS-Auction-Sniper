package auctionsniper.main;

import java.util.ArrayList;
import java.util.List;

public class SniperLauncher implements UserRequestListener
{
	private static List<Auction> notToBeGcd = new ArrayList<Auction>();
	private final AuctionHouse auctionHouse;
	private final SniperCollector collector;
	
	public SniperLauncher(AuctionHouse auctionHouse, SniperCollector snipers)
	{
		this.auctionHouse = auctionHouse;
		this.collector = snipers;
	}
	
	public void joinAuction(String itemId)
	{
		Auction auction = auctionHouse.auctionFor(itemId);
		notToBeGcd.add(auction);				
		AuctionSniper sniper =
			new AuctionSniper(itemId, auction, new SwingThreadSniperListener((SnipersTableModel)collector));
		collector.addSniper(sniper);
		
		auction.addAuctionEventListener(sniper);
		auction.join();
	}
}
