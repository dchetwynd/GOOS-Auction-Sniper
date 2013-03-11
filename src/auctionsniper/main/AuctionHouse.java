package auctionsniper.main;

public interface AuctionHouse
{
	public Auction auctionFor(Item item);
	public void disconnect();
}
