package auctionsniper.main;

import java.util.ArrayList;
import java.util.List;

public class SniperLauncher implements UserRequestListener
{
	private static List<Auction> notToBeGcd = new ArrayList<Auction>();
	private final AuctionHouse auctionHouse;
	private final SnipersTableModel snipers;
	
	public SniperLauncher(AuctionHouse auctionHouse, SnipersTableModel snipers)
	{
		this.auctionHouse = auctionHouse;
		this.snipers = snipers;
	}
	
	public void joinAuction(String itemId)
	{
		snipers.addSniper(SniperSnapshot.joining(itemId));
		
		Auction auction = auctionHouse.auctionFor(itemId);
		notToBeGcd.add(auction);				
		auction.addAuctionEventListener(
			new AuctionSniper(itemId, auction, new SwingThreadSniperListener(snipers)));
		auction.join();
	}
}
