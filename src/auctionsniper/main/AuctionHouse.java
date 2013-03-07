package auctionsniper.main;

public interface AuctionHouse
{
	public Auction auctionFor(String itemId);
	public void disconnect();
}
